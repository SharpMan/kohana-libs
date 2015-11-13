package koh.inter;

/**
 *
 * @author Neo-Craft
 */
public enum InterMessageEnum {

    HelloMessage(1),
    PlayerCommingMessage(2),
    ExpulseAccount(3),
    PlayerCreated(4);

    int value;

    private InterMessageEnum(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static InterMessageEnum valueOf(int value) {
        for (InterMessageEnum failure : values()) {
            if (failure.value == value) {
                return failure;
            }
        }
        return null;
    }

}
