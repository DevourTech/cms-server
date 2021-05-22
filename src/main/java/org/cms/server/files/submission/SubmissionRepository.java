package org.cms.server.files.submission;

import org.cms.core.files.submission.Submission;
import org.springframework.data.repository.CrudRepository;

public interface SubmissionRepository extends CrudRepository<Submission, Integer> {
	Submission findById(String id);
}
