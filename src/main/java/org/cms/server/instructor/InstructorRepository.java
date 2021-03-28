package org.cms.server.instructor;

import java.util.List;
import org.cms.core.instructor.Instructor;
import org.springframework.data.repository.CrudRepository;

public interface InstructorRepository extends CrudRepository<Instructor, Integer> {
	List<Instructor> findByName(String name);
	Instructor findById(String id);
}
