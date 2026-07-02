package com.binair.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO implements Serializable {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String realName;
    private String confirmPassword;
}
