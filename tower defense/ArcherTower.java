import java.util.ArrayList;
import java.util.List;

/**
 * Archer Tower - Okçu Kulesi in ancient Egypt theme.
 * Single target, rapid fire, reduced damage to armored enemies.
 * Demonstrates Inheritance and Polymorphism.
 */
public class ArcherTower extends Tower {
    
    public ArcherTower(double x, double y) {
        super(x, y, 150.0, 25.0, 0.9, 50); // Hasar: 12 -> 25 (%108 artış)
    }
    
    @Override
    public String getTowerType() {
        return "OkcuKulesi";
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
            // Base damage (50% reduction for armored enemies)
            double actualDamage = damageOutput;
            if (target instanceof ArmoredEnemy) {
                actualDamage = damageOutput * 0.5;
            }
            
            // Apply distance-based damage multiplier
            double distanceMultiplier = calculateDistanceDamageMultiplier(target);
            actualDamage *= distanceMultiplier;
            
            // Only damage if enemy is still alive
            if (target.isAlive()) {
                target.takeDamage(actualDamage);
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

