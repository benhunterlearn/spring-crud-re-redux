package com.benhunterlearn.springcrudreredux.controller;

import com.benhunterlearn.springcrudreredux.model.AuthenticationDto;
import com.benhunterlearn.springcrudreredux.model.CountDto;
import com.benhunterlearn.springcrudreredux.model.User;
import com.benhunterlearn.springcrudreredux.model.UserDto;
import com.benhunterlearn.springcrudreredux.repository.UserRepository;
import com.benhunterlearn.springcrudreredux.view.UserDtoView;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    UserRepository repository;

    @Autowired
    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    @JsonView(UserDtoView.DefaultView.class)
    public ArrayList<UserDto> getAllUsersFromRepository() {
        ArrayList<UserDto> userDtoArrayList = new ArrayList<UserDto>();
        for (User user : this.repository.findAll()) {
            userDtoArrayList.add(new UserDto(user));
        }
        return userDtoArrayList;
    }

    @PostMapping("")
    @JsonView(UserDtoView.DefaultView.class)
    public UserDto postCreateUser(@RequestBody UserDto userDto) {
        return new UserDto(this.repository.save(new User(userDto)));
    }

    @GetMapping("/{id}")
    @JsonView(UserDtoView.DefaultView.class)
    public UserDto getUserById(@PathVariable Long id) {
        return new UserDto(this.repository.findById(id).get());
    }

    @PatchMapping("/{id}")
    @JsonView(UserDtoView.DefaultView.class)
    public UserDto patchUserById(@PathVariable Long id, @RequestBody UserDto userDto) {
        User currentUser = this.repository.findById(id).get();
        currentUser.patch(userDto);
        this.repository.save(currentUser);
        return new UserDto(currentUser);
    }

    @DeleteMapping("/{id}")
    public CountDto deleteUserByIdRendersCountOfAllUsers(@PathVariable Long id) {
        return new CountDto().setCount(this.repository.count());
    }

    @PostMapping("/authenticate")
    public AuthenticationDto auth(@RequestBody UserDto userDto) {
        User currentUser = this.repository.findFirstUserByEmail(userDto.getEmail());
        AuthenticationDto authenticationDto = new AuthenticationDto();
        if (currentUser.getPassword().equals(userDto.getPassword())) {
            authenticationDto.setAuthenticated(true)
                    .setUser(new UserDto(currentUser));
        } else {
            authenticationDto.setAuthenticated(false);
        }
        return authenticationDto;
    }
}
