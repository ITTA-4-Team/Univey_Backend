package ita.univey.domain.payment.domain.service;

import ita.univey.domain.global.exception.CustomLogicException;
import ita.univey.domain.global.exception.ExceptionCode;
import ita.univey.domain.payment.domain.config.TossPaymentConfig;
import ita.univey.domain.payment.domain.dto.PaymentSuccessDto;
import ita.univey.domain.payment.domain.entity.Payment;
import ita.univey.domain.payment.domain.repository.JpaPaymentRepository;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.json.JSONObject;

import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final JpaPaymentRepository paymentRepository;
    private final UserService userService;
    private final TossPaymentConfig tossPaymentConfig;


    public PaymentServiceImpl(JpaPaymentRepository paymentRepository, UserService userService, TossPaymentConfig tossPaymentConfig) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.tossPaymentConfig = tossPaymentConfig;
    }

    public Payment requestTossPayment(Payment payment, String userEmail) {
        User user = userService.findUser(userEmail);
        if (payment.getAmount() < 1000) {
            throw new CustomLogicException(ExceptionCode.INVALID_PAYMENT_AMOUNT);
        }
        payment.setCustomer(member);
        return paymentRepository.save(payment);
    }

    @Transactional
    public PaymentSuccessDto tossPaymentSuccess(String paymentKey, String orderId, Long amount) {
        Payment payment = verifyPayment(orderId, amount);
        PaymentSuccessDto result = requestPaymentAccept(paymentKey, orderId, amount);
        payment.setPaymentKey(paymentKey); //추후 결제 취소, 결제 조회
        payment.setPaySuccessYN(true);
        payment.getCustomer().setPoint(payment.getCustomer().getPoint() + amount);
        memberService.updateMemberCache(payment.getCustomer());
        return result;
    }

    @Transactional
    public PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("amount", amount);

        PaymentSuccessDto result = null;
        try {
            result = restTemplate.postForObject(TossPaymentConfig.URL + paymentKey,
                    new HttpEntity<>(params, headers),
                    PaymentSuccessDto.class);
        } catch (Exception e) {
            throw new CustomLogicException(ExceptionCode.ALREADY_APPROVED);
        }

        return result;

    }

    public Payment verifyPayment(String orderId, Long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> {
            throw new CustomLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        });
        if (!payment.getAmount().equals(amount)) {
            throw new CustomLogicException(ExceptionCode.PAYMENT_AMOUNT_EXP);
        }
        return payment;
    }

    @Transactional
    public void tossPaymentFail(String code, String message, String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> {
            throw new CustomLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        });
        payment.setPaySuccessYN(false);
        payment.setFailReason(message);
    }

    @Transactional
    public Map cancelPaymentPoint(String userEmail, String paymentKey, String cancelReason) {
        Payment payment = paymentRepository.findByPaymentKeyAndUserEmail(paymentKey, userEmail).orElseThrow(() -> {
            throw new CustomLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        });
        // 취소 할려는데 포인트가 그만큼 없으면 환불 못함
        if (payment.getCustomer().getPoint() >= payment.getAmount()) {
            payment.setCancelYN(true);
            payment.setCancelReason(cancelReason);
            payment.getCustomer().setPoint(payment.getCustomer().getPoint() - payment.getAmount());
            return tossPaymentCancel(paymentKey, cancelReason);
        }

        throw new CustomLogicException(ExceptionCode.PAYMENT_NOT_ENOUGH_POINT);
    }

    public Map tossPaymentCancel(String paymentKey, String cancelReason) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("cancelReason", cancelReason);

        return restTemplate.postForObject(TossPaymentConfig.URL + paymentKey + "/cancel",
                new HttpEntity<>(params, headers),
                Map.class);
    }

    @Override
    public Slice<Payment> findAllChargingHistories(String username, Pageable pageable) {
        userService.verifyUser(username);
        return paymentRepository.findAllByUserEmail(username,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.Direction.DESC, "paymentId")
        );
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuthKey = new String(
                Base64.getEncoder().encode((tossPaymentConfig.getTestSecretKey() + ":").getBytes(StandardCharsets.UTF_8)));
        headers.setBasicAuth(encodedAuthKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}