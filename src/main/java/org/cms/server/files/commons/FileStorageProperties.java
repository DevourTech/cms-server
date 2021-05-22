package org.cms.server.files.commons;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

	private String assignmentDir;
	private String submissionDir;

	public String getSubmissionDir() {
		return submissionDir;
	}

	public void setSubmissionDir(String submissionDir) {
		this.submissionDir = submissionDir;
	}

	public String getAssignmentDir() {
		return assignmentDir;
	}

	public void setAssignmentDir(String assignmentDir) {
		this.assignmentDir = assignmentDir;
	}
}
