package ita.univey.domain.payment.domain.repository;

import ita.univey.domain.payment.domain.entity.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long> { //저장 장소

    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByPaymentKeyAndUserEmail(String paymentKey, String email);
    Optional<Payment> findAllByUserEmail(String email, Pageable pageable); //findByUser_Email ?
}
