package fm.last.commons.kyoto;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FixedPointTest {
  private static final double ERROR_MARGIN = 0.000000000000001d;

  private static final byte[] _10_0001 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 23, 72, 118, -25, -1 };
  private static final byte[] __1 = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0 };
  private static final byte[] __1_1 = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -91, 12, -17, -123, -64, 0 };
  private static final byte[] _1_1 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 90, -13, 16, 122, 64, 0 };

  private byte[] bytes;
  private double expected;

  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> data = new ArrayList<Object[]>();
    data.add(new Object[] { 10.0001d, _10_0001 });
    data.add(new Object[] { -1.0d, __1 });
    data.add(new Object[] { -1.1d, __1_1 });
    data.add(new Object[] { 1.1d, _1_1 });
    return data;
  }

  public FixedPointTest(double expected, byte[] bytes) {
    this.expected = expected;
    this.bytes = bytes;
  }

  @Test
  public void testFixedPointToDouble() {
    double decodedValue = FixedPoint.toDouble(bytes);
    assertEquals("Expected to get " + expected, expected, decodedValue, ERROR_MARGIN);
  }
}
