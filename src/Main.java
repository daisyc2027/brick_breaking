import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

class Paddle {
    int x, y, width, height, moveSpeed;
    Color color;

    Paddle(int x, int y, int width, int height, int moveSpeed, Color color){
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
    Ball(int x, int y, int diameter, int xSpeed, int ySpeed, Color color){
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.color = color;
    }
//    public static Ball getBall(int x, int y, int diameter, int xSpeed, int ySpeed, Color color){
//        return new Ball(x, y, diameter,xSpeed, ySpeed, color);
//    }
    public void move(int width, int height){
        x += xSpeed;
        y += ySpeed;

        collideWall(width, height);

    }
    public void collideWall(int width, int height){
        if(x<0 || x + diameter > width){
            xSpeed = -xSpeed;
        }

        if(y < 0 || y + diameter > height){
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

    public int getHit(int health){
        return health - 1;
    }

    public void draw(Graphics g){
        g.setColor(color);
        g.fillRect(x, y, length, length);
    }
}

class GamePanel extends JPanel implements KeyListener, ActionListener {
    Paddle paddle = new Paddle(150, 600, 100, 20, 20, Color.BLACK);
    Ball ball = new Ball(paddle.width/2+paddle.x-15,paddle.y+30,30,5,5, Color.BLUE);
    ArrayList<Brick> listOfBricks = new ArrayList<>();
    int rows, bricksPerRow;

    public GamePanel(){
        setFocusable(true);
        addKeyListener(this);
//        int currentX = 0, currentY = 0;
//        for(int i = 0; i<rows; i++){
//            for (int j = 0; i <bricksPerRow; j++){
//                listOfBricks.add(new Brick(currentX,currentY,50, Color.YELLOW));
//                currentX += 50;
//            }
//            currentY +=50;
//        }

        Timer timer = new Timer(15, this);
        timer.start();
    }

    public boolean checkCollisionWithBrick(Brick brick, Ball ball){
        boolean xCollide = ball.x + ball.diameter > brick.x && ball.x < brick.x + brick.length;
        boolean yCollide = ball.y + ball.diameter > brick.y && ball.y < brick.y + brick.length;

        return xCollide && yCollide;
    }

    public void collisionWithBrick(Brick brick, Ball ball){
        if(checkCollisionWithBrick(brick, ball)){
            ball.xSpeed = -ball.xSpeed;
            ball.ySpeed = -ball.ySpeed;
        }
    }

    public boolean checkCollisionWithPaddle(Paddle paddle, Ball ball){
        boolean yCollide = paddle.y <= ball.y + ball.diameter && ball.y + ball.diameter <= paddle.y + paddle.height;
        boolean xCollide = ball.x+ball.diameter >= paddle.x && ball.x <= paddle.x + paddle.width;

        return yCollide && xCollide;
    }
    public void collisionWithPaddle(Paddle paddle, Ball ball){
        if(checkCollisionWithPaddle(paddle, ball)){
            ball.xSpeed = -ball.xSpeed;
            ball.ySpeed = -ball.ySpeed;
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        paddle.draw(g);
        ball.draw(g);
        listOfBricks.getFirst().draw(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){//right arrow
            paddle.movePaddleRight(getWidth());
        }

        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            paddle.movePaddleLeft();
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ball.move(getWidth(), getHeight());
        collisionWithPaddle(paddle, ball);
        collisionWithBrick(listOfBricks.getFirst(), ball);
        repaint();
    }
}

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Brick breaker");
        GamePanel game = new GamePanel();
        frame.add(game);
        frame.setSize(400,700);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
}