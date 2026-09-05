package com.asdin.test_rest.service.impl;

import com.asdin.test_rest.domain.*;
import com.asdin.test_rest.dto.auction.*;
import com.asdin.test_rest.enums.*;
import com.asdin.test_rest.exception.BusinessException;
import com.asdin.test_rest.integration.NotificationClient;
import com.asdin.test_rest.repository.*;
import com.asdin.test_rest.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.Instant;
import java.util.*;

/** Serializes concurrent bids by locking the item row before validation. */
@Service
public class BidServiceImpl implements BidService {
    private static final BigDecimal MIN_INCREMENT_RATE = new BigDecimal("0.05");
    private final AuctionItemRepository items;
    private final UserRepository users;
    private final BidRepository bids;
    private final NotificationClient notifications;

    public BidServiceImpl(AuctionItemRepository items, UserRepository users, BidRepository bids,
            NotificationClient notifications) {
        this.items = items;
        this.users = users;
        this.bids = bids;
        this.notifications = notifications;
    }

    @Transactional
    public BidResponse submit(Long itemId, BidRequest r, Long bidderId) {
        AuctionItem i = items.findByIdForUpdate(itemId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Item not found"));
        if (i.getStatus() != AuctionStatus.ONGOING || !i.getStartTime().isBefore(Instant.now())
                || !i.getEndTime().isAfter(Instant.now()))
            throw new BusinessException(HttpStatus.CONFLICT, "Auction is not accepting bids");
        if (i.getSeller().getId().equals(bidderId))
            throw new BusinessException(HttpStatus.FORBIDDEN, "Seller cannot bid on own item");
        BigDecimal minimum = i.getCurrentHighestBid().multiply(BigDecimal.ONE.add(MIN_INCREMENT_RATE)).setScale(2,
                RoundingMode.HALF_UP);
        if (r.getAmount().compareTo(minimum) < 0)
            throw new BusinessException(HttpStatus.CONFLICT, "Bid must be at least " + minimum);
        User bidder = users.findById(bidderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
        Long previous = bids.findFirstByItemIdOrderByAmountDescCreatedAtAsc(itemId).map(b -> b.getBidder().getId())
                .orElse(null);
        Bid bid = bids
                .save(Bid.builder().item(i).bidder(bidder).amount(r.getAmount()).createdAt(Instant.now()).build());
        i.setCurrentHighestBid(r.getAmount());
        if (previous != null && !previous.equals(bidderId))
            notifications.outbid(previous, "You were outbid on '" + i.getTitle() + "'.");
        return map(bid);
    }

    @Transactional(readOnly = true)
    public List<BidResponse> history(Long id) {
        return bids.findByItemIdOrderByAmountDescCreatedAtAsc(id).stream().map(this::map).toList();
    }

    private BidResponse map(Bid b) {
        return BidResponse.builder().id(b.getId()).bidderId(b.getBidder().getId()).bidderName(b.getBidder().getName())
                .amount(b.getAmount()).createdAt(b.getCreatedAt()).build();
    }
}
