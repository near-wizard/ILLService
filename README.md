# ILLService
InterLibraryLoan Service for interconnecting Programming 2 Library Management system project
Files:

There are some interfaces the code I provide will assume, please ensure you have these (will add .java files that have these interfaces, I assume you have some sort of an implementation for them):

## IBook

This is a common interface so that we all agree the minimum of what a book is. Your system should work with IBooks and if you have unique parts for your programs, you should be able to have some sort of reasonable defaults or ways to handle fields you do not receive. Working with IBooks should not break your core functionalities, though it is okay to exclude books that are missing fields for some additional functionalities you have added.

Note that I added extends Serializable. This will allow for IBooks to be easily sent over the network as the actual Java Objects and will be required to interact with the server. Simply adding the import and the extends Serializable should handle it for us.

```
import java.io.Serializable;

public interface IBook extends Serializable {
    public String getTitle();
    public String getAuthor();
    public String getISBN();
    public String getHomeLibrary();
}
```
## ILLInterface

The interface has been pared down to just one method, request book. This is how your library will interact with the ILL Service.
```
public interface ILLInterface {
    public boolean requestBook(IBook book);
}
```
## ILLClient

This is the client you will primarily be interacting with and making some updates or changes to. Areas you will be making changes to should have a comment with TODO so it should be easy to Ctrl+F through. You can create a client with groupName and libraryName and you can optionally add the IP and port of the server.

You create a client object, then call the start function on it. This will start up the client in a separate thread so your program should be able to continue while the client runs as normal.


Example using:
```
ILLClient client = new ILLClient("<groupNameHere>", "<libraryNameHere>");
/* 
You may ask why did I use the pattern of client.start() instead of starting in the constructor, there is a lot you can explore for why someone may make this choice, and I encourage exploring
*/
client.start();

// In a place you would like to request a book
client.requestBook(myIBookObject)
```

## MessageType

Just a collection of strings I use to indicate the type of message, could also make sense to use an enum, and I probably would swap it over to an enum if I were building this as a serious project

## Message
The actual objects being sent over the wire, the include the group, the sender, the type, and some payload or Object to deliver (eg: IBook)

## ILLSocketServer

Don't worry about making changes, it listens to port 12345
You can run it locally and hit the ip "localhost" or you can hit the server's ip address
Right now, if you request a book, it asks everyone in the group, and may send multiple your way (eg: if 5 in the group, and 3 have it, it will send you 3 copies)

Server-Client Architecture


(The point of this section is to give a high level understanding and explanation of what is happening under the hood, you can also skip this section and just use the code as a black box)

Server-Client architecture via sockets in Java involves two programs communicating over a network (eg: the internet):

Server: Listens on a specific port using a ServerSocket. When a client connects, it creates a Socket to communicate with that client. A socket serves like a two lane street where the server and the connected client can send and receive information.

Client: Connects to the server's IP and port using a Socket.  For an analogy, You can think of the IP Address like a home address, and a port as a door to get into the home.

They exchange data using input/output streams (InputStream, OutputStream, often wrapped with BufferedReader and PrintWriter).
