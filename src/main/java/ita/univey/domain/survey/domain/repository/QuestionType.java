package ita.univey.domain.survey.domain.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QuestionType {
    MULTIPLE_CHOICE("multipleChoice"),
    SHORT_ANSWER("shortAnswer");
    private String value;
}
