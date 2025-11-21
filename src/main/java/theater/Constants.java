package theater;

/**
 * Constants used in invoice calculation.
 */
public final class Constants {
    public static final int BASE_TRAGEDY = 40000;
    public static final int BASE_COMEDY = 30000;
    public static final int TRAGEDY_EXTRA_PER_PERSON = 1000;
    public static final int COMEDY_EXTRA_PER_PERSON = 1000;
    public static final int COMEDY_BASE_EXTRA = 300;
    public static final int AUDIENCE_THRESHOLD = 30;
    public static final int COMEDY_CREDITS_DIVISOR = 5;
    public static final int PERCENT_FACTOR = 100;

    private Constants() {
        // prevent instantiation
    }
}

