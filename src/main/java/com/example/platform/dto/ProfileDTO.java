package com.example.platform.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Getter
public class ProfileDTO {
    private String picture_url;
    private String headline;
    private String summary;
    private String industry;
    private String token;

}
