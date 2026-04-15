package document_editor;

public abstract class Persistence {
    public abstract void save(Entity entity);
    public abstract Entity load(String entityId);
}
