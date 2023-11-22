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
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;

    private String providerId;

    public static UserJoinDto from(User user) {
        if (user == null) return null;

        return UserJoinDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .providerId(user.getProviderId())
                .build();
    }
}
