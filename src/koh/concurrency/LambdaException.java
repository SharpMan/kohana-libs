package koh.concurrency;

public class LambdaException extends RuntimeException {

    private final Runnable op;

    public LambdaException(Runnable op) {
        this.op = op;
    }

    public void treat() {
        op.run();
    }
}
