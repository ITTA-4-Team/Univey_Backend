package ita.univey.domain.user.domain.dto;

import ita.univey.domain.point.domain.PointType;
import lombok.Builder;

import javax.validation.constraints.NotEmpty;

@Builder
public class UserPointHistoryResponse {
    @NotEmpty
    private String topic;

    @NotEmpty
    private String createdDay;

    @NotEmpty
    private PointType pointType;

    @NotEmpty
    private Integer point;

    @NotEmpty
    private Integer remainingPoint;
}
