package document_editor;

import java.util.ArrayList;
import java.util.List;

public class DocEditor implements EntityHandler {
    
    List<Entity> entities;
    public DocEditor() {
        entities = new ArrayList<>();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entities == null) ? 0 : entities.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DocEditor other = (DocEditor) obj;
        if (entities == null) {
            if (other.entities != null)
                return false;
        } else if (!entities.equals(other.entities))
            return false;
        return true;
    }

    @Override
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    @Override
    public void display() {
        for (Entity entity : entities) {
            System.out.println("Entity Type: " + entity.EntityType);
            System.out.println("Entity Content: " + entity.EntityContent);
            System.out.println("-------------------------");
        }
    }

    @Override
    public List<Object> getEntities() {
        // Implementation to return all entities
        return new ArrayList<>(entities);
    }
    

}
