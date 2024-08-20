enum SpaceState {
    COVERED,
    UNCOVERED,
    FLAGGED
}

class Space {
    private SpaceState state = SpaceState.COVERED;
    private boolean isMine;
    private int neighboringMineCount;

    SpaceState getState() {
        return this.state;
    }
    void setState(SpaceState state) {
        this.state = state;
    }

    boolean getIsMine() {
        return this.isMine;
    }
    void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    int getNeighboringMineCount() {
        return this.neighboringMineCount;
    }
    void setNeighboringMineCount(int neighboringMineCount) {
        this.neighboringMineCount = neighboringMineCount;
    }

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
