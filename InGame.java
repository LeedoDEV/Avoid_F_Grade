package javaproject;

import javax.swing.*; // GUI 관련 클래스들을 가져옴
import java.awt.*; // 그래픽과 관련된 클래스들을 가져옴
import java.awt.event.*; // 이벤트 처리를 위한 클래스들
import java.io.File; // 파일 관련 클래스
import java.util.ArrayList; // 리스트 클래스
import java.util.Random; // 난수 생성 클래스

public class InGame extends JFrame implements KeyListener, ActionListener {
    private final Timer timer = new Timer(20, this); // 게임의 타이머, 20ms마다 ActionEvent를 발생시킴
    private final ArrayList<Rectangle> obstacles = new ArrayList<>(); // 장애물 리스트
    private final ArrayList<Point> directions = new ArrayList<>(); // 장애물의 이동 방향 리스트
    private final Random random = new Random(); // 랜덤 객체
    private final int OBSTACLE_SIZE = 24; // 기본 장애물 크기
    private final int LARGE_OBSTACLE_SIZE = 50; // 큰 장애물 크기
    private long lastLargeObstacleTime = 0; // 마지막 큰 장애물이 생성된 시간
    private int spawnInterval = 8; // 장애물 생성 간격
    private int spawnCounter = 0; // 장애물 생성 카운터
    private int obstacleSpeed = 0; // 장애물 속도
    private Image backgroundImage; // 배경 이미지
    private Image circleImage; // 원 형태의 이미지
    private Image playerImage; // 플레이어 이미지
    private Image obstacleImage; // 장애물 이미지
    private Image largeObstacleImage; // 큰 장애물 이미지

    private int x = 950; // 플레이어의 초기 x좌표
    private int y = 535; // 플레이어의 초기 y좌표
    private boolean up, down, left, right; // 방향 키 상태

    private int remainingTime = 0; // 남은 시간
    private Timer countdownTimer; // 카운트다운 타이머

    private void openMainMenu() {
        // Main 클래스의 새 인스턴스를 생성해 표시
        Main mainMenu = new Main(); 
        mainMenu.setVisible(true);
    }

    // 생성자 - 게임 창과 초기 설정을 수행
    public InGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫기 동작 설정
        setUndecorated(true); // 창의 기본 장식 제거
        setExtendedState(JFrame.MAXIMIZED_BOTH); // 창을 최대화 상태로 설정

        // 패널 생성 및 paintComponent 메서드 오버라이드
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;

                // 배경 이미지 그리기
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }

               // 원 안에 이미지를 그리기 위한 클리핑 설정
               int diameter = 800; // 원의 지름
               int xCircle = (getWidth() - diameter) / 2;
               int yCircle = (getHeight() - diameter) / 2;

               Shape originalClip = g2d.getClip(); // 원래 클리핑 영역 저장
               g2d.setClip(new java.awt.geom.Ellipse2D.Float(xCircle, yCircle, diameter, diameter)); // 클리핑 영역을 원으로 설정

               if (circleImage != null) {
                   g2d.drawImage(circleImage, xCircle, yCircle, diameter, diameter, this); // 원 안에 이미지를 그리기
               }

               g2d.setClip(originalClip); // 클리핑 영역 복원

               // 장애물 그리기
               for (Rectangle obstacle : obstacles) {
                   int obstacleCenterX = obstacle.x + obstacle.width / 2;
                   int obstacleCenterY = obstacle.y + obstacle.height / 2;

                   if (isInsideCircle(obstacleCenterX, obstacleCenterY, xCircle + diameter / 2, yCircle + diameter / 2, diameter / 2)) {
                       Image imageToDraw = (obstacle.width == OBSTACLE_SIZE) ? obstacleImage : largeObstacleImage;

                       if (imageToDraw != null) {
                           g2d.drawImage(imageToDraw, obstacle.x, obstacle.y, obstacle.width, obstacle.height, this);
                       } else {
                           g2d.setColor(Color.GRAY); // 장애물이 이미지가 없을 경우 기본 색상으로 그리기
                           g2d.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
                       }
                   }
               }

               // 플레이어 이미지 그리기
               if (playerImage != null) {
                   g.drawImage(playerImage, x, y, 19, 19, this); // 플레이어 이미지를 19x19 크기로 그리기
               } else {
                   g.setColor(Color.RED); // 기본 사각형으로 플레이어 그리기
                   g.fillRect(x, y, 19, 19);
               }

               // 남은 시간과 점수 표시
               try {
                   Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("tvNJoyfulBold.ttf")).deriveFont(80f);
                   g.setFont(customFont);
               } catch (Exception e) {
                   e.printStackTrace();
               }
               g.setColor(Color.white);
               g.drawString("강의시간 : " + remainingTime + "초", 110, 130); // 남은 시간 표시

               // 점수 계산 및 표시
               String score = "F";
               if (remainingTime >= 80) score = "A+";
               else if (remainingTime >= 70) score = "A";
               else if (remainingTime >= 60) score = "B+";
               else if (remainingTime >= 50) score = "B";
               else if (remainingTime >= 40) score = "C+";
               else if (remainingTime >= 30) score = "C";
               else if (remainingTime >= 20) score = "D+";
               else if (remainingTime >= 10) score = "D";

               g.drawString("현재학점 : " + score, 110, 200); // 점수 표시
           }
       };


       add(panel); // 패널을 프레임에 추가
        addKeyListener(this); // 키 리스너 추가
        setFocusable(true); // 포커스를 받을 수 있도록 설정


        timer.start(); // 타이머 시작
        setVisible(true); // 창을 보이도록 설정

        // 카운트다운 타이머 설정
        countdownTimer = new Timer(1000, e -> {
            if (remainingTime >= 0) {
                remainingTime++;

                if (remainingTime % 5 == 0) {
                    if (obstacleSpeed != 13) obstacleSpeed++;
                    spawnInterval = Math.max(4, spawnInterval - 2);
                }
            }
            repaint();// 화면 갱신
        });
        countdownTimer.start(); // 카운트다운 타이머 시작
    }

    public void move() {
        int diameter = 700; // 원의 지름
        int xCircle = (getWidth() - diameter) / 2; // 원의 x좌표
        int yCircle = (getHeight() - diameter) / 2; // 원의 y좌표
        int radius = diameter / 2; // 원의 반지름

        int nextX = x; // 다음 x좌표
        int nextY = y; // 다음 y좌표

        // 방향키 입력에 따라 플레이어의 위치 업데이트
        if (up) nextY -= 10;
        if (down) nextY += 10;
        if (left) nextX -= 10;
        if (right) nextX += 10;

        int playerCenterX = nextX + 9; // 플레이어 중심 x좌표
        int playerCenterY = nextY + 9; // 플레이어 중심 y좌표

        int dx = playerCenterX - (xCircle + radius); // 중심 간 x 거리
        int dy = playerCenterY - (yCircle + radius); // 중심 간 y 거리

        // 플레이어가 원 안에 있는지 확인
        if (dx * dx + dy * dy <= radius * radius) {
            x = nextX; // x좌표 업데이트
            y = nextY; // y좌표 업데이트
        }
    }

    // 키가 눌렸을 때 방향 상태 변경
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> up = true;
            case KeyEvent.VK_DOWN -> down = true;
            case KeyEvent.VK_LEFT -> left = true;
            case KeyEvent.VK_RIGHT -> right = true;
        }
    }
    // 키가 떼졌을 때 방향 상태 변경
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> up = false;
            case KeyEvent.VK_DOWN -> down = false;
            case KeyEvent.VK_LEFT -> left = false;
            case KeyEvent.VK_RIGHT -> right = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        spawnCounter++;
        if (spawnCounter >= spawnInterval) {
            spawnObstacle();
            spawnCounter = 0;
        }
        if (remainingTime >= 30 && remainingTime % 5 == 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastLargeObstacleTime > 1000) {
                spawnLargeObstacle();
                lastLargeObstacleTime = currentTime;
            }
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle o = obstacles.get(i);
            Point dir = directions.get(i);
            o.x += dir.x;
            o.y += dir.y;

            if (o.y > getHeight() || o.x > getWidth() || o.x < -OBSTACLE_SIZE) {
                obstacles.remove(i);
                directions.remove(i);
                i--;
                continue;
            }

            Rectangle playerRect = new Rectangle(x, y, 19, 19);
            if (o.intersects(playerRect)) {
                timer.stop();
                countdownTimer.stop();
                showGameOverDialog();
                return;
            }
        }

        repaint();
    }

    private void restartGame() {
        x = 950;
        y = 535;
        obstacles.clear();
        directions.clear();
        obstacleSpeed = 5;
        spawnInterval = 8;
        spawnCounter = 0;
        remainingTime = 0;
        lastLargeObstacleTime = 0;

        up = false;
        down = false;
        left = false;
        right = false;

        timer.start();
        countdownTimer.start();
        repaint();
    }
    private void showGameOverDialog() {
        String score;
        if (remainingTime >= 80) score = "A+";
        else if (remainingTime >= 70) score = "A";
        else if (remainingTime >= 60) score = "B+";
        else if (remainingTime >= 50) score = "B";
        else if (remainingTime >= 40) score = "C+";
        else if (remainingTime >= 30) score = "C";
        else if (remainingTime >= 20) score = "D+";
        else if (remainingTime >= 10) score = "D";
        else score = "F";

    
        JDialog gameOverDialog = new JDialog(this);
        gameOverDialog.setLayout(new BorderLayout());
        gameOverDialog.setSize(400, 200);
        gameOverDialog.setLocationRelativeTo(this);

    
        JLabel message = new JLabel("<html>강의종료!<br>학점: " + score + "<br>강의시간: " + remainingTime + "초</html>", SwingConstants.CENTER);
        
        message.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        gameOverDialog.add(message, BorderLayout.CENTER);
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
    
        JButton restartButton = new JButton("재수강하기");
        restartButton.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        restartButton.addActionListener(e -> {
            gameOverDialog.dispose();
            restartGame();
        });
        buttonPanel.add(restartButton);
    
        JButton mainMenuButton = new JButton("강의실 나가기");
        mainMenuButton.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        mainMenuButton.addActionListener(e -> {
            gameOverDialog.dispose();
            dispose(); // Close the InGame window
            openMainMenu(); // Call a method to open the Main window
        });
        buttonPanel.add(mainMenuButton);
    
        gameOverDialog.add(buttonPanel, BorderLayout.SOUTH);
        gameOverDialog.setVisible(true);
    }


    private void spawnObstacle() {
        int direction = random.nextInt(4);
        switch (direction) {
            case 0 -> {
                obstacles.add(new Rectangle(random.nextInt(getWidth() - OBSTACLE_SIZE), 0, OBSTACLE_SIZE, OBSTACLE_SIZE));
                directions.add(new Point(0, obstacleSpeed));
            }
            case 1 -> {
                obstacles.add(new Rectangle(0, random.nextInt(getHeight() - OBSTACLE_SIZE), OBSTACLE_SIZE, OBSTACLE_SIZE));
                directions.add(new Point(obstacleSpeed, 0));
            }
            case 2 -> {
                obstacles.add(new Rectangle(random.nextInt(getWidth() - OBSTACLE_SIZE), getHeight() - OBSTACLE_SIZE, OBSTACLE_SIZE, OBSTACLE_SIZE));
                directions.add(new Point(0, -obstacleSpeed));
            }
            case 3 -> {
                obstacles.add(new Rectangle(getWidth() - OBSTACLE_SIZE, random.nextInt(getHeight() - OBSTACLE_SIZE), OBSTACLE_SIZE, OBSTACLE_SIZE));
                directions.add(new Point(-obstacleSpeed, 0));
            }
        }
    }

    private void spawnLargeObstacle() {
        int direction = random.nextInt(4);
        switch (direction) {
            case 0 -> {
                obstacles.add(new Rectangle(random.nextInt(getWidth() - LARGE_OBSTACLE_SIZE), 0, LARGE_OBSTACLE_SIZE, LARGE_OBSTACLE_SIZE));
                directions.add(new Point(0, obstacleSpeed));
            }
            case 1 -> {
                obstacles.add(new Rectangle(0, random.nextInt(getHeight() - LARGE_OBSTACLE_SIZE), LARGE_OBSTACLE_SIZE, LARGE_OBSTACLE_SIZE));
                directions.add(new Point(obstacleSpeed, 0));
            }
            case 2 -> {
                obstacles.add(new Rectangle(random.nextInt(getWidth() - LARGE_OBSTACLE_SIZE), getHeight() - LARGE_OBSTACLE_SIZE, LARGE_OBSTACLE_SIZE, LARGE_OBSTACLE_SIZE));
                directions.add(new Point(0, -obstacleSpeed));
            }
            case 3 -> {
                obstacles.add(new Rectangle(getWidth() - LARGE_OBSTACLE_SIZE, random.nextInt(getHeight() - LARGE_OBSTACLE_SIZE), LARGE_OBSTACLE_SIZE, LARGE_OBSTACLE_SIZE));
                directions.add(new Point(-obstacleSpeed, 0));
            }
        }
    }

    public void setObstacleSpeed(int speed) {
        obstacleSpeed = speed;
    }

    public void setBackgroundImage(Image image) {
        this.backgroundImage = image;
        repaint();
    }

    public void setCircleImage(Image image) {
        this.circleImage = image;
        repaint();
    }

    public void setPlayerImage(Image image) {
        this.playerImage = image;
        repaint();
    }

    public void setObstacleImage(Image image) {
        this.obstacleImage = image;
        repaint();
    }
    
    public void setLargeObstacleImage(Image image) {
        this.largeObstacleImage = image;
        repaint();
    }
    
    // 원 안에 점이 있는지 확인하는 메서드
    private boolean isInsideCircle(int pointX, int pointY, int centerX, int centerY, int radius) {
        int dx = pointX - centerX;
        int dy = pointY - centerY;
        return dx * dx + dy * dy <= radius * radius;
    }

    public static void main(String[] args) {
        InGame game = new InGame();
        ImageIcon backgroundIcon = new ImageIcon("image/board.png");
        game.setBackgroundImage(backgroundIcon.getImage());

        ImageIcon circleIcon = new ImageIcon("image/ingameBoard.png");
        game.setCircleImage(circleIcon.getImage());

        ImageIcon playerIcon = new ImageIcon("image/player.png"); // 플레이어 이미지 추가
        game.setPlayerImage(playerIcon.getImage());

        ImageIcon obstacleIcon = new ImageIcon("image/Fsmall.png");
        game.setObstacleImage(obstacleIcon.getImage());

        ImageIcon largeObstacleIcon = new ImageIcon("image/Fbig.png");
        game.setLargeObstacleImage(largeObstacleIcon.getImage());

        game.setObstacleSpeed(5); 
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
