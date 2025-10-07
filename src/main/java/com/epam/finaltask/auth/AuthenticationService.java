package com.epam.finaltask.auth;

import com.epam.finaltask.auth.dto.AuthResponse;
import com.epam.finaltask.auth.dto.LoginRequest;
import com.epam.finaltask.auth.dto.RegisterRequest;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.token.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest) {
        UserDTO dto = new UserDTO();
        dto.setUsername(registerRequest.username());
        dto.setPassword(registerRequest.password());
        dto.setPhoneNumber(registerRequest.phoneNumber());
        dto.setRole(Role.USER.name());

        UserDTO savedUser = userService.register(dto);
        String token = jwtService.generateToken(savedUser.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );


        UserDTO user = userService.getUserByUsername(request.username());
        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token);
    }
}
