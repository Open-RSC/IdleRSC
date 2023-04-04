package scripting.idlescript.framework.util;

import java.util.LinkedList;
import java.util.List;

public class LimitedQueue<E> {

  private final LinkedList<E> queue;
  private final int limit;

  public LimitedQueue(int limit) {
    this.queue = new LinkedList<>();
    this.limit = limit;
  }

  public void add(E element) {
    if (queue.size() >= limit) {
      queue.removeLast();
    }
    queue.addFirst(element);
  }

  public boolean contains(E element) {
    return queue.contains(element);
  }

  public int size() {
    return queue.size();
  }

  public boolean isEmpty() {
    return queue.isEmpty();
  }

  public List<E> getAllElements() {
    return queue.subList(0, queue.size());
  }
}
