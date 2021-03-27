package org.cms.server.student;

import org.cms.core.student.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

	private final Logger logger = LoggerFactory.getLogger(StudentService.class);
	private static final String getAllStudentPrefix = "GET - getAllStudents";
	private static final String getStudentPrefix = "GET - getStudent";
	private static final String addStudentPrefix = "POST - addStudent";
	private static final String updateStudentPrefix = "PUT - updateStudent";
	private static final String deleteStudentPrefix = "DELETE - deleteStudent";

	private final StudentRepository studentRepository;

	public StudentService(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
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
		if (!otherStudent.getName().equals("")) {
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
}
