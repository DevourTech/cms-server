package org.cms.server.commons;

public class SQLQueries {

	public static final String DUE_ASSIGNMENTS_QUERY =
		"""
 SELECT *
	FROM   assignment
	WHERE  id IN (SELECT id
              FROM   assignment
		              WHERE  course_course_id IN (SELECT subscribedCourses_course_id
		              FROM   student_subscribedCourses
		              WHERE  students_id = :studentId)
	AND id NOT IN (SELECT assignment_id
                               FROM   submission
		                               WHERE  student_id = :studentId));""";
}
