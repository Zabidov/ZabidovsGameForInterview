package enums;

public enum ChooseType {
    ROCK,
    SCISSORS,
    PAPER;

    public static ChooseType find(String value) {
        for (ChooseType choose : ChooseType.values()) {
            if (choose.name().equalsIgnoreCase(value)) {
                return choose;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
