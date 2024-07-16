package com.example.platform.dto;

import com.example.platform.model.Company;
import com.example.platform.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareEntity {
    private User user;
    private Company company;
}
