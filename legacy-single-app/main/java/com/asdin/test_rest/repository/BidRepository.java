package com.asdin.test_rest.repository;

import com.asdin.test_rest.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByItemIdOrderByAmountDescCreatedAtAsc(Long itemId);

    Optional<Bid> findFirstByItemIdOrderByAmountDescCreatedAtAsc(Long itemId);
}
