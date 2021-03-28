package org.cms.server.course;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.cms.core.course.Course;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
public class MockAPITests {

	@MockBean
	CourseRepository courseRepoMock;

	@Autowired
	CourseService courseService;

	@Test
	public void testGetAllCourses() {
		List<Course> courseList = Arrays.asList(
			new Course("OOPS", "CSE", "I did it again!"),
			new Course("Formal methods", "CSE", "Wasn't so formal!")
		);

		when(courseRepoMock.findAll()).thenReturn(courseList);
		assertEquals(courseList, courseService.getAllCourses());
	}

	@Test
	public void testGetCourseNotNull() {
		Course expectedCourse = new Course("1", "Networking", "CSE", "Something which I didn't do!");

		when(courseRepoMock.findById("1")).thenReturn(expectedCourse);
		assertEquals(expectedCourse, courseService.getCourse("1"));
	}

	@Test
	public void testGetCourseNull() {
		when(courseRepoMock.findById("1")).thenReturn(null);
		assertNull(courseService.getCourse("1"));
	}

	@Test
	public void testAddCourse() {
		String courseName = "Research Trends in CS";
		String desc = "Why though?";
		String branch = "CSE";

		Course tobeAdded = new Course(courseName, branch, desc);
		Course savedCourseWithId = new Course("3", courseName, branch, desc);

		when(courseRepoMock.save(tobeAdded)).thenReturn(savedCourseWithId);
		assertEquals("3", courseService.addCourse(tobeAdded));
	}

	@Test
	public void testUpdateCourseHappyPath() {
		Course savedCourse = new Course("4", "Operating Systems", "CSE", "Do I have a thing for them?");
		Course tobeUpdated = new Course("4", "CSE", "I love them!");

		when(courseRepoMock.findById("4")).thenReturn(savedCourse);
		when(courseRepoMock.save(savedCourse)).thenReturn(savedCourse);

		boolean isSuccessful = courseService.updateCourse("4", tobeUpdated);
		assertTrue(isSuccessful);
		assertEquals(tobeUpdated.getDescription(), savedCourse.getDescription());
	}

	@Test
	public void testUpdateCourseNegativeCase() {
		when(courseRepoMock.findById("4")).thenReturn(null);

		boolean isSuccessful = courseService.updateCourse("4", new Course());
		assertFalse(isSuccessful);
	}

	@Test
	public void testDeleteCourseHappyPath() {
		Course savedCourse = new Course("DBMS", "CSE", "You're gone!");
		String id = "45";

		when(courseRepoMock.findById(id)).thenReturn(savedCourse);
		doNothing().when(courseRepoMock).delete(savedCourse);

		assertTrue(courseService.deleteCourse(id));
	}

	@Test
	public void testDeleteCourseNegativeCase() {
		String id = "45";

		when(courseRepoMock.findById(id)).thenReturn(null);

		assertFalse(courseService.deleteCourse(id));
	}
}
