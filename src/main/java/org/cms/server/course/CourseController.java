package org.cms.server.course;

import org.cms.core.course.Course;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CourseController {

	private static final String POST_COURSE_SUCCESS = "Course added successfully with id = %s";
	private static final String UPDATE_COURSE_FAILED = "Course to be updated with id %s doesn't exist in our database records";
	private static final String UPDATE_COURSE_SUCCESS = "Course with id %s is successfully updated";
	private static final String DELETE_COURSE_FAILED = "Course to be deleted with id %s doesn't exist in our database records";
	private static final String DELETE_COURSE_SUCCESS = "Course with id %s is successfully deleted";

	private final CourseService courseService;

	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}

	@RequestMapping("/api/courses")
	public List<Course> getAllCourses() {
		return courseService.getAllCourses();
	}

	@RequestMapping("/api/courses/{id}")
	public ResponseEntity<Course> getCourse(@PathVariable String id) {
		Course fetchedCourse = courseService.getCourse(id);
		if (fetchedCourse != null) {
			return ResponseEntity.ok(fetchedCourse);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/api/courses")
	public ResponseEntity<String> addCourse(@RequestBody Course course) {
		String idSaved = courseService.addCourse(course);
		return ResponseEntity.ok(String.format(POST_COURSE_SUCCESS, idSaved));
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/api/courses/{id}")
	public ResponseEntity<String> addCourse(@RequestBody Course course, @PathVariable String id) {
		boolean isSuccessful = courseService.updateCourse(id, course);
		if (!isSuccessful) {
			return new ResponseEntity<>(String.format(UPDATE_COURSE_FAILED, id), HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(String.format(UPDATE_COURSE_SUCCESS, id));
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/api/courses/{id}")
	public ResponseEntity<String> deleteCourse(@PathVariable String id) {
		boolean isSuccessful = courseService.deleteCourse(id);
		if (!isSuccessful) {
			return new ResponseEntity<>(String.format(DELETE_COURSE_FAILED, id), HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(String.format(DELETE_COURSE_SUCCESS, id));
	}
}
