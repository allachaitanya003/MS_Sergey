package com.appsdeveloperblog.photoapp.api.users.websecurity;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.appsdeveloperblog.photoapp.api.users.service.UsersService;
import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;
import com.appsdeveloperblog.photoapp.api.users.ui.model.LoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private UsersService usersService;
	private Environment environment;
	
	
	public AuthenticationFilter(UsersService usersService, Environment environment,
			AuthenticationManager authenticationManager) {
		super(authenticationManager);
		this.usersService = usersService;
		this.environment = environment;
	}
	
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		try {
			LoginRequestModel model = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);
			
			return getAuthenticationManager().authenticate(
					new UsernamePasswordAuthenticationToken(model.getEmail(), model.getPassword(),new ArrayList<>()));
		}  catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {

		String userName = ((User) auth.getPrincipal()).getUsername();
		UserDto userDetails = usersService.getUserDetailsByEmail(userName);
		String tokenSecret = environment.getProperty("token.secret");
		byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
		SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
		System.out.println(environment.getProperty("token.expiration_time"));
		Instant now = Instant.now();

		String token = Jwts.builder()
			    //.claim("scope", auth.getAuthorities())
			    .setSubject(userDetails.getUserId())
			    .setExpiration(Date.from(now.plusMillis(
			        Long.parseLong(environment.getProperty("token.expiration_time")))))
			    .setIssuedAt(Date.from(now))
			    .signWith(secretKey)
			    .compact();

		res.addHeader("token", token);
		res.addHeader("userId", userDetails.getUserId());
	}

}
