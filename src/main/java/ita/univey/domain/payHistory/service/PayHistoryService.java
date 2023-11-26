package ita.univey.domain.payHistory.service;

import ita.univey.domain.payHistory.entity.PayHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PayHistoryService {
    Slice<PayHistory> findPayHistory(String userName, Pageable pageable);
}