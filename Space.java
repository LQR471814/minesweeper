enum SpaceState {
    COVERED,
    UNCOVERED,
    NUMBER_SHOWN,
    FLAGGED
}

class Space {
    SpaceState state = SpaceState.COVERED;
    boolean isMine;
    int neighboringMineCount;

    @Override
    public String toString() {
        switch (this.state) {
            case SpaceState.COVERED:
                return ".";
            case SpaceState.UNCOVERED:
                if (this.isMine) {
                    return Color.BOLD + Color.RED + "*" + Color.RESET;
                }
                return " ";
            case SpaceState.NUMBER_SHOWN:
                return Color.NUM[this.neighboringMineCount] + this.neighboringMineCount + Color.RESET;
            case SpaceState.FLAGGED:
                return Color.CYAN + ">" + Color.RESET;
            default:
                return "?";
        }
    }
}
