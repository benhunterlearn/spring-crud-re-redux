package com.benhunterlearn.springcrudreredux;

import com.benhunterlearn.springcrudreredux.model.User;
import com.benhunterlearn.springcrudreredux.model.UserDto;
import com.benhunterlearn.springcrudreredux.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.core.Is.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    private UserRepository repository;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public UserControllerTest(UserRepository repository) {
        this.repository = repository;
    }

    @Test
    public void getAllUsersFromRepository() throws Exception {
        // Initialize the repository and store the Users.
        User firstUser = this.repository.save(new User().setEmail("first@user.com").setPassword("firstpassword"));
        User secondUser = this.repository.save(new User().setEmail("second@user.com").setPassword("secondpassword"));

        RequestBuilder request = MockMvcRequestBuilders.get("/users")
                .accept(MediaType.APPLICATION_JSON);
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(firstUser.getId().intValue())))
                .andExpect(jsonPath("$[0].email", is(firstUser.getEmail())))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].id", is(secondUser.getId().intValue())))
                .andExpect(jsonPath("$[1].email", is(secondUser.getEmail())))
                .andExpect(jsonPath("$[1].password").doesNotExist());
    }

    @Test
    public void createUserWithValidData() throws Exception {
        UserDto newUser = new UserDto().setEmail("new@user.com").setPassword("new");
        RequestBuilder request = MockMvcRequestBuilders.post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(newUser));
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email", is(newUser.getEmail())))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void getUserByIdFromRepository() throws Exception {
        // Initialize the repository and store the Users.
        User firstUser = this.repository.save(new User().setEmail("first@user.com").setPassword("firstpassword"));
        User secondUser = this.repository.save(new User().setEmail("second@user.com").setPassword("secondpassword"));

        RequestBuilder request = MockMvcRequestBuilders.get("/users/" + firstUser.getId())
                .accept(MediaType.APPLICATION_JSON);
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail())))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void patchUserByIdWithValidEmail() throws Exception {
        // Initialize the repository and store the Users.
        User firstUser = this.repository.save(new User().setEmail("first@user.com").setPassword("firstpassword"));
        User secondUser = this.repository.save(new User().setEmail("second@user.com").setPassword("secondpassword"));
        UserDto userDto = new UserDto().setEmail("new@new.new");

        RequestBuilder request = MockMvcRequestBuilders.patch("/users/" + firstUser.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(userDto));
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void patchUserByIdWithValidEmailAndPassword() throws Exception {
        // Initialize the repository and store the Users.
        User firstUser = this.repository.save(new User().setEmail("first@user.com").setPassword("firstpassword"));
        User secondUser = this.repository.save(new User().setEmail("second@user.com").setPassword("secondpassword"));
        UserDto userDto = new UserDto().setEmail("new@new.new").setPassword("newbad");

        RequestBuilder request = MockMvcRequestBuilders.patch("/users/" + firstUser.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(userDto));
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.password").doesNotExist());
        String expectedPassword = userDto.getPassword();
        String actualPassword = this.repository.findById(firstUser.getId()).get().getPassword();
        assertEquals(expectedPassword, actualPassword);
    }

    @Test
    public void deleteUserByIdSuccessful() throws Exception {
        // Initialize the repository and store the Users.
        User firstUser = this.repository.save(new User().setEmail("first@user.com").setPassword("firstpassword"));
        User secondUser = this.repository.save(new User().setEmail("second@user.com").setPassword("secondpassword"));

        RequestBuilder request = MockMvcRequestBuilders.delete("/users/" + firstUser.getId())
                .accept(MediaType.APPLICATION_JSON);
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(Long.valueOf(this.repository.count()).intValue())));
    }

    @Test
    public void postUsersAuthenticateWithValidPasswordAuthenticates() throws Exception {
        // Initialize the repository and store the Users.
        User firstUser = this.repository.save(new User().setEmail("first@user.com").setPassword("firstpassword"));
        User secondUser = this.repository.save(new User().setEmail("second@user.com").setPassword("secondpassword"));
        UserDto userDto = new UserDto()
                .setEmail(firstUser.getEmail())
                .setPassword(firstUser.getPassword());

        RequestBuilder request = MockMvcRequestBuilders.post("/users/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(userDto));

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated", is(true)))
                .andExpect(jsonPath("$.user.id", is(firstUser.getId().intValue())))
                .andExpect(jsonPath("$.user.email", is(firstUser.getEmail())))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    public void postUsersAuthenticateWithInvalidPasswordDoesNotAuthenticate() throws Exception {
        // Initialize the repository and store the Users.
        User firstUser = this.repository.save(new User().setEmail("first@user.com").setPassword("firstpassword"));
        User secondUser = this.repository.save(new User().setEmail("second@user.com").setPassword("secondpassword"));
        UserDto userDto = new UserDto()
                .setEmail(firstUser.getEmail())
                .setPassword("wrong password");

        RequestBuilder request = MockMvcRequestBuilders.post("/users/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(userDto));

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated", is(false)))
                .andExpect(jsonPath("$.user").doesNotExist());
    }
}
