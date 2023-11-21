package ita.univey.domain.user.domain.dto;

import ita.univey.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinDto {
    @NotEmpty
    private String name;
    private String email;
    private String password;

    public static UserJoinDto from(User user) {
        if (user == null) return null;

        return UserJoinDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
