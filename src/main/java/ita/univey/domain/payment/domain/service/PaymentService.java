package ita.univey.domain.payment.domain.service;

import com.example.tosspayments.config.TossPaymentConfig;
import com.example.tosspayments.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j

public class PaymentService {


    private final TossPaymentConfig tossPaymentConfig;

    //결제승인
    public PaymentResponse tossPaymentSuccess(String paymentKey, String orderId, Long amount) {
        log.info("secret : {}", tossPaymentConfig.getTestSecretApiKey());
        // 결제 승인 전에 승인할 데이터 검증하기
        //결제 인증이 완료되면 성공 리다이렉트 URL에 들어온 값을 확인합니다. 돌아오는 값은 항상 paymentKey, orderId, amount, paymentType 네 가지입니다.
        //orderId로 결제 요청 전에 저장해 둔 임시 정보를 불러옵니다.
        //적립금 및 쿠폰을 사용할 수 있는지, 적립금과 쿠폰을 적용한 최종 결제 금액이 토스페이먼츠에서 돌아온 성공 리다이렉트 URL을 통해 받은 금액과 같은지 확인해보세요.
        //문제가 없다면 돌아온 데이터를 사용해서 결제 승인을 요청하세요.
        // Payment payment = verifyPayment(orderId, amount);
        PaymentResponse result = requestPaymentAccept(paymentKey, orderId, amount);

        return result;
    }
    /*public Payment verifyPayment(String orderId, Long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> {
            throw new CustomLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        });
        if (!payment.getAmount().equals(amount)) {
            throw new CustomLogicException(ExceptionCode.PAYMENT_AMOUNT_EXP);
        }
        return payment;
    }*/

    public PaymentResponse requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("paymentKey", paymentKey);
        params.put("orderId", orderId);
        params.put("amount", amount);

        log.info("params : {}", params);
        String u = TossPaymentConfig.URL + "confirm" ; //"https://api.tosspayments.com/v1/payments/confirm"
        log.info("url : {}", u);
        HttpEntity<String> jsonObjectHttpEntity = new HttpEntity<>(params.toString(), headers);
        log.info("jsonObjectHttpEntity : {}", jsonObjectHttpEntity);

        PaymentResponse paymentSuccessDto = restTemplate.postForObject(u,
                jsonObjectHttpEntity,
                PaymentResponse.class);

        return paymentSuccessDto;

    }

    //paymentKey로 결제내역 조회
    public PaymentResponse getPaymentDetailsByPaymentKey(String paymentKey) {
        RestTemplate restTemplate = new RestTemplate();


        HttpHeaders headers = getHeaders();

        String apiUrl = TossPaymentConfig.URL + paymentKey;

        HttpEntity<String> entity = new HttpEntity<String>("", headers);
        // GET 요청 보내기 및 응답 받기
        ResponseEntity<PaymentResponse> forEntity =
                restTemplate.exchange(apiUrl, HttpMethod.GET, entity, PaymentResponse.class);

        PaymentResponse paymentSuccessDto = forEntity.getBody();

        return paymentSuccessDto;
    }


    //orderId로 결제내역 조회
    public PaymentResponse getPaymentDetailsByOrderId(String orderId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        String apiUrl = TossPaymentConfig.URL + "orders/" + orderId;
        HttpEntity<String> entity = new HttpEntity<String>("", headers);
        // GET 요청 보내기 및 응답 받기
        ResponseEntity<PaymentResponse> forEntity =
                restTemplate.exchange(apiUrl, HttpMethod.GET, entity, PaymentResponse.class);
        PaymentResponse paymentSuccessDto = forEntity.getBody();
        return paymentSuccessDto;
    }

    //결제 취소
    public PaymentResponse cancelPayment(String paymentKey,String cancelReason, String email) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("cancelReason",cancelReason);
        HttpEntity<String> jsonObjectHttpEntity = new HttpEntity<>(params.toString(), headers);
        String apiUrl = TossPaymentConfig.URL + paymentKey + "/cancel";


        PaymentResponse paymentSuccessDto = restTemplate.postForObject(apiUrl,
                jsonObjectHttpEntity,
                PaymentResponse.class);
        return paymentSuccessDto;
    }


    //헤더 필수값
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        log.info("header secret : {}", tossPaymentConfig.getTestSecretApiKey());
        String encodedAuthKey = new String(
                Base64.getEncoder().encode((tossPaymentConfig.getTestSecretApiKey() + ":").getBytes(StandardCharsets.UTF_8)));
        log.info("encodedAuthKey : {}", encodedAuthKey);
        headers.setBasicAuth(encodedAuthKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        UUID u = UUID.randomUUID();
        headers.set("Idempotency-Key", u.toString());

        return headers;
    }
}