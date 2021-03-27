package org.cms.server.server;

import org.cms.core.student.Student;
import org.cms.server.student.StudentRepository;
import org.cms.server.student.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MockAPITests {

	@MockBean
    StudentRepository studentRepoMock;

	@Autowired
    StudentService studentService;

	@Test
	public void testGetAllStudents() {
		List<Student> studentList = Arrays.asList(new Student(1, "tourist"), new Student(2, "ecnerwala "));

		when(studentRepoMock.findAll()).thenReturn(studentList);
		assertEquals(studentList, studentService.getAllStudents());
	}

	@Test
	public void testGetStudentNotNull() {
		Student expectedStudent = new Student(1, "Vagesh Verma");

		when(studentRepoMock.findById(1)).thenReturn(expectedStudent);
		assertEquals(expectedStudent, studentService.getStudent(1));
	}

	@Test
	public void testGetStudentNull() {
		when(studentRepoMock.findById(1)).thenReturn(null);
		assertNull(studentService.getStudent(1));
	}

	@Test
	public void testAddStudent() {
		String studentName = "Shivam Tanay";

		Student tobeAdded = new Student(studentName);
		Student savedStudentWithId = new Student(3, studentName);

		when(studentRepoMock.save(tobeAdded)).thenReturn(savedStudentWithId);
		assertEquals("3", studentService.addStudent(tobeAdded));
	}

	@Test
	public void testUpdateStudentHappyPath() {
		Student savedStudent = new Student(4, "Yash Chapani");
		Student tobeUpdated = new Student(4, "Chinmay Pani");

		when(studentRepoMock.findById(4)).thenReturn(savedStudent);
		when(studentRepoMock.save(savedStudent)).thenReturn(savedStudent);

		boolean isSuccessful = studentService.updateStudent(4, tobeUpdated);
		assertTrue(isSuccessful);
		//assertEquals(tobeUpdated.getDescription(), savedStudent.getDescription());
	}

	@Test
	public void testUpdateStudentNegativeCase() {
		when(studentRepoMock.findById(4)).thenReturn(null);

		boolean isSuccessful = studentService.updateStudent(4, new Student());
		assertFalse(isSuccessful);
	}

	@Test
	public void testDeleteStudentHappyPath() {
		Student savedStudent = new Student(7, "Anukul Joshi");
		int id = 45;

		when(studentRepoMock.findById(id)).thenReturn(savedStudent);
		doNothing().when(studentRepoMock).delete(savedStudent);

		assertTrue(studentService.deleteStudent(id));
	}

	@Test
	public void testDeleteStudentNegativeCase() {
		int id = 45;

		when(studentRepoMock.findById(id)).thenReturn(null);

		assertFalse(studentService.deleteStudent(id));
	}
}
