const state = {
    transactions: [],
    categoryChart: null,
    qualityChart: null,
    dailyRevenueChart: null
};

const money = value => new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2
}).format(Number(value) || 0);


async function fetchJson(url) {
    const response = await fetch(url, {
        credentials: "same-origin"
    });

    const contentType = response.headers.get("content-type") || "";

    if (!response.ok) {
        throw new Error(
            `Request failed (${response.status}): ${url}`
        );
    }

    if (!contentType.includes("application/json")) {
        throw new Error(
            `Expected JSON but received ${contentType || "unknown content type"} from ${url}. Check authentication or redirects.`
        );
    }

    return response.json();
}


function renderTransactions() {
    const filter = document.getElementById("filterSelect").value;

    const rows = state.transactions.filter(transaction => {
        if (filter === "valid") return transaction.valid;
        if (filter === "invalid") return !transaction.valid;
        return true;
    });

    const tbody = document.getElementById("transactionRows");

    if (rows.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6">No transactions found.</td></tr>';
        return;
    }

    tbody.innerHTML = rows.map(transaction => `
        <tr>
            <td>${escapeHtml(transaction.orderId)}</td>
            <td>${escapeHtml(transaction.productName)}</td>
            <td>${escapeHtml(transaction.category)}</td>
            <td>${Number(transaction.quantity)}</td>
            <td>${money(transaction.unitPrice)}</td>
            <td>
                <span class="badge ${transaction.valid ? "valid" : "invalid"}">
                    ${transaction.valid ? "Valid" : "Invalid"}
                </span>
            </td>
        </tr>
    `).join("");
}

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, char => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[char]);
}

function renderCharts(validCount, invalidCount, categoryRevenue) {
    if (typeof Chart === "undefined") {
        throw new Error("Chart.js could not load. Check your internet connection.");
    }

    if (state.categoryChart) state.categoryChart.destroy();
    if (state.qualityChart) state.qualityChart.destroy();

    state.categoryChart = new Chart(document.getElementById("categoryChart"), {
        type: "bar",
        data: {
            labels: Object.keys(categoryRevenue),
            datasets: [{
                label: "Revenue (₹)",
                data: Object.values(categoryRevenue).map(Number),
                backgroundColor: "#73e0c1",
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                x: {
                    ticks: { color: "#a7b2c7" },
                    grid: { display: false }
                },
                y: {
                    beginAtZero: true,
                    ticks: { color: "#a7b2c7" },
                    grid: { color: "#253149" }
                }
            }
        }
    });

    state.qualityChart = new Chart(document.getElementById("qualityChart"), {
        type: "doughnut",
        data: {
            labels: ["Valid", "Invalid"],
            datasets: [{
                data: [validCount, invalidCount],
                backgroundColor: ["#73e0c1", "#ff8292"],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: "bottom",
                    labels: { color: "#cbd5e1", padding: 20 }
                }
            }
        }
    });
}


function renderDailyRevenue(dailyRevenue) {
    if (typeof Chart === "undefined") {
        throw new Error("Chart.js could not load.");
    }

    if (state.dailyRevenueChart) {
        state.dailyRevenueChart.destroy();
    }

    const entries = Object.entries(dailyRevenue)
        .sort(([dateA], [dateB]) => dateA.localeCompare(dateB));

    state.dailyRevenueChart = new Chart(
        document.getElementById("dailyRevenueChart"),
        {
            type: "line",
            data: {
                labels: entries.map(([date]) => date),
                datasets: [{
                    label: "Revenue (₹)",
                    data: entries.map(([, revenue]) => Number(revenue)),
                    borderColor: "#73e0c1",
                    backgroundColor: "rgba(115, 224, 193, 0.12)",
                    fill: true,
                    tension: 0.3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        labels: { color: "#cbd5e1" }
                    }
                },
                scales: {
                    x: {
                        ticks: { color: "#a7b2c7" },
                        grid: { display: false }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: { color: "#a7b2c7" },
                        grid: { color: "#253149" }
                    }
                }
            }
        }
    );
}


async function loadDashboard() {
    const message = document.getElementById("message");
    message.textContent = "Loading latest data...";

    try {
        const [
            transactions,
            valid,
            invalid,
            revenue,
            categoryRevenue,
            categoryQuantities,
            topCategory,
            dailyRevenue
        ] = await Promise.all([
            fetchJson("/api/transactions"),
            fetchJson("/api/transactions/valid"),
            fetchJson("/api/transactions/invalid"),
            fetchJson("/api/transactions/revenue"),
            fetchJson("/api/transactions/category-revenue"),
            fetchJson("/api/transactions/category-quantities"),
            fetch("/api/transactions/top-category").then(response => {
                if (!response.ok) {
                    throw new Error("Failed to load top category");
                }
                return response.text();
            }),
            fetchJson("/api/transactions/daily-revenue")
        ]);

        const topQuantity = categoryQuantities[topCategory] ?? 0;

        document.getElementById("topCategory").textContent = topCategory;
        document.getElementById("topCategoryQuantity").textContent =
            `Items sold: ${topQuantity}`;

        renderDailyRevenue(dailyRevenue);

        state.transactions = transactions;

        document.getElementById("totalTransactions").textContent = transactions.length;
        document.getElementById("validTransactions").textContent = valid.length;
        document.getElementById("invalidTransactions").textContent = invalid.length;
        document.getElementById("totalRevenue").textContent = money(revenue);

        renderTransactions();
        renderCharts(valid.length, invalid.length, categoryRevenue);
        message.textContent = "Data loaded successfully from your backend.";
    } catch (error) {
        console.error("Dashboard loading failed:", error);
        message.textContent = `Dashboard error: ${error.message}`;
    }
}

document.getElementById("refreshBtn").addEventListener("click", loadDashboard);
document.getElementById("filterSelect").addEventListener("change", renderTransactions);

loadDashboard();




const logoutButton = document.getElementById("logoutButton");

if (logoutButton) {
    logoutButton.addEventListener("click", async () => {
        logoutButton.disabled = true;

        try {
            const csrfResponse = await fetch("/api/auth/csrf", {
                credentials: "same-origin"
            });

            if (!csrfResponse.ok) {
                throw new Error("Unable to initialize secure logout.");
            }

            const csrf = await csrfResponse.json();

            const response = await fetch("/logout", {
                method: "POST",
                credentials: "same-origin",
                headers: {
                    [csrf.headerName]: csrf.token
                },
                redirect: "manual"
            });

            if (response.status === 403) {
                throw new Error("Security token expired. Refresh and try again.");
            }

            window.location.replace("/login.html");
        } catch (error) {
            alert(error.message || "Logout failed. Please try again.");
            logoutButton.disabled = false;
        }
    });
}
