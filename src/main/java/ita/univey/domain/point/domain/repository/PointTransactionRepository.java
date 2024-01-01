package ita.univey.domain.point.domain.repository;

import ita.univey.domain.point.domain.PointTransaction;
import ita.univey.domain.point.domain.PointType;
import ita.univey.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    List<PointTransaction> findAllByUserAndPointTypeOrderByCreatedAtDesc(User user, PointType pointType);
}
