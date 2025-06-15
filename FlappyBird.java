import java.awt.*;
import java.awt.event.*;
import java.io.*; 
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener, MouseListener {

    int boardWidth = 360;
    int boardHeight = 640;

    private static final String HIGH_SCORE_FILE = "highscore.dat";

    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX; 
        int y = birdY; 
        int width = birdWidth; 
        int height = birdHeight; 
        Image img; 

        Bird(Image img) {
            this.img = img; 
        }
    }

    int pipeX = boardWidth; 
    int pipeY = 0; 
    int pipeWidth = 64; 
    int pipeHeight = 512; 

    class Pipe {
        int x = pipeX; 
        int y = pipeY; 
        int width = pipeWidth; 
        int height = pipeHeight; 
        Image img; 
        boolean passed = false; 

        Pipe(Image img) {
            this.img = img; 
        }
    }

    Bird bird; 
    int velocityX = -4; 
    int velocityY = 0; 
    int gravity = 1; 

    int openingSpace = boardHeight / 4; 

    ArrayList<Pipe> pipes; 
    Random random = new Random(); 

    Timer gameLoop; 
    Timer placePipeTimer; 
    boolean gameOver = false; 
    double score = 0; 
    double highScore = 0; 

    boolean gameStarted = false; 

    Rectangle playButton = new Rectangle(140, 500, 80, 50);

    Rectangle pauseButton = new Rectangle(10, 10, 50, 30);

    Rectangle continueButton = new Rectangle(0, 0, 110, 50);
    Rectangle leaveButton = new Rectangle(0, 0, 110, 50);

    boolean isPaused = false;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight)); // set panel size
        setFocusable(true); 
        addKeyListener(this); 
        addMouseListener(this); 

        backgroundImg = new ImageIcon(getClass().getResource("flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("bottompipe.png")).getImage();

        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();


        loadHighScore();

        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused) {
                    placePipes(); 
                }
            }
        });
        
        gameLoop = new Timer(1000 / 60, this);

    }

    void placePipes() {

        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe); 

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe); 
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        g2d.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        if (!gameStarted) {

            g2d.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

            String title = "FlappyBird";
            Font titleFont = new Font("Verdana", Font.BOLD, 52);
            g2d.setFont(titleFont);
            int titleWidth = g2d.getFontMetrics().stringWidth(title);
            int titleX = (boardWidth - titleWidth) / 2;
            int titleY = 150;

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(title, titleX + 3, titleY + 3);

            g2d.setColor(Color.white);
            g2d.drawString(title, titleX, titleY);

            int birdStartY = (boardHeight - birdHeight) / 2;
            int birdStartX = (boardWidth - birdWidth) / 2;
            g2d.drawImage(birdImg, birdStartX, birdStartY, birdWidth, birdHeight, null);

            playButton.x = (boardWidth - playButton.width) / 2;
            playButton.y = boardHeight - playButton.height - 60; 

            g2d.setColor(new Color(0, 150, 0));

            int[] xPoints = { playButton.x + 25, playButton.x + 25, playButton.x + 55 };
            int[] yPoints = { playButton.y + 15, playButton.y + 35, playButton.y + 25 };
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(new Color(0, 150, 0)); 
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(playButton.x, playButton.y, playButton.width, playButton.height, 15, 15);

            return; 
        }

        GradientPaint pauseButtonGradient = new GradientPaint(pauseButton.x, pauseButton.y, new Color(255, 140, 0),
                pauseButton.x + pauseButton.width, pauseButton.y + pauseButton.height, new Color(255, 69, 0));
        g2d.setPaint(pauseButtonGradient);
        g2d.fillRoundRect(pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height, 15, 15);

        g2d.setColor(new Color(139, 0, 0));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height, 15, 15);

        Font pauseButtonFont = new Font("Comic Sans MS", Font.BOLD, 18);
        g2d.setFont(pauseButtonFont);

        String pauseButtonText = "Pause";
        int pauseButtonTextWidth = g2d.getFontMetrics().stringWidth(pauseButtonText);
        int pauseButtonTextX = pauseButton.x + (pauseButton.width - pauseButtonTextWidth) / 2;
        int pauseButtonTextY = pauseButton.y + (pauseButton.height + g2d.getFontMetrics().getAscent()) / 2 - 4;

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(pauseButtonText, pauseButtonTextX + 2, pauseButtonTextY + 2);

        g2d.setColor(new Color(255, 255, 224));
        g2d.drawString(pauseButtonText, pauseButtonTextX, pauseButtonTextY);

        if (isPaused) {

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, boardWidth, boardHeight);

            g2d.setColor(new Color(255, 255, 255));
            int pauseBoxX = 50;
            int pauseBoxY = 200;
            int pauseBoxWidth = boardWidth - 100;
            int pauseBoxHeight = 130; 
            g2d.fillRoundRect(pauseBoxX, pauseBoxY, pauseBoxWidth, pauseBoxHeight, 20, 20);
            g2d.setColor(Color.black);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(pauseBoxX, pauseBoxY, pauseBoxWidth, pauseBoxHeight, 20, 20);

            Font pauseFont = new Font("Verdana", Font.BOLD, 36);
            g2d.setFont(pauseFont);
            String pauseText = "Pause";
            int pauseTextWidth = g2d.getFontMetrics().stringWidth(pauseText);
            g2d.drawString(pauseText, (boardWidth - pauseTextWidth) / 2, pauseBoxY + 50);

            int buttonWidth = 110;
            int buttonHeight = 50;
            int buttonSpacing = 20;
            int totalButtonsWidth = buttonWidth * 2 + buttonSpacing;
            int startX = (boardWidth - totalButtonsWidth) / 2;
            int buttonsY = pauseBoxY + 70;

            continueButton.x = startX;
            continueButton.y = buttonsY;
            continueButton.width = buttonWidth;
            continueButton.height = buttonHeight;

            leaveButton.x = startX + buttonWidth + buttonSpacing;
            leaveButton.y = buttonsY;
            leaveButton.width = buttonWidth;
            leaveButton.height = buttonHeight;

            g2d.setColor(new Color(0, 150, 0));
            g2d.fillRoundRect(continueButton.x, continueButton.y, continueButton.width, continueButton.height, 15, 15);
            g2d.setColor(Color.white);
            g2d.setFont(new Font("Verdana", Font.BOLD, 20));
            String continueText = "Continue";
            int continueTextWidth = g2d.getFontMetrics().stringWidth(continueText);
            g2d.drawString(continueText, continueButton.x + (continueButton.width - continueTextWidth) / 2,
                    continueButton.y + 32);

            g2d.setColor(new Color(200, 0, 0));
            g2d.fillRoundRect(leaveButton.x, leaveButton.y, leaveButton.width, leaveButton.height, 15, 15);
            g2d.setColor(Color.white);
            String leaveText = "Leave";
            int leaveTextWidth = g2d.getFontMetrics().stringWidth(leaveText);
            g2d.drawString(leaveText, leaveButton.x + (leaveButton.width - leaveTextWidth) / 2, leaveButton.y + 32);

            return; 
        }

        g2d.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g2d.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        String scoreText;
        Font scoreFont = new Font("Verdana", Font.BOLD, 36); // reduced font size
        g2d.setFont(scoreFont);

        if (gameOver) {
            scoreText = "Game Over: " + (int) score;
            int textWidth = g2d.getFontMetrics().stringWidth(scoreText);
            int x = (boardWidth - textWidth) / 2;
            int y = 100; 

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(scoreText, x + 3, y + 3);

            g2d.setColor(Color.red);
            g2d.drawString(scoreText, x, y);

            Font restartFont = new Font("Verdana", Font.PLAIN, 18); // reduced font size
            g2d.setFont(restartFont);
            String restartText = "Press SPACE to Restart";
            int restartWidth = g2d.getFontMetrics().stringWidth(restartText);
            int restartX = (boardWidth - restartWidth) / 2;
            int restartY = 130; 

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(restartText, restartX + 2, restartY + 2);

            g2d.setColor(Color.white);
            g2d.drawString(restartText, restartX, restartY);

            String highScoreText = "High Score: " + (int) highScore;
            int highScoreWidth = g2d.getFontMetrics().stringWidth(highScoreText);
            int highScoreX = (boardWidth - highScoreWidth) / 2;
            int highScoreY = 170; // moved down a bit more

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(highScoreText, highScoreX + 2, highScoreY + 2);

            g2d.setColor(Color.white);
            g2d.drawString(highScoreText, highScoreX, highScoreY);
        } else {
            scoreText = String.valueOf((int) score);
            int scoreWidth = g2d.getFontMetrics().stringWidth(scoreText);
            int scoreX = (boardWidth - scoreWidth) / 2;
            int scoreY = 50;

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(scoreText, scoreX + 2, scoreY + 2);

            g2d.setColor(Color.white);
            g2d.drawString(scoreText, scoreX, scoreY);
        }
    }

    public void move() {
        if (!gameStarted) {
            return; 
        }

        velocityY += gravity;
        bird.y += velocityY; 
        bird.y = Math.max(bird.y, 0); 

        updateDifficulty();

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5; 
                pipe.passed = true;
                if (score > highScore) {
                    highScore = score; 
                    saveHighScore(); 
                }
            }

            // Check collision between bird and pipe
            if (collision(bird, pipe)) {
                gameOver = true; 

                placePipeTimer.stop();
                gameLoop.stop();
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;

            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && 
                a.x + a.width > b.x && 
                a.y < b.y + b.height && 
                a.y + a.height > b.y; 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move(); 
        repaint(); 
        if (gameOver) {
            placePipeTimer.stop(); 
            gameLoop.stop(); 
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameStarted) {
                startGame();
            }
            velocityY = -9; 

            if (gameOver) {

                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                startGame();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();

        if (!gameStarted) {

            if (playButton.contains(p)) {
                startGame();
            }
        } else {
            if (pauseButton.contains(p)) {

                isPaused = !isPaused;
                if (isPaused) {

                    placePipeTimer.stop();
                    gameLoop.stop();

                } else {

                    placePipeTimer.start();
                    gameLoop.start();
                }
                repaint();
                return;
            }

            if (isPaused) {
                if (continueButton.contains(p)) {

                    isPaused = false;
                    placePipeTimer.start();
                    gameLoop.start();
                    repaint();
                    

                } else if (leaveButton.contains(p)) {

                    isPaused = false;
                    gameOver = false;
                    gameStarted = false;
                    score = 0;
                    velocityY = 0;
                    bird.y = birdY;
                    pipes.clear();
                    placePipeTimer.stop();
                    gameLoop.stop();
                    repaint();
                    
                }
            }
        }
    }

    private void startGame() {
        gameStarted = true;
        gameOver = false;
        score = 0;
        velocityY = 0;
        bird.y = birdY;
        pipes.clear();
        // Reset difficulty parameters
        velocityX = -4;
        openingSpace = boardHeight / 4;
        placePipeTimer.setDelay(1500);

        placePipeTimer.start();
        gameLoop.start();
    }

    private void updateDifficulty() {

        int speedIncrease = (int) (score / 5);
        velocityX = Math.max(-10, -4 - speedIncrease);

        int gapDecrease = (int) (score / 5) * 10;
        openingSpace = Math.max(100, (boardHeight / 4) - gapDecrease);

        int newDelay = Math.max(800, 1500 - (speedIncrease * 100));
        if (placePipeTimer.getDelay() != newDelay) {
            placePipeTimer.setDelay(newDelay);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void loadHighScore() {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(HIGH_SCORE_FILE))) {
            highScore = dis.readDouble();
        } catch (IOException e) {

            highScore = 0;
        }
    }

    // Save high score to file
    private void saveHighScore() {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(HIGH_SCORE_FILE))) {
            dos.writeDouble(highScore);
        } catch (IOException e) {

        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird gamePanel = new FlappyBird();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
