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
                IBook requestedBook = findBookByISBN(isbn);
                sendMessage(new Message("SEND_BOOK", requestedBook));

                // TODO Insert function to handle inventory after sending book here

                break;
            case "RECEIVE_BOOK":
                // Server tells us to receive a book
                IBook book = (IBook) msg.getData();
                sendMessage(new Message("BOOK_RECEIVED", book));

                // TODO Insert function to handle inventory after receiving book here

                break;
            default:
                System.out.println("Unknown server message: " + msg.getType());
        }
    }

    private void sendMessage(Message msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    // TODO return a list of available books
    private List<IBook> getAvailableBooks() {
        return List.of(); // Replace with actual list
    }

    // TODO Replace with actual lookup, return null if not found
    private IBook findBookByISBN(String isbn) {
        return null; 
    }
}
