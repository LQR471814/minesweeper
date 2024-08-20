import java.util.Random;

enum GameState {
    INIT, //mine have yet to be initialized
    PLAYING,
    LOST,
    WON
}

class Game {
    Space[][] spaces;
    int numberMines;
    GameState state = GameState.INIT;

    private void initSpaces() {
        for (int y = 0; y < this.spaces.length; y++) {
            for (int x = 0; x < this.spaces[y].length; x++) {
                this.spaces[y][x] = new Space();
            }
        }
    }

    void startEasy() {
        this.spaces = new Space[9][9];
        this.numberMines = 10;
        this.initSpaces();
    }

    void startMedium() {
        this.spaces = new Space[16][16];
        this.numberMines = 40;
        this.initSpaces();
    }

    void startHard() {
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
            if (existing.isMine || existing.state != SpaceState.COVERED) {
                continue;
            }

            this.addMine(x, y);
            generated++;
        }

        this.state = GameState.PLAYING;
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

    void uncover(int x, int y) {
        Space uncovered = this.spaces[y][x];
        uncovered.state = SpaceState.UNCOVERED;

        if (this.state == GameState.INIT) {
            this.initMines();
        }
        if (uncovered.isMine) {
            this.state = GameState.LOST;
            return;
        }

        if (y-1 >= 0) {
            this.uncoverNeighbor(x, y-1);
        }
        if (y+1 < this.spaces.length) {
            this.uncoverNeighbor(x, y+1);
        }
        if (x-1 >= 0) {
            this.uncoverNeighbor(x-1, y);
        }
        if (x+1 < this.spaces[y].length) {
            this.uncoverNeighbor(x+1, y);
        }

        if (y-1 >= 0 && x-1 >= 0) {
            this.uncoverNeighbor(x-1, y-1);
        }
        if (y-1 >= 0 && x+1 < this.spaces[y].length) {
            this.uncoverNeighbor(x+1, y-1);
        }
        if (y+1 < this.spaces.length && x-1 >= 0) {
            this.uncoverNeighbor(x-1, y+1);
        }
        if (y+1 < this.spaces.length && x+1 < this.spaces[y].length) {
            this.uncoverNeighbor(x+1, y+1);
        }

        if (this.hasWon()) {
            this.state = GameState.WON;
        }
    }

    private void uncoverNeighbor(int x, int y) {
        Space space = this.spaces[y][x];
        if (space.state == SpaceState.UNCOVERED) {
            return;
        }

        if (space.neighboringMineCount == 0) {
            space.state = SpaceState.UNCOVERED;
        } else {
            space.state = SpaceState.NUMBER_SHOWN;
            return;
        }

        if (y-1 >= 0) {
            this.uncoverNeighbor(x, y-1);
        }
        if (y+1 < this.spaces.length) {
            this.uncoverNeighbor(x, y+1);
        }
        if (x-1 >= 0) {
            this.uncoverNeighbor(x-1, y);
        }
        if (x+1 < this.spaces[y].length) {
            this.uncoverNeighbor(x+1, y);
        }

        if (y-1 >= 0 && x-1 >= 0) {
            this.uncoverNeighbor(x-1, y-1);
        }
        if (y-1 >= 0 && x+1 < this.spaces[y].length) {
            this.uncoverNeighbor(x+1, y-1);
        }
        if (y+1 < this.spaces.length && x-1 >= 0) {
            this.uncoverNeighbor(x-1, y+1);
        }
        if (y+1 < this.spaces.length && x+1 < this.spaces[y].length) {
            this.uncoverNeighbor(x+1, y+1);
        }
    }

    void flag(int x, int y) {
        this.spaces[y][x].state = SpaceState.FLAGGED;
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
