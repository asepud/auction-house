package com.asdin.test_rest.controller;

import com.asdin.test_rest.dto.auction.*;
import com.asdin.test_rest.enums.AuctionStatus;
import com.asdin.test_rest.service.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/** Auction listing, detail, and bidding HTTP endpoints. */
@RestController @RequestMapping("/api/items")
public class ItemController {
 private final AuctionService auctions;private final BidService bids;
 public ItemController(AuctionService auctions,BidService bids){this.auctions=auctions;this.bids=bids;}
 @GetMapping public List<ItemResponse> list(@RequestParam(required=false) AuctionStatus status,@RequestParam(required=false) String category){return auctions.list(status,category);}
 @GetMapping("/{id}") public ItemResponse detail(@PathVariable Long id){return auctions.detail(id);}
 @PostMapping @PreAuthorize("hasRole('SELLER')") public ResponseEntity<ItemResponse> create(@Valid @RequestBody ItemRequest r,Authentication a){return ResponseEntity.status(HttpStatus.CREATED).body(auctions.create(r,userId(a)));}
 @PutMapping("/{id}") @PreAuthorize("hasRole('SELLER')") public ItemResponse update(@PathVariable Long id,@Valid @RequestBody ItemUpdateRequest r,Authentication a){return auctions.update(id,r,userId(a));}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @PreAuthorize("hasRole('SELLER')") public void delete(@PathVariable Long id,Authentication a){auctions.delete(id,userId(a));}
 @PostMapping("/{id}/bids") @PreAuthorize("hasRole('BIDDER')") public ResponseEntity<BidResponse> bid(@PathVariable Long id,@Valid @RequestBody BidRequest r,Authentication a){return ResponseEntity.status(HttpStatus.CREATED).body(bids.submit(id,r,userId(a)));}
 @GetMapping("/{id}/bids") public List<BidResponse> bids(@PathVariable Long id){return bids.history(id);}
 @PostMapping("/{id}/close") @PreAuthorize("hasRole('ADMIN')") public ItemResponse close(@PathVariable Long id){return auctions.close(id);}
 private Long userId(Authentication a){return Long.valueOf(a.getName());}
}
