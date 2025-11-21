package theater;

import java.util.List;

/**
 * Represents a customer's invoice containing one or more performances.
 */
public class Invoice {

    private final String customer;
    private final List<Performance> performances;

    public Invoice(String customer, List<Performance> performances) {
        this.customer = customer;
        this.performances = performances;
    }

    public String getCustomer() {
        return customer;
    }

    public List<Performance> getPerformances() {
        return performances;
    }
}

