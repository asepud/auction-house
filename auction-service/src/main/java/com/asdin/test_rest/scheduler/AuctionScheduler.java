package com.asdin.test_rest.scheduler;

import com.asdin.test_rest.service.AuctionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Activates scheduled auctions and closes expired auctions every ten seconds.
 */
@Component
public class AuctionScheduler {
    private final AuctionService auctions;

    public AuctionScheduler(AuctionService auctions) {
        this.auctions = auctions;
    }

    @Scheduled(fixedDelay = 10000)
    public void processExpiredAuctions() {
        auctions.closeExpired();
    }
}
