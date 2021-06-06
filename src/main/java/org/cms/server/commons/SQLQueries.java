package org.cms.server.commons;

public class SQLQueries {

	public static final String DUE_ASSIGNMENTS =
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

	public static final String ASSIGNMENTS_BY_INSTRUCTOR = """
SELECT *
	FROM assignment
	WHERE instructor_id = :instructorId""";

	public static final String ASSIGNMENT_STATUS_OF_STUDENT =
		"""
        SELECT count(*)
            FROM submission
            WHERE student_id = :studentId and assignment_id = :assignmentId""";

	public static final String STUDENTS_SUBSCRIBED_TO_COURSE =
		"""
            SELECT *
                FROM student
                WHERE id in (select students_id from student_subscribedCourses WHERE subscribedCourses_course_id = :courseId);""";
}
