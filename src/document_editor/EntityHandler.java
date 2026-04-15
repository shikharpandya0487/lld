package document_editor;

import java.util.List;

public interface EntityHandler {
    void addEntity(Entity entity);
    void removeEntity(Entity entity);
    void display();
    List<Object>getEntities();
}

