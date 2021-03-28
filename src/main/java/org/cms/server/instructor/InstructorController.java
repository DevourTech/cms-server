package org.cms.server.instructor;

import java.util.List;
import org.cms.core.instructor.Instructor;
import org.cms.server.instructor.InstructorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class InstructorController {

	private static final String POST_INSTRUCTOR_SUCCESS = "Instructor added successfully with id = %s";
	private static final String UPDATE_INSTRUCTOR_FAILED = "Instructor to be updated with id %s doesn't exist in our database records";
	private static final String UPDATE_INSTRUCTOR_SUCCESS = "Instructor with id %s is successfully updated";
	private static final String DELETE_INSTRUCTOR_FAILED = "Instructor to be deleted with id %s doesn't exist in our database records";
	private static final String DELETE_INSTRUCTOR_SUCCESS = "Instructor with id %s is successfully deleted";

	private final InstructorService instructorService;

	public InstructorController(InstructorService instructorService) {
		this.instructorService = instructorService;
	}

	@RequestMapping("/api/instructors")
	public List<Instructor> getAllInstructors() {
		return instructorService.getAllInstructors();
	}

	@RequestMapping("/api/instructors/{id}")
	public ResponseEntity<Instructor> getInstructor(@PathVariable String id) {
		Instructor fetchedInstructor = instructorService.getInstructor(id);
		if (fetchedInstructor != null) {
			return ResponseEntity.ok(fetchedInstructor);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/api/instructors")
	public ResponseEntity<String> addInstructor(@RequestBody Instructor instructor) {
		String idSaved = instructorService.addInstructor(instructor);
		return ResponseEntity.ok(String.format(POST_INSTRUCTOR_SUCCESS, idSaved));
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/api/instructors/{id}")
	public ResponseEntity<String> addInstructor(@RequestBody Instructor instructor, @PathVariable String id) {
		boolean isSuccessful = instructorService.updateInstructor(id, instructor);
		if (!isSuccessful) {
			return new ResponseEntity<>(String.format(UPDATE_INSTRUCTOR_FAILED, id), HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(String.format(UPDATE_INSTRUCTOR_SUCCESS, id));
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/api/instructors/{id}")
	public ResponseEntity<String> deleteInstructor(@PathVariable String id) {
		boolean isSuccessful = instructorService.deleteInstructor(id);
		if (!isSuccessful) {
			return new ResponseEntity<>(String.format(DELETE_INSTRUCTOR_FAILED, id), HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(String.format(DELETE_INSTRUCTOR_SUCCESS, id));
	}
}
