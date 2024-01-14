package ita.univey.domain.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponseDto {

    @NotEmpty
    private String userName;

    @NotEmpty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer point;

    @NotEmpty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String email;


}
