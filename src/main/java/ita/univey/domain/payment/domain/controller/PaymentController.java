package ita.univey.domain.payment.domain.controller;

import ita.univey.domain.global.response.SingleResponse;
import ita.univey.domain.global.response.SliceInfo;
import ita.univey.domain.global.response.SliceResponseDto;
import ita.univey.domain.payment.domain.config.TossPaymentConfig;
import ita.univey.domain.payment.domain.dto.PaymentDto;
import ita.univey.domain.payment.domain.dto.PaymentFailDto;
import ita.univey.domain.payment.domain.dto.PaymentResDto;
import ita.univey.domain.payment.domain.dto.PaymentResponse;
import ita.univey.domain.payment.domain.entity.Payment;
import ita.univey.domain.payment.domain.mapper.PaymentMapper;
import ita.univey.domain.payment.domain.service.PaymentServiceImpl;
import ita.univey.domain.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.validation.Valid;
import java.awt.image.RescaleOp;

@RestController
@Validated
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentController {

    private final PaymentServiceImpl paymentService;
    private final TossPaymentConfig tossPaymentConfig;
    private final PaymentMapper mapper;

    public PaymentController(PaymentServiceImpl paymentService, TossPaymentConfig tossPaymentConfig, PaymentMapper mapper) {
        this.paymentService = paymentService;
        this.tossPaymentConfig = tossPaymentConfig;
        this.mapper = mapper;
    }

    /*requestTossPayment() : 프론트에서 결제 요청하기 위해 1차적으로 요청하는 api
    Service 로직에서 검증 과정을 마치고 정상적으로 진행이 되면 토스페이먼츠에 실제 결제 요청을 보냄
    응답 dto에 성공, 실패 url을 담음*/
    @PostMapping("/toss")
    public ResponseEntity requestTossPayment(@AuthenticationPrincipal User principal, @RequestBody @Valid PaymentDto paymentReqDto) {
        PaymentResDto paymentResDto = paymentService.requestTossPayment(paymentReqDto.toEntity(), principal.getName()).toPaymentResDto();
        paymentResDto.setSuccessUrl(paymentReqDto.getSuccessUrl() == null ? tossPaymentConfig.getSuccessUrl() : paymentReqDto.getSuccessUrl());
        paymentResDto.setFailUrl(paymentReqDto.getFailUrl() == null ? tossPaymentConfig.getFailUrl() : paymentReqDto.getFailUrl());
        return ResponseEntity.ok().body(new SingleResponse<>(paymentResDto));
    }
    //결제 승인
    @GetMapping("/toss/success")
    public ResponseEntity tossPaymentSuccess(@RequestParam String paymentKey,
                                             @RequestParam String orderId,
                                             @RequestParam Long amount) {
        return ResponseEntity.ok().body(new SingleResponse<>(paymentService.tossPaymentSuccess(paymentKey, orderId, amount)));
    }

    //결제 실패시
    @GetMapping("/toss/fail")
    public ResponseEntity tossPaymentFail(@RequestParam String code,
                                          @RequestParam String message,
                                          @RequestParam String orderId) {
        paymentService.tossPaymentFail(code, message, orderId);

        return ResponseEntity.ok().body(new SingleResponse<>(
                PaymentFailDto.builder()
                        .errorCode(code)
                        .errorMessage(message)
                        .orderId(orderId)
                        .build()
        ));
    }

    //결제 조회
    @PostMapping("/toss/cancel/point")
    public ResponseEntity tossPaymentCancelPoint(
            @AuthenticationPrincipal User principal,
            @RequestParam String paymentKey,
            @RequestParam String cancelReason
    ) {
        return ResponseEntity.ok().body(new SingleResponse<>(
                paymentService
                        .cancelPaymentPoint(principal.getName(), paymentKey, cancelReason)));
    }

    // 결제 내역 조회
    @GetMapping("/history")
    public ResponseEntity getChargingHistory(@AuthenticationPrincipal User authUser,
                                             Pageable pageable) {
        Slice<Payment> chargingHistories = paymentService.findAllChargingHistories(authUser.getName(), pageable);
        SliceInfo sliceInfo = new SliceInfo(pageable, chargingHistories.getNumberOfElements(), chargingHistories.hasNext());
        return new ResponseEntity<>(
                new SliceResponseDto<>(mapper.chargingHistoryToChargingHistoryResponses(chargingHistories.getContent()), sliceInfo), HttpStatus.OK);
    }

}