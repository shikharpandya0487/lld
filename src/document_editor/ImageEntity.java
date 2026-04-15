package document_editor;

public class ImageEntity extends Entity {
    
    public ImageEntity(String content) {
        this.EntityType = "ImageEntity";
        this.EntityContent = content;
    }

    String getContent() {
        return EntityContent;
    }

}
