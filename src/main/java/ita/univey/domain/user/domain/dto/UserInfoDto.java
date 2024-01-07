package ita.univey.domain.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

    @NotEmpty
    public String name;

    @NotEmpty
    public String email;

    // 회원가입 시 닉네임, 전화번호 입력하는 화면 나오면 추가
    //@NotEmpty
    public String nickName;

    //@NotEmpty
    public String phoneNumber;
}

