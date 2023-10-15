package com.txtsportteam.website.survey;

import com.txtsportteam.website.user.User;
import com.txtsportteam.website.user.UserRepository;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SurveyService {

    public enum op {
        CREATED, UPDATED, ALREADYPRESENT
    }
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Survey> allSurveys() {
        return surveyRepository.findAll();
    }

    public List<Survey> surveyByActive(Boolean active) {
        return surveyRepository.findSurveyByActive(active);
    }
    public Survey surveyByDate(Long date) {
        return surveyRepository.findSurveyByDate(date);
    }

    public Survey surveyById(ObjectId id) {
        return surveyRepository.findSurveyById(id);
    }

    public List<Survey> surveysByUsername(String username) {
        User actualUser = userRepository.findUserByUsername(username).get();
        Query query = new Query()
                .addCriteria(Criteria.where("players").in(actualUser));
        return mongoTemplate.find(query, Survey.class);
    }

    public Survey insertSurvey(Survey survey) {
        return surveyRepository.insert(survey);
    }

    public JSONObject manageSurvey(JSONObject info) {
        JSONObject return_values = new JSONObject();
        String operation = info.getString("operation");

        // check operation type (edit on players or on active boolean)
        if(operation.contains("user")) {
            Long surveyDate = info.getLong("date");
            Survey survey = surveyByDate(surveyDate);
            // if it's an operation on survey users
            boolean conflict = false;
            String playerUsername = info.getString("username");

            User actualUser = userRepository.findUserByUsername(playerUsername).get();

            if (survey != null) {
                // check if user is already present
                for (User user : survey.getPlayers()) {
                    if (operation.contains("del"))
                        // it doesn't need to check if user is already present
                        break;
                    if (actualUser.getUsername().equals(user.getUsername())) {
                        conflict = true;
//                        return_values.put("survey", survey);
                        return_values.put("code", op.ALREADYPRESENT);
                    }
                }
            }
            if (!conflict) {
                if (survey != null) {
                    // update present survey

                    // operations on players
                    List<User> players = survey.getPlayers();
                    if (operation.contains("del")) {
                        // remove a player from "players" list in db
                        players.remove(actualUser);
                        if(players.size() == 0) {
                            surveyRepository.delete(survey);
                            return_values.put("code", op.UPDATED);
                            return_values.put("survey", survey);
                            return return_values;
                        }
                    } else if (operation.contains("add")) {
                        // add a player to "players" list in db
                        players.add(actualUser);
                    }
                    survey.setPlayers(players);
                    surveyRepository.save(survey);
                    return_values.put("code", op.UPDATED);
                } else {
                    // insert new survey with date and player received

                    List<User> players = new ArrayList<>();
                    players.add(actualUser);
                    survey = new Survey(surveyDate, players);
                    insertSurvey(survey);
                    return_values.put("code", op.CREATED);
                }
            }

            return_values.put("survey", survey);
        } else if(operation.contains("active")) {
            Long surveyDate = info.getLong("surveyDate");
            Survey survey = surveyByDate(surveyDate);
            // if it's an operation on active boolean
            Boolean active = info.getBoolean("active");
            survey.setActive(active);
            surveyRepository.save(survey);

            return_values.put("survey", survey);

            // now work on match info into db
            // active = true -> (insert)
            if(active) {
                Long datetime = info.getLong("matchDate");
                String location = info.getString("location");
                survey.setDatetime(datetime);
                survey.setLocation(location);
                surveyRepository.save(survey);

                return_values.put("code", op.CREATED);
            }
            else {
                // active = false -> (remove)
                survey.setDatetime(null);
                survey.setLocation(null);
                surveyRepository.save(survey);

                return_values.put("code", op.UPDATED);
            }
        }


        return return_values;
    }

}
