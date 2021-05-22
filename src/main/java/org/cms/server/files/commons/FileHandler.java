package org.cms.server.files.commons;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class FileHandler {

	private static final String UPLOAD_FILE_SUCCESS = "Upload success : File %s added successfully uploaded";
	private static final String UPLOAD_FILE_FAILED = "Upload failed : File %s could not be uploaded";

	private static final Logger logger = LoggerFactory.getLogger(FileHandler.class);

	private final FileStorageService fileStorageService;

	public FileHandler(FileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}

	public ResponseEntity<UploadFileResponse> uploadFile(MultipartFile file, FileType fileType) {
		logger.info("Upload file hit for file type - " + fileType.name());

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		logger.info("Filename of the uploaded file - " + fileName);
		if (fileName.contains("..")) {
			logger.error("Filename contains invalid characters");
			return new ResponseEntity<>(
				new UploadFileResponse("Sorry! Filename contains invalid path sequence " + fileName),
				HttpStatus.BAD_REQUEST
			);
		}

		logger.info("Uploading file to filesystem....");
		FileStatus status = fileStorageService.storeFile(file, fileName, fileType);

		String fileDownloadUri = ServletUriComponentsBuilder
			.fromCurrentContextPath()
			.path("/download/" + fileType.name().toLowerCase() + "s/")
			.path(fileName)
			.toUriString();

		if (status == FileStatus.UPLOAD_SUCCESS) {
			logger.info(String.format(UPLOAD_FILE_SUCCESS, fileName));
			return new ResponseEntity<>(
				new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize()),
				HttpStatus.CREATED
			);
		}

		logger.error("File couldn't be uploaded to filesystem due to some error");
		return new ResponseEntity<>(new UploadFileResponse(String.format(UPLOAD_FILE_FAILED, fileName)), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<Resource> downloadFile(String fileName, HttpServletRequest request, FileType fileType) {
		logger.info("Download file hit with requested file name - " + fileName);

		DownloadFileResponse response = fileStorageService.loadFileAsResource(fileName, fileType);
		if (response.getStatus() == FileStatus.FILE_NOT_FOUND) {
			logger.error("File couldn't be found on the server");
			return ResponseEntity.notFound().build();
		}

		if (response.getStatus() == FileStatus.DOWNLOAD_FAILURE) {
			logger.error("File couldn't be fetched from filesystem due to some error");
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Resource resource = response.getResource();
		String contentType;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.warn("Could not determine file type. Assuming octet-stream");
			contentType = "application/octet-stream";
		}

		logger.info("File fetched from filesystem successfully");
		return ResponseEntity
			.ok()
			.contentType(MediaType.parseMediaType(contentType))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
			.body(resource);
	}
}
