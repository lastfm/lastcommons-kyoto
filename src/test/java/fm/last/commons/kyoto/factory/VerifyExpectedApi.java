package fm.last.commons.kyoto.factory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

import kyotocabinet.DB;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fm.last.commons.kyoto.DbType;
import fm.last.commons.kyoto.KyotoDb;
import fm.last.commons.test.file.TemporaryFolder;

public class VerifyExpectedApi {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private DB db;
  private KyotoDb kyotoDb;

  @Before
  public void setup() throws IOException {
    File file = temporaryFolder.newFile("KyotoDbImplTest.kct");
    db = new DB();
    db.open(file.getAbsolutePath(), DB.OCREATE | DB.OWRITER);
    kyotoDb = new KyotoDbImpl(DbType.FILE_TREE, db, file.getAbsolutePath(), EnumSet.of(Mode.CREATE, Mode.READ_WRITE),
        file);
  }

  @After
  public void teardown() {
    if (db != null) {
      db.close();
    }
  }

  @Test
  public void getNull() throws IOException {
    assertThat(kyotoDb.get("DOESNOTEXIST"), is(nullValue()));
  }

  @Test
  public void getPutAndGet() throws IOException {
    db.set("x", "y");
    assertThat(kyotoDb.get("x"), is("y"));
    assertThat(kyotoDb.get("DOESNOTEXIST"), is(nullValue()));
  }

}
