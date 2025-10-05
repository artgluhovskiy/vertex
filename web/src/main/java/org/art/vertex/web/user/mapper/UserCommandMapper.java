package org.art.vertex.web.user.mapper;

import org.art.vertex.application.user.command.LoginCommand;
import org.art.vertex.application.user.command.RegisterUserCommand;
import org.art.vertex.web.user.request.UserLoginRequest;
import org.art.vertex.web.user.request.UserRegistrationRequest;

public class UserCommandMapper {

    public RegisterUserCommand toCommand(UserRegistrationRequest request) {
        return RegisterUserCommand.builder()
            .email(request.email())
            .password(request.password())
            .build();
    }

    public LoginCommand toCommand(UserLoginRequest request) {
        return LoginCommand.builder()
            .email(request.email())
            .password(request.password())
            .build();
    }
}
