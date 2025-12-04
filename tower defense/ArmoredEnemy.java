import java.util.Random;

/**
 * Armored ground enemy unit - heavy warrior with bronze armor in ancient Egypt.
 * Demonstrates Inheritance from Enemy class.
 */
public class ArmoredEnemy extends Enemy {
    
    public ArmoredEnemy() {
        super(50.0, 28.0, generateArmorRating(), false, 20, 12); // HP: 90 -> 50 (%44 azaltma)
    }
    
    /**
     * Generate armor rating between 50-90.
     */
    private static int generateArmorRating() {
        Random rand = new Random();
        return 50 + rand.nextInt(41); // 50-90 inclusive
    }
    
    @Override
    public String getEnemyType() {
        return "ZirhliSavasci";
    }
}

