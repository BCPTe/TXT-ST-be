package com.txtsportteam.website.survey;

import com.txtsportteam.website.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "surveys")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Survey {
    @Id
    private ObjectId id;
    private Long date;
    private Boolean active = false;
    private List<User> players;
    private Long datetime = null;
    private String location = null;

    public Survey(Long date, Boolean active, List<User> players) {
        this.date = date;
        this.active = active;
        this.players = players;
    }
    public Survey(Long date, List<User> players) {
        this.date = date;
        this.players = players;
    }
    public Survey(Long date, Boolean active) {
        this.date = date;
        this.active = active;
    }

}
