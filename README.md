# CommerceIQ: E-Commerce Log Analyzer

CommerceIQ is a web-based e-commerce transaction log analyzer built using Java functional programming. It processes transaction records, filters invalid logs, and summarizes sales data through an interactive dashboard.

## Features

- **Transaction Analysis:** View all, valid, and invalid transaction records.
- **Functional Programming:** Uses Java Stream API, `filter()`, `map()`, and `reduce()` for data processing.
- **Revenue Analytics:** Calculate total valid revenue and revenue by category.
- **Sales Insights:** Identify the top-selling product category.
- **Daily Revenue:** Visualize revenue trends over time.
- **Interactive Dashboard:** Display key metrics and charts.
- **Authentication:** User registration, login, and logout.
- **Automated Tests:** Unit tests for revenue calculations and category analytics.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA and Hibernate
- PostgreSQL
- Java Stream API
- HTML, CSS, and JavaScript
- Chart.js
- Maven
- JUnit 5 and Mockito

## Sample Results

The application includes sample transaction records for demonstration.

| Metric | Expected result |
|---|---:|
| Total transactions | 5 |
| Valid transactions | 4 |
| Invalid transactions | 1 |
| Total valid revenue | ₹66,000 |
| Top-selling category by quantity | Stationery |

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL
- IntelliJ IDEA or another Java IDE

### Setup

1. Clone or download this repository.
2. Create a PostgreSQL database named `ecommerce_analyzer`.
3. Configure the database username and password in your environment.
4. Set the `DB_PASSWORD` environment variable to your PostgreSQL password.
5. Open the project and run `DemoApplication.java`.
6. Visit `http://localhost:8080`.
7. Register an account and sign in to access the dashboard.

### Run Tests

Run the following command in the project directory:

**Windows:**
```powershell
.\mvnw.cmd test
```

## Functional Programming Concepts

- **Filtering:** Separate valid transactions from invalid records.
- **Mapping:** Transform transaction data for calculations.
- **Reduction:** Aggregate transaction values into total revenue.
- **Grouping:** Summarize revenue and quantities by category.
- **Immutability:** Use stream operations that produce results without directly modifying the source collection.

## Future Improvements

- Import transaction logs from CSV files.
- Add configurable currency conversion and tax rates.
- Support larger datasets and additional analytics.
- Add pagination and advanced transaction filtering.

## Author

Developed as a functional programming and software engineering project.