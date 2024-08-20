import java.util.Random;

enum GameState {
    INIT, // mines have yet to be initialized
    PLAYING,
    LOST,
    WON
}

class Game {
    Space[][] spaces;
    int numberMines;
    GameState state = GameState.INIT;

    int flagsUsed;

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
        this.spaces[y][x].isMine = true;

        if (y-1 >= 0) {
            this.spaces[y-1][x].neighboringMineCount++;
        }
        if (y+1 < this.spaces.length) {
            this.spaces[y+1][x].neighboringMineCount++;
        }
        if (x-1 >= 0) {
            this.spaces[y][x-1].neighboringMineCount++;
        }
        if (x+1 < this.spaces[y].length) {
            this.spaces[y][x+1].neighboringMineCount++;
        }

        if (y-1 >= 0 && x-1 >= 0) {
            this.spaces[y-1][x-1].neighboringMineCount++;
        }
        if (y-1 >= 0 && x+1 < this.spaces[y].length) {
            this.spaces[y-1][x+1].neighboringMineCount++;
        }
        if (y+1 < this.spaces.length && x-1 >= 0) {
            this.spaces[y+1][x-1].neighboringMineCount++;
        }
        if (y+1 < this.spaces.length && x+1 < this.spaces[y].length) {
            this.spaces[y+1][x+1].neighboringMineCount++;
        }
    }

    private void initMines() {
        Random rand = new Random();

        int generated = 0;
        while (generated < numberMines) {
            int y = rand.nextInt(this.spaces.length);
            int x = rand.nextInt(this.spaces[y].length);

            Space existing = this.spaces[y][x];
            if (existing.isMine || existing.state == SpaceState.UNCOVERED) {
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
                if (!(space.state == SpaceState.UNCOVERED || space.isMine)) {
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
                if (space.isMine) {
                    space.state = SpaceState.UNCOVERED;
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
        if (uncovered.state == SpaceState.UNCOVERED) {
            return null;
        }
        if (root && uncovered.state == SpaceState.FLAGGED) {
            return new Error("You cannot uncover a flagged space.");
        }

        uncovered.state = SpaceState.UNCOVERED;

        if (this.state == GameState.INIT) {
            this.initMines();
            this.state = GameState.PLAYING;
        }
        if (root && uncovered.isMine) {
            this.state = GameState.LOST;
            this.uncoverAllMines();
            return null;
        }

        if (uncovered.neighboringMineCount > 0) {
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
        if (space.state != SpaceState.COVERED) {
            return new Error("You cannot flag a non-covered space.");
        }
        this.spaces[y][x].state = SpaceState.FLAGGED;
        this.flagsUsed++;
        return null;
    }

    Error unflag(int x, int y) {
        Space space = this.spaces[y][x];
        if (space.state != SpaceState.FLAGGED) {
            return new Error("You cannot unflag a non-flagged space.");
        }
        this.spaces[y][x].state = SpaceState.COVERED;
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
