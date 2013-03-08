package fm.last.commons.kyoto.factory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.SortedMap;
import java.util.TreeMap;

import kyotocabinet.DB;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fm.last.commons.kyoto.DbType;
import fm.last.commons.kyoto.KyotoDb;
import fm.last.commons.kyoto.mapreduce.Collector;
import fm.last.commons.kyoto.mapreduce.JobExecutor;
import fm.last.commons.kyoto.mapreduce.Mapper;
import fm.last.commons.kyoto.mapreduce.Reducer;
import fm.last.commons.test.file.TemporaryFolder;

public class WordCountTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private DB db;
  private KyotoDb kyotoDb;

  @Before
  public void setup() throws IOException {
    File file = temporaryFolder.newFile("KyotoDbImplTest.kch");
    db = new DB();
    db.open(file.getAbsolutePath(), DB.OCREATE | DB.OWRITER);
    kyotoDb = new KyotoDbImpl(DbType.FILE_HASH, db, file.getAbsolutePath(), EnumSet.of(Mode.CREATE, Mode.READ_WRITE),
        file);
  }

  @After
  public void teardown() {
    if (db != null) {
      db.close();
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void wordCount() {
    final SortedMap<String, Integer> wordCounts = new TreeMap<String, Integer>();
    kyotoDb.set("a", "some words in here");
    kyotoDb.set("b", "words are great");
    kyotoDb.set("c", "some words are great in books");
    JobExecutor executor = KyotoJobExecutorFactory.INSTANCE.newExecutor(kyotoDb);
    executor.execute(new KyotoJob(new Mapper() {
      @Override
      public void map(byte[] key, byte[] value, Collector collector) {
        String[] words = new String(value).split(" ");
        for (String word : words) {
          collector.collect(word.getBytes(), new byte[] { 1 });
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
    }));
    assertThat(wordCounts.size(), is(7));
    assertThat(
        wordCounts.keySet(),
        contains(equalTo("are"), equalTo("books"), equalTo("great"), equalTo("here"), equalTo("in"), equalTo("some"),
            equalTo("words")));
    assertThat(wordCounts.values(),
        contains(equalTo(2), equalTo(1), equalTo(2), equalTo(1), equalTo(2), equalTo(2), equalTo(3)));
  }
}
