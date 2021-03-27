package org.cms.server.course;

import java.util.List;
import org.cms.core.course.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

	private final Logger logger = LoggerFactory.getLogger(CourseService.class);
	private static final String getAllCoursesPrefix = "GET - getAllCourses";
	private static final String getCoursePrefix = "GET - getCourse";
	private static final String addCoursePrefix = "POST - addCourse";
	private static final String updateCoursePrefix = "PUT - updateCourse";
	private static final String deleteCoursePrefix = "DELETE - deleteCourse";

	private final CourseRepository courseRepository;

	public CourseService(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	public List<Course> getAllCourses() {
		logger.info(String.format("[%s] getAllCourses HIT", getAllCoursesPrefix));
		return (List<Course>) courseRepository.findAll();
	}

	// If course doesn't exist in db, it returns null
	public Course getCourse(String id) {
		logger.info(String.format("[%s] id received to fetch course = %s", getCoursePrefix, id));

		Course byId = courseRepository.findById(id);
		if (byId == null) {
			logger.error(String.format("[%s] Course with id %s doesn't exist in db", getCoursePrefix, id));
		} else {
			logger.info(String.format("[%s] Course fetched from db with id %s is %s", getCoursePrefix, id, byId));
		}

		return byId;
	}

	// Returns the id of the added course
	public String addCourse(Course course) {
		logger.info(String.format("[%s] addCourse HIT", addCoursePrefix));
		logger.info(String.format("[%s] Course received to be added - %s", addCoursePrefix, course));

		Course savedCourse = courseRepository.save(course);
		String genId = String.valueOf(savedCourse.getId());
		logger.info(String.format("[%s] Course successfully added with id = %s", addCoursePrefix, genId));

		return genId;
	}

	// Returns true if update was successful
	// Returns false if course to be updated doesn't exist
	public boolean updateCourse(String id, Course course) {
		logger.info(String.format("[%s] updateCourse HIT for id = %s", updateCoursePrefix, id));
		logger.info(String.format("[%s] Course details = %s", updateCoursePrefix, course));

		Course existingCourse = courseRepository.findById(id);
		if (existingCourse == null) {
			logger.error(String.format("[%s] Course to be updated (id = %s) doesn't exist", updateCoursePrefix, id));
			return false;
		}

		updateExistingCourse(existingCourse, course);
		courseRepository.save(existingCourse);
		logger.info(String.format("[%s] Course with id %s successfully updated", updateCoursePrefix, id));

		return true;
	}

	private void updateExistingCourse(Course existingCourse, Course otherCourse) {
		if (!otherCourse.getName().equals("")) {
			existingCourse.setName(otherCourse.getName());
		}

		if (!otherCourse.getDescription().equals("")) {
			existingCourse.setDescription(otherCourse.getDescription());
		}
	}

	// Returns true if course is successfully deleted
	// Returns false if course doesn't exist
	public boolean deleteCourse(String id) {
		logger.info(String.format("[%s] deleteCourse HIT for id = %s", deleteCoursePrefix, id));

		Course toBeRemoved = courseRepository.findById(id);
		if (toBeRemoved == null) {
			logger.error(String.format("[%s] Course to be deleted (id = %s) doesn't exist", deleteCoursePrefix, id));
			return false;
		}

		courseRepository.delete(toBeRemoved);
		logger.info(String.format("[%s] Course with id %s is successfully deleted", deleteCoursePrefix, id));
		return true;
	}
}
