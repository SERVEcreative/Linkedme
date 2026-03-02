package com.linkedme.profile.dto;

import lombok.Data;

@Data
public class CreateOrUpdateProfileRequest {
    private String headline;
    private String about;
}
