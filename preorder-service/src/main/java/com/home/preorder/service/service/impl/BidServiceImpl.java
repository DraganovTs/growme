package com.home.preorder.service.service.impl;

import com.home.preorder.service.model.entity.Bid;
import com.home.preorder.service.model.enums.BidStatus;
import com.home.preorder.service.repository.BidRepository;
import com.home.preorder.service.service.BidService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private static final int BID_EXPIRATION_DAYS = 7;

    public BidServiceImpl(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    @Scheduled(cron = "0 0 0 ? * MON")
    @Override
    @Transactional
    public void cleanupExpiredBids() {

        LocalDateTime threshold = LocalDateTime.now().minusDays(BID_EXPIRATION_DAYS);

        List<Bid> expiredBids = bidRepository.findAllByStatusAndCreatedAtBefore(
                BidStatus.PENDING,
                threshold
        );
        bidRepository.deleteAll(expiredBids);
    }
}
