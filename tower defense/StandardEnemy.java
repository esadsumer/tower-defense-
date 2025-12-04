/**
 * Standard ground enemy unit - basic soldier in ancient Egypt defense.
 * Demonstrates Inheritance from Enemy class.
 */
public class StandardEnemy extends Enemy {
    
    public StandardEnemy() {
        super(30.0, 45.0, 0, false, 10, 6); // HP: 60 -> 30 (%50 azaltma)
    }
    
    @Override
    public String getEnemyType() {
        return "Askari";
    }
}

