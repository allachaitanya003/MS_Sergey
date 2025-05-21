package com.appsdeveloperblog.photoapp.api.users.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.appsdeveloperblog.photoapp.api.users.data.AlbumsServiceClient;
import com.appsdeveloperblog.photoapp.api.users.data.UserEntity;
import com.appsdeveloperblog.photoapp.api.users.data.UsersRepository;
import com.appsdeveloperblog.photoapp.api.users.shared.UserDto;
import com.appsdeveloperblog.photoapp.api.users.ui.model.AlbumResponseModel;
@Service
public class UsersServiceImpl implements UsersService {
	
	UsersRepository usersRepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	RestTemplate restTemplate;
	Environment environment;
	AlbumsServiceClient client;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder,RestTemplate restTemplate,
			Environment environment,AlbumsServiceClient client) {
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.restTemplate= restTemplate;
		this.environment = environment;
		this.client=client;
	}



	@Override
	public UserDto createUser(UserDto userDetails) {
		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserEntity userEntity = mapper.map(userDetails,UserEntity.class);
		usersRepository.save(userEntity);
		UserDto userDto = mapper.map(userEntity,UserDto.class);
		return userDto;
	}



	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserEntity userEntity = usersRepository.findByEmail(username);
		if(userEntity==null)
			throw new UsernameNotFoundException(username);
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true,true,true,true,new ArrayList<>());
	}



	@Override
	public UserDto getUserDetailsByEmail(String email) {
		UserEntity userEntity = usersRepository.findByEmail(email);
		if(userEntity==null)
			throw new UsernameNotFoundException(email);
		return new ModelMapper().map(userEntity, UserDto.class);
	}



	@Override
	public UserDto getUserByUserId(String userId) {
		UserEntity userEntity = usersRepository.findByUserId(userId);
		if(userEntity==null) throw new UsernameNotFoundException("User not found");
		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
		/*
		 * String albumsUrl = String.format("http://ALBUMS-WS/users/%s/albums", userId);
		 * 
		 * ResponseEntity<List<AlbumResponseModel>> albumsListResponse =
		 * restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new
		 * ParameterizedTypeReference<List<AlbumResponseModel>>() { });
		 * 
		 * userDto.setAlbums(albumsListResponse.getBody());
		 */
		List<AlbumResponseModel> userAlbums = client.userAlbums(userId);
		userDto.setAlbums(userAlbums);
		return userDto;
	}

}
