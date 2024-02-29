package com.blogapp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordRequest {

    private String currentPassword;
    private String newPassword;

}
