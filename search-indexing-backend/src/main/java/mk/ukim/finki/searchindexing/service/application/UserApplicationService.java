package mk.ukim.finki.searchindexing.service.application;

import java.util.Optional;
import mk.ukim.finki.searchindexing.model.dto.LoginUserRequestDto;
import mk.ukim.finki.searchindexing.model.dto.LoginUserResponseDto;
import mk.ukim.finki.searchindexing.model.dto.RegisterUserRequestDto;
import mk.ukim.finki.searchindexing.model.dto.RegisterUserResponseDto;

public interface UserApplicationService {
    Optional<RegisterUserResponseDto> register(RegisterUserRequestDto registerUserRequestDto);

    Optional<LoginUserResponseDto> login(LoginUserRequestDto loginUserRequestDto);

    Optional<RegisterUserResponseDto> findByUsername(String username);
}
