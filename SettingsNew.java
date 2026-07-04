package javaproject;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SettingsNew extends JFrame {
    private JSlider MusicS;
    private JSlider SoundS;
    private Clip BgClip1;
    private FloatControl VolumeGain;

    public SettingsNew() {
        setTitle("게임 설정 초안");
        setUndecorated(true);
        setResizable(false);
        setSize(1820, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        BackgroundPanel BGPanel = new BackgroundPanel();
        BGPanel.setLayout(new BorderLayout());

        JPanel topPanel = createTopPanel();
        BGPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(5, 3));
        centerPanel.setOpaque(false);

        // 1-1 여백
        centerPanel.add(new JLabel(""));

        // 1-2 음악 레이블
        JLabel musicLabel = new JLabel("", JLabel.CENTER);
        centerPanel.add(musicLabel);

        // 1-3 여백
        centerPanel.add(new JLabel(""));

        // 2-1 여백
        centerPanel.add(new JLabel(""));

        // 2-2 음악 슬라이더
        MusicS = new JSlider(0, 100, 50);
        MusicS.setPaintTicks(true);
        MusicS.setPaintLabels(true);
        MusicS.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateVolume();
            }
        });
        MusicS.setOpaque(false);
        centerPanel.add(MusicS);

        // 2-3 여백
        centerPanel.add(new JLabel(""));

        // 3-1 여백
        centerPanel.add(new JLabel(""));

        // 3-2 효과음 슬라이더
        SoundS = new JSlider(0, 100, 50);
        SoundS.setPaintTicks(true);
        SoundS.setPaintLabels(true);
        SoundS.setOpaque(false);
        centerPanel.add(SoundS);

        // 3-3 여백
        centerPanel.add(new JLabel(""));

        // 4-1 여백
        centerPanel.add(new JLabel(""));

        // 4-2 적용 버튼
        JButton ApplyButton = new JButton();
        ApplyButton.setPreferredSize(new Dimension(100, 50));
        ImageIcon icon = new ImageIcon(new ImageIcon("image/apply.png").getImage().getScaledInstance(150, 160, Image.SCALE_SMOOTH));
        ApplyButton.setIcon(icon);
        ApplyButton.setBorderPainted(false);
        ApplyButton.setContentAreaFilled(false);
        ApplyButton.setFocusPainted(false);

        ApplyButton.addActionListener(e -> {
            updateVolume();
            JOptionPane.showMessageDialog(this, "적용되었습니다!");
        });
        centerPanel.add(ApplyButton);

        BGPanel.add(centerPanel, BorderLayout.CENTER);

        add(BGPanel);

        setLocationRelativeTo(null);
        setVisible(true);

        new BGM();
    }

    private void updateVolume() {
        if (BgClip1 != null && VolumeGain != null) {
            float volume = -80.0f + (MusicS.getValue() * 0.76f);
            VolumeGain.setValue(volume);
        }
    }

    public class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                backgroundImage = new ImageIcon("image/SoundSetting.png").getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public class BGM {
        public BGM() {
            try {
                File audioFile = new File("bgm/bgm.wav");

                if (!audioFile.exists()) {
                    System.out.println("파일을 찾을 수 없습니다: " + audioFile.getAbsolutePath());
                    return;
                }

                AudioInputStream bgm = AudioSystem.getAudioInputStream(audioFile);
                BgClip1 = AudioSystem.getClip();
                BgClip1.open(bgm);

                VolumeGain = (FloatControl) BgClip1.getControl(FloatControl.Type.MASTER_GAIN);

                if (VolumeGain != null) {
                    VolumeGain.setValue(-30.0f);
                } else {
                    System.out.println("MASTER_GAIN을 지원하지 않습니다.");
                }

                BgClip1.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false); 
        topPanel.setBorder(BorderFactory.createEmptyBorder(150, 0, 0, 450)); // 여백

        ImageIcon closeIcon = new ImageIcon("image/closeicon1.png");
        ImageIcon hoverIcon = new ImageIcon("image/closeicon2.png");

        JButton closeButton = new JButton(closeIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getIcon() != null) {
                    g.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        closeButton.setPreferredSize(new Dimension(40, 40)); // 버튼 크기
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);

        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeButton.setIcon(hoverIcon);
                closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeButton.setIcon(closeIcon);
                closeButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        closeButton.addActionListener(e -> {
            dispose();
        });

        topPanel.add(closeButton);
        return topPanel;
    }

    public static void main(String[] args) {
        new SettingsNew();
    }
}