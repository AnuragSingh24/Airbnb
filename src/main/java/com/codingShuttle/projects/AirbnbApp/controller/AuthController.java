package com.codingShuttle.projects.AirbnbApp.controller;


import com.codingShuttle.projects.AirbnbApp.dto.LoginDto;
import com.codingShuttle.projects.AirbnbApp.dto.LoginResponseDto;
import com.codingShuttle.projects.AirbnbApp.dto.SignUpRequestDto;
import com.codingShuttle.projects.AirbnbApp.dto.UserDto;
import com.codingShuttle.projects.AirbnbApp.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignUpRequestDto signUpRequestDto)
    {
        return new ResponseEntity<>(authService.SignUp(signUpRequestDto) , HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto , HttpServletResponse httpServletResponse , HttpServletRequest httpServletRequest)
    {
        String [] tokens =  authService.login(loginDto);

        Cookie cookie = new Cookie("refreshToken" , tokens[1]);
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));

    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request)
    {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie ->
                        cookie.getName()
                                .equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow( () -> new AuthenticationServiceException("Refresh  token not found inside the cookies"));


        String accessToken = authService.refreshToken(refreshToken);

        return  ResponseEntity.ok(new LoginResponseDto(accessToken));
    }
}
