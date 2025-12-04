import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;

/**
 * Game GUI - Live visualization frontend for the space colony defense game.
 * Displays all simulation events in real-time using Swing.
 *
 * This version draws a cartoon desert map inspired by the sample images
 * the user provided: sandy background, dunes, cactuses, bones and
 * numbered checkpoints along the enemy path.
 */
public class GameGUI extends JPanel implements ActionListener {
    private GameEngine engine;
    private Timer gameTimer;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final double FPS = 60.0;
    private static final double DELTA_TIME = 1.0 / FPS;
    
    // Tower placement mode
    private int selectedTowerType = -1; // 0: Archer, 1: Cannon, 2: Ice
    private boolean placingTower = false;
    
    // Game over state
    private boolean showingGameOver = false;
    
    // --- Desert UI Colors ---
    // Background sand tones
    private static final Color SAND_BG      = new Color(244, 220, 162); // general background
    private static final Color SAND_LIGHT   = new Color(255, 236, 179); // light sand
    private static final Color SAND_DARK    = new Color(222, 184, 135); // darker sand
    // Sky gradient
    private static final Color SKY_TOP      = new Color(135, 206, 235); // light sky blue
    private static final Color SKY_BOTTOM   = new Color(255, 244, 214); // near horizon
    // Path colors (desert road)
    private static final Color PATH_LIGHT   = new Color(250, 210, 140); // light path sand
    private static final Color PATH_DARK    = new Color(198, 140, 83);  // darker path border
    // Enemy colors
    private static final Color STANDARD_ENEMY_COLOR = new Color(97, 67, 38);  // brown scarab/goblin
    private static final Color ARMORED_ENEMY_COLOR  = new Color(60, 42, 29);  // dark stone golem
    private static final Color FLYING_ENEMY_COLOR   = new Color(171, 120, 75); // desert vulture
    // UI / base colors
    private static final Color TEXT_COLOR   = new Color(255, 255, 255); // white text
    private static final Color BASE_COLOR   = new Color(255, 223, 0);   // golden pyramid
    
    public GameGUI(GameEngine engine) {
        this.engine = engine;
        
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(SAND_BG);
        
        // Mouse listener for tower placement and button clicks
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                
                // Check if clicking on tower buttons (at bottom of screen)
                int buttonX = WINDOW_WIDTH - 250;
                int buttonY = WINDOW_HEIGHT - 160; // Match drawUI position
                int buttonWidth = 200;
                int buttonHeight = 40; // Match actual button height
                boolean clickedButton = false;
                
                for (int i = 0; i < 3; i++) {
                    int by = buttonY + i * 50; // Match spacing in drawUI
                    if (x >= buttonX && x <= buttonX + buttonWidth && 
                        y >= by && y <= by + buttonHeight) {
                        // Check if button is enabled (enough gold)
                        int cost = 0;
                        if (i == 0) cost = 50;
                        else if (i == 1) cost = 75;
                        else if (i == 2) cost = 70;
                        
                        if (engine.getEnergyCore() >= cost) {
                            selectedTowerType = i;
                            placingTower = true;
                            clickedButton = true;
                            repaint();
                        }
                        break;
                    }
                }
                
                // If not clicking button and in tower placement mode, place tower
                if (!clickedButton && placingTower && selectedTowerType >= 0) {
                    placeTowerAt(x, y);
                }
                
                // Check if clicking on "Yeniden Oyna" button when game is over
                if (showingGameOver) {
                    int restartButtonCenterX = WINDOW_WIDTH / 2;
                    int restartButtonCenterY = WINDOW_HEIGHT / 2 + 80;
                    int restartButtonWidth = 200;
                    int restartButtonHeight = 50;
                    
                    if (x >= restartButtonCenterX - restartButtonWidth/2 && 
                        x <= restartButtonCenterX + restartButtonWidth/2 &&
                        y >= restartButtonCenterY - restartButtonHeight/2 && 
                        y <= restartButtonCenterY + restartButtonHeight/2) {
                        restartGame();
                    }
                }
            }
        });
        
        // Initialize game timer
        gameTimer = new Timer((int)(1000 / FPS), this);
    }
    
    /**
     * Start the game loop.
     */
    public void start() {
        showingGameOver = false;
        engine.initializeGame();
        engine.startNextWave();
        gameTimer.start();
    }
    
    /**
     * Restart the game.
     */
    public void restartGame() {
        showingGameOver = false;
        selectedTowerType = -1;
        placingTower = false;
        engine.resetGame();
        gameTimer.start();
        repaint();
    }
    
    /**
     * Stop the game loop.
     */
    public void stop() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (engine.isGameRunning()) {
            engine.update(DELTA_TIME);
            
            // Check if wave is complete and start next wave
            if (engine.getEnemies().isEmpty() && engine.getCurrentWave() < engine.getTotalWaves()) {
                engine.startNextWave();
            }
        }
        
        // Check if game ended
        if ((engine.isGameWon() || engine.isGameLost()) && !showingGameOver) {
            showingGameOver = true;
            gameTimer.stop();
        }
        
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        try {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Enable high-quality rendering
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Draw desert background
            drawBackground(g2d);
            
            // Draw path with texture + numbered checkpoints
            drawPath(g2d);
            
            // Draw base (pyramid) with detail
            drawBase(g2d);
            
            // Draw towers with shadows
            drawTowers(g2d);
            
            // Draw enemies with detail
            drawEnemies(g2d);
            
            // Draw UI panel and tower buttons
            drawUI(g2d);
            
            // Draw game over message
            if (engine.isGameWon() || engine.isGameLost()) {
                drawGameOver(g2d);
            }
        } catch (Exception ex) {
            System.err.println("Paint hatası: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Draw cartoon desert background: sky, sun, dunes, cactuses, bones.
     */
    private void drawBackground(Graphics2D g2d) {
        // --- Sky ---
        GradientPaint sky = new GradientPaint(
            0, 0, SKY_TOP,
            0, WINDOW_HEIGHT / 2, SKY_BOTTOM
        );
        g2d.setPaint(sky);
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT / 2);
        
        // Sun
        g2d.setColor(new Color(255, 245, 179));
        g2d.fillOval(WINDOW_WIDTH - 160, 30, 90, 90);
        g2d.setColor(new Color(255, 255, 224, 160));
        g2d.fillOval(WINDOW_WIDTH - 175, 15, 120, 120);
        
        // --- Sand ground ---
        GradientPaint sand = new GradientPaint(
            0, WINDOW_HEIGHT / 3, SAND_LIGHT,
            0, WINDOW_HEIGHT, SAND_DARK
        );
        g2d.setPaint(sand);
        g2d.fillRect(0, WINDOW_HEIGHT / 3, WINDOW_WIDTH, WINDOW_HEIGHT * 2 / 3);
        
        Random rand = new Random(42); // fixed pattern
        
        // Dunes (large soft ovals)
        for (int i = 0; i < 7; i++) {
            int w = 160 + rand.nextInt(120);
            int h = 45 + rand.nextInt(20);
            int x = rand.nextInt(WINDOW_WIDTH + 100) - 50;
            int y = WINDOW_HEIGHT / 2 + rand.nextInt(WINDOW_HEIGHT / 3);
            
            g2d.setColor(new Color(241, 210, 146));
            g2d.fillOval(x, y, w, h);
            g2d.setColor(new Color(214, 176, 108, 160));
            g2d.drawOval(x, y, w, h);
        }
        
        // Rocks
        for (int i = 0; i < 25; i++) {
            int size = 10 + rand.nextInt(12);
            int x = rand.nextInt(WINDOW_WIDTH);
            int y = WINDOW_HEIGHT / 2 + rand.nextInt(WINDOW_HEIGHT / 2 - 20);
            g2d.setColor(new Color(189, 155, 107));
            g2d.fillOval(x, y, size, size / 2);
            g2d.setColor(new Color(150, 120, 82));
            g2d.drawOval(x, y, size, size / 2);
        }
        
        // Cactuses
        for (int i = 0; i < 12; i++) {
            int x = 40 + rand.nextInt(WINDOW_WIDTH - 80);
            int y = WINDOW_HEIGHT / 2 + 40 + rand.nextInt(WINDOW_HEIGHT / 2 - 60);
            drawCactus(g2d, x, y, rand);
        }
        
        // Bone piles
        for (int i = 0; i < 6; i++) {
            int x = 50 + rand.nextInt(WINDOW_WIDTH - 100);
            int y = WINDOW_HEIGHT / 2 + 40 + rand.nextInt(WINDOW_HEIGHT / 2 - 80);
            drawBones(g2d, x, y, rand);
        }
    }
    
    /**
     * Draw a small cactus similar to the reference images.
     */
    private void drawCactus(Graphics2D g2d, int x, int y, Random rand) {
        int height = 28 + rand.nextInt(18);
        int armHeight = 12 + rand.nextInt(10);
        
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 90));
        g2d.fillOval(x - 14, y - 4, 28, 8);
        
        // Main body
        g2d.setColor(new Color(46, 142, 72));
        g2d.fillRoundRect(x - 6, y - height, 12, height, 8, 8);
        
        // Arms (left & right)
        g2d.fillRoundRect(x - 16, y - height / 2, 10, armHeight, 8, 8);
        g2d.fillRoundRect(x + 6, y - height / 2, 10, armHeight, 8, 8);
        
        // Highlights
        g2d.setColor(new Color(71, 173, 96));
        g2d.drawLine(x - 3, y - height + 4, x - 3, y - 6);
        g2d.drawLine(x + 3, y - height + 4, x + 3, y - 6);
    }
    
    /**
     * Draw a small pile of cartoon bones.
     */
    private void drawBones(Graphics2D g2d, int x, int y, Random rand) {
        g2d.setColor(new Color(250, 245, 230));
        // Two crossed bones
        for (int i = 0; i < 2; i++) {
            double angle = (i == 0) ? Math.toRadians(25) : Math.toRadians(-25);
            int len = 26;
            int bx1 = x - (int)(Math.cos(angle) * len / 2);
            int by1 = y - (int)(Math.sin(angle) * len / 2);
            int bx2 = x + (int)(Math.cos(angle) * len / 2);
            int by2 = y + (int)(Math.sin(angle) * len / 2);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine(bx1, by1, bx2, by2);
            g2d.fillOval(bx1 - 4, by1 - 4, 8, 8);
            g2d.fillOval(bx2 - 4, by2 - 4, 8, 8);
        }
        
        // Tiny skull
        g2d.setColor(new Color(252, 248, 235));
        g2d.fillOval(x - 7, y - 18, 14, 12);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x - 4, y - 15, 3, 3);
        g2d.fillOval(x + 1, y - 15, 3, 3);
        g2d.drawArc(x - 4, y - 11, 8, 4, 0, -180);
    }
    
    /**
     * Draw the enemy path with desert dirt texture + numbered checkpoints.
     */
    private void drawPath(Graphics2D g2d) {
        Path path = engine.getPath();
        List<Path.Point> waypoints = path.getWaypoints();
        Random rand = new Random(123); // Fixed seed for consistent texture
        
        if (waypoints.isEmpty()) {
            return;
        }
        
        // Draw path shadow first (darker, offset)
        g2d.setColor(new Color(0, 0, 0, 90));
        g2d.setStroke(new BasicStroke(34, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Path.Point p1 = waypoints.get(i);
            Path.Point p2 = waypoints.get(i + 1);
            g2d.drawLine((int)p1.x + 3, (int)p1.y + 3, (int)p2.x + 3, (int)p2.y + 3);
        }
        
        // Draw main path with sandy gradient
        g2d.setStroke(new BasicStroke(32, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Path.Point p1 = waypoints.get(i);
            Path.Point p2 = waypoints.get(i + 1);
            
            GradientPaint pathGradient = new GradientPaint(
                (int)p1.x, (int)p1.y, PATH_LIGHT,
                (int)p2.x, (int)p2.y, PATH_DARK
            );
            g2d.setPaint(pathGradient);
            g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
            
            // Dirt spots and stones on path
            for (int j = 0; j < 8; j++) {
                double t = j / 8.0;
                int ox = (int)(p1.x + (p2.x - p1.x) * t);
                int oy = (int)(p1.y + (p2.y - p1.y) * t);
                if (rand.nextDouble() > 0.5) {
                    g2d.setColor(new Color(193, 154, 107, 160));
                    int size = 2 + rand.nextInt(3);
                    g2d.fillOval(ox - size, oy - size, size * 2, size * 2);
                }
            }
        }
        
        // Path edge outline
        g2d.setColor(PATH_DARK.darker());
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Path.Point p1 = waypoints.get(i);
            Path.Point p2 = waypoints.get(i + 1);
            g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
        }
        
        // Start marker: wooden entry sign
        Path.Point start = waypoints.get(0);
        g2d.setColor(new Color(120, 82, 45));
        g2d.fillRoundRect((int)start.x - 30, (int)start.y - 24, 60, 40, 10, 10);
        g2d.setColor(new Color(80, 50, 25));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect((int)start.x - 30, (int)start.y - 24, 60, 40, 10, 10);
        g2d.setColor(new Color(255, 235, 195));
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String startText = "START";
        g2d.drawString(startText, (int)start.x - fm.stringWidth(startText)/2, (int)start.y);
        
        // Numbered checkpoints like in the sample map
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        for (int i = 0; i < waypoints.size(); i++) {
            Path.Point p = waypoints.get(i);
            int r = 16;
            int cx = (int)p.x;
            int cy = (int)p.y;
            
            // Coin-like circle
            g2d.setColor(new Color(255, 215, 0));
            g2d.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2d.setColor(new Color(184, 134, 11));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(cx - r, cy - r, r * 2, r * 2);
            
            // Number
            String num = String.valueOf(i + 1);
            FontMetrics fmNum = g2d.getFontMetrics();
            int tx = cx - fmNum.stringWidth(num) / 2;
            int ty = cy + fmNum.getAscent() / 2 - 3;
            g2d.setColor(Color.BLACK);
            g2d.drawString(num, tx + 1, ty + 1);
            g2d.setColor(Color.WHITE);
            g2d.drawString(num, tx, ty);
        }
    }
    
    /**
     * Draw the base (Pharaoh's Palace) with pyramid details.
     */
    private void drawBase(Graphics2D g2d) {
        Path path = engine.getPath();
        Path.Point base = path.getBase();
        int x = (int)base.x;
        int y = (int)base.y;
        
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillOval(x - 35, y + 20, 70, 20);
        
        // Main structure with gradient
        GradientPaint palaceGradient = new GradientPaint(
            x - 35, y - 35, BASE_COLOR,
            x - 35, y + 25, new Color(218, 165, 32)
        );
        g2d.setPaint(palaceGradient);
        g2d.fillRect(x - 35, y - 35, 70, 60);
        
        // Pyramid layers
        int[] xPoints = {x, x - 35, x + 35};
        int[] yPoints = {y - 35, y + 5, y + 5};
        g2d.setColor(new Color(255, 223, 0));
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Top layer
        int[] xTop = {x, x - 20, x + 20};
        int[] yTop = {y - 25, y - 5, y - 5};
        g2d.setColor(new Color(255, 255, 200));
        g2d.fillPolygon(xTop, yTop, 3);
        
        // Borders and details
        g2d.setColor(new Color(184, 134, 11));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(x - 35, y - 35, 70, 60);
        g2d.drawPolygon(xPoints, yPoints, 3);
        g2d.drawPolygon(xTop, yTop, 3);
        
        // Windows/Doors
        g2d.setColor(new Color(139, 101, 8));
        g2d.fillRect(x - 5, y + 10, 10, 15);
        g2d.fillRect(x - 25, y - 5, 8, 8);
        g2d.fillRect(x + 17, y - 5, 8, 8);
        
        // Label with shadow
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.BLACK);
        g2d.drawString("KALE", x - 20, y + 50);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("KALE", x - 21, y + 49);
    }
    
    /**
     * Draw all towers.
     */
    private void drawTowers(Graphics2D g2d) {
        List<Tower> towers = engine.getTowers();
        
        for (Tower tower : towers) {
            drawTower(g2d, tower);
        }
    }
    
    /**
     * Draw a single tower with stone base.
     */
    private void drawTower(Graphics2D g2d, Tower tower) {
        int x = (int) tower.getX();
        int y = (int) tower.getY();
        String type = tower.getTowerType();
        
        // Draw range circle (subtle, only when placing)
        if (placingTower && selectedTowerType >= 0) {
            g2d.setColor(new Color(255, 255, 0, 20));
            g2d.fillOval(x - (int)tower.getTargetingRange(), y - (int)tower.getTargetingRange(),
                        (int)(tower.getTargetingRange() * 2), (int)(tower.getTargetingRange() * 2));
            g2d.setColor(new Color(255, 255, 0, 80));
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawOval(x - (int)tower.getTargetingRange(), y - (int)tower.getTargetingRange(),
                        (int)(tower.getTargetingRange() * 2), (int)(tower.getTargetingRange() * 2));
        }
        
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillOval(x - 15, y + 12, 30, 10);
        
        // Tower base (stone)
        GradientPaint towerGradient = new GradientPaint(
            x - 20, y - 20, new Color(160, 160, 160),
            x - 20, y + 15, new Color(100, 100, 100)
        );
        g2d.setPaint(towerGradient);
        g2d.fillRoundRect(x - 20, y - 20, 40, 35, 8, 8);
        
        // Stone texture
        g2d.setColor(new Color(120, 120, 120, 120));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(x - 18, y - 10, x + 18, y - 10);
        g2d.drawLine(x - 18, y, x + 18, y);
        g2d.drawLine(x - 18, y + 10, x + 18, y + 10);
        g2d.drawLine(x - 10, y - 20, x - 10, y + 15);
        g2d.drawLine(x + 10, y - 20, x + 10, y + 15);
        
        // Tower type specific drawing
        if (type.contains("Okcu")) {
            // Archer tower with bow
            g2d.setColor(new Color(140, 140, 140));
            g2d.fillRect(x - 16, y - 28, 32, 8);
            g2d.setColor(new Color(120, 80, 40));
            g2d.setStroke(new BasicStroke(4));
            g2d.drawArc(x - 8, y - 32, 16, 12, 0, 180);
            g2d.setColor(new Color(230, 230, 230));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(x - 8, y - 26, x + 8, y - 26);
            g2d.setColor(new Color(139, 90, 43));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y - 28, x, y - 20);
            int[] arrowTipX = {x, x - 2, x + 2};
            int[] arrowTipY = {y - 28, y - 25, y - 25};
            g2d.setColor(new Color(192, 192, 192));
            g2d.fillPolygon(arrowTipX, arrowTipY, 3);
        } else if (type.contains("Topcu")) {
            // Cannon tower
            g2d.setColor(new Color(140, 140, 140));
            g2d.fillRect(x - 16, y - 28, 32, 8);
            g2d.setColor(new Color(70, 70, 70));
            g2d.fillOval(x - 8, y - 26, 16, 10);
            g2d.setColor(new Color(170, 170, 170));
            g2d.setStroke(new BasicStroke(5));
            g2d.drawLine(x + 8, y - 22, x + 22, y - 25);
            g2d.setColor(new Color(30, 30, 30));
            g2d.fillOval(x + 18, y - 27, 6, 6);
            g2d.setColor(new Color(110, 110, 110));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawOval(x - 8, y - 26, 16, 10);
        } else if (type.contains("Buz")) {
            // Ice tower with crystal
            g2d.setColor(new Color(140, 140, 140));
            g2d.fillRect(x - 16, y - 28, 32, 8);
            int[] crystalX = {x, x - 6, x + 6, x, x - 4, x + 4};
            int[] crystalY = {y - 30, y - 24, y - 24, y - 20, y - 22, y - 22};
            g2d.setColor(new Color(173, 216, 230, 180));
            g2d.fillPolygon(crystalX, crystalY, 6);
            g2d.setColor(new Color(135, 206, 250));
            int[] innerX = {x, x - 4, x + 4, x, x - 2, x + 2};
            int[] innerY = {y - 28, y - 24, y - 24, y - 21, y - 23, y - 23};
            g2d.fillPolygon(innerX, innerY, 6);
            g2d.setColor(new Color(100, 150, 200));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawPolygon(crystalX, crystalY, 6);
        }
        
        // Tower border
        g2d.setColor(new Color(70, 70, 70));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x - 20, y - 20, 40, 35, 8, 8);
        g2d.setColor(new Color(130, 130, 130));
        g2d.drawRect(x - 16, y - 28, 32, 8);
    }
    
    /**
     * Draw all enemies.
     */
    private void drawEnemies(Graphics2D g2d) {
        List<Enemy> enemies = engine.getEnemies();
        
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                drawEnemy(g2d, enemy);
            }
        }
    }
    
    /**
     * Draw a single enemy (desert-themed).
     */
    private void drawEnemy(Graphics2D g2d, Enemy enemy) {
        int x = (int) enemy.getX();
        int y = (int) enemy.getY();
        
        // Determine color based on enemy type and effects
        Color enemyColor;
        if (enemy instanceof FlyingEnemy) {
            enemyColor = FLYING_ENEMY_COLOR;
        } else if (enemy instanceof ArmoredEnemy) {
            enemyColor = ARMORED_ENEMY_COLOR;
        } else {
            enemyColor = STANDARD_ENEMY_COLOR;
        }
        
        // Apply ice tower effect - blue tint if slowed
        if (enemy.hasSlowEffect()) {
            enemyColor = new Color(
                Math.max(0, Math.min(255, enemyColor.getRed() - 40)),
                Math.max(0, Math.min(255, enemyColor.getGreen() + 40)),
                Math.max(0, Math.min(255, enemyColor.getBlue() + 120))
            );
        }
        
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillOval(x - 10, y + 8, 20, 6);
        
        if (enemy.isFlying()) {
            // Vulture-like flying enemy
            GradientPaint bodyGrad = new GradientPaint(
                x, y - 8, enemyColor.brighter(),
                x, y + 8, enemyColor.darker()
            );
            g2d.setPaint(bodyGrad);
            g2d.fillOval(x - 8, y - 3, 16, 10);
            
            g2d.setColor(enemyColor);
            g2d.fillOval(x - 15, y - 5, 12, 8);
            g2d.fillOval(x + 3, y - 5, 12, 8);
            
            g2d.setColor(enemyColor.darker());
            g2d.fillOval(x - 6, y - 8, 8, 6);
            
            int[] beakX = {x - 2, x + 2, x};
            int[] beakY = {y - 9, y - 9, y - 7};
            g2d.setColor(new Color(255, 193, 7));
            g2d.fillPolygon(beakX, beakY, 3);
            
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x - 3, y - 7, 3, 3);
            g2d.fillOval(x, y - 7, 3, 3);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(x - 2, y - 6, 2, 2);
            g2d.fillOval(x + 1, y - 6, 2, 2);
            
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(x - 8, y - 3, 16, 10);
            g2d.drawOval(x - 15, y - 5, 12, 8);
            g2d.drawOval(x + 3, y - 5, 12, 8);
        } else if (enemy instanceof ArmoredEnemy) {
            // Golem / armored monster
            GradientPaint grad = new GradientPaint(
                x - 20, y - 15, new Color(80, 70, 60),
                x - 20, y + 15, new Color(40, 30, 25)
            );
            g2d.setPaint(grad);
            g2d.fillOval(x - 22, y - 18, 44, 36);
            
            g2d.setColor(new Color(60, 50, 40));
            g2d.fillOval(x - 8, y - 22, 20, 16);
            
            int[] snoutX = {x + 8, x + 16, x + 12};
            int[] snoutY = {y - 18, y - 16, y - 12};
            g2d.fillPolygon(snoutX, snoutY, 3);
            
            g2d.setColor(new Color(255, 87, 34));
            g2d.fillOval(x - 2, y - 20, 6, 6);
            g2d.fillOval(x + 4, y - 20, 6, 6);
            
            g2d.setColor(new Color(40, 30, 25));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - 22, y - 18, 44, 36);
            g2d.drawOval(x - 8, y - 22, 20, 16);
        } else {
            // Standard desert bandit/goblin
            GradientPaint grad = new GradientPaint(
                x - 10, y - 10, new Color(214, 162, 92),
                x - 10, y + 10, new Color(158, 113, 68)
            );
            g2d.setPaint(grad);
            g2d.fillOval(x - 12, y - 12, 24, 24);
            
            g2d.setColor(new Color(214, 184, 153));
            g2d.fillOval(x - 7, y - 18, 14, 12);
            
            g2d.setColor(new Color(97, 67, 38));
            int[] leftEarX = {x - 7, x - 11, x - 8};
            int[] leftEarY = {y - 18, y - 22, y - 16};
            int[] rightEarX = {x + 7, x + 11, x + 8};
            int[] rightEarY = {y - 18, y - 22, y - 16};
            g2d.fillPolygon(leftEarX, leftEarY, 3);
            g2d.fillPolygon(rightEarX, rightEarY, 3);
            
            g2d.setColor(new Color(255, 193, 7));
            g2d.fillOval(x - 4, y - 16, 3, 3);
            g2d.fillOval(x + 1, y - 16, 3, 3);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(x - 3, y - 15, 1, 1);
            g2d.fillOval(x + 2, y - 15, 1, 1);
            
            g2d.setColor(new Color(189, 155, 107));
            g2d.fillOval(x - 1, y - 13, 2, 2);
            
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawArc(x - 3, y - 11, 6, 4, 0, 180);
            
            // Simple scimitar
            g2d.setColor(new Color(224, 224, 224));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(x + 12, y - 10, x + 14, y + 6);
            int[] tipX = {x + 14, x + 18, x + 16};
            int[] tipY = {y - 10, y - 6, y - 4};
            g2d.fillPolygon(tipX, tipY, 3);
            g2d.setColor(new Color(120, 80, 40));
            g2d.fillRect(x + 11, y + 4, 4, 5);
            
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - 12, y - 12, 24, 24);
            g2d.drawOval(x - 7, y - 18, 14, 12);
        }
        
        // Health bar
        double currentHP = enemy.getShieldIntegrity();
        double maxHP = enemy.getMaxShieldIntegrity();
        double healthPercent = currentHP / maxHP;
        int barWidth = 24;
        int barHeight = 5;
        
        g2d.setColor(new Color(150, 0, 0));
        g2d.fillRect(x - barWidth/2, y - 25, barWidth, barHeight);
        
        if (healthPercent > 0) {
            g2d.setColor(new Color(0, (int)(255 * healthPercent), 0));
            g2d.fillRect(x - barWidth/2, y - 25, (int)(barWidth * healthPercent), barHeight);
        }
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(x - barWidth/2, y - 25, barWidth, barHeight);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 8));
        String healthText = String.format("%.0f/%.0f", currentHP, maxHP);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x - fm.stringWidth(healthText) / 2;
        g2d.setColor(Color.WHITE);
        g2d.drawString(healthText, textX, y - 28);
    }
    
    /**
     * Draw UI information panel + bottom tower buttons.
     */
    private void drawUI(Graphics2D g2d) {
        // Left info panel (scroll-like)
        g2d.setColor(new Color(92, 64, 51, 220));
        g2d.fillRoundRect(5, 5, 250, 130, 12, 12);
        
        g2d.setColor(new Color(139, 90, 43));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(5, 5, 250, 130, 12, 12);
        
        g2d.setColor(new Color(210, 180, 140, 120));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(8, 8, 244, 124, 10, 10);
        
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        
        int yPos = 30;
        // Health icon
        g2d.setColor(new Color(220, 20, 60));
        int[] heartX = {20, 20, 24, 28, 28, 24, 20};
        int[] heartY = {yPos - 8, yPos - 12, yPos - 15, yPos - 12, yPos - 8, yPos - 5, yPos - 8};
        g2d.fillPolygon(heartX, heartY, 7);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("Kale Savunması: " + engine.getPlayerShieldIntegrity() + "/150", 35, yPos);
        yPos += 25;
        
        // Gold icon
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillOval(15, yPos - 10, 12, 12);
        g2d.setColor(new Color(255, 255, 200));
        g2d.fillOval(17, yPos - 8, 8, 8);
        g2d.setColor(new Color(184, 134, 11));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(15, yPos - 10, 12, 12);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("Altın: " + engine.getEnergyCore(), 30, yPos);
        yPos += 25;
        
        // Wave info
        g2d.setColor(new Color(100, 150, 255));
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("~", 15, yPos);
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Dalga: " + engine.getCurrentWave() + " / " + engine.getTotalWaves(), 30, yPos);
        yPos += 25;
        
        // Enemy count
        g2d.setColor(new Color(192, 192, 192));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(18, yPos - 8, 18, yPos + 2);
        int[] swordTipX = {18, 16, 20};
        int[] swordTipY = {yPos - 8, yPos - 6, yPos - 6};
        g2d.fillPolygon(swordTipX, swordTipY, 3);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("Düşman: " + engine.getEnemies().size(), 30, yPos);
        
        // Tower buttons at bottom
        int buttonX = WINDOW_WIDTH - 250;
        int buttonY = WINDOW_HEIGHT - 160;
        drawTowerButton(g2d, "Okçu Kulesi 50", buttonX, buttonY, 0);
        drawTowerButton(g2d, "Topçu Kulesi 75", buttonX, buttonY + 50, 1);
        drawTowerButton(g2d, "Buz Kulesi 70", buttonX, buttonY + 100, 2);
        
        // Instructions stripe
        g2d.setColor(new Color(92, 64, 51, 220));
        g2d.fillRoundRect(5, WINDOW_HEIGHT - 55, WINDOW_WIDTH - 10, 50, 12, 12);
        g2d.setColor(new Color(139, 90, 43));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(5, WINDOW_HEIGHT - 55, WINDOW_WIDTH - 10, 50, 12, 12);
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.PLAIN, 13));
        g2d.drawString("Kule yerleştirmek için butona tıklayın, sonra haritaya tıklayın", 15, WINDOW_HEIGHT - 25);
    }
    
    /**
     * Draw a tower selection button.
     */
    private void drawTowerButton(Graphics2D g2d, String text, int x, int y, int type) {
        boolean isSelected = (selectedTowerType == type);
        
        int cost = 0;
        if (type == 0) cost = 50;
        else if (type == 1) cost = 75;
        else if (type == 2) cost = 70;
        
        boolean isDisabled = (engine.getEnergyCore() < cost);
        
        Color bgTop, bgBottom;
        Color borderColor;
        Color textColor;
        
        if (isDisabled) {
            bgTop = new Color(90, 90, 90);
            bgBottom = new Color(60, 60, 60);
            borderColor = new Color(70, 70, 70);
            textColor = new Color(140, 140, 140);
        } else if (isSelected) {
            bgTop = new Color(249, 215, 138);
            bgBottom = new Color(214, 176, 108);
            borderColor = new Color(255, 215, 0);
            textColor = Color.BLACK;
        } else {
            bgTop = new Color(193, 154, 107);
            bgBottom = new Color(158, 113, 68);
            borderColor = new Color(139, 90, 43);
            textColor = TEXT_COLOR;
        }
        
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(x + 3, y + 3, 200, 40, 8, 8);
        
        // Button fill
        GradientPaint buttonGrad = new GradientPaint(
            x, y, bgTop,
            x, y + 40, bgBottom
        );
        g2d.setPaint(buttonGrad);
        g2d.fillRoundRect(x, y, 200, 40, 8, 8);
        
        // Border
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(x, y, 200, 40, 8, 8);
        
        // Inner highlight
        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x + 2, y + 2, 196, 36, 6, 6);
        
        // Text
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, x + 10, y + 26);
        g2d.setColor(textColor);
        g2d.drawString(text, x + 9, y + 25);
        
        // Small tower icon
        int iconX = x + 165;
        int iconY = y + 8;
        if (!isDisabled) {
            g2d.setColor(new Color(120, 120, 120));
            g2d.fillRect(iconX, iconY, 12, 16);
            g2d.setColor(new Color(90, 90, 90));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(iconX, iconY, 12, 16);
            if (type == 0) {
                g2d.setColor(new Color(139, 90, 43));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawLine(iconX + 6, iconY + 2, iconX + 6, iconY + 10);
            } else if (type == 1) {
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(iconX + 8, iconY + 4, iconX + 12, iconY + 6);
            } else if (type == 2) {
                g2d.setColor(new Color(135, 206, 250));
                int[] iconCrystalX = {iconX + 6, iconX + 4, iconX + 8};
                int[] iconCrystalY = {iconY + 2, iconY + 6, iconY + 6};
                g2d.fillPolygon(iconCrystalX, iconCrystalY, 3);
            }
        }
    }
    
    /**
     * Draw game over message with restart button.
     */
    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        String message = engine.isGameWon() ? "KAZANDINIZ!" : "KAYBETTİNİZ!";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(message, (WINDOW_WIDTH - textWidth) / 2 + 3, WINDOW_HEIGHT / 2 + 3);
        
        g2d.setColor(engine.isGameWon() ? new Color(0, 255, 0) : new Color(255, 0, 0));
        g2d.drawString(message, (WINDOW_WIDTH - textWidth) / 2, WINDOW_HEIGHT / 2);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        String subtitle = engine.isGameWon() ? 
            "Tüm istilacı orduları durdurdunuz!" : 
            "Kale savunması tükendi!";
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(subtitle);
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(subtitle, (WINDOW_WIDTH - textWidth) / 2, WINDOW_HEIGHT / 2 + 40);
        
        int buttonCenterX = WINDOW_WIDTH / 2;
        int buttonCenterY = WINDOW_HEIGHT / 2 + 80;
        int buttonWidth = 200;
        int buttonHeight = 50;
        
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(buttonCenterX - buttonWidth/2 + 3, buttonCenterY - buttonHeight/2 + 3, 
                         buttonWidth, buttonHeight, 10, 10);
        
        GradientPaint buttonGradient = new GradientPaint(
            buttonCenterX - buttonWidth/2, buttonCenterY - buttonHeight/2, 
            new Color(249, 215, 138),
            buttonCenterX - buttonWidth/2, buttonCenterY + buttonHeight/2, 
            new Color(214, 176, 108)
        );
        g2d.setPaint(buttonGradient);
        g2d.fillRoundRect(buttonCenterX - buttonWidth/2, buttonCenterY - buttonHeight/2, 
                         buttonWidth, buttonHeight, 10, 10);
        
        g2d.setColor(new Color(218, 165, 32));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(buttonCenterX - buttonWidth/2, buttonCenterY - buttonHeight/2, 
                         buttonWidth, buttonHeight, 10, 10);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String buttonText = "YENİDEN OYNA";
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(buttonText);
        
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(buttonText, buttonCenterX - textWidth/2 + 2, buttonCenterY + 8);
        
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(buttonText, buttonCenterX - textWidth/2, buttonCenterY + 7);
    }
    
    /**
     * Place tower at position.
     */
    private void placeTowerAt(int x, int y) {
        if (selectedTowerType < 0 || !placingTower) {
            return;
        }
        
        // Check if position is on path
        Path path = engine.getPath();
        List<Path.Point> waypoints = path.getWaypoints();
        boolean onPath = false;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Path.Point p1 = waypoints.get(i);
            Path.Point p2 = waypoints.get(i + 1);
            double dist = distanceToLineSegment(x, y, p1.x, p1.y, p2.x, p2.y);
            if (dist < 30) {
                onPath = true;
                break;
            }
        }
        
        if (onPath) {
            JOptionPane.showMessageDialog(this, "Kule yola yerleştirilemez!");
            return;
        }
        
        Tower tower = null;
        switch (selectedTowerType) {
            case 0:
                tower = new ArcherTower(x, y);
                break;
            case 1:
                tower = new CannonTower(x, y);
                break;
            case 2:
                tower = new IceTower(x, y);
                break;
        }
        
        if (tower != null) {
            boolean success = engine.placeTower(tower);
            if (success) {
                placingTower = false;
                selectedTowerType = -1;
                repaint();
            }
        }
    }
    
    /**
     * Calculate distance from point to line segment.
     */
    private double distanceToLineSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;
        
        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = -1;
        
        if (lenSq != 0) {
            param = dot / lenSq;
        }
        
        double xx, yy;
        
        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }
        
        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
