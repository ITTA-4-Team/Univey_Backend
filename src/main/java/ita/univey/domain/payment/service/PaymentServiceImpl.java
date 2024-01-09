package ita.univey.domain.payment.service;

import ita.univey.domain.payment.config.TossPaymentConfig;
import ita.univey.domain.payment.dto.PaymentSuccessDto;
import ita.univey.domain.payment.entity.Payment;
import ita.univey.domain.payment.repository.PaymentRepository;
import ita.univey.domain.point.domain.PointTransaction;
import ita.univey.domain.point.domain.repository.PointTransactionRepository;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.repository.UserRepository;
import ita.univey.domain.user.domain.service.UserService;
import ita.univey.global.CustomLogicException;
import ita.univey.global.ErrorCode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static ita.univey.domain.point.domain.PointType.POINT_PURCHASE;


@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final TossPaymentConfig tossPaymentConfig;
    private final UserRepository userRepository;
    private final PointTransactionRepository pointTransactionRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, TossPaymentConfig tossPaymentConfig, UserRepository userRepository, PointTransactionRepository pointTransactionRepository) {
        this.paymentRepository = paymentRepository;
        this.tossPaymentConfig = tossPaymentConfig;
        this.userRepository = userRepository;
        this.pointTransactionRepository = pointTransactionRepository;
    }


    public Payment requestTossPayment(Payment payment, String userEmail) {
        User user = userRepository.findUserByEmail(userEmail).orElseThrow(() -> new CustomLogicException(ErrorCode.REQUEST_VALIDATION_EXCEPTION));
        /*if (payment.getAmount() < 1000) {
            throw new CustomLogicException(ExceptionCode.INVALID_PAYMENT_AMOUNT);
        }*/
        payment.setUser(user);
        return paymentRepository.save(payment);
    }

    //결제 성공 로직 검증
    @Transactional
    public PaymentSuccessDto tossPaymentSuccess(String paymentKey, String orderId, Integer amount) {
        Payment payment = verifyPayment(orderId, amount); // 요청 가격 = 결제된 금액
        PaymentSuccessDto result = requestPaymentAccept(paymentKey, orderId, amount);
        payment.setPaymentKey(paymentKey); //추후 결제 취소, 결제 조회
        payment.setPaySuccessYN(true); //성공 여부
        payment.getUser().setPoint(payment.getUser().getPoint() + amount); //포인트 업데이트

        PointTransaction pointTransaction = PointTransaction.builder()
                .user(payment.getUser())
                .pointType(POINT_PURCHASE)
                .pointAmount(amount)
                .remainingPoints(payment.getUser().getPoint())
                .build();
        pointTransactionRepository.save(pointTransaction);

        return result;
    }

    //토스페이먼트츠에 최종 결제 승인 요청을 보내기 위해 필요한 정보들을 담아 post로 보내는 부분
    @Transactional
    public PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId, Integer amount) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();

        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("amount", amount);
        params.put("paymentKey", paymentKey);

        PaymentSuccessDto result = null;
        try {
            result = restTemplate.postForObject(TossPaymentConfig.URL + "/confirm",
                    //요청 URL은 Config에 작성한 "http://api.tosspayments.com/v1/payments/" + paymentKey
                    new HttpEntity<>(params, headers),
                    PaymentSuccessDto.class);
            //restTemplate.postForObject() -> post 요청을 보내고 객체로 결과를 반환 받는다
        } catch (Exception e) {
            throw new CustomLogicException(ErrorCode.ALREADY_APPROVED);
        }
        return result;
    }

    //결제 요청된 금액과 실제 결제된 금액 같은지 검증하는 부분
    public Payment verifyPayment(String orderId, Integer amount) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (!payment.getAmount().equals(amount)) {
            throw new CustomLogicException(ErrorCode.PAYMENT_AMOUNT_EXP);
        }
        return payment;
    }

    // 실패 응답을 처리하는 로직
    @Transactional
    public void tossPaymentFail(String code, String message, String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        payment.setPaySuccessYN(false); // 결제 성공 여부를 false로 변경
        payment.setFailReason(message); // 에러 메세지를 set함
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