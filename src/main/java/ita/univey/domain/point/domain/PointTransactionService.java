package ita.univey.domain.point.domain;

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
}
