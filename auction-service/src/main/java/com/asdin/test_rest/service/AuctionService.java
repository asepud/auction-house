package com.asdin.test_rest.service;

import com.asdin.test_rest.dto.auction.*;
import com.asdin.test_rest.enums.AuctionStatus;
import java.util.*;

/** Listing management and closure use cases. */
public interface AuctionService {
    ItemResponse create(ItemRequest request, Long sellerId);

    ItemResponse update(Long id, ItemUpdateRequest request, Long userId);

    void delete(Long id, Long userId);

    List<ItemResponse> list(AuctionStatus status, String category);

    ItemResponse detail(Long id);

    ItemResponse close(Long id);

    void closeExpired();
}
