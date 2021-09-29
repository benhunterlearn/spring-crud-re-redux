package com.benhunterlearn.springcrudreredux.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthenticationDto {
    private boolean authenticated;
    private UserDto user;
}
