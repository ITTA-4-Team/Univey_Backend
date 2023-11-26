package ita.univey.domain.payHistory.service;

import ita.univey.domain.payHistory.entity.PayHistory;
import ita.univey.domain.payHistory.repository.JpaPayHistoryRepository;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PayHistoryServiceImpl implements PayHistoryService {
    private final JpaPayHistoryRepository jpaPayHistoryRepository;
    private final UserService userService;

    public PayHistoryServiceImpl(JpaPayHistoryRepository jpaPayHistoryRepository, UserService userService) {
        this.jpaPayHistoryRepository = jpaPayHistoryRepository;
        this.userService = userService;
    }

    @Override
    public Slice<PayHistory> findPayHistory(String userName, Pageable pageable) {
        User user = userService.verifyUser(userName);

        return jpaPayHistoryRepository.findAllByUser(user, pageable);
    }
}