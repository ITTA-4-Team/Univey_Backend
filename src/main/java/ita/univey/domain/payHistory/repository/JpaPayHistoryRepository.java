package ita.univey.domain.payHistory.repository;

import ita.univey.domain.payHistory.entity.PayHistory;
import ita.univey.domain.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPayHistoryRepository extends JpaRepository<PayHistory, Long>, PayHistoryRepository {
    Slice<PayHistory> findAllByUser(User user, Pageable pageable);
}