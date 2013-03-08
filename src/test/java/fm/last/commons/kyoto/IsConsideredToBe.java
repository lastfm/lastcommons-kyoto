package fm.last.commons.kyoto;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class IsConsideredToBe extends BaseMatcher<Double> {

  public static Matcher<Double> isConsideredToBe(final Double expected, final double errorMargin) {
    return new IsConsideredToBe(expected, errorMargin);
  }

  private final Double expected;
  private final double errorMargin;

  private IsConsideredToBe(Double expected, double errorMargin) {
    this.expected = expected;
    this.errorMargin = errorMargin;
  }

  @Override
  public boolean matches(Object object) {
    Double actual = (Double) object;
    if (Double.compare(expected, actual) == 0) {
      return true;
    }
    if ((Math.abs(expected - actual) <= errorMargin)) {
      return true;
    }
    return false;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("is within ").appendValue(errorMargin).appendText(" of ").appendValue(expected);
  }

}
