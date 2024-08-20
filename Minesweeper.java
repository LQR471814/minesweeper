import java.util.Scanner;

public class Minesweeper {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("What difficulty do you want (easy, medium, hard)? ");

        Game game = new Game();
        String difficulty = scanner.nextLine();
        switch (difficulty) {
            case "easy":
                game.startEasy();
                break;
            case "medium":
                game.startMedium();
                break;
            case "hard":
                game.startHard();
                break;
            default:
                System.out.println("Invalid input " + difficulty);
                System.exit(1);
                return;
        }

        while (true) {
            System.out.println(game.toString());

            if (game.state == GameState.LOST) {
                System.out.println("You lost.");
                return;
            }
            if (game.state == GameState.WON) {
                System.out.println("You won!");
                return;
            }

            System.out.println("Make a move in the format (uncover|flag <row index> <column index>):");
            System.out.print(" > ");

            String input = scanner.nextLine();
            String[] split = input.split(" ");
            if (split.length < 3) {
                System.out.println("\n[ERROR] Invalid format, the 3 arguments should be split by a single whitespace.\n");
                continue;
            }

            String action = split[0];
            switch (action) {
                case "uncover":
                case "flag":
                    break;
                default:
                    System.out.println(String.format(
                        "\n[ERROR] Unknown action. '%1$s' is not either 'uncover' or 'flag'.\n",
                        input
                    ));
                    break;
            }

            int x;
            int y;
            try {
                x = Integer.parseInt(split[1]);
            } catch(Exception e) {
                System.out.println(String.format(
                    "\n[ERROR] Invalid row index, '%1$s' is not an integer.\n",
                    split[1]
                ));
                continue;
            }
            try {
                y = Integer.parseInt(split[2]);
            } catch(Exception e) {
                System.out.println(String.format(
                    "\n[ERROR] Invalid column index, '%1$s' is not an integer.\n",
                    split[2]
                ));
                continue;
            }

            switch (action) {
                case "uncover":
                    game.uncover(x, y);
                    break;
                case "flag":
                    game.flag(x, y);
                    break;
            }
        }
    }
}
