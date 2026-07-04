package javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main extends JFrame {
    public Main() {
        setTitle("재수강 피하기");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true); 
        setLayout(new BorderLayout());

        add(createMainPanel(), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel() {
            private Image backgroundImage;
    
            {
                try {
                    backgroundImage = ImageIO.read(new File("image/Mainboard.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("배경 이미지를 로드할 수 없습니다.");
                }
            }
    
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(new BorderLayout());

        // 설정 버튼
        panel.add(createTopPanel(), BorderLayout.NORTH);
        // 게임시작, 게임방법 버튼
        panel.add(createBottomPanel(), BorderLayout.SOUTH);
    
        return panel;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false); // 배경 투명
        topPanel.setBorder(BorderFactory.createEmptyBorder(80, 0, 0, 90)); //설정버튼 위치조정
        
        ImageIcon defaultIcon = new ImageIcon("image/setting1.png");
        ImageIcon hoverIcon = new ImageIcon("image/setting2.png");
        
        JButton settingsButton = new JButton(defaultIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (getIcon() != null) {
                    g.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, getWidth(), getHeight(), this); //버튼 크기에 맞춰서 이미지를 조정
                }
            }
        };
        
        settingsButton.setPreferredSize(new Dimension(80, 80)); // 설정 버튼 크기
        settingsButton.setBorderPainted(false); // 버튼 테두리 제거
        settingsButton.setContentAreaFilled(false); // 배경 투명화
        
        // 마우스
        settingsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                settingsButton.setIcon(hoverIcon); // 마우스 올릴 때 아이콘 변경
                settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 클릭커서로 변경
            }
        
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                settingsButton.setIcon(defaultIcon); // 원래 아이콘으로 복구
                settingsButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 기본 커서로 복구
            }
        });
    
        settingsButton.addActionListener(e -> {
            settingsButton.setIcon(defaultIcon); // 클릭했을때 이미지가 defaultIcon로 바뀜
            openSettingsPopup(); // 설정 열기
        });
        
        topPanel.add(settingsButton);
        return topPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false); // 배경 투명
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 290, 0)); // 시작,방법 버튼 위치 조정
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 0, 35, 0); // 버튼 간격 조정
    
        // 게임 시작 버튼 이미지
        ImageIcon startDefaultIcon = new ImageIcon("image/gamestart1.png");
        ImageIcon startHoverIcon = new ImageIcon("image/gamestart2.png");
    
        JButton startButton = createImageButton(startDefaultIcon, startHoverIcon, e -> {
            dispose();
            //new InGame(); // 게임 창 실행
            InGame game = new InGame(); // InGame 창 생성
            initializeGame(game); // 이미지 설정 메서드 호출

        });
        startButton.setPreferredSize(new Dimension(230, 60)); // 시작버튼 크기조정
    
    // 게임 방법 버튼 이미지
    ImageIcon ruleDefaultIcon = new ImageIcon("image/gamerule1.png");
    ImageIcon ruleHoverIcon = new ImageIcon("image/gamerule2.png");

    JButton ruleButton = createImageButton(ruleDefaultIcon, ruleHoverIcon, e -> {
    new Gamerule();  // 게임 방법 창 실행
    dispose();  
    });
    ruleButton.setPreferredSize(new Dimension(230, 60)); // 방법버튼 크기조정

    // 버튼 추가
    bottomPanel.add(startButton, gbc);
    bottomPanel.add(ruleButton, gbc);

    return bottomPanel;
    }
    
    
// 이미지 버튼 생성 메서드
private JButton createImageButton(ImageIcon defaultIcon, ImageIcon hoverIcon, ActionListener action) {
    JButton button = new JButton(defaultIcon) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getIcon() != null) {
                // 버튼 크기에 맞게 이미지 크기 조정
                Image img = ((ImageIcon) getIcon()).getImage();
                Image scaledImage = img.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
                g.drawImage(scaledImage, 0, 0, this);
            }
        }
    };

    button.setBorderPainted(false); // 버튼 테두리 제거
    button.setFocusPainted(false); // 포커스 테두리 제거
    button.setContentAreaFilled(false); // 배경 투명화

    button.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            button.setIcon(hoverIcon); // 마우스 올릴 때 아이콘 변경
            button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 클릭커서로 변경
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            button.setIcon(defaultIcon); // 원래 아이콘으로 바꾸기
            button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 기본 커서
        }
    });

    button.addActionListener(action); // 버튼 클릭 시 동작 설정

    return button;
}

    
    
    private void openSettingsPopup() {
        JDialog settingsDialog = new JDialog(this, "설정", true);
        settingsDialog.setUndecorated(true); // 테두리 제거
        settingsDialog.setSize(1820, 750); //설정창 크기
        settingsDialog.setLocationRelativeTo(this);
    
        // 배경 이미지 
        JPanel backgroundPanel = new JPanel() {
            private Image backgroundImage;
    
            {
                try {
                    backgroundImage = ImageIO.read(new File("image/Settingboard.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("배경 이미지를 로드할 수 없습니다.");
                }
            }
    
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
    
        settingsDialog.setContentPane(backgroundPanel); //배경 패널 지정
        backgroundPanel.setLayout(new BorderLayout());  // BorderLayout을 사용해 레이아웃 설정
    
        // X 버튼 이미지
        ImageIcon closeButtonIcon = new ImageIcon("image/closeIcon1.png");
        ImageIcon closeButtonHoverIcon = new ImageIcon("image/closeIcon2.png");
    
        JButton closeButton = new JButton(closeButtonIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getIcon() != null) {
                    g.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        closeButton.setPreferredSize(new Dimension(40, 40));  // X 버튼 크기 설정
        closeButton.setBorderPainted(false);  // 버튼 테두리 제거
        closeButton.setContentAreaFilled(false);  // 배경 투명화
    

        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeButton.setIcon(closeButtonHoverIcon); // 마우스 올릴 때 아이콘 변경
                closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 클릭 커서 변경
            }
    
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeButton.setIcon(closeButtonIcon); // 원래 아이콘으로 복구
                closeButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 기본 커서로 복구
            }
        });
    
        // X 버튼 클릭 시 설정창 닫기
        closeButton.addActionListener(e -> settingsDialog.dispose());
    
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // X버튼을 오른쪽에 배치
        topPanel.setOpaque(false);  // 배경 투명
        topPanel.setBorder(BorderFactory.createEmptyBorder(175, 0, 0, 600));  // X 버튼 위치조정
    
        topPanel.add(closeButton);
    
        backgroundPanel.add(topPanel, BorderLayout.NORTH);  // 상단에 배치
    
        // 소리 설정, 게임 종료 버튼
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);  // 배경 투명
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
    
        buttonsPanel.add(Box.createVerticalStrut(50));  // 버튼 위쪽 공백
    
        // 소리 설정 버튼 이미지
        ImageIcon soundDefaultIcon = new ImageIcon("image/SoundSetting1.png");
        ImageIcon soundHoverIcon = new ImageIcon("image/SoundSetting2.png");
    
        JButton soundSettingsButton = createImageButton(soundDefaultIcon, soundHoverIcon, e -> {
            settingsDialog.dispose(); // 설정창을 닫고
            new SettingsNew(); // 소리 설정 창 열기
        });
        soundSettingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundSettingsButton.setPreferredSize(new Dimension(300, 60)); // 소리설정버튼 크기 조정
    
        // 게임 종료 버튼 이미지
        ImageIcon exitDefaultIcon = new ImageIcon("image/GameExit1.png");
        ImageIcon exitHoverIcon = new ImageIcon("image/GameExit2.png");
    
        JButton exitButton = createImageButton(exitDefaultIcon, exitHoverIcon, e -> System.exit(0));
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setPreferredSize(new Dimension(300, 60)); // 게임종료버튼 크기 조정
    
        buttonsPanel.add(soundSettingsButton);
        buttonsPanel.add(Box.createVerticalStrut(40));  // 버튼 간 간격
        buttonsPanel.add(exitButton);

    
        // 버튼들을 설정 창의 중앙에 배치
        backgroundPanel.add(buttonsPanel, BorderLayout.CENTER);
    
        settingsDialog.setVisible(true);
    }
    private void initializeGame(InGame game) {
        ImageIcon backgroundIcon = new ImageIcon("image/board.png");
        game.setBackgroundImage(backgroundIcon.getImage());

        ImageIcon circleIcon = new ImageIcon("image/ingameBoard.png");
        game.setCircleImage(circleIcon.getImage());

        ImageIcon playerIcon = new ImageIcon("image/player.png");
        game.setPlayerImage(playerIcon.getImage());

        ImageIcon obstacleIcon = new ImageIcon("image/Fsmall.png");
        game.setObstacleImage(obstacleIcon.getImage());

        ImageIcon largeObstacleIcon = new ImageIcon("image/Fbig.png");
        game.setLargeObstacleImage(largeObstacleIcon.getImage());

        game.setObstacleSpeed(5);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}