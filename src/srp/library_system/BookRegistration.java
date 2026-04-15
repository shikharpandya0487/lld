package srp.library_system;

import java.util.ArrayList;
import java.util.List;

public class BookRegistration 
{
    private List<Book> registeredBooks;

    public List<Book> getRegisteredBooks() {
        return registeredBooks;
    }

    public BookRegistration() {
        registeredBooks = new ArrayList<>();
    }

    public void registerBook(Book book){
        registeredBooks.add(book);
        System.out.println("Book registered: " + book.getTitle());
    }

    public void displayRegisteredBooks() {
        System.out.println("Registered Books:");
        for (Book book : registeredBooks) {
            System.out.println("- " + book.getTitle() + " by " + book.getAuthor());
        }
    }

    
    
}
