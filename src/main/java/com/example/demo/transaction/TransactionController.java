
package com.example.demo.transaction;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return service.getAllTransactions();
    }

    @GetMapping("/valid")
    public List<Transaction> getValidTransactions() {
        return service.getValidTransactions();
    }

    @GetMapping("/invalid")
    public List<Transaction> getInvalidTransactions() {
        return service.getInvalidTransactions();
    }

    @GetMapping("/revenue")
    public BigDecimal getTotalRevenue() {
        return service.calculateTotalRevenue();
    }

    @GetMapping("/category-revenue")
    public Map<String, BigDecimal> getRevenueByCategory() {
        return service.revenueByCategory();
    }


    @GetMapping("/daily-revenue")
    public Map<LocalDate, BigDecimal> getDailyRevenue() {
        return service.revenueByDay();
    }


    @GetMapping("/converted-revenue")
    public BigDecimal getConvertedRevenue() {
        return service.calculateConvertedRevenue(
                new BigDecimal("0.01"),
                new BigDecimal("0.18")
        );
    }


    @GetMapping("/category-quantities")
    public Map<String, Integer> getCategoryQuantities() {
        return service.quantitySoldByCategory();
    }

    @GetMapping("/top-category")
    public String getTopSellingCategory() {
        return service.getTopSellingCategory();
    }


}
