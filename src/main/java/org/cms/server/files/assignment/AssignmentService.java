package org.cms.server.files.assignment;

import java.util.List;

import org.cms.core.course.Course;
import org.cms.core.files.assignment.Assignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AssignmentService {

	private final Logger logger = LoggerFactory.getLogger(AssignmentService.class);
	private static final String getAllAssignmentsPrefix = "GET - getAllAssignments";
	private static final String getAssignmentPrefix = "GET - getAssignment";
	private static final String addAssignmentPrefix = "POST - addAssignment";
	private static final String getAssignmentsWithTheseCoursesPrefix = "GET - getAssignmentsWithTheseCourses";

	private final AssignmentRepository assignmentRepository;

	public AssignmentService(AssignmentRepository repository) {
		this.assignmentRepository = repository;
	}

	public List<Assignment> getAllAssignments() {
		logger.info(String.format("[%s] getAllAssignments HIT", getAllAssignmentsPrefix));
		return (List<Assignment>) assignmentRepository.findAll();
	}

	// If assignment doesn't exist in db, it returns null
	public Assignment getAssignment(String id) {
		logger.info(String.format("[%s] id received to fetch assignment = %s", getAssignmentPrefix, id));

		Assignment byId = assignmentRepository.findById(id);
		if (byId == null) {
			logger.error(String.format("[%s] Assignment with id %s doesn't exist in db", getAssignmentPrefix, id));
		} else {
			logger.info(String.format("[%s] Assignment fetched from db with id %s is %s", getAssignmentPrefix, id, byId));
		}

		return byId;
	}

	// Returns the id of the added assignment
	public String addAssignment(Assignment assignment) {
		logger.info(String.format("[%s] addAssignment HIT", addAssignmentPrefix));
		logger.info(String.format("[%s] Assignment received to be added - %s", addAssignmentPrefix, assignment));

		Assignment savedAssignment = assignmentRepository.save(assignment);
		String genId = String.valueOf(savedAssignment.getId());
		logger.info(String.format("[%s] Assignment successfully added with id = %s", addAssignmentPrefix, genId));

		return genId;
	}

	// Returns the list of assignments that have a course mentioned in `courseIds`
	public List<Assignment> getAssignmentsWithCourseIds(List<String> courseIds) {
		logger.info(String.format("[%s] getAssignmentsWithTheseCourses HIT", getAssignmentsWithTheseCoursesPrefix));
		logger.info(String.format("[%s] Looking for assignments having one or many of these courseIds", getAssignmentsWithTheseCoursesPrefix));
		for (String id : courseIds) {
			logger.info(String.format("[%s] Course id - %s", getAssignmentsWithTheseCoursesPrefix, id));
		}

		return assignmentRepository.findByCourses(courseIds);
	}
}
