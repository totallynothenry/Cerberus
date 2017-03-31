import game.Game;

public class Main {
	public static void main(String args[]) {
		// Initializes GLFW, use for absolutely everything and anything


		// Creates a new game
		Game game = new Game();

		// Tries to start the game, always ends game and terminates GLFW
		try {
			game.start();
		} finally {
			game.terminate();
		}
	}
}
