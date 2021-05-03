package org.cms.server.student;

import java.util.List;
import java.util.Set;
import org.cms.core.course.Course;
import org.cms.core.student.Student;
import org.cms.server.course.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

	private final Logger logger = LoggerFactory.getLogger(StudentService.class);
	private static final String getAllStudentPrefix = "GET - getAllStudents";
	private static final String getStudentPrefix = "GET - getStudent";
	private static final String addStudentPrefix = "POST - addStudent";
	private static final String updateStudentPrefix = "PUT - updateStudent";
	private static final String deleteStudentPrefix = "DELETE - deleteStudent";
	private static final String getCoursesForStudentPrefix = "GET - getCoursesForStudent";
	private static final String subscribePrefix = "PUT - subscribe";

	private final StudentRepository studentRepository;
	private final CourseRepository courseRepository;

	public StudentService(StudentRepository studentRepository, CourseRepository courseRepository) {
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
	}

	public List<Student> getAllStudents() {
		logger.info(String.format("[%s] getAllStudents HIT", getAllStudentPrefix));
		return (List<Student>) studentRepository.findAll();
	}

	// If student doesn't exist in db, it returns null
	public Student getStudent(String id) {
		logger.info(String.format("[%s] id received to fetch student = %s", getStudentPrefix, id));

		Student byId = studentRepository.findById(id);
		if (byId == null) {
			logger.error(String.format("[%s] Student with id %s doesn't exist in db", getStudentPrefix, id));
		} else {
			logger.info(String.format("[%s] Student fetched from db with id %s is %s", getStudentPrefix, id, byId));
		}

		return byId;
	}

	// Returns the id of the added student
	public String addStudent(Student student) {
		logger.info(String.format("[%s] addStudent HIT", addStudentPrefix));
		logger.info(String.format("[%s] Student received to be added - %s", addStudentPrefix, student));

		Student savedStudent = studentRepository.save(student);
		String genId = String.valueOf(savedStudent.getId());
		logger.info(String.format("[%s] Student successfully added with id = %s", addStudentPrefix, genId));

		return genId;
	}

	// Returns true if update was successful
	// Returns false if student to be updated doesn't exist
	public boolean updateStudent(String id, Student student) {
		logger.info(String.format("[%s] updateStudent HIT for id = %s", updateStudentPrefix, id));
		logger.info(String.format("[%s] Student details = %s", updateStudentPrefix, student));

		Student existingStudent = studentRepository.findById(id);
		if (existingStudent == null) {
			logger.error(String.format("[%s] Student to be updated (id = %s) doesn't exist", updateStudentPrefix, id));
			return false;
		}

		updateExistingStudent(existingStudent, student);
		studentRepository.save(existingStudent);
		logger.info(String.format("[%s] Student with id %s successfully updated", updateStudentPrefix, id));

		return true;
	}

	private void updateExistingStudent(Student existingStudent, Student otherStudent) {
		if (otherStudent.getName() != null) {
			existingStudent.setName(otherStudent.getName());
		}
	}

	// Returns true if student is successfully deleted
	// Returns false if student doesn't exist
	public boolean deleteStudent(String id) {
		logger.info(String.format("[%s] deleteStudent HIT for id = %s", deleteStudentPrefix, id));

		Student toBeRemoved = studentRepository.findById(id);
		if (toBeRemoved == null) {
			logger.error(String.format("[%s] Student to be deleted (id = %s) doesn't exist", deleteStudentPrefix, id));
			return false;
		}

		studentRepository.delete(toBeRemoved);
		logger.info(String.format("[%s] Student with id %s is successfully deleted", deleteStudentPrefix, id));
		return true;
	}

	public List<Course> getCoursesForStudent(String id) {
		logger.info(String.format("[%s] getCoursesForStudent HIT for id = %s", getCoursesForStudentPrefix, id));
		Student student = getStudent(id);
		return student.getSubscribedCourses();
	}

	public boolean subscribe(String studentId, String courseId) {
		logger.info(String.format("[%s] subscribe HIT for student id = %s and course id = %s", subscribePrefix, studentId, courseId));
		Student student = studentRepository.findById(studentId);
		if (student == null) {
			logger.error(
				String.format(
					"[%s] Student with id = %s doesn't exist; can't subscribe to course with id = %s",
					subscribePrefix,
					studentId,
					courseId
				)
			);
			return false;
		}

		Course course = courseRepository.findById(courseId);
		if (course == null) {
			logger.error(
				String.format(
					"[%s] Course with id = %s doesn't exist; cannot be subscribed to student with id = %s",
					subscribePrefix,
					courseId,
					studentId
				)
			);
			return false;
		}

		student.getSubscribedCourses().add(course);
		studentRepository.save(student);
		logger.info(
			String.format(
				"[%s] Student with id = %s is successfully subscribed to course with id = %s",
				subscribePrefix,
				studentId,
				courseId
			)
		);
		return true;
	}

	public boolean unsubscribe(String studentId, String courseId) {
		logger.info(String.format("[%s] unsubscribe HIT for student id = %s and course id = %s", subscribePrefix, studentId, courseId));
		Student student = studentRepository.findById(studentId);
		if (student == null) {
			logger.error(
				String.format(
					"[%s] Student with id = %s doesn't exist; can't unsubscribe to course with id = %s",
					subscribePrefix,
					studentId,
					courseId
				)
			);
			return false;
		}

		Course course = courseRepository.findById(courseId);
		if (course == null) {
			logger.error(
				String.format(
					"[%s] Course with id = %s doesn't exist; cannot be unsubscribed to student with id = %s",
					subscribePrefix,
					courseId,
					studentId
				)
			);
			return false;
		}

		student.getSubscribedCourses().remove(course);
		studentRepository.save(student);
		logger.info(
			String.format(
				"[%s] Student with id = %s has successfully unsubscribed to course with id = %s",
				subscribePrefix,
				studentId,
				courseId
			)
		);
		return true;
	}
}
