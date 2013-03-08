/*
 * Copyright 2012 Last.fm
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package fm.last.commons.kyoto.factory;

import java.util.Iterator;
import java.util.NoSuchElementException;

import kyotocabinet.ValueIterator;
import fm.last.commons.kyoto.mapreduce.Reducer;

/**
 * Converts a {@link ValueIterator} to an {@link Iterator}{@code <byte[]>} for the {@link Reducer}.
 */
class ValueIteratorAdapter implements Iterable<byte[]>, Iterator<byte[]> {

  private final ValueIterator delegate;
  private byte[] nextValue;
  private volatile boolean iteratorReturned;

  ValueIteratorAdapter(ValueIterator delegate) {
    this.delegate = delegate;
  }

  /**
   * Determine if the delegate {@link ValueIterator} has a {@code next} value;
   * 
   * @see {@link ValueIterator#next()}.
   */
  @Override
  public boolean hasNext() {
    if (nextValue != null) {
      return true;
    }
    nextValue = delegate.next();
    return nextValue != null;
  }

  /**
   * Get the next value.
   * 
   * @return the next value
   * @throws NoSuchElementException if no further elements exist.
   * @see {@link ValueIterator#next()}.
   */
  @Override
  public byte[] next() {
    if (hasNext()) {
      byte[] toReturn = nextValue;
      nextValue = null;
      return toReturn;
    }
    throw new NoSuchElementException();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("remove() not supported by this Iterator.");
  }

  @Override
  public synchronized Iterator<byte[]> iterator() {
    if (iteratorReturned) {
      throw new IllegalStateException("You may only create one iterator from this iterable.");
    }
    iteratorReturned = true;
    return this;
  }

}
