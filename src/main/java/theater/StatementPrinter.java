package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Generates plain-text invoice statements for theater customers.
 */
public class StatementPrinter {

    private final Invoice invoice;
    private final Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        final StringBuilder result = new StringBuilder("Statement for " + invoice.getCustomer() + "\n");

        for (Performance perf : invoice.getPerformances()) {
            result.append(String.format("  %s: %s (%d seats)\n",
                    getPlay(perf).getName(),
                    usd(getAmount(perf)),
                    perf.getAudience()));
        }

        result.append("Amount owed is " + usd(getTotalAmount()) + "\n");
        result.append("You earned " + getTotalVolumeCredits() + " credits\n");
        return result.toString();
    }

    private int getTotalAmount() {
        int total = 0;
        for (Performance perf : invoice.getPerformances()) {
            total += getAmount(perf);
        }
        return total;
    }

    private int getTotalVolumeCredits() {
        int total = 0;
        for (Performance perf : invoice.getPerformances()) {
            total += getVolumeCredits(perf);
        }
        return total;
    }

    private String usd(int amount) {
        final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);
        return frmt.format(amount / Constants.PERCENT_FACTOR);
    }

    private int getVolumeCredits(Performance perf) {
        int result = Math.max(perf.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        if ("comedy".equals(getPlay(perf).getType())) {
            result += perf.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    private Play getPlay(Performance perf) {
        return plays.get(perf.getPlayID());
    }

    private int getAmount(Performance perf) {
        int result = 0;
        final Play play = getPlay(perf);

        switch (play.getType()) {
            case "tragedy":
                result = Constants.TRAGEDY_BASE_AMOUNT;
                if (perf.getAudience() > Constants.BASE_VOLUME_CREDIT_THRESHOLD) {
                    result += Constants.HISTORY_OVER_BASE_CAPACITY_PER_PERSON * (perf.getAudience()
                            - Constants.BASE_VOLUME_CREDIT_THRESHOLD);
                }
                break;
            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                if (perf.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT + Constants
                            .COMEDY_OVER_BASE_CAPACITY_PER_PERSON * (perf.getAudience()
                            - Constants.COMEDY_AUDIENCE_THRESHOLD);
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * perf.getAudience();
                break;
            default:
                throw new IllegalArgumentException("unknown type: " + play.getType());
        }
        return result;
    }
}