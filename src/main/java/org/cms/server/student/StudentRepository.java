package org.cms.server.student;

import java.util.List;
import org.cms.core.student.Student;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Integer> {
	List<Student> findByName(String name);
	Student findById(String id);
}
