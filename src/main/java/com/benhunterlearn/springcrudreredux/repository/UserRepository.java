package com.benhunterlearn.springcrudreredux.repository;

import com.benhunterlearn.springcrudreredux.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    public User findFirstByEmail(String email);
    public User findFirstUserByEmail(String email);
}
