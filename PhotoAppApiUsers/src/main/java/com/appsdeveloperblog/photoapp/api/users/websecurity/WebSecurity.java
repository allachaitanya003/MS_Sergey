package com.appsdeveloperblog.photoapp.api.users.websecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.appsdeveloperblog.photoapp.api.users.service.UsersService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled=true)
public class WebSecurity {
	
	private Environment environment;
	private UsersService usersService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public WebSecurity(Environment environment, UsersService usersService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.environment = environment;
		this.usersService = usersService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		// Configure AuthenticationManagerBuilder
    	AuthenticationManagerBuilder authenticationManagerBuilder = 
    			http.getSharedObject(AuthenticationManagerBuilder.class);
    	
    	authenticationManagerBuilder.userDetailsService(usersService)
    	.passwordEncoder(bCryptPasswordEncoder);
    	
    	AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
    	
    	
		
    	AuthenticationFilter authenticationFilter = 
    			new AuthenticationFilter(usersService, environment, authenticationManager);
    	authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		
		http.csrf(csrf->csrf.disable());
		http.authorizeHttpRequests(auth->auth
		.requestMatchers(new AntPathRequestMatcher("/users/login", "POST")).permitAll()
		.requestMatchers(new AntPathRequestMatcher("/users/insert","POST")).permitAll()
		.requestMatchers(new AntPathRequestMatcher("/users/status/check","GET")).permitAll()
		.requestMatchers(new AntPathRequestMatcher("/users/fetch/**","GET")).permitAll()
		.requestMatchers(new AntPathRequestMatcher("/actuator/**","GET")).permitAll()
		.requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll())
        .addFilter(authenticationFilter)
		.authenticationManager(authenticationManager)
		.sessionManagement(ses->ses.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		
		http.headers(headers->headers.frameOptions(frames->frames.sameOrigin()));
		
		return http.build();
		
	}

}
