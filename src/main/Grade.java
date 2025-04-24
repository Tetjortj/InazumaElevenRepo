package main;

public enum Grade {
    FIRST_YEAR,
    SECOND_YEAR,
    THIRD_YEAR,
    ADULT,
    UNKNOWN;

    public String toStringCorrect() {
        return switch (this) {
            case FIRST_YEAR -> "1st";
            case SECOND_YEAR -> "2nd";
            case THIRD_YEAR -> "3rd";
            case ADULT -> "Adult";
            case UNKNOWN -> "Unknown";
        };
    }
}