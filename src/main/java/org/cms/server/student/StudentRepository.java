package org.cms.server.student;

import org.cms.core.student.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentRepository extends CrudRepository<Student, Integer> {
	List<Student> findByName(String name);
	Student findById(int id);
}
