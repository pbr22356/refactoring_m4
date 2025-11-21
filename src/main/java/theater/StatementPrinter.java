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
        return frmt.format(amount / 100.0);
    }

    private int getVolumeCredits(Performance perf) {
        int result = Math.max(perf.getAudience() - Constants.AUDIENCE_THRESHOLD, 0);
        if ("comedy".equals(getPlay(perf).getType())) {
            result += perf.getAudience() / Constants.COMEDY_CREDITS_DIVISOR;
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
                result = 40000;
                if (perf.getAudience() > 30) {
                    result += 1000 * (perf.getAudience() - 30);
                }
                break;
            case "comedy":
                result = 30000;
                if (perf.getAudience() > 20) {
                    result += 10000 + 500 * (perf.getAudience() - 20);
                }
                result += 300 * perf.getAudience();
                break;
            default:
                throw new IllegalArgumentException("unknown type: " + play.getType());
        }
        return result;
    }
}
