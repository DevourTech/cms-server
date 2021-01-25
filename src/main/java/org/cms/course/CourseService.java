package org.cms.course;

import org.cms.course.Course;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private List<Course> courses = Arrays.asList(
            new Course(1,"Spring Framework","Spring Framework Description"),
            new Course(2,"Maven ","Maven is a build tool"),
            new Course(3,"CICD  ","Jenkins is a CICD pipelining tool")
    );

    public List<Course> getAllCourses(){
        return courses;
    }

    public Course getCourse(int id){
        List<Course>  filteredCourses=
                courses.stream()
                .filter(course->(course.getId()==id))
                .collect(Collectors.toList());

        return filteredCourses.get(0);

    }

}
