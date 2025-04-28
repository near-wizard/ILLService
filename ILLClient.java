// LibraryClient.java
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ILLClient {
    private final String serverHost;
    private final int serverPort;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ILLClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void start() throws IOException, ClassNotFoundException {
        Socket socket = new Socket(serverHost, serverPort);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        // Example: Register with a group
        sendMessage(new Message("REGISTER", "group1"));

        // Listening for server requests
        while (true) {
            Message msg = (Message) in.readObject();
            handleServerMessage(msg);
        }
    }

    private void handleServerMessage(Message msg) throws IOException {
        switch (msg.getType()) {
            case "REQUEST_BOOK_LIST":
                // Respond with available books
                List<IBook> books = getAvailableBooks();
                sendMessage(new Message("BOOK_LIST_RESPONSE", books));
                break;
            case "REQUEST_BOOK":
                // Server tells us to send a book
                String isbn = (String) msg.getData();
                IBook book = findBookByISBN(isbn);
                sendMessage(new Message("SEND_BOOK", book));
                break;
            default:
                System.out.println("Unknown server message: " + msg.getType());
        }
    }

    private void sendMessage(Message msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    // Stub methods for demo
    private List<IBook> getAvailableBooks() {
        return List.of(); // Replace with actual list
    }

    private IBook findBookByISBN(String isbn) {
        return null; // Replace with actual lookup
    }
}
