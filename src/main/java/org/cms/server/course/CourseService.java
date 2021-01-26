package org.cms.server.course;

import org.cms.core.course.Course;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private List<Course> courses =new ArrayList<>(Arrays.asList(
            new Course(1,"Spring Framework","Spring Framework Description"),
            new Course(2,"Maven ","Maven is a build tool"),
            new Course(3,"CICD  ","Jenkins is a CICD pipelining tool"),
            new Course(4,"JPA  ","Hibernate is an implementation of JPA")
    ));

    public List<Course> getAllCourses(){
        return courses;
    }

    public Course getCourse(int id){
        List<Course>  filteredCourses=
                courses.stream()
                .filter(course->(course.getId()==id))
                .collect(Collectors.toList());

        return filteredCourses.get(0);

	public List<Course> getAllCourses() {
		return courses;
	}

    public void addCourse(Course course){
        courses.add(course);
    }

    public void updateCourse(int id, Course course) {
        for(int i=0;i<courses.size();i++){
            Course c=courses.get(i);
            if(c.getId()==id){
                courses.set(i,course);
                return;
            }
        }
    }

    public void deleteCourse(int id) {
        courses.removeIf(course->course.getId()==id);
        return;
    }
}
