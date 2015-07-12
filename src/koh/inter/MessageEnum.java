package koh.inter;

/**
 *
 * @author Neo-Craft
 */
public enum MessageEnum {

    HelloMessage(1),
    PlayerCommingMessage(2),
    ExpulseAccount(3),
    PlayerCreated(4);

    int value;

    private MessageEnum(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static MessageEnum valueOf(int value) {
        for (MessageEnum failure : values()) {
            if (failure.value == value) {
                return failure;
            }
        }
        return null;
    }

}
