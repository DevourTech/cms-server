package org.cms.server.files.submission;

import java.util.List;
import org.cms.core.files.submission.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SubmissionService {

	private final Logger logger = LoggerFactory.getLogger(SubmissionService.class);
	private static final String getAllSubmissionsPrefix = "GET - getAllSubmissions";
	private static final String getSubmissionPrefix = "GET - getSubmission";
	private static final String addSubmissionPrefix = "POST - addSubmission";

	private final SubmissionRepository submissionRepository;

	public SubmissionService(SubmissionRepository repository) {
		this.submissionRepository = repository;
	}

	public List<Submission> getAllSubmissions() {
		logger.info(String.format("[%s] getAllSubmissions HIT", getAllSubmissionsPrefix));
		return (List<Submission>) submissionRepository.findAll();
	}

	// If submission doesn't exist in db, it returns null
	public Submission getSubmission(String id) {
		logger.info(String.format("[%s] id received to fetch submission = %s", getSubmissionPrefix, id));

		Submission byId = submissionRepository.findById(id);
		if (byId == null) {
			logger.error(String.format("[%s] Submission with id %s doesn't exist in db", getSubmissionPrefix, id));
		} else {
			logger.info(String.format("[%s] Submission fetched from db with id %s is %s", getSubmissionPrefix, id, byId));
		}

		return byId;
	}

	// Returns the id of the added submission
	public String addSubmission(Submission submission) {
		logger.info(String.format("[%s] addSubmission HIT", addSubmissionPrefix));
		logger.info(String.format("[%s] Submission received to be added - %s", addSubmissionPrefix, submission));

		Submission savedSubmission = submissionRepository.save(submission);
		String genId = String.valueOf(savedSubmission.getId());
		logger.info(String.format("[%s] Submission successfully added with id = %s", addSubmissionPrefix, genId));

		return genId;
	}
}
