package com.asdin.test_rest.service.impl;

import com.asdin.test_rest.dto.report.*;
import com.asdin.test_rest.repository.ReportRepository;
import com.asdin.test_rest.service.ReportService;
import org.springframework.stereotype.Service;
import java.math.*;
import java.util.*;

/** Maps vendor-neutral native SQL result tuples into report DTOs. */
@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reports;

    public ReportServiceImpl(ReportRepository reports) {
        this.reports = reports;
    }

    public List<LeaderboardRow> leaderboard() {
        return reports.leaderboard().stream().map(r -> new LeaderboardRow(number(r[0]).longValue(),
                String.valueOf(r[1]), number(r[2]).longValue(), decimal(r[3]), number(r[4]).intValue())).toList();
    }

    public SellerSummary sellerSummary(Long id) {
        return reports.sellerSummary(id).stream().findFirst()
                .map(r -> new SellerSummary(number(r[0]).longValue(), number(r[1]).longValue(), decimal(r[2])))
                .orElse(new SellerSummary(id, 0L, BigDecimal.ZERO));
    }

    public List<TopItemRow> topItems() {
        return reports.topItems().stream().map(r -> new TopItemRow(number(r[0]).longValue(), String.valueOf(r[1]),
                number(r[2]).longValue(), decimal(r[3]))).toList();
    }

    private Number number(Object v) {
        return (Number) v;
    }

    private BigDecimal decimal(Object v) {
        return v instanceof BigDecimal b ? b : new BigDecimal(v.toString());
    }
}
