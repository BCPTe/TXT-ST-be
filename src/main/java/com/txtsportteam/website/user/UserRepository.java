package com.txtsportteam.website.user;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<List<User>> findUserByName(String name);
    Optional<List<User>> findUserBySurname(String surname);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByUsername(String username);

    User findUserById(ObjectId id);
}
