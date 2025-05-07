import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ILLClient implements ILLInterface {
    private final String serverHost;
    private final int serverPort;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final String group;
    private final String libraryName;
    private final MessageFactory messageFactory;

    public ILLClient(String groupName, String libraryName) {
        this(groupName, libraryName, "173.255.234.247", 12345);
    }

    public ILLClient(String groupName, String libraryName, String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.group = groupName;
        this.libraryName = libraryName;
        this.messageFactory = new MessageFactory(groupName, libraryName);
    }

    public void start() throws IOException, ClassNotFoundException {
        Socket socket = new Socket(serverHost, serverPort);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        sendMessage(this.messageFactory.createMessage(MessageTypes.REGISTER, null));
        // Start a Thread to listen for requests and responses from the server in a new thread
        new Thread(() -> {
            try {
                while (true) {
                    Message msg = (Message) in.readObject();
                    handleServerMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleServerMessage(Message msg) throws IOException {
        switch (msg.getType()) {
            case "REQUEST_BOOK_LIST":
                List<IBook> books = getAvailableBooks();
                sendMessage(this.messageFactory.createMessage(MessageTypes.BOOK_LIST_RESPONSE, books));
                break;

            case "REQUEST_BOOK_SUCCESS":
                System.out.println("Request Received by server.");
                break;

            case MessageTypes.SEND_BOOK:
                IBook requestedBook = (IBook) msg.getData();
                if (this.bookAvailable(requestedBook)) {
                    // TODO: Update if you need to change the header
                    boolean sent = this.sendBookInventoryManagement(requestedBook); // Replace if needed
                    if (sent) {
                        sendMessage(this.messageFactory.createMessage(MessageTypes.SEND_BOOK_SUCCESS, requestedBook));
                    } else {
                        sendMessage(this.messageFactory.createMessage(MessageTypes.SEND_BOOK_FAILURE, null));
                    }
                } else {
                    sendMessage(this.messageFactory.createMessage(MessageTypes.SEND_BOOK_FAILURE, null));
                }
                break;

            case MessageTypes.RECEIVE_BOOK:
                try {
                    IBook book = (IBook) msg.getData();
                    //TODO: Update if you need to change the header
                    boolean received = this.receiveBookInventoryManagement(book);
                    sendMessage(this.messageFactory.createMessage(MessageTypes.RECEIVE_BOOK_SUCCESS, null));
                } catch(Exception e){
                    System.out.println("Failed to receive a book with error message" + e.toString());
                    sendMessage(this.messageFactory.createMessage(MessageTypes.RECEIVE_BOOK_FAILURE, null));
                }
                break;

            default:
                System.out.println("Unknown server message: " + msg.getType());
        }
    }

    private void sendMessage(Message msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    // Replace with actual inventory logic
    private List<IBook> getAvailableBooks() {
        //OPTIONAL TODO: Optional Returns back a list of available books
        return List.of();
    }

    /*
        TODO: Use this method where you want to request from ILL
        This could be just as a test in main
        This could be if book inventory is 0
    */ 

    public boolean requestBook(IBook book){
        try{
        sendMessage(this.messageFactory.createMessage(MessageTypes.REQUEST_BOOK, book));
        } catch(Exception e){
            System.out.println("Exception: " + e.toString());
            return false;
        }
        return true;
    }

    /*
        TODO: Handles your inventory for when your library sends a book
    */
    private boolean sendBookInventoryManagement(IBook book){
        System.out.println("Sending Book: " + book.getTitle());
        return true;
    }

    /*
        TODO: Handles your inventory for when your library receives a book
    */
    private boolean receiveBookInventoryManagement(IBook book){
        System.out.println("Receiving Book: " + book.getTitle());
        return true;
    }

    /*
        TODO: Returns true if book is available for loan, false otherwise
    */
    public boolean bookAvailable(IBook book){
        return true;
    }
}
