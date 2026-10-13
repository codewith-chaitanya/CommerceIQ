
package com.example.demo.transaction;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TransactionDataLoader implements CommandLineRunner {

    private final TransactionRepository repository;

    public TransactionDataLoader(TransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        Transaction t1 = create("ORD101", "Laptop", "Electronics", 1, 55000, true);
        Transaction t2 = create("ORD102", "Headphones", "Electronics", 2, 1500, true);
        Transaction t3 = create("ORD103", "Office Chair", "Furniture", 1, 7500, true);
        Transaction t4 = create("ORD104", "Notebook", "Stationery", 5, 100, true);
        Transaction t5 = create("ORD105", "Damaged Log", "Electronics", 1, 2000, false);

        repository.saveAll(List.of(t1, t2, t3, t4, t5));
    }

    private Transaction create(
            String orderId,
            String productName,
            String category,
            int quantity,
            double unitPrice,
            boolean valid) {

        Transaction transaction = new Transaction();
        transaction.setOrderId(orderId);
        transaction.setProductName(productName);
        transaction.setCategory(category);
        transaction.setQuantity(quantity);
        transaction.setUnitPrice(unitPrice);
        transaction.setCurrency("INR");
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setValid(valid);

        return transaction;
    }
}
