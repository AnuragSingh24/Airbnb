package com.codingShuttle.projects.AirbnbApp.dto;

import lombok.Data;

@Data
public class SignUpRequestDto
{
    private Long id;
    private  String  email;
    private String name;
    private  String password;

}
