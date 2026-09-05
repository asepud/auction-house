package com.asdin.test_rest.service;

import com.asdin.test_rest.dto.report.*;
import java.util.*;

/** Reporting use cases backed by native SQL. */
public interface ReportService {
    List<LeaderboardRow> leaderboard();

    SellerSummary sellerSummary(Long sellerId);

    List<TopItemRow> topItems();
}
