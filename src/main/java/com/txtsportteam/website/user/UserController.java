package com.txtsportteam.website.user;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Value("${env.LOGO}")
    private String logo;
    @Value("${env.MAIL_USER}")
    private String sender;
    @Value("${env.SITE_URL}")
    private String siteUrl;
    @Autowired
    private UserService userService;
//    @Value("${env.GMAIL_USER}") private String sender;
    @Autowired
    private JavaMailSender mailSender;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<List<User>>(userService.allUsers(), HttpStatus.OK);
    }

    @GetMapping(params = "name")
    public ResponseEntity<Optional<List<User>>> getUserByName(@RequestParam("name") String name) {
        return new ResponseEntity<Optional<List<User>>>(userService.userByName(name), HttpStatus.OK);
    }

    @GetMapping(params = "surname")
    public ResponseEntity<Optional<List<User>>> getUserBySurname(@RequestParam("surname") String surname) {
        return new ResponseEntity<Optional<List<User>>>(userService.userBySurname(surname), HttpStatus.OK);
    }

    @GetMapping(params = "email")
    public ResponseEntity<Optional<User>> getUserByEmail(@RequestParam("email") String email) {
        return new ResponseEntity<Optional<User>>(userService.userByEmail(email), HttpStatus.OK);
    }

    @GetMapping(params = "username")
    public ResponseEntity<Optional<User>> getUserByUsername(@RequestParam("username") String username) {
        return new ResponseEntity<Optional<User>>(userService.userByUsername(username), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody String payload) {
        JSONObject obj = new JSONObject(payload);
        String receivedUsernameOrEmail = obj.getString("usernameOrEmail");
        String receivedPasswordEncoded = obj.getString("password");

        if(getUserByEmail(receivedUsernameOrEmail).getBody().isPresent()) {
            User user = getUserByEmail(receivedUsernameOrEmail).getBody().get();

            if(user.getPassword().equals(receivedPasswordEncoded)) {
                return new ResponseEntity(user, HttpStatus.OK);
            }
        }
        else if(getUserByUsername(receivedUsernameOrEmail).getBody().isPresent()) {
            User user = getUserByUsername(receivedUsernameOrEmail).getBody().get();

            if(user.getPassword().equals(receivedPasswordEncoded)) {
                return new ResponseEntity(user, HttpStatus.OK);
            }
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody String payload) throws IOException {
        JSONObject obj = new JSONObject(payload);
        User user = new User(obj.getString("name"), obj.getString("surname"), obj.getString("date"), obj.getString("username"), obj.getString("email"), obj.getString("password"));


        userService.newUser(user);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        // read html email file
        String emailHtml = "";
        try {
            System.out.println(System.getProperty("user.dir"));
            byte[] bytes = Files.readAllBytes(Paths.get("main/java/com/txtsportteam/website/email/txtsportteam-activation-email.html"));
            emailHtml = new String(bytes);
            emailHtml = emailHtml.replace("[[activationurl]]",siteUrl + "/api/v1/users/confirm-registration?code=" + user.getVerificationCode() + "&userid=" + user.getId());
        } catch (IOException e) {
            return new ResponseEntity<String>("Error sending activation email! Error:" + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            helper.setFrom(new InternetAddress(sender, "TXT E-TECH Sport Team"));
            helper.setTo(obj.getString("email"));
            helper.setText(emailHtml, true);
            helper.setSubject("Confirm Account Registration");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mailSender.send(mimeMessage);

        return new ResponseEntity<String>("User registered!", HttpStatus.CREATED);
    }

    @GetMapping("/confirm-registration")
    public ResponseEntity<String> confirmRegistration(@RequestParam int code, @RequestParam String userid) {
        try {
            Optional<User> optUser = userService.userById(new ObjectId(userid));
            if(userService.updateStatus(new ObjectId(userid), code))
                return new ResponseEntity<String>("Update successful for user: " + optUser.get(), HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<String>("Update failed cause user not found --- \n" + e, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<String>("Update failed cause user not found", HttpStatus.NO_CONTENT);
    }

}
