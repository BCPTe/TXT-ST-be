package com.txtsportteam.website.survey;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/surveys")
public class SurveyController {
    @Autowired
    SurveyService surveyService;

    @GetMapping
    public ResponseEntity<List<Survey>> getAllSurveys() {
        return new ResponseEntity<List<Survey>>(surveyService.allSurveys(), HttpStatus.OK);
    }

    @GetMapping(params = "active")
    public ResponseEntity<List<Survey>> getSurveyByActive(@RequestParam("active") Boolean active) {
        return new ResponseEntity<List<Survey>>(surveyService.surveyByActive(active), HttpStatus.OK);
    }

    @GetMapping(params = "date")
    public ResponseEntity<Survey> getSurveyByDate(@RequestParam("date") Long date) {
        return new ResponseEntity<Survey>(surveyService.surveyByDate(date),HttpStatus.OK);
    }

    @GetMapping(params = "user")
    public ResponseEntity<List<Survey>> getSurveysByUsername(@RequestParam("user") String user) {
        return new ResponseEntity<List<Survey>>(surveyService.surveysByUsername(user),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Survey> manageSurvey(@RequestBody String payload) {
        JSONObject info = new JSONObject(payload);
        JSONObject return_values = surveyService.manageSurvey(info);
        if(return_values.get("code") == SurveyService.op.CREATED) {
            return new ResponseEntity<>((Survey)return_values.get("survey"), HttpStatus.CREATED);
        }
        else if (return_values.get("code") == SurveyService.op.UPDATED) {
            return new ResponseEntity<>((Survey)return_values.get("survey"), HttpStatus.OK);
        }
        else if (return_values.get("code") == SurveyService.op.ALREADYPRESENT) {
            return new ResponseEntity<>((Survey)return_values.get("survey"), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @PostMapping
//    public ResponseEntity<Survey> createSurvey(@RequestBody String payload) {
//        JSONObject obj = new JSONObject(payload);
//        Survey survey = new Survey(obj.getLong("date"), (List<String>)(List)obj.getJSONArray("players").toList());
//
//        return new ResponseEntity<Survey>(surveyService.insertSurvey(survey), HttpStatus.CREATED);
//    }
}
