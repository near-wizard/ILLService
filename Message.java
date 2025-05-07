import java.io.Serializable;

public class Message implements Serializable {
    private final String type;
    private final Object data;
    private final String sender;
    private final String group;

    public Message(String type, Object data, String sender, String group) {
        this.type = type;
        this.data = data;
        this.sender = sender;
        this.group = group;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public String getSender() {
        return sender;
    }

    public String getGroup() {
        return group;
    }
}
