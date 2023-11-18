package ita.univey.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {

    FEMALE("여성"),
    MALE("남성");

    private String value;

}