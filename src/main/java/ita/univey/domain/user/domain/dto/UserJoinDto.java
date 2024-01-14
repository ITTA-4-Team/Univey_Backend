package ita.univey.domain.user.domain.dto;

import ita.univey.domain.user.domain.User;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

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

    @Getter
    @Setter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Long id;
        @NotEmpty
        private String name;
        @NotEmpty
        private String email;
        @NotEmpty
        private String password;

        private String providerId;

        private Set<String> roleSet;

        private Long point;

        public void privateResponse() {
            this.point = point;
        }
    }
}
