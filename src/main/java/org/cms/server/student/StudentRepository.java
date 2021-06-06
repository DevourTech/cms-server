package org.cms.server.student;

import java.util.List;
import org.cms.core.files.assignment.Assignment;
import org.cms.core.student.Student;
import org.cms.server.commons.SQLQueries;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends CrudRepository<Student, Integer> {
	List<Student> findByName(String name);
	Student findById(String id);

	@Query(nativeQuery = true, value = SQLQueries.STUDENTS_SUBSCRIBED_TO_COURSE)
	List<Student> getStudentsSubscribedToCourse(@Param("courseId") String courseId);
}
