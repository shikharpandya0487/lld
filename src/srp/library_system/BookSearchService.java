package srp.library_system;

import java.util.List;

public class BookSearchService {
    BookRegistration bookRegistration;
    List<Book> registeredBooks = bookRegistration.getRegisteredBooks();
    
    public Book retrieveBookByISBN(String isbn) {
        for (Book book : registeredBooks) {
            if (book.getIsbn().toLowerCase().equals(isbn.toLowerCase())) {
                return book;
            }
        }
        return null; 
    }

    public Book retrieveBookByTitle(String title) {
        for (Book book : registeredBooks) {
            if (book.getTitle().toLowerCase().equalsIgnoreCase(title.toLowerCase())) {
                return book;
            }
        }
        return null; 
    }

    public Book retrieveBookByAuthor(String author) {
        for (Book book : registeredBooks) {
            if (book.getAuthor().toLowerCase().equalsIgnoreCase(author.toLowerCase())) {
                return book;
            }
        }
        return null; 
    }
}
