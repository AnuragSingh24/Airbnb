package com.codingShuttle.projects.AirbnbApp.security;


import com.codingShuttle.projects.AirbnbApp.dto.LoginDto;
import com.codingShuttle.projects.AirbnbApp.dto.SignUpRequestDto;
import com.codingShuttle.projects.AirbnbApp.dto.UserDto;
import com.codingShuttle.projects.AirbnbApp.entity.User;
import com.codingShuttle.projects.AirbnbApp.entity.enums.Role;
import com.codingShuttle.projects.AirbnbApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private  final UserRepository userRepository;
    private  final ModelMapper modelMapper;
    private  final PasswordEncoder passwordEncoder;
    private  final AuthenticationManager authenticationManager;

    private final JWTService jwtService;
    public UserDto SignUp(SignUpRequestDto signUpRequestDto)
    {
        User user = userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);

        if(user != null)
        {
            throw new RuntimeException("User already exists");
        }

        User newUser = modelMapper.map(signUpRequestDto, User.class);


        //change to the guest role
        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));

        newUser = userRepository.save(newUser);
        return modelMapper.map(newUser , UserDto.class);
    }

    public String[] login(LoginDto loginDto)
    {
        Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()
        ));
        User user = (User) authentication.getPrincipal(); //we get the user object

        String[] arr =  new String[2];
        arr[0] = jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateRefreshToken(user);
        return arr;
    }

    public String refreshToken(String refreshToken)
    {
       Long id  = jwtService.getUserIdFromToken(refreshToken);
       User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

      return jwtService.generateAccessToken(user);
    }

}
