package org.cms.server.files.commons;

import java.io.Serializable;

public class UploadFileResponse implements Serializable {

	private String fileName;
	private String fileDownloadUri;
	private String fileType;
	private long size;
	private String error;

	public UploadFileResponse(String fileName, String fileDownloadUri, String fileType, long size) {
		this.fileName = fileName;
		this.fileDownloadUri = fileDownloadUri;
		this.fileType = fileType;
		this.size = size;
	}

	public UploadFileResponse(String error) {
		this.error = error;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDownloadUri() {
		return fileDownloadUri;
	}

	public void setFileDownloadUri(String fileDownloadUri) {
		this.fileDownloadUri = fileDownloadUri;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public UploadFileResponse() {}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
