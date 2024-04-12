package socket.object;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;

    private final MessageType type;

    public Message(String message, MessageType type) {
        this.message = message;
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
