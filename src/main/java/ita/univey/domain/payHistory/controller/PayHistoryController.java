package ita.univey.domain.payHistory.controller;

import ita.univey.domain.global.response.SliceInfo;
import ita.univey.domain.global.response.SliceResponseDto;
import ita.univey.domain.payHistory.mapper.PayHistoryMapper;
import ita.univey.domain.payHistory.entity.PayHistory;
import ita.univey.domain.payHistory.service.PayHistoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;


@RestController
@Validated
@RequestMapping("/api/v1/payHistory")
public class PayHistoryController {
    private final PayHistoryService payHistoryService;
    private final PayHistoryMapper mapper;

    public PayHistoryController(PayHistoryService payHistoryService, PayHistoryMapper mapper) {
        this.payHistoryService = payHistoryService;
        this.mapper = mapper;
    }

    // 지불 내역 조회
    @GetMapping()
    public ResponseEntity getPayHistory(@AuthenticationPrincipal User authUser,
                                        Pageable pageable) {
        Slice<PayHistory> payHistory = payHistoryService.findPayHistory(authUser.getUsername(), pageable);
        SliceInfo sliceInfo = new SliceInfo(pageable, payHistory.getNumberOfElements(), payHistory.hasNext());
        return new ResponseEntity<>(
                new SliceResponseDto<>(mapper.payHistoryToPayHistoryResponse(payHistory.getContent()), sliceInfo), HttpStatus.OK);
    }
}