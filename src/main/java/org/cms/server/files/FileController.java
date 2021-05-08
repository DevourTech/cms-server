package org.cms.server.files;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cms.core.course.Course;
import org.cms.core.instructor.Instructor;
import org.cms.events.apis.assignment.upload.AssignmentUploadEvent;
import org.cms.server.files.errors.FileStatus;
import org.cms.server.kafka.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class FileController {

	private static final String UPLOAD_FILE_SUCCESS = "Upload success : File %s added successfully uploaded";
	private static final String UPLOAD_FILE_FAILED = "Upload failed : File %s could not be uploaded";

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	private final KafkaProducer producer;
	private final FileStorageService fileStorageService;

	public FileController(KafkaProducer producer, FileStorageService fileStorageService) {
		this.producer = producer;
		this.fileStorageService = fileStorageService;
	}

	@PostMapping(value = "/upload/assignments", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<UploadFileResponse> uploadFile(
		@RequestPart("instructor") String instructor,
		@RequestPart("course") String course,
		@RequestPart("file") MultipartFile file
	) throws JsonProcessingException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		if (fileName.contains("..")) {
			return new ResponseEntity<>(
				new UploadFileResponse("Sorry! Filename contains invalid path sequence " + fileName),
				HttpStatus.BAD_REQUEST
			);
		}
		FileStatus status = fileStorageService.storeFile(file, fileName);

		String fileDownloadUri = ServletUriComponentsBuilder
			.fromCurrentContextPath()
			.path("/download/assignments/")
			.path(fileName)
			.toUriString();

		if (status == FileStatus.UPLOAD_SUCCESS) {
			logger.info(String.format(UPLOAD_FILE_SUCCESS, fileName));
			produceAssignmentUploadEvent(instructor, fileDownloadUri, course);
			return new ResponseEntity<>(
				new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize()),
				HttpStatus.CREATED
			);
		}

		return new ResponseEntity<>(new UploadFileResponse(String.format(UPLOAD_FILE_FAILED, fileName)), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/download/assignments/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		DownloadFileResponse response = fileStorageService.loadFileAsResource(fileName);
		if (response.getStatus() == FileStatus.FILE_NOT_FOUND) {
			return ResponseEntity.notFound().build();
		}

		if (response.getStatus() == FileStatus.DOWNLOAD_FAILURE) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Resource resource = response.getResource();
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.warn("Could not determine file type. Assuming octet-stream");
			contentType = "application/octet-stream";
		}

		return ResponseEntity
			.ok()
			.contentType(MediaType.parseMediaType(contentType))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
			.body(resource);
	}

	private void produceAssignmentUploadEvent(String instructorJSON, String downloadPath, String courseJSON)
		throws JsonProcessingException {
		logger.info("[KAFKA] Instructor - " + instructorJSON);
		ObjectMapper mapper = new ObjectMapper();
		Instructor instructor = mapper.readValue(instructorJSON, Instructor.class);
		Course course = mapper.readValue(courseJSON, Course.class);
		AssignmentUploadEvent event = new AssignmentUploadEvent(instructor, downloadPath, course);
		logger.info("Event created");
		producer.produce(event, "sample");
		logger.info("Event sent to kafka");
	}
}
