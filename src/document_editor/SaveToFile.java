package document_editor;

public class SaveToFile extends Persistence {
    @Override
    public void save(Entity entity) {
        // Code to save the entity to a file
        System.out.println("Saving " + entity.EntityType + " to file with content: " + entity.EntityContent);
    }

    @Override
    public Entity load(String entityId) {
        // Code to load the entity from a file based on the entityId
        System.out.println("Loading entity with ID: " + entityId + " from file");
        return null; // Placeholder return statement
    }

}
