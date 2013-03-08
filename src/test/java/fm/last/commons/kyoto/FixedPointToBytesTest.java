package fm.last.commons.kyoto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FixedPointToBytesTest {

  private static final byte[] _10_0001 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 23, 72, 118, -25, -1 };
  private static final byte[] __1 = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0 };
  private static final byte[] __1_1 = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -91, 12, -17, -123, -64, 0 };
  private static final byte[] _1_1 = new byte[] { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 90, -13, 16, 122, 64, 0 };

  private final byte[] expected;
  private final double value;

  @Parameters
  public static Collection<Object[]> data() {
    List<Object[]> data = new ArrayList<Object[]>();
    data.add(new Object[] { _10_0001, 10.0001d });
    data.add(new Object[] { __1, -1.0d });
    data.add(new Object[] { __1_1, -1.1d });
    data.add(new Object[] { _1_1, 1.1d });
    return data;
  }

  public FixedPointToBytesTest(byte[] expected, double value) {
    this.expected = expected;
    this.value = value;
  }

  @Test
  public void testFixedPointToDouble() {
    byte[] bytes = FixedPoint.toBytes(value);
    assertThat(bytes, is(expected));
  }

}
