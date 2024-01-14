package ita.univey.domain.survey.domain.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SurveyStatus {
    IN_PROGRESS("activeSurvey"),
    COMPLETED("completedSurvey");
    private String value;

    public static SurveyStatus getStatusByValue(String value) {
        for (SurveyStatus status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return IN_PROGRESS;
    }
}