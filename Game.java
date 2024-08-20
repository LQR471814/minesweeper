import java.util.Random;

enum GameState {
    INIT, // mines have yet to be initialized
    PLAYING,
    LOST,
    WON
}

class Game {
    private Space[][] spaces;
    private int numberMines;
    private GameState state = GameState.INIT;
    private int flagsUsed;

    GameState getState() {
        return this.state;
    }
    int getFlagsUsed() {
        return this.flagsUsed;
    }

    private void initSpaces() {
        for (int y = 0; y < this.spaces.length; y++) {
            for (int x = 0; x < this.spaces[y].length; x++) {
                this.spaces[y][x] = new Space();
            }
        }
    }

    void startEasy() {
        this.flagsUsed = 0;
        this.spaces = new Space[9][9];
        this.numberMines = 10;
        this.initSpaces();
    }

    void startMedium() {
        this.flagsUsed = 0;
        this.spaces = new Space[16][16];
        this.numberMines = 40;
        this.initSpaces();
    }

    void startHard() {
        this.flagsUsed = 0;
        this.spaces = new Space[16][30];
        this.numberMines = 99;
        this.initSpaces();
    }

    private void addMine(int x, int y) {
        this.spaces[y][x].setIsMine(true);

        if (y-1 >= 0) {
            Space space = this.spaces[y-1][x];
            space.setNeighboringMineCount(space.getNeighboringMineCount() + 1);
        }
        if (y+1 < this.spaces.length) {
            Space space = this.spaces[y+1][x];
            space.setNeighboringMineCount(space.getNeighboringMineCount() + 1);
        }
        if (x-1 >= 0) {
            Space space = this.spaces[y][x-1];
            space.setNeighboringMineCount(space.getNeighboringMineCount() + 1);
        }
        if (x+1 < this.spaces[y].length) {
            Space space = this.spaces[y][x+1];
            space.setNeighboringMineCount(space.getNeighboringMineCount() + 1);
        }

        if (y-1 >= 0 && x-1 >= 0) {
            Space space = this.spaces[y-1][x-1];
            space.setNeighboringMineCount(space.getNeighboringMineCount() + 1);
        }
        if (y-1 >= 0 && x+1 < this.spaces[y].length) {
            Space space = this.spaces[y-1][x+1];
            space.setNeighboringMineCount(space.getNeighboringMineCount() + 1);
        }
        if (y+1 < this.spaces.length && x-1 >= 0) {
            Space space = this.spaces[y+1][x-1];
            space.setNeighboringMineCount(space.getNeighboringMineCount() + 1);
        }
        if (y+1 < this.spaces.length && x+1 < this.spaces[y].length) {
            Space space = this.spaces[y+1][x+1];
            space.setNeighboringMineCount(space.getNeighboringMineCount() + 1);
        }
    }

    private void initMines() {
        Random rand = new Random();

        int generated = 0;
        while (generated < numberMines) {
            int y = rand.nextInt(this.spaces.length);
            int x = rand.nextInt(this.spaces[y].length);

            Space existing = this.spaces[y][x];
            if (existing.getIsMine() || existing.getState() == SpaceState.UNCOVERED) {
                continue;
            }

            this.addMine(x, y);
            generated++;
        }
    }

    private boolean hasWon() {
        for (int y = 0; y < this.spaces.length; y++) {
            for (int x = 0; x < this.spaces.length; x++) {
                Space space = this.spaces[y][x];
                if (!(space.getState() == SpaceState.UNCOVERED || space.getIsMine())) {
                    return false;
                }
            }
        }
        return true;
    }

    private void uncoverAllMines() {
        for (int y = 0; y < this.spaces.length; y++) {
            for (int x = 0; x < this.spaces[y].length; x++) {
                Space space = this.spaces[y][x];
                if (space.getIsMine()) {
                    space.setState(SpaceState.UNCOVERED);
                }
            }
        }
    }

    Error uncover(int x, int y) {
        Error err = this.uncoverInner(x, y, true);
        if (this.hasWon()) {
            this.state = GameState.WON;
        }
        return err;
    }

    private Error uncoverInner(int x, int y, boolean root) {
        Space uncovered = this.spaces[y][x];
        if (uncovered.getState() == SpaceState.UNCOVERED) {
            return null;
        }
        if (root && uncovered.getState() == SpaceState.FLAGGED) {
            return new Error("You cannot uncover a flagged space.");
        }

        uncovered.setState(SpaceState.UNCOVERED);

        if (this.state == GameState.INIT) {
            this.initMines();
            this.state = GameState.PLAYING;
        }
        if (root && uncovered.getIsMine()) {
            this.state = GameState.LOST;
            this.uncoverAllMines();
            return null;
        }

        if (uncovered.getNeighboringMineCount() > 0) {
            return null;
        }

        if (y-1 >= 0) {
            this.uncoverInner(x, y-1, false);
        }
        if (y+1 < this.spaces.length) {
            this.uncoverInner(x, y+1, false);
        }
        if (x-1 >= 0) {
            this.uncoverInner(x-1, y, false);
        }
        if (x+1 < this.spaces[y].length) {
            this.uncoverInner(x+1, y, false);
        }

        if (y-1 >= 0 && x-1 >= 0) {
            this.uncoverInner(x-1, y-1, false);
        }
        if (y-1 >= 0 && x+1 < this.spaces[y].length) {
            this.uncoverInner(x+1, y-1, false);
        }
        if (y+1 < this.spaces.length && x-1 >= 0) {
            this.uncoverInner(x-1, y+1, false);
        }
        if (y+1 < this.spaces.length && x+1 < this.spaces[y].length) {
            this.uncoverInner(x+1, y+1, false);
        }

        return null;
    }

    Error flag(int x, int y) {
        Space space = this.spaces[y][x];
        if (space.getState() != SpaceState.COVERED) {
            return new Error("You cannot flag a non-covered space.");
        }
        space.setState(SpaceState.FLAGGED);
        this.flagsUsed++;
        return null;
    }

    Error unflag(int x, int y) {
        Space space = this.spaces[y][x];
        if (space.getState() != SpaceState.FLAGGED) {
            return new Error("You cannot unflag a non-flagged space.");
        }
        space.setState(SpaceState.COVERED);
        this.flagsUsed--;
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("   ");
        for (int y = 0; y < this.spaces[0].length; y++) {
            builder.append(String.format("%1$-3s", y));
        }
        builder.append("\n");

        for (int y = 0; y < this.spaces.length; y++) {
            builder.append(String.format("%1$2s ", y));
            Space[] row = this.spaces[y];
            for (int x = 0; x < row.length; x++) {
                Space space = row[x];
                builder.append(space.toString() + "  ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
