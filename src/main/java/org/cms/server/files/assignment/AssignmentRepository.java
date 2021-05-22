package org.cms.server.files.assignment;

import java.util.List;
import org.cms.core.files.assignment.Assignment;
import org.cms.server.commons.SQLQueries;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AssignmentRepository extends CrudRepository<Assignment, Integer> {
	Assignment findById(String id);

	@Query(nativeQuery = true, value = SQLQueries.DUE_ASSIGNMENTS_QUERY)
	List<Assignment> dueAssignmentsForStudent(@Param("studentId") String studentId);
}
