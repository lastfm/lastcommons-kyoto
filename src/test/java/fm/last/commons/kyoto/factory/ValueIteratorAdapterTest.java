package fm.last.commons.kyoto.factory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import kyotocabinet.ValueIterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ValueIteratorAdapterTest {

  private static final byte[] VALUE_1 = new byte[1];
  private static final byte[] VALUE_2 = new byte[1];
  private static final byte[] VALUE_3 = new byte[1];
  @Mock
  private ValueIterator mockValueIterator;

  @Test
  public void typical() {
    when(mockValueIterator.next()).thenReturn(VALUE_1).thenReturn(VALUE_2).thenReturn(VALUE_3).thenReturn(null);
    ValueIteratorAdapter adapter = new ValueIteratorAdapter(mockValueIterator);

    assertThat(adapter.hasNext(), is(true));
    assertThat(adapter.next(), is(VALUE_1));
    assertThat(adapter.hasNext(), is(true));
    assertThat(adapter.next(), is(VALUE_2));
    assertThat(adapter.hasNext(), is(true));
    assertThat(adapter.next(), is(VALUE_3));
    assertThat(adapter.hasNext(), is(false));
    try {
      adapter.next();
      fail();
    } catch (NoSuchElementException e) {
      // left blank
    }
  }

  @Test
  public void empty() {
    when(mockValueIterator.next()).thenReturn(null);
    ValueIteratorAdapter adapter = new ValueIteratorAdapter(mockValueIterator);

    assertThat(adapter.hasNext(), is(false));
    try {
      adapter.next();
      fail();
    } catch (NoSuchElementException e) {
      // left blank
    }
  }

}
