package org.cms.server.security;

import org.cms.server.commons.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/")
			.permitAll()
			.antMatchers(HttpMethod.GET, "/api/courses")
			.permitAll()
			.antMatchers(HttpMethod.POST, "/api/courses")
			.hasRole(Role.ADMIN.name())
			.antMatchers(HttpMethod.DELETE, "/api/courses/**")
			.hasRole(Role.ADMIN.name())
			.antMatchers(HttpMethod.PUT, "/api/courses/**")
			.hasAnyRole(Role.ADMIN.name(), Role.INSTRUCTOR.name())
			.antMatchers(HttpMethod.GET, "/api/students")
			.permitAll()
			.antMatchers(HttpMethod.POST, "/api/students")
			.hasRole(Role.ADMIN.name())
			.antMatchers(HttpMethod.PUT, "/api/students/**")
			.hasRole(Role.ADMIN.name())
			.antMatchers(HttpMethod.DELETE, "/api/students/**")
			.hasRole(Role.ADMIN.name())
			//			.antMatchers(HttpMethod.GET, "/api/instructors").permitAll()
			//			.antMatchers(HttpMethod.POST, "/api/instructors").hasRole(Role.ADMIN.name())
			//			.antMatchers(HttpMethod.PUT, "/api/instructor/**").hasRole(Role.ADMIN.name())
			//			.antMatchers(HttpMethod.DELETE, "/api/instructor/**").hasRole(Role.ADMIN.name())
			.and()
			.httpBasic();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new CmsUserDetailsService();
	}

	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
		return authenticationProvider;
	}
}
