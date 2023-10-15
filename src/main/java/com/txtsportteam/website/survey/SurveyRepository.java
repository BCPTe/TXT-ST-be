package com.txtsportteam.website.survey;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends MongoRepository<Survey, ObjectId> {
    List<Survey> findSurveyByActive(Boolean active);
    Survey findSurveyByDate(Long date);
    Survey findSurveyById(ObjectId id);
}
