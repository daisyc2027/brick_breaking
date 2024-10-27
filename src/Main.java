import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;

class Paddle {
    int x, y, width, height, moveSpeed;
    Color color;

    private Paddle(int x, int y, int width, int height, int moveSpeed, Color color){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.moveSpeed = moveSpeed;
        this.color = color;
    }

    public static Paddle getPaddle(int x, int y, int width, int height, int moveSpeed, Color color){
        return new Paddle(x, y, width, height, moveSpeed, color);
    }

    public void draw(Graphics g){
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public void movePaddleRight(int panelWidth) {
        if (x + width <= panelWidth) {
            x += moveSpeed;
        }
    }

    public void movePaddleLeft(){
        if(x >= 0){
        x -= moveSpeed;
        }
    }

}

class Ball{
    int x, y, diameter, xSpeed, ySpeed;
    Color color;
    private Ball(int x, int y, int diameter, int xSpeed, int ySpeed, Color color){
        this.x = x - diameter/2;
        this.y = y;
        this.diameter = diameter;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.color = color;
    }
    public static Ball getBall(int x, int y, int diameter, int xSpeed, int ySpeed, Color color){
        return new Ball(x, y, diameter,xSpeed, ySpeed, color);
    }
    public void move(int width, int height){
        System.out.println("why");
        x += xSpeed;
        y += ySpeed;
        collideWall(width, height);
    }
    public void collideWall(int width, int height){
        if(x<0 || x + diameter > width){
            xSpeed = -xSpeed;
        }

        if(y < 0 ){
            ySpeed = -ySpeed;
        }
    }
    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval(x, y, diameter, diameter);
    }
}

class Brick{
    int x, y, length;
    Color color;
    public Brick(int x, int y, int length, Color color){
        this.x = x;
        this.y = y;
        this.length = length;
        this.color = color;
    }

    public void draw(Graphics g){
        g.setColor(color);
        g.fillRect(x, y, length, length);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, length, length);
    }
}

class GamePanel extends JPanel implements KeyListener, ActionListener {
    Paddle paddle = Paddle.getPaddle(150, 600, 100, 20, 20, Color.BLACK);
    Ball ball = Ball.getBall(paddle.x + paddle.width / 2, paddle.y - 20, 20, 5, -5, Color.BLUE);
    ArrayList<Brick> listOfBricks = new ArrayList<>();
    int bricksPerRow, rows;
    Timer timer;
    JLabel score = new JLabel("0");
    boolean gameOver = false;
    boolean gameStarted = false;

    public GamePanel(int bricksPerRow, int rows) {
        setFocusable(true);
        addKeyListener(this);
        add(score);
        this.bricksPerRow = bricksPerRow;
        this.rows = rows;
        int currentX = 10, currentY = 50;
        Color color1 = Color.YELLOW;
        Color color2 = Color.CYAN;
        Color color3 = Color.MAGENTA;
        for (int i = 0; i < rows; i++) {
            Color brickColor;
            if ((i / 2) % 3 == 0) {
                brickColor = color1;
            } else if ((i / 2) % 3 == 1) {
                brickColor = color2;
            } else {
                brickColor = color3;
            }
            for (int j = 0; j < bricksPerRow; j++) {
                listOfBricks.add(new Brick(currentX, currentY, 50, brickColor));
                currentX += 50;
            }
            currentX = 10;
            currentY += 52;
        }

        this.timer = new Timer(15, this);
        timer.start();
    }

    public boolean checkGameOver(Ball ball) {
        return ball.y + ball.diameter > getHeight();
    }

    public boolean checkCollisionWithBrick(Brick brick, Ball ball) {
        boolean checkCollisionWithBrick;
        boolean xCollide = ball.x + ball.diameter > brick.x && ball.x < brick.x + brick.length;
        boolean yCollide = ball.y + ball.diameter > brick.y && ball.y < brick.y + brick.length;


        checkCollisionWithBrick = xCollide && yCollide;


        if (checkCollisionWithBrick) {
            int currentScore = Integer.parseInt(score.getText());
            currentScore += 1;
            score.setText(String.valueOf(currentScore));
            ball.xSpeed = -ball.xSpeed;
            ball.ySpeed = -ball.ySpeed;
        }
        return checkCollisionWithBrick;
    }

    public void checkCollisionWithPaddle(Paddle paddle, Ball ball) {
        boolean checkCollisionWithPaddle;
        boolean yCollide = paddle.y <= ball.y + ball.diameter && ball.y + ball.diameter <= paddle.y + paddle.height;
        boolean xCollide = ball.x + ball.diameter >= paddle.x && ball.x <= paddle.x + paddle.width;

        checkCollisionWithPaddle = yCollide && xCollide;

        if (checkCollisionWithPaddle) {
            ball.ySpeed = -ball.ySpeed;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paddle.draw(g);
        ball.draw(g);
        for (Brick brick : listOfBricks) {
            brick.draw(g);
        }

        if(gameOver){
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over!", getWidth()/2 - 80, getHeight()/2);
        } else if(!gameStarted) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press SPACE to start", getWidth()/2 - 100, getHeight()/2);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameStarted && e.getKeyCode() == KeyEvent.VK_SPACE) {
            gameStarted = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            paddle.movePaddleRight(getWidth());
            if (!gameStarted) {
                ball.x = paddle.x + paddle.width/2 - ball.diameter/2;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            paddle.movePaddleLeft();
            if (!gameStarted) {
                ball.x = paddle.x + paddle.width/2 - ball.diameter/2;
            }
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(gameOver || !gameStarted){
            return;
        }
        if(checkGameOver(ball)){
            gameOver = true;
            timer.stop();
            repaint();
            return;
        }

        ball.move(getWidth(), getHeight());
        checkCollisionWithPaddle(paddle, ball);

        Iterator<Brick> iterator = listOfBricks.iterator();
        while (iterator.hasNext()) {
            Brick brick = iterator.next();
            if (checkCollisionWithBrick(brick, ball)) {
                iterator.remove();
                break;
            }
        }
        repaint();

    }
}
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Brick breaker");
        GamePanel game = new GamePanel(8,6);
        frame.add(game);
        frame.setSize(420,700);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
}