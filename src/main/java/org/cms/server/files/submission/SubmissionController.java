package org.cms.server.files.submission;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.cms.core.commons.DateTimePattern;
import org.cms.core.files.assignment.Assignment;
import org.cms.core.files.submission.Submission;
import org.cms.core.student.Student;
import org.cms.events.Event;
import org.cms.server.commons.JacksonConfiguration;
import org.cms.server.files.commons.FileHandler;
import org.cms.server.files.commons.FileType;
import org.cms.server.files.commons.UploadFileResponse;
import org.cms.server.kafka.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class SubmissionController {

	private static final Logger logger = LoggerFactory.getLogger(SubmissionController.class);

	private final FileHandler fileHandler;
	private final KafkaProducer producer;
	private final SubmissionService submissionService;
	private final JacksonConfiguration jacksonConfiguration;

	public SubmissionController(
		FileHandler fileHandler,
		KafkaProducer producer,
		SubmissionService submissionService,
		JacksonConfiguration jacksonConfiguration
	) {
		this.fileHandler = fileHandler;
		this.producer = producer;
		this.submissionService = submissionService;
		this.jacksonConfiguration = jacksonConfiguration;
	}

	@PostMapping(value = "/upload/submissions", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<UploadFileResponse> uploadFile(
		@RequestPart("student") String student,
		@RequestPart("assignment") String assignment,
		@RequestPart("file") MultipartFile file
	) throws JsonProcessingException {
		logger.info(String.format("Request for submission of assignment by student %s", student));
		ResponseEntity<UploadFileResponse> responseEntity = fileHandler.uploadFile(file, FileType.SUBMISSION);

		if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
			logger.error("Some error in uploading submission to FS");
			return responseEntity;
		}

		String downloadPath = responseEntity.getBody().getFileDownloadUri();

		// Persist submission to the database
		Assignment assignmentForSubmission = jacksonConfiguration.getMapper().readValue(assignment, Assignment.class);
		Submission submission = createAndStoreSubmission(student, assignmentForSubmission, downloadPath);
		logger.info("Submission created and stored");

		// Produce submission upload event to Kafka
		String topic = "submissions";
		produceSubmissionUploadEvent(topic, submission);
		return responseEntity;
	}

	@GetMapping("/download/submissions/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		return fileHandler.downloadFile(fileName, request, FileType.SUBMISSION);
	}

	@RequestMapping("/api/submissions")
	public List<Submission> getAllAssignments() {
		return submissionService.getAllSubmissions();
	}

	@RequestMapping("/api/submissions/{id}")
	public ResponseEntity<Submission> getSubmission(@PathVariable String id) {
		Submission fetchedSubmission = submissionService.getSubmission(id);
		if (fetchedSubmission != null) {
			return ResponseEntity.ok(fetchedSubmission);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	private void produceSubmissionUploadEvent(String topic, Submission submission) throws JsonProcessingException {
		logger.info("Producing Submission Upload Event to Kafka...");
		producer.produce(topic, Event.ASSIGNMENT_SUBMISSION, submission);
		logger.info("Submission Upload Event produced to kafka");
	}

	private Submission createAndStoreSubmission(String studentJSON, Assignment assignment, String downloadPath)
		throws JsonProcessingException {
		Student student = jacksonConfiguration.getMapper().readValue(studentJSON, Student.class);
		Submission submission = new Submission(assignment, student, downloadPath);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimePattern.YYYY_MM_DD_HH_MM);
		String currentTime = LocalDateTime.now().format(formatter);
		submission.setUploadDate(currentTime);
		String submissionId = submissionService.addSubmission(submission);
		submission.setId(submissionId);
		return submission;
	}
}
