package ita.univey.domain.survey.domain.dto;

//import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SurveyDto {
    private String topic;
    private String description;
    private List<QuestionDto> userQuestions;


    //@QueryProjection
    /*public SurveyDto(Long id, String topic, String description, String category, String deadline, Integer age, Gender gender, String targetRespondents, Integer totalQuestionCnt, Integer targetNumberOfRes, Integer actualNumberOfRes, String trend, Integer point) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.category = category;
        this.deadline = deadline;
        this.age = age;
        this.gender = gender;
        this.targetRespondents = targetRespondents;
        this.totalQuestionCnt = totalQuestionCnt;
        this.targetNumberOfRes = targetNumberOfRes;
        this.actualNumberOfRes = actualNumberOfRes;
        this.trend = trend;
        this.point = point;
    }*/
}
