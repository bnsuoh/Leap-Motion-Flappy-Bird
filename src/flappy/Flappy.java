package flappy;
import com.leapmotion.leap.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
/**
 *
 * @author bensu
 */
class SampleListener extends Listener {
    public int appY_2, appY_1;
    public boolean replay=false;
    public boolean inGame=true;
    public int screenHeight=760;
    public float vectorX;
    public float vectorY;
    public int a=2;
    public boolean flap=false;
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }
    public void onConnect(Controller controller) {
        System.out.println("Connected"); //to indicate whether the leap motion connection is successful
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE); //to accept circle gesture
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
    }
    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }
    public void onExit(Controller controller) {
        System.out.println("Exited");
    }
    public void onFrame(Controller controller) { //to get the most recent frame and information
        Frame frame = controller.frame(); 
        GestureList gestures = frame.gestures();
        flap=false;
        for(Gesture gesture : frame.gestures()){
            switch (gesture.type()) {
                case TYPE_CIRCLE:
                    replay=true; //drawing a circle enables replay
                case TYPE_SWIPE:
                    SwipeGesture swipe = new SwipeGesture(gesture);
                    Vector swipeDirection = swipe.direction();
                    float dirX = swipeDirection.get(0);
                    float dirY = swipeDirection.get(1);
                    if(dirY<0 && Math.abs(dirY)>Math.abs(dirX)){
                        flap=true; //down
                    }
                    System.out.println(dirX + " " + dirY + " " + flap);
                break;
            }
        } 
    }
    public boolean checkSwipe(){
        return flap;
    }
    public boolean getReplay(){
        return replay;
    }
}
class Game extends JPanel implements ActionListener{
    public static int screenWidth=1366;
    public static int screenHeight=740;
    public boolean replay=false;
    public boolean inGame=true;
    public double speed=0;
    public double acceleration=1;
    public int score=0;
    public int birdY=40;
    public boolean eaten=false;
    public int wallX=screenWidth;
    public int wallX2=screenWidth+screenWidth/2+85;
    public int wallHeight;
    public int wallHeight2;
    public int height;
    public boolean fall=false;
    public int space=250;
    public int fallCount=1;
    public int initCount=1;
    public int time=0;
    public boolean flap=false;
    Controller controller = new Controller(); //initializes leap
    SampleListener listener = new SampleListener();
    //check checker = new check(); //creates timer to refresh frames
    public Game(){
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(screenWidth,screenHeight));
        InitGame();
        controller.addListener(listener); //starts leap
        
    }
    private void InitGame(){
        replay=false;
        inGame=true;
        score=0;
        locateWall();
        wallHeight=height;
        locateWall();
        wallHeight2=height;
        Timer timer = new Timer(true);
        TimerTask taskToExecute = new check();
        timer.scheduleAtFixedRate(taskToExecute, 0, 30); //refreshes the frame at every 30 miliseconds
    }  
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        doDrawing(g);
    }
    private void flap(){
        speed=-15;
        //birdY-=150;
    }
    private void checkCrash(){
        if(((wallX<100&&wallX+170>30) && (birdY<wallHeight || (birdY+70>=wallHeight+space)))||((wallX2<100&&wallX2+170>30)&& (birdY<wallHeight2 || (birdY+70>=wallHeight2+space)))){
            fall=true;
        }
        if(birdY+100>screenHeight){
            fall=true;
        }
        if(birdY<0){
            fall=true;
        }
    }
    public void checkScore(){
        if(time>153 && time%153==0){
            score++;
        }
    }
    public void locateWall() {
        height=(int)(Math.random()*screenHeight/3)+200;
    }
    private void checkWall(){
        if(wallX+170<=0){
            wallX=screenWidth;
            locateWall();
            wallHeight=height;
        }
        if(wallX2+170<=0){
            wallX2=screenWidth;
            locateWall();
            wallHeight2=height;
        }
    }
    private void doDrawing(Graphics g){
        Font smallest = new Font("Helvetica", Font.BOLD,20);
        Font scoreFont = new Font("Helvetica", Font.BOLD,120);
        Font big2 = new Font("Helvetica", Font.BOLD,100);
        Font small2 = new Font("Helvetica", Font.BOLD,50);
        if(inGame==true){ //paints game objects when game isn't over
            //g.drawRect(corner,2*corner,((screenWidth-140)/sqSize)*20,((screenHeight-70)/sqSize)*10);
            g.setColor(Color.red);
            g.fillOval(30, birdY,70,70);
            g.setColor(Color.white);
            g.setFont(small2);
            g.fillRect(wallX, 0, 170, wallHeight);
            g.fillRect(wallX, wallHeight+space, 170, screenHeight-wallHeight-space);
            g.fillRect(wallX2, 0, 170, wallHeight2);
            g.fillRect(wallX2, wallHeight2+space, 170, screenHeight-wallHeight2-space);
            g.setColor(Color.gray);
            g.setFont(scoreFont);
            g.drawString(Integer.toString(score), screenWidth-180, 120);
            g.setFont(smallest);
            g.drawString("Bensu Sicim", screenWidth-150, screenHeight-50);
            Toolkit.getDefaultToolkit().sync();
        }
        else if(inGame==false){ //game over page
            g.setColor(Color.white);
            g.setFont(big2);
            g.drawString("Game Over", 400, screenHeight/2-100);
            g.setFont(small2);
            g.drawString("Score:" + score, 550, screenHeight/2);
            g.setFont(small2);
            g.drawString("Draw a circle with your finger to replay", 225, screenHeight/2+200);
            g.setFont(smallest);
            g.setColor(Color.gray);
            g.drawString("Bensu Sicim", screenWidth-150, screenHeight-50);
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint(); //To change body of generated methods, choose Tools | Templates.
    }
    public class check extends TimerTask{ //refresh class, when timer reaches the indicated time (every 30 miliseconds)
        public void run(){
            if(inGame){
                replay=false;
                checkSwipe();
                checkCrash();
                checkWall();
                checkScore();
                if(!fall){
                    if(initCount<35 && initCount%35!=0){
                        speed=0;
                        initCount++;
                    }
                    else{
                        if(flap){
                            flap();
                        }
                        speed+=acceleration;
                    }
                    time++;
                    birdY+=speed;
                    wallX-=5;
                    wallX2-=5;
                    //System.out.println(score);
                }
                else if(fall){
                    if(fallCount<10 && fallCount%10!=0){
                        speed=0;
                        fallCount++;
                    }
                    else{
                        speed+=acceleration;  
                    }
                    birdY+=speed;
                    if(birdY>screenHeight+1000){
                        inGame=false;
                    }
                }
            }
            else if (inGame=false){
                replay=false;
                replay=listener.getReplay(); //checks if replay is true from the leap
                if(replay==true){
                    score=0;
                    inGame=true;
                    replay=false;
                }
            }
            repaint();
        }
    }
    public void checkSwipe() {
        flap=listener.checkSwipe();
    }
    /*private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_UP) {
                if(!fall){
                    flap();
                }
            }
        }
    }*/
}
public class Flappy extends JFrame{
    public Flappy(){
        add(new Game());
        setResizable(false);
        pack();
        setTitle("Flappy");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                JFrame ex = new Flappy();
                ex.setVisible(true);
            }
        });
    }
}

