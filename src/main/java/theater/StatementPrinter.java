package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Generates plain-text invoice statements for theater customers.
 * Calculates total amount owed and volume credits based on performances and play types.
 */
public class StatementPrinter {

    /**
     * Produces a plain-text statement for the given invoice and play catalog.
     *
     * @param invoice the customer's invoice containing performances
     * @param plays   catalog of all plays with their details
     * @return formatted plain-text statement
     * @throws IllegalArgumentException if an unknown play type is encountered
     */
    public String statement(Invoice invoice, Map<String, Play> plays) {
        final var result = new StringBuilder();
        result.append(String.format("Statement for %s\n", invoice.getCustomer()));

        final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

        int totalAmount = 0;
        int volumeCredits = 0;

        for (final var perf : invoice.getPerformances()) {
            final Play play = plays.get(perf.getPlayID());
            int thisAmount = 0;

            switch (play.getType()) {
                case "tragedy":
                    thisAmount = Constants.BASE_TRAGEDY;
                    if (perf.getAudience() > Constants.AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.TRAGEDY_EXTRA_PER_PERSON
                                * (perf.getAudience() - Constants.AUDIENCE_THRESHOLD);
                    }
                    break;
                case "comedy":
                    thisAmount = Constants.BASE_COMEDY;
                    if (perf.getAudience() > Constants.AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.COMEDY_EXTRA_PER_PERSON
                                * (perf.getAudience() - Constants.AUDIENCE_THRESHOLD);
                    }
                    thisAmount += Constants.COMEDY_BASE_EXTRA * perf.getAudience();
                    break;
                default:
                    throw new IllegalArgumentException("unknown type: " + play.getType());
            }

            // add volume credits
            volumeCredits += Math.max(perf.getAudience() - Constants.AUDIENCE_THRESHOLD, 0);
            if ("comedy".equals(play.getType())) {
                volumeCredits += Math.floor(perf.getAudience() / Constants.COMEDY_CREDITS_DIVISOR);
            }

            // print line for this order
            result.append(String.format("  %s: %s (%d seats)\n",
                    play.getName(),
                    frmt.format(thisAmount / Constants.PERCENT_FACTOR),
                    perf.getAudience()));
            totalAmount += thisAmount;
        }

        result.append(String.format("Amount owed is %s\n",
                frmt.format(totalAmount / Constants.PERCENT_FACTOR)));
        result.append(String.format("You earned %d credits\n", volumeCredits));
        return result.toString();
    }
}
