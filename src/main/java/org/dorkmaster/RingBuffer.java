package org.dorkmaster;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RingBuffer<T> {

    protected CopyOnWriteArrayList<T> buffer;
    protected int head;
    protected int tail;
    protected int capacity;

    protected RingBuffer() {
        head = 0;
        tail = 0;
        capacity = Integer.MAX_VALUE; // default capacity
    }

    public RingBuffer(int capacity) {
        super();
        this.capacity = capacity;
        buffer = new CopyOnWriteArrayList<>();
    }

    public synchronized void add(T item) {
        if (buffer.size() >= capacity) {
            // remove oldest item when buffer is full
            buffer.remove(head);
            head = (head + 1) % capacity;
        }
        buffer.add(tail, item);
        tail = (tail + 1) % capacity;
    }

    public synchronized List<T> tail(int n) {
        int start = (tail - Math.min(buffer.size(), n) + capacity) % capacity;
        return buffer.subList(start, tail);
    }
}
