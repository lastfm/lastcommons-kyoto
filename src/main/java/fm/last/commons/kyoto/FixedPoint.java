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
   * Converts a 16 byte fixed point decimal (64.64 bits) to a double. Thanks to jamesg for putting me on the right track
   * with this conversion.
   * 
   * @param value Array of 16 bytes that represents a decimal value.
   * @return value representation as a double.
   */
  public static double toDouble(byte[] value) {
    if (value == null || value.length != 16) {
      throw new IllegalArgumentException("Not a 16 byte fixed point number - array does not contain exactly 16 bytes.");
    }
    LongBuffer buffer = ByteBuffer.wrap(value).asLongBuffer();
    long integerPart = buffer.get();
    long fractionalPart = buffer.get();
    return integerPart + (fractionalPart / 1000000000000000d);
  }

  /**
   * Converts a double to a 16 byte fixed point decimal representation (64.64 bits).
   * 
   * @param value Decimal value.
   * @return Array of 16 bytes that represents a decimal value.
   */
  public static byte[] toBytes(double value) {
    double fractionalPart = value % 1;
    double integerPart = value - fractionalPart;
    ByteBuffer bytes = ByteBuffer.allocate(16);
    bytes.asLongBuffer().put(new long[] { (long) integerPart, (long) (fractionalPart * 1000000000000000d) });
    return bytes.array();
  }

}
