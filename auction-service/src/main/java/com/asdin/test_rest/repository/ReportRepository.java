package com.asdin.test_rest.repository;

import com.asdin.test_rest.domain.AuctionItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

/** Native reporting SQL kept in the repository layer. */
public interface ReportRepository extends JpaRepository<AuctionItem, Long> {
    @Query(
        value = "select b.bidder_id, u.name, count(b.id), max(b.amount), rank() over (order by count(b.id) desc, max(b.amount) desc) from bids b join users u on u.id=b.bidder_id group by b.bidder_id,u.name order by count(b.id) desc,max(b.amount) desc", 
        nativeQuery = true
    )
    List<Object[]> leaderboard();

    @Query(value = "select i.seller_id, count(i.id), coalesce(sum(i.current_highest_bid),0) from items i where i.seller_id=:sellerId and i.status='SOLD' and i.id in (select distinct item_id from bids) group by i.seller_id", nativeQuery = true)
    List<Object[]> sellerSummary(@Param("sellerId") Long sellerId);

    @Query(value = "select i.id,i.title,count(b.id),i.current_highest_bid from items i join bids b on b.item_id=i.id where i.status='SOLD' group by i.id,i.title,i.current_highest_bid order by count(b.id) desc,i.current_highest_bid desc limit 5", nativeQuery = true)
    List<Object[]> topItems();
}
