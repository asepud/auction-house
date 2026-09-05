package com.asdin.test_rest.controller;

import com.asdin.test_rest.dto.report.*;
import com.asdin.test_rest.exception.BusinessException;
import com.asdin.test_rest.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/** Administrative reporting endpoints backed by native SQL. */
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reports;

    public ReportController(ReportService reports) {
        this.reports = reports;
    }

    @GetMapping("/leaderboard")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LeaderboardRow> leaderboard() {
        return reports.leaderboard();
    }

    @GetMapping("/items/top")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TopItemRow> topItems() {
        return reports.topItems();
    }

    @GetMapping("/sellers/{sellerId}/summary")
    public SellerSummary seller(@PathVariable Long sellerId, Authentication a) {
        boolean admin = a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !Long.valueOf(a.getName()).equals(sellerId))
            throw new BusinessException(HttpStatus.FORBIDDEN, "Only seller owner or admin may view this report");
        return reports.sellerSummary(sellerId);
    }
}
