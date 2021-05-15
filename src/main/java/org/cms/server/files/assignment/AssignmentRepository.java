package org.cms.server.files.assignment;

import org.cms.core.files.assignment.Assignment;
import org.springframework.data.repository.CrudRepository;

public interface AssignmentRepository extends CrudRepository<Assignment, Integer> {
	Assignment findById(String id);
}
