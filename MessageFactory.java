public class MessageFactory {
    private final String sender;
    private final String group;

    public MessageFactory(String group, String sender) {
        this.sender = sender;
        this.group = group;
    }

    public Message createMessage(String type, Object data) {
        return new Message(type, data, this.sender, this.group);
    }

    public Message createMessage(String type, Object data, String group) {
        return new Message(type, data, this.sender, group);
    }
}
