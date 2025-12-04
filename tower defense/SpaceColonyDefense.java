import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main class for Space Colony Defense game.
 * Integrates GameEngine (simulation backend) with GameGUI (visualization frontend).
 */
public class SpaceColonyDefense {
    private GameEngine engine;
    private GameGUI gui;
    private MainMenu mainMenu;
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    public SpaceColonyDefense() {
        engine = new GameEngine();
        gui = new GameGUI(engine);
        mainMenu = new MainMenu(new MainMenu.MainMenuListener() {
            @Override
            public void onStartGame() {
                startGame();
            }
            
            @Override
            public void onExit() {
                System.exit(0);
            }
        });
        
        setupFrame();
    }
    
    /**
     * Setup the main game window.
     */
    private void setupFrame() {
        frame = new JFrame("Antik Mısır Kalesi Savunma - Ancient Egypt Tower Defense");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Add menu and game panels
        mainPanel.add(mainMenu, "MENU");
        mainPanel.add(gui, "GAME");
        
        frame.add(mainPanel);
        
        // Set minimum size to ensure window is visible
        frame.setMinimumSize(new Dimension(800, 600));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        
        // Show menu first
        cardLayout.show(mainPanel, "MENU");
        
        // Force window to front
        frame.toFront();
        frame.repaint();
    }
    
    /**
     * Start the game.
     */
    private void startGame() {
        cardLayout.show(mainPanel, "GAME");
        gui.start();
        frame.requestFocus();
    }
    
    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                SpaceColonyDefense game = new SpaceColonyDefense();
                System.out.println("Antik Mısır Kalesi Savunma Oyunu başlatıldı! GUI penceresi açılmalı.");
            } catch (Exception e) {
                System.err.println("Hata oluştu: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}

