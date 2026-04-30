package prototype_pattern;

import java.util.ArrayList;
import java.util.List;

// Example 2: Game Character with deep copy
public class GameCharacter implements Cloneable {
    private String name;
    private int health;
    private int attackPower;
    private List<String> inventory;

    public GameCharacter(String name, int health, int attackPower) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
        this.inventory = new ArrayList<>();
    }

    public void addItem(String item) { inventory.add(item); }
    public void setName(String name) { this.name = name; }
    public void setHealth(int health) { this.health = health; }

    // Deep clone — inventory list is cloned separately
    @Override
    public GameCharacter clone() {
        try {
            GameCharacter cloned = (GameCharacter) super.clone();
            cloned.inventory = new ArrayList<>(this.inventory);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "GameCharacter{name=" + name + ", hp=" + health
                + ", atk=" + attackPower + ", inventory=" + inventory + "}";
    }
}
