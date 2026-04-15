package document_editor;

public class SaveToDB extends Persistence {
    
    @Override
    public void save(Entity entity) {
        // Implementation for saving entity to DB
        System.out.println("Saving " + entity.EntityType + " to DB with content: " + entity.EntityContent);
        
    }

    @Override
    public Entity load(String entityId) {
        // Implementation for loading entity from DB
        System.out.println("Loading entity with ID: " + entityId + " from DB");
        return null;
    }

}
