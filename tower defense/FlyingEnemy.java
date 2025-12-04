/**
 * Flying enemy unit - vulture unit in ancient Egypt defense.
 * Demonstrates Inheritance from Enemy class.
 */
public class FlyingEnemy extends Enemy {
    
    public FlyingEnemy() {
        super(28.0, 70.0, 0, true, 15, 6); // HP: 55 -> 28 (%49 azaltma)
    }
    
    @Override
    public String getEnemyType() {
        return "UcanAkbaba";
    }
}

