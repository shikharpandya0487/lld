package document_editor;

public class TextEntity extends  Entity {
    
    public TextEntity(String content) {
        this.EntityType = "TextEntity";
        this.EntityContent = content;
    }

    String getContent(){
        return EntityContent;
    }
}
