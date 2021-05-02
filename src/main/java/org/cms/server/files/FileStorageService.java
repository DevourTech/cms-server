package org.cms.server.files;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.cms.server.files.errors.FileStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;

	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public FileStatus storeFile(MultipartFile file, String fileName) {
		try {
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return FileStatus.UPLOAD_SUCCESS;
		} catch (IOException ex) {
			return FileStatus.UPLOAD_FAILURE;
		}
	}

	public DownloadFileResponse loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return new DownloadFileResponse(resource, FileStatus.DOWNLOAD_SUCCESS);
			}
			return new DownloadFileResponse(FileStatus.FILE_NOT_FOUND);
		} catch (MalformedURLException ex) {
			return new DownloadFileResponse(FileStatus.DOWNLOAD_FAILURE);
		}
	}
}
