package com.epam.finaltask.config;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class InitUserConfig implements CommandLineRunner {

    private final UserService userService;
    private final Environment env;

    @Override
    public void run(String... args) throws Exception {
        UserDTO user = new UserDTO();
        user.setUsername(env.getProperty("custom.user.username"));
        user.setPassword(env.getProperty("custom.user.password"));
        user.setRole(Role.USER.name());
        user.setBalance(10000.0);

        UserDTO admin = new UserDTO();
        admin.setUsername(env.getProperty("custom.admin.username"));
        admin.setPassword(env.getProperty("custom.admin.password"));
        admin.setRole(Role.ADMIN.name());

        userService.register(user);
        userService.register(admin);
    }
}
