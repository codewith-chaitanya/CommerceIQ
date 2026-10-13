
package com.example.demo.transaction;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.util.TreeMap;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }


    public List<Transaction> getValidTransactions() {
        return repository.findAll()
                .stream()
                .filter(Transaction::isValid)
                .toList();
    }

    public List<Transaction> getInvalidTransactions() {
        return repository.findAll()
                .stream()
                .filter(transaction -> !transaction.isValid())
                .toList();
    }


    public BigDecimal calculateTotalRevenue() {
        return getValidTransactions().stream()
                .map(transaction -> BigDecimal.valueOf(transaction.getUnitPrice())
                        .multiply(BigDecimal.valueOf(transaction.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, BigDecimal> revenueByCategory() {
        return getValidTransactions().stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                transaction -> BigDecimal.valueOf(transaction.getUnitPrice())
                                        .multiply(BigDecimal.valueOf(transaction.getQuantity())),
                                BigDecimal::add
                        )
                ));
    }


    public Map<LocalDate, BigDecimal> revenueByDay() {
        return getValidTransactions().stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionTime().toLocalDate(),
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                transaction -> BigDecimal.valueOf(transaction.getUnitPrice())
                                        .multiply(BigDecimal.valueOf(transaction.getQuantity())),
                                BigDecimal::add
                        )
                ));
    }


    public Map<String, Integer> quantitySoldByCategory() {
        return getValidTransactions().stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingInt(Transaction::getQuantity)
                ));
    }

    public String getTopSellingCategory() {
        return quantitySoldByCategory().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No data");
    }



    public BigDecimal calculateConvertedRevenue(
            BigDecimal exchangeRate,
            BigDecimal taxRate) {

        return getValidTransactions().stream()
                .map(transaction ->
                        BigDecimal.valueOf(transaction.getUnitPrice())
                                .multiply(BigDecimal.valueOf(transaction.getQuantity()))
                                .multiply(exchangeRate)
                                .multiply(BigDecimal.ONE.add(taxRate)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
