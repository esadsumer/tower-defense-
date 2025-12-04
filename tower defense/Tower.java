import java.util.List;
import java.awt.Point;

/**
 * Abstract base class for all tower defense structures.
 * Demonstrates Abstraction and will be used for Inheritance.
 */
public abstract class Tower {
    protected double x, y; // Position
    protected double targetingRange; // MENZIL
    protected double damageOutput; // HASAR
    protected double fireRate; // Ateş hızı (saniye cinsinden)
    protected double lastFireTime; // Son atış zamanı
    protected int energyCost; // MALIYET
    protected boolean isActive;
    
    public Tower(double x, double y, double targetingRange, double damageOutput, double fireRate, int energyCost) {
        this.x = x;
        this.y = y;
        this.targetingRange = targetingRange;
        this.damageOutput = damageOutput;
        this.fireRate = fireRate;
        this.energyCost = energyCost;
        this.lastFireTime = 0;
        this.isActive = true;
    }
    
    // Encapsulation: Getters and Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getTargetingRange() { return targetingRange; }
    public double getDamageOutput() { return damageOutput; }
    public double getFireRate() { return fireRate; }
    public int getEnergyCost() { return energyCost; }
    public boolean isActive() { return isActive; }
    
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Check if tower can fire (cooldown ready).
     */
    public boolean canFire(double currentTime) {
        return (currentTime - lastFireTime) >= fireRate;
    }
    
    /**
     * Update last fire time.
     */
    protected void updateFireTime(double currentTime) {
        lastFireTime = currentTime;
    }
    
    /**
     * Calculate distance to enemy.
     */
    protected double calculateDistance(Enemy enemy) {
        double dx = enemy.getX() - x;
        double dy = enemy.getY() - y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Calculate damage multiplier based on distance to enemy.
     * Closer enemies take more damage, far edge of range takes less.
     * - 0% - 33% of range: 1.5x damage
     * - 33% - 66% of range: 1.0x damage
     * - 66% - 100% of range: 0.7x damage
     */
    protected double calculateDistanceDamageMultiplier(Enemy enemy) {
        double distance = calculateDistance(enemy);
        if (targetingRange <= 0) {
            return 1.0;
        }
        double ratio = distance / targetingRange; // 0.0 (close) - 1.0 (edge of range)
        
        if (ratio <= 0.33) {
            return 1.5; // very close targets take more damage
        } else if (ratio <= 0.66) {
            return 1.0; // mid-range targets take normal damage
        } else {
            return 0.7; // far targets take slightly reduced damage
        }
    }
    
    /**
     * Check if enemy is in range.
     */
    protected boolean isInRange(Enemy enemy) {
        return calculateDistance(enemy) <= targetingRange;
    }
    
    /**
     * Polymorphism: Each tower type implements its own targeting and firing logic.
     * Returns list of enemies hit (for splash damage towers).
     */
    public abstract List<Enemy> fire(List<Enemy> enemies, double currentTime);
    
    /**
     * Get tower type name for display and logging.
     */
    public abstract String getTowerType();
}

