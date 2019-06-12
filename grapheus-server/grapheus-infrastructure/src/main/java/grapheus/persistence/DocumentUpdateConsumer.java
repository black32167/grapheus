package grapheus.persistence;

@FunctionalInterface
public interface DocumentUpdateConsumer<T> { 
    T update(T source);
}