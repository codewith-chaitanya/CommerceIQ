
package com.example.demo.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @InjectMocks
    private TransactionService service;

    private Transaction createTransaction(
            String orderId,
            String productName,
            String category,
            int quantity,
            double price,
            int day) {

        Transaction transaction = new Transaction();
        transaction.setOrderId(orderId);
        transaction.setProductName(productName);
        transaction.setCategory(category);
        transaction.setQuantity(quantity);
        transaction.setUnitPrice(price);
        transaction.setCurrency("INR");
        transaction.setTransactionTime(
                LocalDateTime.of(2026, 10, day, 12, 0));
        transaction.setValid(true);

        return transaction;
    }

    private List<Transaction> sampleTransactions() {
        return List.of(
                createTransaction("ORD101", "Laptop",
                        "Electronics", 1, 55000, 10),
                createTransaction("ORD102", "Headphones",
                        "Electronics", 2, 1500, 10),
                createTransaction("ORD103", "Chair",
                        "Furniture", 1, 7500, 11),
                createTransaction("ORD104", "Notebook",
                        "Stationery", 5, 100, 11)
        );
    }

    @Test
    void shouldCalculateTotalRevenue() {
        when(repository.findAll())
                .thenReturn(sampleTransactions());

        assertThat(service.calculateTotalRevenue())
                .isEqualByComparingTo("66000.0");
    }

    @Test
    void shouldCalculateRevenueByCategory() {
        when(repository.findAll())
                .thenReturn(sampleTransactions());

        Map<String, BigDecimal> result =
                service.revenueByCategory();

        assertThat(result.get("Electronics"))
                .isEqualByComparingTo("58000.0");

        assertThat(result.get("Furniture"))
                .isEqualByComparingTo("7500.0");

        assertThat(result.get("Stationery"))
                .isEqualByComparingTo("500.0");
    }

    @Test
    void shouldCalculateRevenueByDay() {
        when(repository.findAll())
                .thenReturn(sampleTransactions());

        Map<LocalDate, BigDecimal> result =
                service.revenueByDay();

        assertThat(result.get(LocalDate.of(2026, 10, 10)))
                .isEqualByComparingTo("58000.0");

        assertThat(result.get(LocalDate.of(2026, 10, 11)))
                .isEqualByComparingTo("8000.0");
    }

    @Test
    void shouldFindTopSellingCategory() {
        when(repository.findAll())
                .thenReturn(sampleTransactions());

        assertThat(service.getTopSellingCategory())
                .isEqualTo("Stationery");
    }


    @Test
    void shouldCalculateConvertedRevenueWithTax() {
        when(repository.findAll()).thenReturn(sampleTransactions());

        BigDecimal result = service.calculateConvertedRevenue(
                new BigDecimal("0.01"),
                new BigDecimal("0.18")
        );

        assertThat(result).isEqualByComparingTo("778.8000");
    }

}
