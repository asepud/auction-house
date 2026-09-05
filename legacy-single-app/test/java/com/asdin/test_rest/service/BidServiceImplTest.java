package com.asdin.test_rest.service;

import com.asdin.test_rest.domain.*;
import com.asdin.test_rest.dto.auction.BidRequest;
import com.asdin.test_rest.enums.*;
import com.asdin.test_rest.exception.BusinessException;
import com.asdin.test_rest.repository.*;
import com.asdin.test_rest.service.impl.BidServiceImpl;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Unit test for the minimum-increment rule without a database. */
class BidServiceImplTest {
    @Test
    void rejectsBidBelowFivePercentIncrement() {
        AuctionItemRepository items = mock(AuctionItemRepository.class);
        UserRepository users = mock(UserRepository.class);
        BidRepository bids = mock(BidRepository.class);
        NotificationService notifications = mock(NotificationService.class);
        AuctionItem item = AuctionItem.builder().id(8L).seller(User.builder().id(1L).build())
                .currentHighestBid(new BigDecimal("100.00")).status(AuctionStatus.ONGOING)
                .startTime(Instant.now().minusSeconds(60)).endTime(Instant.now().plusSeconds(60)).build();
        when(items.findByIdForUpdate(8L)).thenReturn(Optional.of(item));
        BidService service = new BidServiceImpl(items, users, bids, notifications);
        BusinessException error = assertThrows(BusinessException.class,
                () -> service.submit(8L, new BidRequest(new BigDecimal("104.99")), 2L));
        assertEquals("Bid must be at least 105.00", error.getMessage());
        verifyNoInteractions(users, bids, notifications);
    }
}
