public enum Directions {
    NORTH(1),
    EAST(2),
    SOUTH(3),
    WEST(4);

    private final int VALUE;

    Directions(int value) {
        VALUE = value;
    }

    public int getValue() {
        return VALUE;
    }
}
