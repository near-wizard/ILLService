// Message.java
import java.io.Serializable;

public class ILLMessage implements Serializable {
    private String type;
    private Object data;

    public ILLMessage(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
