package com.txtsportteam.website.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private ObjectId id;
    private String token;
    private String name;
    private String surname;
    private String birthdate;
    private String username;
    private String email;
    private String password;
    private int verificationCode;
    private boolean active;
    private boolean admin;

    public User(String name, String surname, String birthdate, String username, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
        this.username = username;
        this.email = email;
        this.password = password;

        // generate a random number between 100000 and 999999 (of 6 digits) for registration validation
        this.verificationCode = ((int) (Math.random() * (999999 - 100000))) + 100000;
        this.active = false;
        this.admin = false;
    }

    public ObjectId getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getVerificationCode() {
        return verificationCode;
    }

    public boolean isActive() {
        return active;
    }
}
