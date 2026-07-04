package javaproject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Gamerule extends JFrame {
    public Gamerule() {
        setTitle("게임 방법");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
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
                    backgroundImage = ImageIO.read(new File("image/Ruleboard.png"));
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

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 닫기 버튼 이미지
        ImageIcon closeDefaultIcon = new ImageIcon("image/close1.png"); //기본 아이콘
        ImageIcon closeHoverIcon = new ImageIcon("image/close2.png");  // 마우스 올렸을때 아이콘

        JButton closeButton = new JButton(closeDefaultIcon);
        closeButton.setPreferredSize(new Dimension(150, 60));  // 버튼 크기
        closeButton.setBorderPainted(false);  // 테두리 제거
        closeButton.setContentAreaFilled(false);  // 배경 투명

        // 버튼의 마우스 이벤트 설정
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                closeButton.setIcon(closeHoverIcon);  // 마우스 올렸을 때 아이콘 변경
                closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // 커서 변경
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                closeButton.setIcon(closeDefaultIcon);  // 원래 아이콘으로 복구
                closeButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));  // 기본 커서로 복구
            }
        });

        // 버튼 클릭 시
        closeButton.addActionListener(e -> {
            new Main();  // 게임 메인 화면
            dispose(); 
        });

        gbc.insets = new Insets(700, 0, 35, 0);  // 버튼 위치
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(closeButton, gbc);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Gamerule::new);
    }
}