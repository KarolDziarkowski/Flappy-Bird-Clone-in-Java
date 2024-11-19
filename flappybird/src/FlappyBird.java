import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class FlappyBird extends JPanel implements ActionListener, KeyListener {


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameStarted) {
                gameStarted = true;
                gameLoop.start();
                placePipesTimer.start();
            } else if (!gameOver) {
                VelocityY = -9; // Bird jumps
            }

            if (gameOver) {
                bird.y = birdY;
                VelocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameStarted = false; // Reset game state
            }
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    int boardWidth = 360;
    int boardHeight = 640;

    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
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

    //Rury
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe{
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


    //Ptaszor
    Bird bird;
    int VelocityX = -4;
    int VelocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();
    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;

    double score = 0;
    double highScore = 0;
    boolean gameStarted = false;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.BLUE);
        setFocusable(true);
        addKeyListener(this);
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();


    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void placePipes(){
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardWidth/3;
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Aerial", Font.PLAIN, 32));
        if (!gameStarted) {
            g.drawString("Press SPACE to Start", boardWidth / 8, boardHeight / 2);
        } else if (gameOver) {
            g.drawString("Game Over", 10, 35);
            g.drawString("Score: " + (int) score, 10, 70);
            g.drawString("High Score: " + (int) highScore, 10, 105);
        } else {
            g.drawString("Score: " + (int) score, 10, 35);
            g.drawString("High Score: " + (int) highScore, 10, 70);
        }
    }

    public void move(){
        VelocityY += gravity;
        bird.y += VelocityY;
        bird.y = Math.max(bird.y, 0);
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += VelocityX;
            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;
            }

            if(collision(bird, pipe)){
                gameOver = true;
            }
        }

        if(bird.y> boardHeight){
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y ;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted) {
            move();
            repaint();
            if (gameOver) {
                if (score > highScore) {
                    highScore = score; // Update high score
                }
                placePipesTimer.stop();
                gameLoop.stop();
            }
        }
    }

}
