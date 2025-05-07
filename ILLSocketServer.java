// InterlibraryLoanServer.java
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ILLSocketServer {
    private final int port;
    private MessageFactory messageFactory;
    // Map<GroupId, Map<LibraryId, ClientHandler>>
    private final Map<String, Map<String, ClientHandler>> groupRegistry = new HashMap<>();


    public ILLSocketServer(int port) {
        this.port = port;
        this.messageFactory = new MessageFactory("serverGroup", "server");
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Interlibrary Loan Server running...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket, this.messageFactory)).start();
        }
    }

    public void adminCommandLoop() throws IOException {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Enter command (LIST or REQUEST groupId ISBN):");
                String line = scanner.nextLine();
                String[] parts = line.split("\\s+");

                if (parts[0].equalsIgnoreCase("LIST")) {
                    // Request all book lists from all groups
                    for (Map<String, ClientHandler> group : groupRegistry.values()) {
                        for (ClientHandler handler : group.values()) {
                            handler.requestBookList();
                        }
                    }
                } else if (parts[0].equalsIgnoreCase("REQUEST") && parts.length == 3) {
                    String groupId = parts[1];
                    String isbn = parts[2];
                    if (groupRegistry.containsKey(groupId)) {
                        Map<String, ClientHandler> group = groupRegistry.get(groupId);
                        if (group != null) {
                            for (ClientHandler handler : group.values()) {
                                handler.requestBook(isbn);
                            }
                        }
                    } else {
                        System.out.println("No such group.");
                    }
                 } else if (parts[0].equalsIgnoreCase("SEND") && parts.length == 3) {
                    // XXX fix SEND 
                    String groupId = parts[1];
                    String isbn = parts[2];
                    if (groupRegistry.containsKey(groupId)) {
                        Map<String, ClientHandler> group = groupRegistry.get(groupId);
                        for (ClientHandler handler : group.values()) {
                            handler.requestBook(isbn);
                        }
                    } else {
                        System.out.println("No such group.");
                    }
                }
                else {
                    System.out.println("Invalid command.");
                }
            }
        }

        // In your InterlibraryLoanServer main()
        public static void main(String[] args) {
            try {
                ILLSocketServer server = new ILLSocketServer(12345);
                new Thread(() -> {
                    try {
                        server.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                server.adminCommandLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String groupId;
        private MessageFactory messageFactory;

        public ClientHandler(Socket socket, MessageFactory messageFactory) {
            this.socket = socket;
            this.messageFactory = messageFactory;
        }

        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    Message msg = (Message) in.readObject();
                    handleClientMessage(msg);
                }
            } catch (Exception e) {
                System.out.println("Client disconnected.");
                if (groupId != null) {
                    Map<String, ClientHandler> group = groupRegistry.get(groupId);
                    if (group != null) {
                        group.values().remove(this);
                    }

                }
            }
        }

        private void handleClientMessage(Message msg) throws IOException {
            IBook book;
            switch (msg.getType()) {
                case MessageTypes.REGISTER:
                    groupId = msg.getGroup();
                    String libraryId = msg.getSender();
                    groupRegistry.computeIfAbsent(groupId, g -> new HashMap<>()).put(libraryId, this);
                    System.out.println("Client registered to group: " + groupId);
                    break;
                case MessageTypes.BOOK_LIST_RESPONSE:
                    List<IBook> books = (List<IBook>) msg.getData();
                    System.out.println("Received book list: " + books + " books");
                    break;
                case MessageTypes.REQUEST_BOOK:
                    // When book is requested, send to all in the group, then if any have it, send to the caller
                    book = (IBook) msg.getData();
                    System.out.println("Request receive.\n\t" + msg.getSender() + " Requests Group " + msg.getGroup() + " for "+ displayBook(book) );
                    Map<String, ClientHandler> group = groupRegistry.get(msg.getGroup());
                    for (ClientHandler handler : group.values()) {
                        handler.askForBook(msg.getGroup(), book);
                    }
                    // After asking all for book, just send a copy regardless
                    ClientHandler requester = groupRegistry.getOrDefault(msg.getGroup(), Map.of()).get(msg.getSender());
                    if (requester != null) {
                        requester.receiveBook(book);
                    }
                    break;
                case MessageTypes.SEND_BOOK_SUCCESS:
                    book = (IBook) msg.getData();
                    System.out.println("Received book to lend: " + book.getTitle() + "From: " + msg.getSender());
                    break;
                case MessageTypes.RECEIVE_BOOK_SUCCESS:
                    System.out.println("Book Received by" + msg.getSender());
                    break;
                default:
                    System.out.println("Unknown message type: " + msg.getType() + "From " + msg.getSender());
            }
        }

        public void requestBookList() throws IOException {
            sendMessage(this.messageFactory.createMessage("REQUEST_BOOK_LIST", null));
        }

        public void askForBook(String group, IBook book) throws IOException {
            sendMessage(this.messageFactory.createMessage("SEND_BOOK", book, group));
        }

        public void requestBook(String isbn) throws IOException {
            sendMessage(this.messageFactory.createMessage("REQUEST_BOOK", isbn));
        }

        public void receiveBook(IBook book) throws IOException {
            sendMessage(this.messageFactory.createMessage("RECEIVE_BOOK", book));
        }

        private void sendMessage(Message msg) throws IOException {
            out.writeObject(msg);
            out.flush();
        }

        private String displayBook(IBook book){
            return "\n\tTitle: " + book.getTitle() +"\n\tAuthor: " + book.getAuthor() + "\n\tisbn: " + book.getISBN();
        }

    }
}
