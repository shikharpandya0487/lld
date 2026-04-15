package document_editor;

public class Runner {
    public static void main(String[] args) {

           DocEditor docEditor = new DocEditor();
           Persistence db=new SaveToDB();
           Persistence file=new SaveToFile();

           TextEntity textEntity = new TextEntity("This is a text entity.");
           TextEntity textEntity2 = new TextEntity("This is another text entity.");
           TextEntity textEntity3 = new TextEntity("This is yet another text entity.");

           ImageEntity imageEntity = new ImageEntity("url_to_image");

           docEditor.addEntity(textEntity);
           docEditor.addEntity(textEntity2);
           docEditor.addEntity(textEntity3);
           docEditor.addEntity(imageEntity);
           docEditor.display();

           db.save(textEntity);
           file.save(imageEntity);

           System.out.println("Removing one text entity...\n");

           docEditor.removeEntity(textEntity);
           docEditor.display();
        
    }

}
