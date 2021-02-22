package org.cms.server.student;

import org.cms.core.student.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentController {

	private static final String POST_STUDENT_SUCCESS = "Student added successfully with id = %s";
	private static final String UPDATE_STUDENT_FAILED = "Student to be updated with id %d doesn't exist in our database records";
	private static final String UPDATE_STUDENT_SUCCESS = "Student with id %d is successfully updated";
	private static final String DELETE_STUDENT_FAILED = "Student to be deleted with id %d doesn't exist in our database records";
	private static final String DELETE_STUDENT_SUCCESS = "Student with id %d is successfully deleted";

	private final StudentService studentService;

	public StudentController(StudentService studentService) {
		this.studentService = studentService;
	}

	@RequestMapping("/api/students")
	public List<Student> getAllStudents() {
		return studentService.getAllStudents();
	}

	@RequestMapping("/api/students/{id}")
	public ResponseEntity<Student> getStudent(@PathVariable int id) {
		Student fetchedStudent = studentService.getStudent(id);
		if (fetchedStudent != null) {
			return ResponseEntity.ok(fetchedStudent);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/api/students")
	public ResponseEntity<String> addStudent(@RequestBody Student student) {
		String idSaved = studentService.addStudent(student);
		return ResponseEntity.ok(String.format(POST_STUDENT_SUCCESS, idSaved));
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/api/students/{id}")
	public ResponseEntity<String> addStudent(@RequestBody Student student, @PathVariable int id) {
		boolean isSuccessful = studentService.updateStudent(id, student);
		if (!isSuccessful) {
			return new ResponseEntity<>(String.format(UPDATE_STUDENT_FAILED, id), HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(String.format(UPDATE_STUDENT_SUCCESS, id));
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/api/students/{id}")
	public ResponseEntity<String> deleteStudent(@PathVariable int id) {
		boolean isSuccessful = studentService.deleteStudent(id);
		if (!isSuccessful) {
			return new ResponseEntity<>(String.format(DELETE_STUDENT_FAILED, id), HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(String.format(DELETE_STUDENT_SUCCESS, id));
	}
}
