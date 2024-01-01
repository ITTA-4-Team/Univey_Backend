package ita.univey.domain.point.domain.service;

import ita.univey.domain.point.domain.PointTransaction;
import ita.univey.domain.point.domain.PointType;
import ita.univey.domain.point.domain.repository.PointTransactionRepository;
import ita.univey.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointTransactionService {
    private final PointTransactionRepository pointTransactionRepository;

    public List<PointTransaction> getUserTransactionsByTypeOrderedByTime(User user, PointType pointType) {
        return pointTransactionRepository.findAllByUserAndPointTypeOrderByCreatedAtDesc(user, pointType);
    }

    public void savePointTransaction(PointTransaction pointTransaction) {
        pointTransactionRepository.save(pointTransaction);
    }
}
