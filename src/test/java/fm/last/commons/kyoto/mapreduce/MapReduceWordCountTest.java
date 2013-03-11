package fm.last.commons.kyoto.mapreduce;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fm.last.commons.kyoto.KyotoDb;
import fm.last.commons.kyoto.factory.KyotoDbBuilder;
import fm.last.commons.kyoto.factory.MapReduceJob;
import fm.last.commons.kyoto.factory.Mode;
import fm.last.commons.test.file.TemporaryFolder;

public class MapReduceWordCountTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private KyotoDb kyotoDb;

  @Before
  public void setup() throws IOException {
    File file = temporaryFolder.newFile("WordCountTest.kch");
    kyotoDb = new KyotoDbBuilder(file).modes(Mode.CREATE, Mode.READ_WRITE).buildAndOpen();
  }

  @After
  public void teardown() {
    IOUtils.closeQuietly(kyotoDb);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void wordCount() {
    final SortedMap<String, Integer> wordCounts = new TreeMap<String, Integer>();
    kyotoDb.set("a", "some words in here");
    kyotoDb.set("b", "words are great");
    kyotoDb.set("c", "some words are great in books");

    new MapReduceJob(new Mapper() {
      @Override
      public void map(byte[] key, byte[] value, Context context) {
        String[] words = new String(value).split(" ");
        for (String word : words) {
          context.write(word.getBytes(), new byte[] { 1 });
        }
      }
    }, new Reducer() {
      @Override
      public void reduce(byte[] key, Iterable<byte[]> values) {
        int count = 0;
        for (byte[] value : values) {
          count += value[0];
        }
        wordCounts.put(new String(key), count);
      }
    }).executeWith(kyotoDb);

    assertThat(wordCounts.size(), is(7));
    assertThat(
        wordCounts.keySet(),
        contains(equalTo("are"), equalTo("books"), equalTo("great"), equalTo("here"), equalTo("in"), equalTo("some"),
            equalTo("words")));
    assertThat(wordCounts.values(),
        contains(equalTo(2), equalTo(1), equalTo(2), equalTo(1), equalTo(2), equalTo(2), equalTo(3)));
  }

}
