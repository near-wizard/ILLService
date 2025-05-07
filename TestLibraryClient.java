// TestLibraryClient.java
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TestLibraryClient {
    private final String serverHost;
    private final int serverPort;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private MessageFactory messageFactory;
    private final List<IBook> libraryBooks = new ArrayList<>();

    public TestLibraryClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;

        // Add some test books
        libraryBooks.add(new SimpleBook("Effective Java", "Joshua Bloch", "9780134685991"));
        libraryBooks.add(new SimpleBook("Clean Code", "Robert C. Martin", "9780132350884"));
    }

    public void start() throws IOException, ClassNotFoundException {
        Socket socket = new Socket(serverHost, serverPort);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        messageFactory = new MessageFactory("testLibrary","testGroup");
        // Register with a group
        sendMessage(this.messageFactory.createMessage("REGISTER", "group1"));

        // Listen for server messages
        while (true) {
            Message msg = (Message) in.readObject();
            handleServerMessage(msg);
        }
    }

    private void handleServerMessage(Message msg) throws IOException {
        switch (msg.getType()) {
            case "REQUEST_BOOK_LIST":
                System.out.println("Server requested book list.");
                sendMessage(this.messageFactory.createMessage("BOOK_LIST_RESPONSE", libraryBooks));
                break;
            case "REQUEST_BOOK":
                String requestedIsbn = (String) msg.getData();
                IBook found = findBookByISBN(requestedIsbn);
                if (found != null) {
                    System.out.println("Sending book: " + found.getTitle());
                    sendMessage(this.messageFactory.createMessage("SEND_BOOK", found));
                } else {
                    System.out.println("Book not found: " + requestedIsbn);
                }
                break;
            case "RECEIVE_BOOK":
                // Server tells us to receive a book
                IBook book = (IBook) msg.getData();
                sendMessage(this.messageFactory.createMessage("BOOK_RECEIVED", book));
                System.out.println("Book Received");
                break;
            default:
                System.out.println("Unknown server message: " + msg.getType());
        }
    }

    private IBook findBookByISBN(String isbn) {
        return libraryBooks.stream()
            .filter(book -> book.getISBN().equals(isbn))
            .findFirst()
            .orElse(null);
    }

    private void sendMessage(Message msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    public static void main(String[] args) {
        try {
            // TODO change group and libraryname
            // ILLClient client = new ILLClient("group1", "myLibrary", "localhost", 12345);
            ILLClient client = new ILLClient("group1", "myLibrary", "173.255.234.247", 12345);
            client.start();
            client.requestBook(new SimpleBook("Effective Java", "Joshua Bloch", "9780134685991"));
            client.requestBook(new SimpleBook("Clean Code", "Robert C. Martin", "9780132350884"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}