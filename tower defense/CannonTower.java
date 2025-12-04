import java.util.ArrayList;
import java.util.List;

/**
 * Cannon Tower - Topçu Kulesi in ancient Egypt theme.
 * Area of effect damage, cannot target flying enemies.
 * Demonstrates Inheritance and Polymorphism.
 */
public class CannonTower extends Tower {
    private static final double EXPLOSION_RADIUS = 50.0;
    
    public CannonTower(double x, double y) {
        super(x, y, 200.0, 45.0, 2.8, 75); // Hasar: 25 -> 45 (%80 artış)
    }
    
    @Override
    public String getTowerType() {
        return "TopcuKulesi";
    }
    
    @Override
    public List<Enemy> fire(List<Enemy> enemies, double currentTime) {
        List<Enemy> hitEnemies = new ArrayList<>();
        
        if (!canFire(currentTime)) {
            return hitEnemies;
        }
        
        // Find target: closest to base, excluding flying enemies
        Enemy target = findTarget(enemies);
        
        if (target != null && target.isAlive()) {
            // Apply distance-based damage to primary target (only if alive)
            if (target.isAlive()) {
                double primaryDamage = damageOutput * calculateDistanceDamageMultiplier(target);
                target.takeDamage(primaryDamage);
                hitEnemies.add(target);
            }
            
            // Apply splash damage to nearby enemies (excluding flying, only alive ones)
            for (Enemy enemy : enemies) {
                if (enemy != target && enemy.isAlive() && !enemy.isFlying()) {
                    double distance = calculateDistance(enemy);
                    if (distance <= EXPLOSION_RADIUS) {
                        // Double check enemy is still alive before damaging
                        if (enemy.isAlive()) {
                            double splashDamage = damageOutput * calculateDistanceDamageMultiplier(enemy);
                            enemy.takeDamage(splashDamage);
                            hitEnemies.add(enemy);
                        }
                    }
                }
            }
            
            updateFireTime(currentTime);
        }
        
        return hitEnemies;
    }
    
    /**
     * Find the enemy closest to base (highest pathIndex) in range, excluding flying enemies.
     */
    private Enemy findTarget(List<Enemy> enemies) {
        Enemy target = null;
        double maxPathIndex = -1.0;
        
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && !enemy.isFlying() && isInRange(enemy)) {
                if (enemy.getPathIndex() > maxPathIndex) {
                    maxPathIndex = enemy.getPathIndex();
                    target = enemy;
                }
            }
        }
        
        return target;
    }
}

