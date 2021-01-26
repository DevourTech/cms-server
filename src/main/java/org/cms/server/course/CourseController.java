package org.cms.server.course;

import org.cms.core.course.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CourseController {

    @Autowired
    private CourseService courseService;

    @RequestMapping("/courses")
    public List<Course> getAllCourses(){
        return courseService.getAllCourses();
    }

    @RequestMapping("/courses/{id}")
    public Course getCourse(@PathVariable int id){
        return courseService.getCourse(id);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/courses")
    public void addCourse(@RequestBody Course course){
        courseService.addCourse(course);
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/courses/{id}")
    public void addCourse(@RequestBody Course course,@PathVariable int id){
        courseService.updateCourse(id,course);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/courses/{id}")
    public void deleteCourse(@PathVariable int id){
        courseService.deleteCourse(id);
    }

}
