package com.asdin.test_rest.service.impl;

import com.asdin.test_rest.domain.*;
import com.asdin.test_rest.dto.auction.*;
import com.asdin.test_rest.enums.*;
import com.asdin.test_rest.exception.BusinessException;
import com.asdin.test_rest.integration.NotificationClient;
import com.asdin.test_rest.integration.PaymentClient;
import com.asdin.test_rest.repository.*;
import com.asdin.test_rest.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;

/**
 * Coordinates listing lifecycle and invokes payment/notification ports on
 * closure.
 */
@Service
public class AuctionServiceImpl implements AuctionService {
    private final AuctionItemRepository items;
    private final CategoryRepository categories;
    private final UserRepository users;
    private final BidRepository bids;
    private final PaymentClient payments;
    private final NotificationClient notifications;

    public AuctionServiceImpl(AuctionItemRepository items, CategoryRepository categories, UserRepository users,
            BidRepository bids, PaymentClient payments, NotificationClient notifications) {
        this.items = items;
        this.categories = categories;
        this.users = users;
        this.bids = bids;
        this.payments = payments;
        this.notifications = notifications;
    }

    @Transactional
    public ItemResponse create(ItemRequest r, Long sellerId) {
        validatePeriod(r.getStartTime(), r.getEndTime());
        Category c = category(r.getCategoryId());
        User seller = user(sellerId);
        AuctionStatus status = r.getStartTime().isAfter(Instant.now()) ? AuctionStatus.SCHEDULED
                : AuctionStatus.ONGOING;
        AuctionItem i = items.save(AuctionItem.builder().title(r.getTitle().trim())
                .description(r.getDescription().trim()).category(c).seller(seller).startingPrice(r.getStartingPrice())
                .currentHighestBid(r.getStartingPrice()).startTime(r.getStartTime()).endTime(r.getEndTime())
                .status(status).imageUrl(r.getImageUrl()).build());
        return map(i, false);
    }

    @Transactional
    public ItemResponse update(Long id, ItemUpdateRequest r, Long userId) {
        validatePeriod(r.getStartTime(), r.getEndTime());
        AuctionItem i = item(id);
        owner(i, userId);
        if (i.getStatus() != AuctionStatus.DRAFT && i.getStatus() != AuctionStatus.SCHEDULED)
            throw new BusinessException(HttpStatus.CONFLICT, "Only draft or scheduled items may be edited");
        i.setTitle(r.getTitle().trim());
        i.setDescription(r.getDescription().trim());
        i.setCategory(category(r.getCategoryId()));
        i.setStartTime(r.getStartTime());
        i.setEndTime(r.getEndTime());
        i.setImageUrl(r.getImageUrl());
        i.setStatus(r.getStartTime().isAfter(Instant.now()) ? AuctionStatus.SCHEDULED : AuctionStatus.ONGOING);
        return map(i, false);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        AuctionItem i = item(id);
        owner(i, userId);
        if (i.getStatus() != AuctionStatus.DRAFT && i.getStatus() != AuctionStatus.SCHEDULED)
            throw new BusinessException(HttpStatus.CONFLICT, "Only draft or scheduled items may be deleted");
        items.delete(i);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> list(AuctionStatus status, String category) {
        return items.findAll().stream().filter(i -> status == null || i.getStatus() == status)
                .filter(i -> category == null || i.getCategory().getName().equalsIgnoreCase(category))
                .sorted(Comparator.comparing(AuctionItem::getEndTime)).map(i -> map(i, false)).toList();
    }

    @Transactional(readOnly = true)
    public ItemResponse detail(Long id) {
        return map(item(id), true);
    }

    @Transactional
    public ItemResponse close(Long id) {
        return closeItem(items.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Item not found")));
    }

    @Transactional
    public void closeExpired() {
        Instant now = Instant.now();
        items.findByStatus(AuctionStatus.SCHEDULED).stream().filter(i -> !i.getStartTime().isAfter(now))
                .forEach(i -> i.setStatus(AuctionStatus.ONGOING));
        items.findByStatusAndEndTimeBefore(AuctionStatus.ONGOING, now).forEach(this::closeItem);
    }

    private ItemResponse closeItem(AuctionItem i) {
        if (i.getStatus() != AuctionStatus.ONGOING && i.getStatus() != AuctionStatus.SCHEDULED)
            return map(i, true);
        var highest = bids.findFirstByItemIdOrderByAmountDescCreatedAtAsc(i.getId());
        if (highest.isEmpty()) {
            i.setStatus(AuctionStatus.ENDED);
            notifications.ended(i.getSeller().getId(),
                    "Auction '" + i.getTitle() + "' ended without bids.");
        } else {
            Bid b = highest.get();
            i.setWinner(b.getBidder());
            i.setCurrentHighestBid(b.getAmount());
            i.setStatus(AuctionStatus.SOLD);
            payments.createInvoice(i.getId(), b.getBidder().getId(), b.getAmount());
            notifications.winner(b.getBidder().getId(),
                    "You won auction '" + i.getTitle() + "'.");
            notifications.ended(i.getSeller().getId(),
                    "Auction '" + i.getTitle() + "' was sold.");
        }
        return map(i, true);
    }

    private Category category(Long id) {
        return categories.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private User user(Long id) {
        return users.findById(id).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private AuctionItem item(Long id) {
        return items.findById(id).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Item not found"));
    }

    private void owner(AuctionItem i, Long id) {
        if (!i.getSeller().getId().equals(id))
            throw new BusinessException(HttpStatus.FORBIDDEN, "Only item seller may modify this item");
    }

    private void validatePeriod(Instant s, Instant e) {
        if (!e.isAfter(s))
            throw new BusinessException(HttpStatus.BAD_REQUEST, "endTime must be after startTime");
    }

    private ItemResponse map(AuctionItem i, boolean withBids) {
        return ItemResponse.builder().id(i.getId()).title(i.getTitle()).description(i.getDescription())
                .categoryId(i.getCategory().getId()).categoryName(i.getCategory().getName())
                .sellerId(i.getSeller().getId()).sellerName(i.getSeller().getName()).startingPrice(i.getStartingPrice())
                .currentHighestBid(i.getCurrentHighestBid()).startTime(i.getStartTime()).endTime(i.getEndTime())
                .status(i.getStatus()).imageUrl(i.getImageUrl())
                .winnerId(i.getWinner() == null ? null : i.getWinner().getId())
                .bids(withBids ? bids.findByItemIdOrderByAmountDescCreatedAtAsc(i.getId()).stream()
                        .map(b -> BidResponse.builder().id(b.getId()).bidderId(b.getBidder().getId())
                                .bidderName(b.getBidder().getName()).amount(b.getAmount()).createdAt(b.getCreatedAt())
                                .build())
                        .toList() : null)
                .build();
    }
}
