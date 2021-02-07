package org.cms.server.course;

import java.util.List;
import org.cms.core.course.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Integer> {
	List<Course> findByName(String name);
	Course findById(int id);
}
