/*
 * Copyright 2012 Last.fm
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package fm.last.commons.kyoto.factory;

import static fm.last.commons.kyoto.IsConsideredToBe.isConsideredToBe;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import kyotocabinet.DB;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.commons.kyoto.DbType;
import fm.last.commons.kyoto.FixedPoint;
import fm.last.commons.kyoto.KyotoDb;
import fm.last.commons.kyoto.KyotoException;
import fm.last.commons.kyoto.ReadOnlyVisitor;
import fm.last.commons.test.file.TemporaryFolder;

@RunWith(MockitoJUnitRunner.class)
public class KyotoDbImplTest {

  private static final double ERROR_MARGIN = 0.000000000001d;
  private static final byte[] BYTE_ARRAY_VALUE = new byte[3];
  private static final String STRING_VALUE = "value";

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

  @Test
  public void close() throws IOException {
    File file = temporaryFolder.newFile("KyotoDbImplTest-close.kch");
    db = new DB();
    kyotoDb = new KyotoDbImpl(DbType.FILE_HASH, db, file.getAbsolutePath(), EnumSet.of(Mode.CREATE, Mode.READ_WRITE),
        file);
    kyotoDb.open();
    kyotoDb.close();
    assertThat(db.close(), is(false));
  }

  @Test(expected = IOException.class)
  public void closeFail() throws IOException {
    File file = temporaryFolder.newFile("KyotoDbImplTest-closeFail.kch");
    db = new DB();
    kyotoDb = new KyotoDbImpl(DbType.FILE_HASH, db, file.getAbsolutePath(), EnumSet.of(Mode.CREATE, Mode.READ_WRITE),
        file);
    kyotoDb.open();
    kyotoDb.close();
    kyotoDb.close();
  }

  @Test
  public void open() throws IOException {
    File file = temporaryFolder.newFile("KyotoDbImplTest-open.kch");
    db = new DB();
    kyotoDb = new KyotoDbImpl(DbType.FILE_HASH, db, file.getAbsolutePath(), EnumSet.of(Mode.CREATE, Mode.READ_WRITE),
        file);
    kyotoDb.open();
    assertThat(db.close(), is(true));
  }

  @Test(expected = IllegalStateException.class)
  public void openFail() throws IOException {
    File file = temporaryFolder.newFile("KyotoDbImplTest-openFail.kch");
    db = new DB();
    kyotoDb = new KyotoDbImpl(DbType.FILE_HASH, db, file.getAbsolutePath(), EnumSet.of(Mode.CREATE, Mode.READ_WRITE),
        file);
    kyotoDb.open();
    kyotoDb.open();
  }

  @Test
  public void putIfAbsentByte() {
    boolean wasAbsent = kyotoDb.putIfAbsent(BYTE_ARRAY_VALUE, BYTE_ARRAY_VALUE);
    assertThat(wasAbsent, is(true));
    wasAbsent = kyotoDb.putIfAbsent(BYTE_ARRAY_VALUE, BYTE_ARRAY_VALUE);
    assertThat(wasAbsent, is(false));
  }

  @Test
  public void getNull() {
    assertThat(kyotoDb.get("DOESNOTEXIST"), is(nullValue()));
  }

  @Test(expected = KyotoException.class)
  public void putIfAbsentByteFail() {
    db.close();
    kyotoDb.putIfAbsent(BYTE_ARRAY_VALUE, BYTE_ARRAY_VALUE);
  }

  @Test
  public void putIfAbsentString() {
    boolean wasAbsent = kyotoDb.putIfAbsent(STRING_VALUE, STRING_VALUE);
    assertThat(wasAbsent, is(true));
    wasAbsent = kyotoDb.putIfAbsent(STRING_VALUE, STRING_VALUE);
    assertThat(wasAbsent, is(false));
  }

  @Test(expected = KyotoException.class)
  public void putIfAbsentStringFail() {
    db.close();
    kyotoDb.putIfAbsent(STRING_VALUE, STRING_VALUE);
  }

  @Test
  public void removeByteArray() {
    boolean recordExisted = kyotoDb.remove(BYTE_ARRAY_VALUE);
    assertThat(recordExisted, is(false));
    kyotoDb.putIfAbsent(BYTE_ARRAY_VALUE, BYTE_ARRAY_VALUE);
    recordExisted = kyotoDb.remove(BYTE_ARRAY_VALUE);
    assertThat(recordExisted, is(true));
  }

  @Test(expected = KyotoException.class)
  public void removeByteArrayFail() {
    db.close();
    kyotoDb.remove(BYTE_ARRAY_VALUE);
  }

  @Test
  public void removeString() {
    boolean recordExisted = kyotoDb.remove(STRING_VALUE);
    assertThat(recordExisted, is(false));
    kyotoDb.putIfAbsent(STRING_VALUE, STRING_VALUE);
    recordExisted = kyotoDb.remove(STRING_VALUE);
    assertThat(recordExisted, is(true));
  }

  @Test(expected = KyotoException.class)
  public void removeStringFail() {
    db.close();
    kyotoDb.remove(STRING_VALUE);
  }

  @Test
  public void appendByte() {
    kyotoDb.append(BYTE_ARRAY_VALUE, "a".getBytes());
    assertThat(db.get(BYTE_ARRAY_VALUE), is("a".getBytes()));
    kyotoDb.append(BYTE_ARRAY_VALUE, "b".getBytes());
    assertThat(db.get(BYTE_ARRAY_VALUE), is("ab".getBytes()));
    kyotoDb.append(BYTE_ARRAY_VALUE, "c".getBytes());
    assertThat(db.get(BYTE_ARRAY_VALUE), is("abc".getBytes()));
  }

  @Test(expected = KyotoException.class)
  public void appendByteFail() {
    db.close();
    kyotoDb.append(BYTE_ARRAY_VALUE, BYTE_ARRAY_VALUE);
  }

  @Test
  public void iterateIdempotentByteArray() {
    kyotoDb.append("1", "a");
    kyotoDb.append("2", "b");
    kyotoDb.append("3", "c");
    final Map<String, String> capture = new HashMap<String, String>();
    kyotoDb.iterate(new ReadOnlyVisitor() {

      @Override
      public void record(byte[] key, byte[] value) {
        capture.put(new String(key), new String(value));
      }

      @Override
      public void emptyRecord(byte[] key) {
      }
    });
    assertThat(capture.size(), is(3));
    assertThat(capture.get("1"), is("a"));
    assertThat(capture.get("2"), is("b"));
    assertThat(capture.get("3"), is("c"));
  }

  @Test
  public void incrementWithDefaultNonExistentOk() {
    byte[] key = "non-existent".getBytes();
    double newValue = kyotoDb.incrementWithDefault(key, -0.1d, -1838.0908d);

    assertThat(newValue, isConsideredToBe(-1838.1908d, ERROR_MARGIN));
    assertThat(FixedPoint.toDouble(db.get(key)), isConsideredToBe(-1838.1908d, ERROR_MARGIN));
  }

  @Test
  public void incrementWithDefaultOk() {
    byte[] key = "non-existent".getBytes();
    double newValue = kyotoDb.incrementWithDefault(key, 0.1d, 1.1d);
    assertThat(newValue, isConsideredToBe(1.2d, ERROR_MARGIN));
    assertThat(FixedPoint.toDouble(db.get(key)), isConsideredToBe(1.2d, ERROR_MARGIN));

    newValue = kyotoDb.incrementWithDefault(key, -2.0d, 1.0d);
    assertThat(newValue, isConsideredToBe(-0.8d, ERROR_MARGIN));
    assertThat(FixedPoint.toDouble(db.get(key)), isConsideredToBe(-0.8d, ERROR_MARGIN));
  }

  @Test(expected = KyotoException.class)
  public void incrementNonExistent() {
    kyotoDb.increment("non-existent", -1.1);
  }

  @Test
  public void incrementOk() {
    byte[] key = "non-existent".getBytes();
    kyotoDb.set(key, FixedPoint.toBytes(-1.843d));
    double newValue = kyotoDb.increment(key, 1.01d);
    assertThat(newValue, isConsideredToBe(-0.833d, ERROR_MARGIN));
    assertThat(FixedPoint.toDouble(db.get(key)), isConsideredToBe(-0.833d, ERROR_MARGIN));
  }

  @Test
  public void setDouble() {
    byte[] key = "non-existent".getBytes();
    kyotoDb.set(key, 10.0001d);
    assertThat(kyotoDb.get(key), is(new byte[] { 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 23, 72, 118, -25, -1 }));
  }

}
