// SimpleBook.java
public class SimpleBook implements IBook {
    private final String title;
    private final String author;
    private final String isbn;
    private final String homeLibrary;

    public SimpleBook(String title, String author, String isbn, String homeLibrary) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.homeLibrary = homeLibrary;
    }

    public SimpleBook(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.homeLibrary = "TEST";
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getISBN() {
        return isbn;
    }

    @Override
    public String getHomeLibrary() {
        return homeLibrary;
    }
}
