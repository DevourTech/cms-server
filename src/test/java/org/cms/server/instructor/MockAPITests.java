package org.cms.server.instructor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.cms.core.course.Course;
import org.cms.core.instructor.Instructor;
import org.cms.server.course.CourseRepository;
import org.cms.server.course.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class MockAPITests {

	@MockBean
	InstructorRepository instructorRepoMock;

	@Autowired
	InstructorService instructorService;

	@Test
	public void testGetAllInstructors() {
		List<Instructor> instructorList = Arrays.asList(new Instructor("INST1", "Pulkit chochu"), new Instructor("INST2", "Yadav feku"));

		when(instructorRepoMock.findAll()).thenReturn(instructorList);
		assertEquals(instructorList, instructorService.getAllInstructors());
	}

	@Test
	public void testInstructorNotNull() {
		Instructor expectedInstructor = new Instructor("INST1", "Chabra bhaukali");

		when(instructorRepoMock.findById("INST1")).thenReturn(expectedInstructor);
		assertEquals(expectedInstructor, instructorService.getInstructor("INST1"));
	}

	@Test
	public void testGetInstructorNull() {
		when(instructorRepoMock.findById("1")).thenReturn(null);
		assertNull(instructorService.getInstructor("1"));
	}

	@Test
	public void testAddInstructor() {
		String instructorName = "Archit lolu";

		Instructor tobeAdded = new Instructor(instructorName);
		Instructor savedInstructorWithId = new Instructor("INST3", instructorName);

		when(instructorRepoMock.save(tobeAdded)).thenReturn(savedInstructorWithId);
		assertEquals("INST3", instructorService.addInstructor(tobeAdded));
	}

	@Test
	public void testUpdateInstructorHappyPath() {
		Instructor savedInstructor = new Instructor("INST4", "Rajat bhalu");
		Instructor tobeUpdated = new Instructor("INST4", "Apoorv uhg");

		when(instructorRepoMock.findById("INST4")).thenReturn(savedInstructor);
		when(instructorRepoMock.save(savedInstructor)).thenReturn(savedInstructor);

		boolean isSuccessful = instructorService.updateInstructor("INST4", tobeUpdated);
		assertTrue(isSuccessful);
		assertEquals(tobeUpdated.getName(), savedInstructor.getName());
	}

	@Test
	public void testUpdateCourseNegativeCase() {
		when(instructorRepoMock.findById("INST4")).thenReturn(null);

		boolean isSuccessful = instructorService.updateInstructor("INST4", new Instructor());
		assertFalse(isSuccessful);
	}

	@Test
	public void testDeleteInstructorHappyPath() {
		Instructor savedInstructor = new Instructor("INST5", "Shekhar hsbc");
		String id = "INST5";

		when(instructorRepoMock.findById(id)).thenReturn(savedInstructor);
		doNothing().when(instructorRepoMock).delete(savedInstructor);

		assertTrue(instructorService.deleteInstructor(id));
	}

	@Test
	public void testDeleteInstructorNegativeCase() {
		String id = "INST5";

		when(instructorRepoMock.findById(id)).thenReturn(null);

		assertFalse(instructorService.deleteInstructor(id));
	}
}
