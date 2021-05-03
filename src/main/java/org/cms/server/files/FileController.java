package org.cms.server.files;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.cms.server.files.errors.FileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class FileController {

	private static final String UPLOAD_FILE_SUCCESS = "Upload success : File %s added successfully uploaded";
	private static final String UPLOAD_FILE_FAILED = "Upload failed : File %s could not be uploaded";

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/upload/assignments")
	public ResponseEntity<UploadFileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
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
}
