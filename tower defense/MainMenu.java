import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ana Menü ekranı - Oyunu Başlat ve Çıkış butonları içerir.
 */
public class MainMenu extends JPanel {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private JButton startButton;
    private JButton exitButton;
    private MainMenuListener listener;
    
    // Ancient Egypt theme colors
    private static final Color MENU_BG = new Color(40, 25, 15); // Desert sand brown
    private static final Color BUTTON_COLOR = new Color(218, 165, 32); // Goldenrod
    private static final Color BUTTON_HOVER = new Color(255, 215, 0); // Gold
    private static final Color TEXT_COLOR = new Color(255, 215, 0); // Gold text
    private static final Color TITLE_COLOR = new Color(255, 223, 0); // Golden yellow
    
    public interface MainMenuListener {
        void onStartGame();
        void onExit();
    }
    
    public MainMenu(MainMenuListener listener) {
        this.listener = listener;
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(MENU_BG);
        setLayout(new BorderLayout());
        
        setupUI();
    }
    
    private void setupUI() {
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BorderLayout());
        
        JLabel title = new JLabel("ANTIK MISIR KALESI SAVUNMA", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(TITLE_COLOR);
        titlePanel.add(title, BorderLayout.CENTER);
        
        JLabel subtitle = new JLabel("Ancient Egypt Tower Defense", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitle.setForeground(TEXT_COLOR);
        titlePanel.add(subtitle, BorderLayout.SOUTH);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        
        // Start button
        startButton = createButton("Oyunu Başlat", 200, 50);
        startButton.addActionListener(e -> {
            if (listener != null) {
                listener.onStartGame();
            }
        });
        gbc.gridy = 0;
        buttonPanel.add(startButton, gbc);
        
        // Exit button
        exitButton = createButton("Çıkış", 200, 50);
        exitButton.addActionListener(e -> {
            if (listener != null) {
                listener.onExit();
            }
        });
        gbc.gridy = 1;
        buttonPanel.add(exitButton, gbc);
        
        add(buttonPanel, BorderLayout.CENTER);
        
        // Instructions panel
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setOpaque(false);
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
        
        JLabel instructions = new JLabel("<html><center>Firavun'un kalesini savun!<br/>Kuleler inşa et ve istilacı orduları durdur!</center></html>", SwingConstants.CENTER);
        instructions.setFont(new Font("Arial", Font.PLAIN, 14));
        instructions.setForeground(TEXT_COLOR);
        instructionsPanel.add(instructions);
        
        add(instructionsPanel, BorderLayout.SOUTH);
    }
    
    private JButton createButton(String text, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_HOVER);
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - fm.getDescent();
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(width, height));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        
        return button;
    }
}

