package ita.univey.domain.point.domain.repository;

import ita.univey.domain.point.domain.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
}
