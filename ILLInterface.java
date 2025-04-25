public interface ILLInterface {
    public boolean register(String groupName, String libraryName, String libraryUrl);
    public boolean requestBook(IBook book, String requestingLibrary);
    public boolean sendBook(IBook book, String receivingLibrary);
    public boolean receiveBook(IBook book);

    public boolean bookAvailable();

}