package fm.last.commons.kyoto.mapreduce;

import java.util.Iterator;

import kyotocabinet.ValueIterator;
import fm.last.commons.kyoto.KyotoException;

public interface Reducer {

  /**
   * Reduce a record data.
   * 
   * @param key specifies the key.
   * @param iter the iterator to get the values.
   * @note To avoid deadlock, any explicit database operation must not be performed in this method.
   * @throws KyotoException on failure.
   * @see {@link kyotocabinet.MapReduce#reduce(byte[], ValueIterator)}
   */
  void reduce(byte[] key, Iterator<byte[]> valueIterator);

}
