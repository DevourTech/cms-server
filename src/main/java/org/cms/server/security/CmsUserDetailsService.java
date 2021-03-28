package org.cms.server.security;

import org.cms.core.student.Student;
import org.cms.server.commons.Role;
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

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Try loading student from student table
		Student student = studentRepository.findById(username);
		if (student != null) {
			return new CmsUserDetails(student.getId(), student.getPassword(), Role.STUDENT.name());
		}
		throw new UsernameNotFoundException("User '" + username + "' not found");
	}
}
