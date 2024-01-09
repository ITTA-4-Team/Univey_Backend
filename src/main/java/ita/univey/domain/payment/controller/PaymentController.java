package ita.univey.domain.payment.controller;

import ita.univey.domain.payment.config.TossPaymentConfig;
import ita.univey.domain.payment.dto.PaymentDto;
import ita.univey.domain.payment.dto.PaymentFailDto;
import ita.univey.domain.payment.dto.PaymentResDto;
import ita.univey.domain.payment.service.PaymentServiceImpl;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.repository.UserRepository;
import ita.univey.global.CustomLogicException;
import ita.univey.global.ErrorCode;
import ita.univey.global.response.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentController {

    private final PaymentServiceImpl paymentService;
    private final TossPaymentConfig tossPaymentConfig;

    public PaymentController(PaymentServiceImpl paymentService, TossPaymentConfig tossPaymentConfig) {
        this.paymentService = paymentService;
        this.tossPaymentConfig = tossPaymentConfig;
    }

    /*requestTossPayment() : 프론트에서 결제 요청하기 위해 1차적으로 요청하는 api
    Service 로직에서 검증 과정을 마치고 정상적으로 진행이 되면 토스페이먼츠에 실제 결제 요청을 보냄
    응답 dto에 성공, 실패 url을 담음*/
    @PostMapping("/toss")
    public ResponseEntity requestTossPayment(Authentication authentication, @RequestBody @Valid PaymentDto paymentReqDto) {
        PaymentResDto paymentResDto = paymentService.requestTossPayment(paymentReqDto.toEntity(), authentication.getName()).toPaymentResDto();
        paymentResDto.setSuccessUrl(paymentReqDto.getSuccessUrl() == null ? tossPaymentConfig.getSuccessUrl() : paymentReqDto.getSuccessUrl());
        paymentResDto.setFailUrl(paymentReqDto.getFailUrl() == null ? tossPaymentConfig.getFailUrl() : paymentReqDto.getFailUrl());
        return ResponseEntity.ok().body(new SingleResponse<>(paymentResDto));
    }
    //결제 성공
    @GetMapping("/toss/success")
    public ResponseEntity tossPaymentSuccess(@RequestParam String paymentKey,
                                             @RequestParam String orderId,
                                             @RequestParam Integer amount) {
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
}