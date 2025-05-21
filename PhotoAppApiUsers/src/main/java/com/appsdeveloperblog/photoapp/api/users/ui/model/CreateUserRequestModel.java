package com.appsdeveloperblog.photoapp.api.users.ui.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateUserRequestModel {
	
	@NotNull(message="firstName cannot be null")
	@Size(min = 2,message = "firstName must not be lessthan two characters")
	private String firstName;
	
	@NotNull(message="lastName cannot be null")
	@Size(min = 2,message = "lastName must not be lessthan two characters")
	private String lastName;
	
	@NotNull(message="Password cannot be null")
	@Size(min = 8,max=16,message="Password must be equal or grater than 8 characters and less than 16 characters")
	private String password;
	
	@NotNull(message="Email cannot be null")
	@Email
	private String email;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
