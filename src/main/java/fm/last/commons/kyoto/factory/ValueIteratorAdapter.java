package fm.last.commons.kyoto.factory;

import java.util.Iterator;

import kyotocabinet.ValueIterator;

class ValueIteratorAdapter implements Iterator<byte[]> {

  private final ValueIterator delegate;
  private byte[] nextValue;

  ValueIteratorAdapter(ValueIterator delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean hasNext() {
    if (nextValue != null) {
      return true;
    }
    nextValue = delegate.next();
    return nextValue != null;
  }

  @Override
  public byte[] next() {
    if (hasNext()) {
      byte[] toReturn = nextValue;
      nextValue = null;
      return toReturn;
    }
    return null;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("remove() not supported by this Iterator.");
  }

}
