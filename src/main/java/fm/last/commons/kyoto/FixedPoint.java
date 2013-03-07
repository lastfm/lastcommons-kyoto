package fm.last.commons.kyoto;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

/**
 * Methods for converting kyotocabinet's 16 byte fixed point decimal representation.
 */
public final class FixedPoint {

  private FixedPoint() {
  }

  /**
   * Converts a 16 byte fixed point decimal to a double. Thanks to jamesg for putting me on the right track with this
   * conversion.
   * 
   * @param value Array of 16 bytes that represents a decimal value.
   * @return value representation as a double.
   */
  public static double toDouble(byte[] value) {
    if (value == null || value.length != 16) {
      throw new IllegalArgumentException("Not a 16 byte fixed point number - array does not contain exactly 16 bytes.");
    }
    LongBuffer buffer = ByteBuffer.wrap(value).asLongBuffer();
    long integerValue = buffer.get();
    long fractionalValue = buffer.get();
    return integerValue + (fractionalValue / 1000000000000000d);
  }

}
