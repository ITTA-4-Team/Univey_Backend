package ita.univey.domain.payment.service;


import ita.univey.domain.payment.dto.PaymentSuccessDto;
import ita.univey.domain.payment.entity.Payment;

public interface PaymentService {
    Payment requestTossPayment(Payment payment, String userEmail);
    PaymentSuccessDto tossPaymentSuccess(String paymentKey, String orderId, Integer amount);
    PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId, Integer amount);
    Payment verifyPayment(String orderId, Integer amount);
}