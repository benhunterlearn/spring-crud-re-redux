package com.benhunterlearn.springcrudreredux.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    @JsonIgnore
    private String password;

    public User(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getPassword() == null) {
            throw new IllegalArgumentException("New User must have an email and password.");
        }
        this.email = userDto.getEmail();
        this.password = userDto.getPassword();
    }

    public User patch(UserDto userDto) {
        if (userDto.getEmail() != null) {
            this.email = userDto.getEmail();
        }
        if (userDto.getPassword() != null) {
            this.password = userDto.getPassword();
        }
        return this;
    }
}
