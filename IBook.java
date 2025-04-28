import java.io.Serializable;

public interface IBook extends Serializable {
    public String getTitle();
    public String getAuthor();
    public String getISBN();
    public String getHomeLibrary();
}