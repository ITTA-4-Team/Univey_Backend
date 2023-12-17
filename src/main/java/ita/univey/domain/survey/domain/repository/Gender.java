package ita.univey.domain.survey.domain.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
    ALL("전체"),
    FEMALE("여성"),
    MALE("남성");

    private String value;

}