package com.benhunterlearn.springcrudreredux.model;

import com.benhunterlearn.springcrudreredux.view.UserDtoView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    @JsonView(UserDtoView.DefaultView.class)
    private Long id;

    @JsonView(UserDtoView.DefaultView.class)
    private String email;

    @JsonView(UserDtoView.AdminView.class)
    private String password;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        // Do not set password when constructing from a User.
    }
}
