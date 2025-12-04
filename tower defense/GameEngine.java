import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Game Engine - Simulation backend for the space colony defense game.
 * Handles enemy movement, tower targeting, damage/armor calculations,
 * wave management, and game state updates.
 */
public class GameEngine {
    private List<Enemy> enemies;
    private List<Tower> towers;
    private Path path;
    
    private int kaleSavunmasi; // OYUNCUNUN CANI (Firavun'un Kalesi)
    private int altinHazinesi; // PARA (Antik Mısır Altını)
    private int currentWave;
    private int totalWaves;
    private boolean gameRunning;
    private boolean gameWon;
    private boolean gameLost;
    private double gameTime; // Game time in seconds
    
    private PrintWriter logWriter;
    
    // Encapsulation: private game state
    private Random random;
    
    public GameEngine() {
        enemies = new ArrayList<>();
        towers = new ArrayList<>();
        path = new Path();
        
        kaleSavunmasi = 150;
        altinHazinesi = 300;
        currentWave = 0;
        totalWaves = 4;
        gameRunning = false;
        gameWon = false;
        gameLost = false;
        gameTime = 0.0;
        random = new Random();
        
        try {
            logWriter = new PrintWriter(new FileWriter("savunma_gunlugu.txt", true));
        } catch (IOException e) {
            System.err.println("Kayıt dosyası oluşturulamadı: " + e.getMessage());
        }
    }
    
    // Encapsulation: Getters
    public List<Enemy> getEnemies() { return new ArrayList<>(enemies); }
    public List<Tower> getTowers() { return new ArrayList<>(towers); }
    public Path getPath() { return path; }
    public int getPlayerShieldIntegrity() { return kaleSavunmasi; }
    public int getEnergyCore() { return altinHazinesi; }
    public int getCurrentWave() { return currentWave; }
    public int getTotalWaves() { return totalWaves; }
    public boolean isGameRunning() { return gameRunning; }
    public boolean isGameWon() { return gameWon; }
    public boolean isGameLost() { return gameLost; }
    public double getGameTime() { return gameTime; }
    
    /**
     * Initialize game at start.
     */
    public void initializeGame() {
        kaleSavunmasi = 150;
        altinHazinesi = 300;
        currentWave = 0;
        gameRunning = true;
        gameWon = false;
        gameLost = false;
        gameTime = 0.0;
        enemies.clear();
        towers.clear();
        
        logMessage("=== Antik Mısır Kalesi Savunma Sistemleri Aktif ===");
        logMessage("İstilacı Ordusu Tespit Edildi - Savunma Moduna Geçildi");
        logMessage("Başlangıç Altın Hazinesi: " + altinHazinesi);
        logMessage("Başlangıç Kale Savunması: " + kaleSavunmasi);
    }
    
    /**
     * Reset game to initial state (for replay).
     */
    public void resetGame() {
        initializeGame();
        startNextWave();
    }
    
    /**
     * Start next wave.
     */
    public void startNextWave() {
        if (currentWave >= totalWaves) {
            return;
        }
        
        currentWave++;
        logMessage("=== İstilacı Ordu Dalgası " + currentWave + " Tespit Edildi ===");
        
        if (currentWave == 1) {
            // First wave: 2 Standard, 1 Armored, 1 Flying
            createWave1();
        } else if (currentWave == 2) {
            // Second wave: 5-10 enemies, at least 1 of each type
            createWave2();
        } else if (currentWave == 3) {
            // Third wave: 8-12 enemies, more armored
            createWave3();
        } else if (currentWave == 4) {
            // Fourth wave: 10-15 enemies, mixed types
            createWave4();
        }
    }
    
    /**
     * Create wave 1 enemies.
     */
    private void createWave1() {
        // 2 Standard enemies
        for (int i = 0; i < 2; i++) {
            Enemy enemy = new StandardEnemy();
            enemy.setPathIndex(0.0); // Explicitly set starting path index
            enemy.setPosition(path.getWaypoint(0).x, path.getWaypoint(0).y);
            enemies.add(enemy);
            logMessage("Askari (Normal Asker) Oluşturuldu");
        }
        
        // 1 Armored enemy
        Enemy armored = new ArmoredEnemy();
        armored.setPathIndex(0.0); // Explicitly set starting path index
        armored.setPosition(path.getWaypoint(0).x, path.getWaypoint(0).y);
        enemies.add(armored);
        logMessage("Zırhlı Savaşçı Oluşturuldu (Zırh: " + armored.getArmorRating() + ")");
        
        // 1 Flying enemy
        Enemy flying = new FlyingEnemy();
        flying.setPathIndex(0.0); // Explicitly set starting path index
        flying.setPosition(path.getWaypoint(0).x, path.getWaypoint(0).y);
        enemies.add(flying);
        logMessage("Uçan Akbaba Oluşturuldu");
    }
    
    /**
     * Create wave 2 enemies (5-10 enemies, at least 1 of each type).
     */
    private void createWave2() {
        int totalEnemies = 5 + random.nextInt(6); // 5-10 enemies
        
        // At least 1 of each type
        Enemy standard = new StandardEnemy();
        standard.setPathIndex(0.0);
        standard.setPosition(path.getWaypoint(0).x, path.getWaypoint(0).y);
        enemies.add(standard);
        logMessage("Standart Piyade Birimi Oluşturuldu");
        
        Enemy armored = new ArmoredEnemy();
        armored.setPathIndex(0.0);
        armored.setPosition(path.getWaypoint(0).x, path.getWaypoint(0).y);
        enemies.add(armored);
        logMessage("Zırhlı Savaşçı Oluşturuldu (Zırh: " + armored.getArmorRating() + ")");
        
        Enemy flying = new FlyingEnemy();
        flying.setPathIndex(0.0);
        flying.setPosition(path.getWaypoint(0).x, path.getWaypoint(0).y);
        enemies.add(flying);
        logMessage("Uçan Akbaba Oluşturuldu");
        
        // Add remaining enemies randomly
        for (int i = 3; i < totalEnemies; i++) {
            int type = random.nextInt(3);
            Enemy enemy;
            if (type == 0) {
                enemy = new StandardEnemy();
                logMessage("Askari (Normal Asker) Oluşturuldu");
            } else if (type == 1) {
                enemy = new ArmoredEnemy();
                logMessage("Zırhlı Piyade Birimi Oluşturuldu (Zırh: " + enemy.getArmorRating() + ")");
            } else {
                enemy = new FlyingEnemy();
                logMessage("Uçan Akbaba Oluşturuldu");
            }
            enemy.setPathIndex(0.0);
            enemy.setPosition(path.getWaypoint(0).x, path.getWaypoint(0).y);
            enemies.add(enemy);
        }
    }
    
    /**
     * Create wave 3 enemies (6-9 enemies, more armored units).
     */
    private void createWave3() {
        int totalEnemies = 6 + random.nextInt(4); // 6-9 enemies
        
        // At least 2 of each type
        for (int i = 0; i < 2; i++) {
            Enemy standard = new StandardEnemy();
            standard.setPathIndex(0.0);
            standard.setPosition(path.getWaypoint(0).x, path.getWaypoint(0).y);
            enemies.add(standard);
            logMessage("Standart Piyade Birimi Oluşturuldu");
            
            Enemy armored = new ArmoredEnemy();
            armored.setPathIndex(0.0);
            armored.setPosition(path.getWaypoint(0).x, path.getWaypoint(0).y);
            enemies.add(armored);
            logMessage("Zırhlı Savaşçı Oluşturuldu (Zırh: " + armored.getArmorRating() + ")");
            
            Enemy flying = new FlyingEnemy();
            flying.setPathIndex(0.0);
            flying.setPosition(path.getWaypoint(0).x, path.getWaypoint(0).y);
            enemies.add(flying);
            logMessage("Uçan Akbaba Oluşturuldu");
        }
        
        // Add remaining enemies, favor armored (40% chance)
        for (int i = 6; i < totalEnemies; i++) {
            int type = random.nextInt(10);
            Enemy enemy;
            if (type < 4) {
                enemy = new ArmoredEnemy();
                logMessage("Zırhlı Piyade Birimi Oluşturuldu (Zırh: " + enemy.getArmorRating() + ")");
            } else if (type < 7) {
                enemy = new StandardEnemy();
                logMessage("Askari (Normal Asker) Oluşturuldu");
            } else {
                enemy = new FlyingEnemy();
                logMessage("Uçan Akbaba Oluşturuldu");
            }
            enemy.setPathIndex(0.0);
            enemy.setPosition(path.getWaypoint(0).x, path.getWaypoint(0).y);
            enemies.add(enemy);
        }
    }
    
    /**
     * Create wave 4 enemies (8-12 enemies, final boss wave).
     */
    private void createWave4() {
        int totalEnemies = 8 + random.nextInt(5); // 8-12 enemies
        
        // Mix of all types
        for (int i = 0; i < totalEnemies; i++) {
            int type = random.nextInt(10);
            Enemy enemy;
            if (type < 3) {
                enemy = new StandardEnemy();
                logMessage("Askari (Normal Asker) Oluşturuldu");
            } else if (type < 6) {
                enemy = new ArmoredEnemy();
                logMessage("Zırhlı Savaşçı Oluşturuldu (Zırh: " + enemy.getArmorRating() + ")");
            } else {
                enemy = new FlyingEnemy();
                logMessage("Uçan Akbaba Oluşturuldu");
            }
            enemy.setPathIndex(0.0);
            enemy.setPosition(path.getWaypoint(0).x, path.getWaypoint(0).y);
            enemies.add(enemy);
        }
    }
    
    /**
     * Update game simulation (called every frame).
     */
    public void update(double deltaTime) {
        if (!gameRunning) {
            return;
        }
        
        gameTime += deltaTime;
        
        // Update enemies
        updateEnemies(deltaTime);
        
        // Update towers
        updateTowers(deltaTime);
        
        // Remove dead enemies
        removeDeadEnemies();
        
        // Check win/lose conditions
        checkGameState();
    }
    
    /**
     * Update enemy positions and slow effects.
     */
    private void updateEnemies(double deltaTime) {
        for (Enemy enemy : enemies) {
            // Skip dead enemies - they cannot move or deal damage
            if (!enemy.isAlive()) {
                continue;
            }
            
            // Update slow effect
            enemy.updateSlowEffect(deltaTime);
            
            // Move enemy along path
            double velocity = enemy.getVelocity();
            double pixelsPerSecond = velocity * 2.0; // Convert to pixels per second
            
            double newPathIndex = enemy.getPathIndex() + (pixelsPerSecond * deltaTime / 50.0);
            enemy.setPathIndex(newPathIndex);
            
            // Update position
            Path.Point pos = path.getPosition(newPathIndex);
            enemy.setPosition(pos.x, pos.y);
            
            // Check if enemy reached base (only if still alive)
            // Make sure enemy has actually moved along the path (pathIndex >= 0.1) before checking
            if (enemy.isAlive() && enemy.getPathIndex() >= 0.1 && enemy.reachedBase(path.getLength())) {
                // Enemy reached base - damage player
                kaleSavunmasi -= enemy.getBaseDamage();
                enemy.setAlive(false);
                logMessage(enemy.getEnemyType() + " kaleye ulaştı! Kale Savunması: " + kaleSavunmasi);
                
                if (kaleSavunmasi <= 0) {
                    kaleSavunmasi = 0;
                }
            }
        }
    }
    
    /**
     * Update tower firing.
     */
    private void updateTowers(double deltaTime) {
        for (Tower tower : towers) {
            if (!tower.isActive()) {
                continue;
            }
            
            // Fire at enemies (towers only target alive enemies)
            List<Enemy> hitEnemies = tower.fire(enemies, gameTime);
            
            if (!hitEnemies.isEmpty()) {
                for (Enemy hit : hitEnemies) {
                    // Only log if enemy was actually hit and is still alive or just died
                    if (hit != null) {
                        logMessage(tower.getTowerType() + " -> " + hit.getEnemyType() + " hasar verdi (Can: " + 
                                  String.format("%.1f", hit.getShieldIntegrity()) + ")");
                        
                        if (!hit.isAlive()) {
                            logMessage(hit.getEnemyType() + " yok edildi! Altın Kazancı: " + hit.getRewardEnergy());
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Remove dead enemies and award energy.
     */
    private void removeDeadEnemies() {
        List<Enemy> toRemove = new ArrayList<>();
        
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                // Award energy if enemy was killed (not reached base)
                if (enemy.getShieldIntegrity() <= 0 && !enemy.reachedBase(path.getLength())) {
                    altinHazinesi += enemy.getRewardEnergy();
                    logMessage("Altın Hazinesi: " + altinHazinesi + " (+" + enemy.getRewardEnergy() + ")");
                }
                toRemove.add(enemy);
            }
        }
        
        enemies.removeAll(toRemove);
    }
    
    /**
     * Check win/lose conditions.
     */
    private void checkGameState() {
        // Check lose condition
        if (kaleSavunmasi <= 0) {
            gameRunning = false;
            gameLost = true;
            logMessage("=== KAYBETTINIZ ===");
            logMessage("Kale Savunması tükendi. Firavun'un kalesi işgal edildi!");
            return;
        }
        
        // Check win condition: all waves completed and no enemies left
        if (currentWave >= totalWaves && enemies.isEmpty()) {
            gameRunning = false;
            gameWon = true;
            logMessage("=== KAZANDINIZ ===");
            logMessage("Tüm istilacı orduları yok edildi. Firavun'un kalesi güvende!");
        }
    }
    
    /**
     * Place tower at position.
     */
    public boolean placeTower(Tower tower) {
        if (altinHazinesi >= tower.getEnergyCost()) {
            altinHazinesi -= tower.getEnergyCost();
            towers.add(tower);
            logMessage(tower.getTowerType() + " yerleştirildi. Maliyet: " + tower.getEnergyCost() + 
                      ", Kalan Altın: " + altinHazinesi);
            return true;
        } else {
            logMessage("Yetersiz altın! Gerekli: " + tower.getEnergyCost() + ", Mevcut: " + altinHazinesi);
            return false;
        }
    }
    
    /**
     * Log message to file.
     */
    private void logMessage(String message) {
        if (logWriter != null) {
            logWriter.println("[" + String.format("%.2f", gameTime) + "s] " + message);
            logWriter.flush();
        }
        System.out.println("[LOG] " + message);
    }
    
    /**
     * Close log file.
     */
    public void close() {
        if (logWriter != null) {
            logWriter.close();
        }
    }
}

