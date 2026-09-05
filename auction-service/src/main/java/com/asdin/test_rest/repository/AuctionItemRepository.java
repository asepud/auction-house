package com.asdin.test_rest.repository;

import com.asdin.test_rest.domain.AuctionItem;
import com.asdin.test_rest.enums.AuctionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface AuctionItemRepository extends JpaRepository<AuctionItem, Long> {
    List<AuctionItem> findByStatus(AuctionStatus status);

    List<AuctionItem> findByStatusAndEndTimeBefore(AuctionStatus status, Instant time);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from AuctionItem i where i.id = :id")
    Optional<AuctionItem> findByIdForUpdate(@Param("id") Long id);
}
