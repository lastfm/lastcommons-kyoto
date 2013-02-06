package fm.last.commons.kyoto.mapreduce;

import fm.last.commons.kyoto.KyotoException;

public interface Mapper {

  /**
   * Map a record data.
   * 
   * @param key specifies the key.
   * @param value specifies the value.
   * @note To avoid deadlock, any explicit database operation must not be performed in this method.
   * @throws KyotoException on failure.
   * @see {@link kyotocabinet.MapReduce#map(byte[], byte[])}
   */
  void map(byte[] key, byte[] value, Collector emitter);

}
