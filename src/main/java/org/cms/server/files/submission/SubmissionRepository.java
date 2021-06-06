package org.cms.server.files.submission;

import org.cms.core.files.submission.Submission;
import org.cms.server.commons.SQLQueries;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepository extends CrudRepository<Submission, Integer> {
	Submission findById(String id);

	@Query(nativeQuery = true, value = SQLQueries.ASSIGNMENT_STATUS_OF_STUDENT)
	int getAssignmentStatusForStudent(@Param("studentId") String studentId, @Param("assignmentId") String assignmentId);
}
