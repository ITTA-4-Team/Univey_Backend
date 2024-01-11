package ita.univey.domain.user.domain.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
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

    public ImageDto imageDto;
}

