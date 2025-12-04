/**
 * Abstract base class for all enemy units in the space colony defense game.
 * Demonstrates Abstraction and will be used for Inheritance.
 */
public abstract class Enemy {
    protected double shieldIntegrity; // HP/CAN
    protected double maxShieldIntegrity;
    protected double velocity; // HIZ
    protected int armorRating; // ZIRH
    protected boolean isFlying;
    protected double x, y; // Position
    protected double pathIndex; // Current position on path (double for smooth movement)
    protected boolean isAlive;
    
    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }
    protected double slowEffectTimer; // For ice tower effect
    protected double originalVelocity;
    
    // Encapsulation: private fields with getters/setters
    private int rewardEnergy; // KAZANILAN PARA
    private int baseDamage; // USSE ULASTIGINDA VERILEN HASAR
    
    public Enemy(double shieldIntegrity, double velocity, int armorRating, boolean isFlying, int rewardEnergy, int baseDamage) {
        this.shieldIntegrity = shieldIntegrity;
        this.maxShieldIntegrity = shieldIntegrity;
        this.velocity = velocity;
        this.originalVelocity = velocity;
        this.armorRating = armorRating;
        this.isFlying = isFlying;
        this.pathIndex = 0;
        this.isAlive = true;
        this.slowEffectTimer = 0;
        this.rewardEnergy = rewardEnergy;
        this.baseDamage = baseDamage;
        this.x = 0;
        this.y = 0;
    }
    
    // Encapsulation: Getters and Setters
    public double getShieldIntegrity() { return shieldIntegrity; }
    public double getMaxShieldIntegrity() { return maxShieldIntegrity; }
    public double getVelocity() { return velocity; }
    public double getOriginalVelocity() { return originalVelocity; }
    public double getSlowEffectTimer() { return slowEffectTimer; }
    public boolean hasSlowEffect() { return slowEffectTimer > 0; }
    public int getArmorRating() { return armorRating; }
    public boolean isFlying() { return isFlying; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getPathIndex() { return pathIndex; }
    public boolean isAlive() { return isAlive; }
    public int getRewardEnergy() { return rewardEnergy; }
    public int getBaseDamage() { return baseDamage; }
    
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void setPathIndex(double index) {
        this.pathIndex = index;
    }
    
    public void setPathIndexInt(int index) {
        this.pathIndex = index;
    }
    
    // Polymorphism: Abstract methods to be implemented by subclasses
    public abstract String getEnemyType();
    
    /**
     * Apply damage to this enemy, considering armor.
     * Returns true if enemy is destroyed.
     */
    public boolean takeDamage(double rawDamage) {
        double netDamage = calculateNetDamage(rawDamage);
        shieldIntegrity -= netDamage;
        
        if (shieldIntegrity <= 0) {
            shieldIntegrity = 0;
            isAlive = false;
            return true; // Enemy destroyed
        }
        return false; // Enemy still alive
    }
    
    /**
     * Calculate net damage after armor reduction.
     * Formula: Net_Hasar = Kule_Hasarı * (1 - (Zırh / (Zırh + 100.0)))
     */
    protected double calculateNetDamage(double rawDamage) {
        if (armorRating <= 0) {
            return rawDamage;
        }
        return rawDamage * (1 - (armorRating / (armorRating + 100.0)));
    }
    
    /**
     * Apply slow effect from ice tower.
     */
    public void applySlowEffect(double duration) {
        slowEffectTimer = duration;
        velocity = originalVelocity * 0.5; // 50% speed reduction
    }
    
    /**
     * Update slow effect timer.
     */
    public void updateSlowEffect(double deltaTime) {
        if (slowEffectTimer > 0) {
            slowEffectTimer -= deltaTime;
            if (slowEffectTimer <= 0) {
                slowEffectTimer = 0;
                velocity = originalVelocity; // Restore original velocity
            }
        }
    }
    
    /**
     * Check if enemy reached the base.
     */
    public boolean reachedBase(int pathLength) {
        // Enemy has reached base if pathIndex is at or beyond the last waypoint
        // pathLength is the number of waypoints (e.g., 7 waypoints = indices 0-6)
        // Base is at index pathLength - 1, so we check if pathIndex >= pathLength - 1
        return pathIndex >= (pathLength - 1.0);
    }
}

