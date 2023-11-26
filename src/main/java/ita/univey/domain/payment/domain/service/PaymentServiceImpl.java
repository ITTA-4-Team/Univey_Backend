package ita.univey.domain.payment.domain.service;

import ita.univey.domain.global.exception.CustomLogicException;
import ita.univey.domain.global.exception.ExceptionCode;
import ita.univey.domain.payment.domain.config.TossPaymentConfig;
import ita.univey.domain.payment.domain.dto.PaymentSuccessDto;
import ita.univey.domain.payment.domain.entity.Payment;
import ita.univey.domain.payment.domain.repository.JpaPaymentRepository;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.service.UserService;
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
        payment.setCustomer(user);
        return paymentRepository.save(payment);
    }

    //결제 성공 로직 검증
    @Transactional
    public PaymentSuccessDto tossPaymentSuccess(String paymentKey, String orderId, Long amount) {
        Payment payment = verifyPayment(orderId, amount); // 요청 가격 = 결제된 금액
        PaymentSuccessDto result = requestPaymentAccept(paymentKey, orderId, amount);
        payment.setPaymentKey(paymentKey); //추후 결제 취소, 결제 조회
        payment.setPaySuccessYN(true); //성공 여부
        payment.getCustomer().setPoint(payment.getCustomer().getPoint() + amount); //포인트 업데이트
        userService.updateUserCache(payment.getCustomer());
        return result;
    }

    //토스페이먼트에 최종 결제 승인 요청을 보내기 위해 필요한 정보들을 담아 post로 보내는 부분
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
                    //요청 URL은 Config에 작성한 "http://api.tosspayments.com/v1/payments/" + paymentKey
                    new HttpEntity<>(params, headers),
                    PaymentSuccessDto.class);
            //restTemplate.postForObject() -> post 요청을 보내고 객체로 결과를 반환 받는다
        } catch (Exception e) {
            throw new CustomLogicException(ExceptionCode.ALREADY_APPROVED);
        }

        return result;

    }

    //결제 요청된 금액과 실제 결제된 금액 같은지 검증하는 부분
    public Payment verifyPayment(String orderId, Long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> {
            throw new CustomLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        });
        if (!payment.getAmount().equals(amount)) {
            throw new CustomLogicException(ExceptionCode.PAYMENT_AMOUNT_EXP);
        }
        return payment;
    }

    // 실패 응답을 처리하는 로직
    @Transactional
    public void tossPaymentFail(String code, String message, String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> {
            throw new CustomLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        });
        payment.setPaySuccessYN(false); // 결제 성공 여부를 false로 변경
        payment.setFailReason(message); // 에러 메세지를 set함
    }

    // Controller에서 전달받은 값들을 이용하여 검증 로직들을 처리하는 메소드
    @Transactional
    public Map cancelPaymentPoint(String userEmail, String paymentKey, String cancelReason) {
        Payment payment = paymentRepository.findByPaymentKeyAndCustomer_Email(paymentKey, userEmail).orElseThrow(() -> {
            throw new CustomLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        });
        // 취소 하려는데 포인트가 그만큼 없으면 환불 못함
        if (payment.getCustomer().getPoint() >= payment.getAmount()) {
            payment.setCancelYN(true);
            payment.setCancelReason(cancelReason);
            payment.getCustomer().setPoint(payment.getCustomer().getPoint() - payment.getAmount());
            return tossPaymentCancel(paymentKey, cancelReason);
        }

        throw new CustomLogicException(ExceptionCode.PAYMENT_NOT_ENOUGH_POINT);
    }

    // 토스페이먼츠에 최종 취소 승인 요청을 보내기 위해 필요한 정보들을 담아 post로 보내는 부분
    public Map tossPaymentCancel(String paymentKey, String cancelReason) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("cancelReason", cancelReason);

        return restTemplate.postForObject(TossPaymentConfig.URL + paymentKey + "/cancel",
                //요청 URL -> "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"
                new HttpEntity<>(params, headers),
                Map.class);
    }

    // 고객 정보를 가져와 해당 고객이 맞는지 검증 후에 paymentRepository에서 해당 고객의 결제 내역을 모두 가져와 반환하는 부분
    @Override
    public Slice<Payment> findAllChargingHistories(String username, Pageable pageable) {
        userService.verifyUser(username);
        return paymentRepository.findAllByCustomer_Email(username,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.Direction.DESC, "paymentId") // paymentId를 기준으로 내림차순 정렬하여 반환
        );
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuthKey = new String(
                Base64.getEncoder().encode((tossPaymentConfig.getTestSecretApiKey() + ":").getBytes(StandardCharsets.UTF_8)));
        headers.setBasicAuth(encodedAuthKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}