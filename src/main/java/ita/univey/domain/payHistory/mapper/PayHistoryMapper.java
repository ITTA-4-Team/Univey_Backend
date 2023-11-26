package ita.univey.domain.payHistory.mapper;

import ita.univey.domain.payHistory.entity.PayHistory;
import ita.univey.domain.payHistory.dto.PayHistoryDto;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PayHistoryMapper {

    default List<PayHistoryDto.Response> payHistoryToPayHistoryResponse(List<PayHistory> payHistories) {
        if (payHistories == null) {
            return null;
        }

        return payHistories.stream()
                .map(payHistory -> PayHistoryDto.Response.builder()
                        .payHistoryId(payHistory.getPayHistoryId())
                        .email(payHistory.getUser().getEmail())
                        .nickname(payHistory.getUser().getName())
                        .paidPrice(payHistory.getPaidPrice())
                        .point(payHistory.getUser().getPoint())
                        .createdAt(payHistory.getCreatedAt())
                        //.type(payHistory.getType())
                        .build()).collect(Collectors.toList());
    }
}