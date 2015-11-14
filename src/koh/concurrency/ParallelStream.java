package koh.concurrency;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.function.*;
import java.util.stream.*;

public class ParallelStream<T> implements Stream<T> {

    public static <R> ParallelStream<R> parallelStreamOn(Stream<R> stream, ForkJoinPool pool) {
        return new ParallelStream<>(pool, stream);
    }

    private final Stream<T> delegate;
    private final ForkJoinPool parallelism;

    private ParallelStream(ForkJoinPool parallelism, Stream<T> stream) {
        this.delegate = stream.parallel();
        this.parallelism = parallelism;
    }

    private <R> ParallelStream<R> doAsyncReturn(Callable<Stream<R>> task) {
        try {
            return new ParallelStream<>(parallelism, parallelism.submit(task).get());
        } catch(Exception e) {
            return new ParallelStream<>(parallelism, Stream.empty());
        }
    }

    private void doAsyncVoid(Runnable task) {
        try {
            parallelism.submit(task).get();
        } catch(Exception ignored) {
        }
    }

    private boolean doAsyncBoolean(Callable<Boolean> task) {
        try {
            return parallelism.submit(task).get();
        } catch(Exception e) {
            return false;
        }
    }

    private <R> R doAsyncFirst(Callable<R> task) {
        try {
            return parallelism.submit(task).get();
        } catch(Exception e) {
            return null;
        }
    }

    public Stream<T> filter(Predicate<? super T> predicate) {
        return doAsyncReturn(() -> delegate.filter(predicate));
    }

    public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
        return doAsyncReturn(() -> delegate.map(mapper));
    }

    public IntStream mapToInt(ToIntFunction<? super T> mapper) {
        return doAsyncFirst(() -> delegate.mapToInt(mapper));
    }

    public LongStream mapToLong(ToLongFunction<? super T> mapper) {
        return doAsyncFirst(() -> delegate.mapToLong(mapper));
    }

    public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
        return doAsyncFirst(() -> delegate.mapToDouble(mapper));
    }

    public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return doAsyncReturn(() -> delegate.flatMap(mapper));
    }

    public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
        return doAsyncFirst(() -> delegate.flatMapToInt(mapper));
    }

    public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
        return doAsyncFirst(() -> delegate.flatMapToLong(mapper));
    }

    public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
        return doAsyncFirst(() -> delegate.flatMapToDouble(mapper));
    }

    public Stream<T> distinct() {
        return doAsyncReturn(delegate::distinct);
    }

    public Stream<T> sorted() {
        return doAsyncReturn(delegate::sorted);
    }

    public Stream<T> sorted(Comparator<? super T> comparator) {
        return doAsyncReturn(() -> delegate.sorted(comparator));
    }

    public Stream<T> peek(Consumer<? super T> action) {
        return doAsyncReturn(() -> delegate.peek(action));
    }

    public Stream<T> limit(long maxSize) {
        return doAsyncReturn(() -> delegate.limit(maxSize));
    }

    public Stream<T> skip(long n) {
        return doAsyncReturn(() -> delegate.skip(n));
    }

    public void forEach(Consumer<? super T> action) {
        doAsyncVoid(() -> delegate.forEach(action));
    }

    public void forEachOrdered(Consumer<? super T> action) {
        doAsyncVoid(() -> delegate.forEachOrdered(action));
    }

    public Object[] toArray() {
        return doAsyncFirst(delegate::toArray);
    }

    public <A> A[] toArray(IntFunction<A[]> generator) {
        return doAsyncFirst(() -> delegate.toArray(generator));
    }

    public T reduce(T identity, BinaryOperator<T> accumulator) {
        return doAsyncFirst(() -> delegate.reduce(identity, accumulator));
    }

    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        return doAsyncFirst(() -> delegate.reduce(accumulator));
    }

    public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
        return doAsyncFirst(() -> delegate.reduce(identity, accumulator, combiner));
    }

    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        return doAsyncFirst(() -> delegate.collect(supplier, accumulator, combiner));
    }

    public <R, A> R collect(Collector<? super T, A, R> collector) {
        return doAsyncFirst(() -> delegate.collect(collector));
    }

    public Optional<T> min(Comparator<? super T> comparator) {
        return doAsyncFirst(() -> delegate.min(comparator));
    }

    public Optional<T> max(Comparator<? super T> comparator) {
        return doAsyncFirst(() -> delegate.max(comparator));
    }

    public long count() {
        return delegate.count();
    }

    public boolean anyMatch(Predicate<? super T> predicate) {
        return doAsyncBoolean(() -> delegate.anyMatch(predicate));
    }

    public boolean allMatch(Predicate<? super T> predicate) {
        return doAsyncBoolean(() -> delegate.allMatch(predicate));
    }

    public boolean noneMatch(Predicate<? super T> predicate) {
        return doAsyncBoolean(() -> delegate.noneMatch(predicate));
    }

    public Optional<T> findFirst() {
        return doAsyncFirst(delegate::findFirst);
    }

    public Optional<T> findAny() {
        return doAsyncFirst(delegate::findAny);
    }

    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    public Spliterator<T> spliterator() {
        return delegate.spliterator();
    }

    public boolean isParallel() {
        return false;
    }

    public Stream<T> sequential() {
        return delegate.sequential();
    }

    public Stream<T> parallel() {
        return this;
    }

    public Stream<T> unordered() {
        return doAsyncReturn(delegate::unordered);
    }

    public Stream<T> onClose(Runnable closeHandler) {
        return delegate.onClose(closeHandler);
    }

    public void close() {
        delegate.close();
    }

}
