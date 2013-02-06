package fm.last.commons.kyoto.mapreduce;

import fm.last.commons.kyoto.KyotoException;

public interface Collector {

  /**
   * Emit a record from the mapper.
   * 
   * @param key specifies the key.
   * @param value specifies the value.
   * @throws KyotoException on failure.
   */
  void collect(byte[] key, byte[] value);

}
