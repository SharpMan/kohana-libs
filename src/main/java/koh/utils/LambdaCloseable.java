package koh.utils;

@FunctionalInterface
public interface LambdaCloseable extends AutoCloseable {

    void close();

}
