package ita.univey.domain.survey.domain.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
    Female("여성"),
    MALE("남성");

    private String value;
}