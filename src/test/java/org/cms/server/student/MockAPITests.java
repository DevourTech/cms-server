package org.cms.server.student;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.cms.core.student.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class MockAPITests {

	@MockBean
	StudentRepository studentRepoMock;

	@Autowired
	StudentService studentService;

	@Test
	public void testGetAllStudents() {
		List<Student> studentList = Arrays.asList(new Student("STU1", "Archit"), new Student("STU2", "Rohan"));

		when(studentRepoMock.findAll()).thenReturn(studentList);
		assertEquals(studentList, studentService.getAllStudents());
	}

	@Test
	public void testGetStudentNotNull() {
		Student expectedStudent = new Student("STU1", "Rohan");

		when(studentRepoMock.findById("STU1")).thenReturn(expectedStudent);
		assertEquals(expectedStudent, studentService.getStudent("STU1"));
	}

	@Test
	public void testGetStudentNull() {
		when(studentRepoMock.findById("1")).thenReturn(null);
		assertNull(studentService.getStudent("STU1"));
	}

	@Test
	public void testAddStudent() {
		String studentName = "Ishan";

		Student tobeAdded = new Student(studentName);
		Student savedStudentWithId = new Student("STU3", studentName);

		when(studentRepoMock.save(tobeAdded)).thenReturn(savedStudentWithId);
		assertEquals("STU3", studentService.addStudent(tobeAdded));
	}

	@Test
	public void testUpdateStudentHappyPath() {
		Student savedStudent = new Student("STU4", "Ramesh Sir");
		Student tobeUpdated = new Student("STU4", "Saleel Sir");

		when(studentRepoMock.findById("STU4")).thenReturn(savedStudent);
		when(studentRepoMock.save(savedStudent)).thenReturn(savedStudent);

		boolean isSuccessful = studentService.updateStudent("STU4", tobeUpdated);
		assertTrue(isSuccessful);
		assertEquals(tobeUpdated.getName(), savedStudent.getName());
	}

	@Test
	public void testUpdateStudentNegativeCase() {
		when(studentRepoMock.findById("STU4")).thenReturn(null);

		boolean isSuccessful = studentService.updateStudent("STU4", new Student());
		assertFalse(isSuccessful);
	}

	@Test
	public void testDeleteStudentHappyPath() {
		Student savedStudent = new Student("STU45", "Rajat");
		String id = "STU45";

		when(studentRepoMock.findById(id)).thenReturn(savedStudent);
		doNothing().when(studentRepoMock).delete(savedStudent);

		assertTrue(studentService.deleteStudent(id));
	}

	@Test
	public void testDeleteStudentNegativeCase() {
		String id = "STU45";

		when(studentRepoMock.findById(id)).thenReturn(null);

		assertFalse(studentService.deleteStudent(id));
	}
}
