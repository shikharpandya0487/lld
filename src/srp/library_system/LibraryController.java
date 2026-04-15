package srp.library_system;
import java.util.Scanner;

public class LibraryController {
    private static final String LIBRARY = "LIBRARY";
    private BookRegistration bookRegistration;
    private BookSearchService bookSearchService;


    public void manageLibrary() {
        System.out.println("Managing library operations...");
        System.out.println(LIBRARY + " is being managed by the controller...");
        Scanner sc = new Scanner(System.in);

        bookRegistration = new BookRegistration();
        bookSearchService = new BookSearchService();

        System.out.println("If you want to register a book, please use the book registration service.");
        System.out.println("If you want to search for a book, please use the book search service.");    
        // Taking input from user and calling respective services can be implemented here.

        int k=sc.nextInt();
        System.out.println("Enter book details to register:");
        if(k==1){
            sc.nextLine(); // Consume the newline
            System.out.println("Enter book title:");
            String title = sc.nextLine();
            System.out.println("Enter book author:");
            String author = sc.nextLine();
            System.out.println("Enter book ISBN:");
            String isbn = sc.nextLine();

            Book newBook = new Book(title, author, isbn);
            bookRegistration.registerBook(newBook);
        }
         else if(k==2){
            sc.nextLine(); // Consume the newline
            System.out.println("Enter book title to search:");
            String title = sc.nextLine();
            Book foundBook = bookSearchService.retrieveBookByTitle(title);
            if (foundBook != null) {
                System.out.println("Book found: " + foundBook.getTitle() + " by " + foundBook.getAuthor());
            } else {
                System.out.println("Book not found.");
            }
        }
         else{
            System.out.println("Invalid option selected.");
        }   
        sc.close();
    }

}
