package com.appsdeveloperblog.photoapp.api.users.ui.controllers;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.photoapp.api.users.service.UsersService;
import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;
import com.appsdeveloperblog.photoapp.api.users.ui.model.CreateUserRequestModel;
import com.appsdeveloperblog.photoapp.api.users.ui.model.CreateUserResponseModel;
import com.appsdeveloperblog.photoapp.api.users.ui.model.UserResponseModel;

import jakarta.validation.Valid;
import jakarta.ws.rs.core.MediaType;

@RestController
@RequestMapping("/users")
public class UsersController {
	
	@Autowired
	Environment environment;
	@Autowired
	UsersService service;
	
	@GetMapping("/status/check")
	public String status() {
		System.out.println("UsersController.status()");
		return "Working"+environment.getProperty("local.server.port")+
				"secret key is:"+environment.getProperty("token.secret");
		
	}
	@PostMapping(value = "/insert",
			consumes = {MediaType.APPLICATION_JSON ,MediaType.APPLICATION_XML},
			produces = {MediaType.APPLICATION_JSON ,MediaType.APPLICATION_XML})
	public ResponseEntity<CreateUserResponseModel> createUser(@RequestBody @Valid CreateUserRequestModel userDetails){
		
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserDto dto = mapper.map(userDetails,UserDto.class);
		UserDto createdUser = service.createUser(dto);
		CreateUserResponseModel responseModel = mapper.map(createdUser, CreateUserResponseModel.class);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
	}
	
	@GetMapping(value="/fetch/{userId}",produces = {MediaType.APPLICATION_JSON ,MediaType.APPLICATION_XML})
	public ResponseEntity<UserResponseModel> getUser(@PathVariable("userId") String userId){
		
		UserDto userDto = service.getUserByUserId(userId);
		UserResponseModel userResponseModel = new ModelMapper().map(userDto, UserResponseModel.class);
		return ResponseEntity.status(HttpStatus.OK).body(userResponseModel);
	}

}
