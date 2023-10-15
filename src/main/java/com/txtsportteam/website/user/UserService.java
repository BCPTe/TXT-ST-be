package com.txtsportteam.website.user;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public Optional<List<User>> userByName(String name) {
        return userRepository.findUserByName(name);
    }

    public Optional<List<User>> userBySurname(String surname) { return userRepository.findUserBySurname(surname); }

    public Optional<User> userByEmail(String email) { return userRepository.findUserByEmail(email); }

    public Optional<User> userByUsername(String username) { return userRepository.findUserByUsername(username); }
    public Optional<User> userById(ObjectId id) { return userRepository.findById(id); }
    public User newUser(User user) { return userRepository.insert(user); }


    public boolean updateStatus(ObjectId userid, int code) {
        User user = userRepository.findUserById(userid);
        if(user.getVerificationCode() == code) {
            user.setActive(true);
            userRepository.save(user);
            return true;
        }
        else return false;
    }

}
