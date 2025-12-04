import java.util.ArrayList;
import java.util.List;

/**
 * Ice Tower - Buz Kulesi (Kum Kulesi) in ancient Egypt theme.
 * Slows enemy movement speed, single target.
 * Demonstrates Inheritance and Polymorphism.
 */
public class IceTower extends Tower {
    private static final double SLOW_DURATION = 3.0; // 3 seconds
    
    public IceTower(double x, double y) {
        super(x, y, 150.0, 35.0, 1.8, 70); // Hasar: 18 -> 35 (%94 artış)
    }
    
    @Override
    public String getTowerType() {
        return "BuzKulesi";
    }
    
    @Override
    public List<Enemy> fire(List<Enemy> enemies, double currentTime) {
        List<Enemy> hitEnemies = new ArrayList<>();
        
        if (!canFire(currentTime)) {
            return hitEnemies;
        }
        
        // Find target: closest to base among enemies in range
        Enemy target = findTarget(enemies);
        
        if (target != null && target.isAlive()) {
            // Only damage if enemy is still alive
            if (target.isAlive()) {
                // Apply distance-based damage
                double actualDamage = damageOutput * calculateDistanceDamageMultiplier(target);
                target.takeDamage(actualDamage);
                
                // Apply slow effect (50% speed reduction for 3 seconds)
                target.applySlowEffect(SLOW_DURATION);
                
                hitEnemies.add(target);
                updateFireTime(currentTime);
            }
        }
        
        return hitEnemies;
    }
    
    /**
     * Find the enemy closest to base (highest pathIndex) in range.
     */
    private Enemy findTarget(List<Enemy> enemies) {
        Enemy target = null;
        double maxPathIndex = -1.0;
        
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && isInRange(enemy)) {
                if (enemy.getPathIndex() > maxPathIndex) {
                    maxPathIndex = enemy.getPathIndex();
                    target = enemy;
                }
            }
        }
        
        return target;
    }
}

