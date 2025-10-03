package com.epam.finaltask.service;

import java.math.BigDecimal;
import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;

	@Override
	public UserDTO register(UserDTO userDTO) {
		User user = userMapper.toUser(userDTO);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(Role.USER);
		user.setActive(true);

		User saved = userRepository.save(user);
		return userMapper.toUserDTO(saved);
	}

	@Override
	public UserDTO updateUser(String username, UserDTO userDTO) {
		User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user found with such username"));
		user.setPhoneNumber(userDTO.getPhoneNumber());
		user.setBalance(BigDecimal.valueOf(userDTO.getBalance()));
		user.setVouchers(userDTO.getVouchers());
		User updated = userRepository.save(user);
		return userMapper.toUserDTO(updated);
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		var user = this.userRepository.findUserByUsername(username).orElseThrow(()->new UsernameNotFoundException("No user with such an username"));
		return userMapper.toUserDTO(user);
	}

	@Override
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		var userToChange = this.userRepository.findById(UUID.fromString(userDTO.getId()))
				.orElseThrow(() -> new UsernameNotFoundException("No user found with such an username"));

		var mappedUser = userMapper.toUser(userDTO);
		mappedUser.setActive(userDTO.isActive());
		this.userRepository.save(mappedUser);

		return userMapper.toUserDTO(mappedUser);
	}



	@Override
	public UserDTO getUserById(UUID id) {
		var user = this.userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("No user with such an id"));
		return userMapper.toUserDTO(user);
	}

}
