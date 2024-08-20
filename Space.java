enum SpaceState {
    COVERED,
    UNCOVERED,
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
                if (this.neighboringMineCount > 0) {
                    return Color.NUM[this.neighboringMineCount] + this.neighboringMineCount + Color.RESET;
                }
                return " ";
            case SpaceState.FLAGGED:
                return Color.CYAN + ">" + Color.RESET;
            default:
                return "?";
        }
    }
}
