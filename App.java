/**
 * Main application class to launch the Flappy Bird game.
 * 
 * This class creates a JFrame window with specified dimensions,
 * sets up the window properties such as title, size, and close operation,
 * and adds the FlappyBird JPanel which contains the game logic and rendering.
 * 
 * The game window is centered on the screen, not resizable,
 * and made visible to start the game.
 */
import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");
        // frame.setVisible(true);
		frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true);
    }
}
