package org.cms.server.files;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import org.cms.server.files.errors.FileStatus;
import org.springframework.core.io.Resource;

public class DownloadFileResponse implements Serializable {

	private Resource resource;

	@JsonIgnore
	private FileStatus status;

	private String message;

	public DownloadFileResponse() {}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public DownloadFileResponse(Resource resource, FileStatus status) {
		this.resource = resource;
		this.status = status;
	}

	public DownloadFileResponse(Resource resource, String message) {
		this.resource = resource;
		this.message = message;
	}

	public DownloadFileResponse(FileStatus status) {
		this.status = status;
	}

	public DownloadFileResponse(String message) {
		this.message = message;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
	}
}
