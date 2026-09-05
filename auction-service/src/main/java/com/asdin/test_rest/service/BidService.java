package com.asdin.test_rest.service;

import com.asdin.test_rest.dto.auction.*;
import java.util.*;

/** Bidding use cases. */
public interface BidService {
    BidResponse submit(Long itemId, BidRequest request, Long bidderId);

    List<BidResponse> history(Long itemId);
}
