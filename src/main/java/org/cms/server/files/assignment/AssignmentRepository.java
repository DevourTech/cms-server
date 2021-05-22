package org.cms.server.files.assignment;

import org.cms.core.files.assignment.Assignment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentRepository extends CrudRepository<Assignment, Integer> {
	Assignment findById(String id);

	@Query(nativeQuery = true, value = "SELECT * from org_cms.assignment where course_course_id IN (:courses)")
	List<Assignment> findByCourses(@Param("courses") List<String> courseIds);
}
