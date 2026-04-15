package document_editor;

public class VideoEntity extends Entity {
    
    public VideoEntity(String content) {
        this.EntityType = "VideoEntity";
        this.EntityContent = content;
    }

    String getContent() {
        return EntityContent;
    }
}
