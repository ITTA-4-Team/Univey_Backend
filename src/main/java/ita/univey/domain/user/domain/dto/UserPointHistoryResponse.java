package ita.univey.domain.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ita.univey.domain.point.domain.PointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
public class UserPointHistoryResponse {
    @NotEmpty
    private String topic;

    private String sub;
    
    @NotEmpty
    private String createdDay;

    @NotEmpty
    private PointType pointType;

    @NotEmpty
    private Integer point;

    @NotEmpty
    private Integer remainingPoint;
}
