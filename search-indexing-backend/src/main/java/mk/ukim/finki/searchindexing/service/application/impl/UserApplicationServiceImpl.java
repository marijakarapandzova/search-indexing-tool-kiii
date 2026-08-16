package mk.ukim.finki.searchindexing.service.application.impl;

import java.util.Optional;
import mk.ukim.finki.searchindexing.helpers.JwtHelper;
import mk.ukim.finki.searchindexing.model.domain.User;
import mk.ukim.finki.searchindexing.model.dto.LoginUserRequestDto;
import mk.ukim.finki.searchindexing.model.dto.LoginUserResponseDto;
import mk.ukim.finki.searchindexing.model.dto.RegisterUserRequestDto;
import mk.ukim.finki.searchindexing.model.dto.RegisterUserResponseDto;
import mk.ukim.finki.searchindexing.service.application.UserApplicationService;
import mk.ukim.finki.searchindexing.service.domain.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationServiceImpl implements UserApplicationService {
    private final UserService userService;
    private final JwtHelper jwtHelper;

    public UserApplicationServiceImpl(UserService userService, JwtHelper jwtHelper) {
        this.userService = userService;
        this.jwtHelper = jwtHelper;
    }

    @Override
    public Optional<RegisterUserResponseDto> register(RegisterUserRequestDto registerUserRequestDto) {
        User user = userService.register(registerUserRequestDto.toUser());
        return Optional.of(RegisterUserResponseDto.from(user));
    }

    @Override
    public Optional<LoginUserResponseDto> login(LoginUserRequestDto loginUserRequestDto) {
        User user = userService.login(loginUserRequestDto.username(), loginUserRequestDto.password());

        String token = jwtHelper.generateToken(user);

        return Optional.of(new LoginUserResponseDto(token));
    }

    @Override
    public Optional<RegisterUserResponseDto> findByUsername(String username) {
        return userService
            .findByUsername(username)
            .map(RegisterUserResponseDto::from);
    }
}
