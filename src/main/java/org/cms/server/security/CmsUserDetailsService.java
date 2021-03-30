package org.cms.server.security;

import org.cms.core.admin.Admin;
import org.cms.core.instructor.Instructor;
import org.cms.core.student.Student;
import org.cms.server.admin.AdminRepository;
import org.cms.server.commons.Role;
import org.cms.server.instructor.InstructorRepository;
import org.cms.server.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CmsUserDetailsService implements UserDetailsService {

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private InstructorRepository instructorRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Try loading student from student table
		Student student = studentRepository.findById(username);
		if (student != null) {
			System.out.println("Found " + username + " in student table");
			return new CmsUserDetails(student.getId(), student.getPassword(), Role.STUDENT);
		}
		Admin admin = adminRepository.findById(username);
		if (admin != null) {
			System.out.println("Found " + username + " in admin table");
			return new CmsUserDetails(admin.getId(), admin.getPassword(), Role.ADMIN);
		}
		Instructor instructor = instructorRepository.findById(username);
		if (instructor != null) {
			System.out.println("Found " + username + " in instructor table");
			return new CmsUserDetails(instructor.getId(), instructor.getPassword(), Role.INSTRUCTOR);
		}
		throw new UsernameNotFoundException("User '" + username + "' not found");
	}
}
