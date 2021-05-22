package org.cms.server.files.assignment;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.cms.core.commons.DateTimePattern;
import org.cms.core.course.Course;
import org.cms.core.files.assignment.Assignment;
import org.cms.core.instructor.Instructor;
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
public class AssignmentController {

	private static final Logger logger = LoggerFactory.getLogger(AssignmentController.class);

	private final FileHandler fileHandler;
	private final KafkaProducer producer;
	private final AssignmentService assignmentService;
	private final JacksonConfiguration jacksonConfiguration;

	public AssignmentController(
		FileHandler fileHandler,
		KafkaProducer producer,
		AssignmentService assignmentService,
		JacksonConfiguration jacksonConfiguration
	) {
		this.fileHandler = fileHandler;
		this.producer = producer;
		this.assignmentService = assignmentService;
		this.jacksonConfiguration = jacksonConfiguration;
	}

	@PostMapping(value = "/upload/assignments", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<UploadFileResponse> uploadFile(
		@RequestPart("instructor") String instructor,
		@RequestPart("course") String course,
		@RequestPart("file") MultipartFile file,
		@RequestPart("dueDate") String dueDate
	) throws JsonProcessingException {
		logger.info(String.format("Request for upload of assignment by instructor %s", instructor));
		ResponseEntity<UploadFileResponse> responseEntity = fileHandler.uploadFile(file, FileType.ASSIGNMENT);

		if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
			logger.error("Some error in uploading assignment to FS");
			return responseEntity;
		}

		String downloadPath = responseEntity.getBody().getFileDownloadUri();

		// Persist assignment to the database
		Course assignmentCourse = getCourse(course);
		Assignment assignment = createAndStoreAssignment(instructor, assignmentCourse, downloadPath, dueDate);
		logger.info("Assignment created and stored to DB");

		// Produce assignment upload event to Kafka
		String topic = "assignments";
		produceAssignmentUploadEvent(topic, assignment);
		return responseEntity;
	}

	@GetMapping("/download/assignments/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		return fileHandler.downloadFile(fileName, request, FileType.ASSIGNMENT);
	}

	@RequestMapping("/api/assignments")
	public List<Assignment> getAllAssignments() {
		return assignmentService.getAllAssignments();
	}

	@RequestMapping("/api/assignments/{id}")
	public ResponseEntity<Assignment> getAssignment(@PathVariable String id) {
		Assignment fetchedAssignment = assignmentService.getAssignment(id);
		if (fetchedAssignment != null) {
			return ResponseEntity.ok(fetchedAssignment);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@RequestMapping("/api/assignments/withCourses")
	public List<Assignment> getAssignmentsWithCourses(@RequestParam Map<String, String> params) {
		List<String> courseIds = new ArrayList<>(params.values());
		return assignmentService.getAssignmentsWithCourseIds(courseIds);
	}

	private void produceAssignmentUploadEvent(String topic, Assignment assignment) throws JsonProcessingException {
		logger.info("Producing Assignment Upload Event to Kafka...");
		producer.produce(topic, Event.ASSIGNMENT_UPLOAD, assignment);
		logger.info("Assignment Upload Event produced to kafka");
	}

	private Assignment createAndStoreAssignment(String instructorJSON, Course course, String downloadPath, String dueDate)
		throws JsonProcessingException {
		Instructor instructor = jacksonConfiguration.getMapper().readValue(instructorJSON, Instructor.class);
		Assignment assignment = new Assignment(instructor, downloadPath, course);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimePattern.YYYY_MM_DD_HH_MM);
		String currentTime = LocalDateTime.now().format(formatter);

		assignment.setUploadDate(currentTime);
		assignment.setDueDate(dueDate);

		String assignmentId = assignmentService.addAssignment(assignment);
		assignment.setId(assignmentId);
		return assignment;
	}

	private Course getCourse(String json) throws JsonProcessingException {
		return jacksonConfiguration.getMapper().readValue(json, Course.class);
	}
}
