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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import kyotocabinet.Cursor;
import kyotocabinet.Error;
import kyotocabinet.Visitor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.commons.kyoto.CursorStep;
import fm.last.commons.kyoto.ReadOnlyVisitor;
import fm.last.commons.kyoto.KyotoCursor;
import fm.last.commons.kyoto.KyotoException;
import fm.last.commons.kyoto.WritableVisitor;

@RunWith(MockitoJUnitRunner.class)
public class CursorAdapterTest {

  private static final byte[] BYTE_ARRAY_VALUE = new byte[3];
  private static final byte[][] BYTE_2D_ARRAY_VALUE = new byte[2][2];
  private static final String STRING_VALUE = "";
  private static final String[] STRING_ARRAY_VALUE = { "1", "2" };

  @Mock
  private Cursor mockDelegate;
  @Mock
  private ReadOnlyVisitor mockIdempotentVisitor;
  @Mock
  private WritableVisitor mockMutatingVisitor;

  private KyotoCursor adapter;

  @Before
  public void setup() {
    adapter = new CursorAdapter(mockDelegate);
    when(mockDelegate.error()).thenReturn(new Error(Error.SYSTEM, ""));
    when(mockDelegate.accept(any(Visitor.class), anyBoolean(), anyBoolean())).thenReturn(true);
    when(mockDelegate.set_value(any(byte[].class), anyBoolean())).thenReturn(true);
    when(mockDelegate.set_value(any(String.class), anyBoolean())).thenReturn(true);
    when(mockDelegate.remove()).thenReturn(true);
    when(mockDelegate.get_key(anyBoolean())).thenReturn(BYTE_ARRAY_VALUE);
    when(mockDelegate.get_key_str(anyBoolean())).thenReturn(STRING_VALUE);
    when(mockDelegate.get_value(anyBoolean())).thenReturn(BYTE_ARRAY_VALUE);
    when(mockDelegate.get_value_str(anyBoolean())).thenReturn(STRING_VALUE);
    when(mockDelegate.get(anyBoolean())).thenReturn(BYTE_2D_ARRAY_VALUE);
    when(mockDelegate.get_str(anyBoolean())).thenReturn(STRING_ARRAY_VALUE);
    when(mockDelegate.jump()).thenReturn(true);
    when(mockDelegate.jump_back()).thenReturn(true);
    when(mockDelegate.step()).thenReturn(true);
    when(mockDelegate.step_back()).thenReturn(true);
  }

  @Test
  public void close() {
    adapter.close();
    verify(mockDelegate).disable();
  }

  @Test
  public void acceptIdempotentNextRecord() {
    adapter.accept(mockIdempotentVisitor, CursorStep.NEXT_RECORD);
    verify(mockDelegate).accept(any(ReadOnlyVisitorAdapter.class), eq(false), eq(true));
  }

  @Test
  public void acceptIdempotentNoStep() {
    adapter.accept(mockIdempotentVisitor, CursorStep.NO_STEP);
    verify(mockDelegate).accept(any(ReadOnlyVisitorAdapter.class), eq(false), eq(false));
  }

  @Test(expected = KyotoException.class)
  public void acceptIdempotentError() {
    when(mockDelegate.accept(any(Visitor.class), anyBoolean(), anyBoolean())).thenReturn(false);
    adapter.accept(mockIdempotentVisitor, CursorStep.NO_STEP);
  }

  @Test
  public void acceptMutatingNextRecord() {
    adapter.accept(mockMutatingVisitor, CursorStep.NEXT_RECORD);
    verify(mockDelegate).accept(any(WritableVisitorAdapter.class), eq(true), eq(true));
  }

  @Test
  public void acceptMutatingNoStep() {
    adapter.accept(mockMutatingVisitor, CursorStep.NO_STEP);
    verify(mockDelegate).accept(any(WritableVisitorAdapter.class), eq(true), eq(false));
  }

  @Test(expected = KyotoException.class)
  public void acceptMutatingError() {
    when(mockDelegate.accept(any(Visitor.class), anyBoolean(), anyBoolean())).thenReturn(false);
    adapter.accept(mockMutatingVisitor, CursorStep.NO_STEP);
  }

  @Test
  public void setValueByteArrayNextRecord() {
    adapter.setValue(BYTE_ARRAY_VALUE, CursorStep.NEXT_RECORD);
    verify(mockDelegate).set_value(BYTE_ARRAY_VALUE, true);
  }

  @Test
  public void setValueByteArrayNoStep() {
    adapter.setValue(BYTE_ARRAY_VALUE, CursorStep.NO_STEP);
    verify(mockDelegate).set_value(BYTE_ARRAY_VALUE, false);
  }

  @Test(expected = KyotoException.class)
  public void setValueByteArrayError() {
    when(mockDelegate.set_value(any(byte[].class), anyBoolean())).thenReturn(false);
    adapter.setValue(BYTE_ARRAY_VALUE, CursorStep.NO_STEP);
  }

  @Test
  public void setValueStringNextRecord() {
    adapter.setValue(STRING_VALUE, CursorStep.NEXT_RECORD);
    verify(mockDelegate).set_value(STRING_VALUE, true);
  }

  @Test
  public void setValueStringNoStep() {
    adapter.setValue(STRING_VALUE, CursorStep.NO_STEP);
    verify(mockDelegate).set_value(STRING_VALUE, false);
  }

  @Test(expected = KyotoException.class)
  public void setValueStringError() {
    when(mockDelegate.set_value(any(String.class), anyBoolean())).thenReturn(false);
    adapter.setValue(STRING_VALUE, CursorStep.NO_STEP);
  }

  @Test
  public void remove() {
    adapter.remove();
    verify(mockDelegate).remove();
  }

  @Test(expected = KyotoException.class)
  public void removeError() {
    when(mockDelegate.remove()).thenReturn(false);
    adapter.remove();
  }

  @Test
  public void getKeyNextRecord() {
    byte[] value = adapter.getKey(CursorStep.NEXT_RECORD);
    verify(mockDelegate).get_key(true);
    assertThat(value, is(BYTE_ARRAY_VALUE));
  }

  @Test
  public void getKeyNoStep() {
    byte[] value = adapter.getKey(CursorStep.NO_STEP);
    verify(mockDelegate).get_key(false);
    assertThat(value, is(BYTE_ARRAY_VALUE));
  }

  @Test(expected = KyotoException.class)
  public void getKeyError() {
    when(mockDelegate.get_key(anyBoolean())).thenReturn(null);
    adapter.getKey(CursorStep.NO_STEP);
  }

  @Test
  public void getKeyAsStringNextRecord() {
    String value = adapter.getKeyAsString(CursorStep.NEXT_RECORD);
    verify(mockDelegate).get_key_str(true);
    assertThat(value, is(STRING_VALUE));
  }

  @Test
  public void getKeyAsStringNoStep() {
    String value = adapter.getKeyAsString(CursorStep.NO_STEP);
    verify(mockDelegate).get_key_str(false);
    assertThat(value, is(STRING_VALUE));
  }

  @Test(expected = KyotoException.class)
  public void getKeyAsStringError() {
    when(mockDelegate.get_key_str(anyBoolean())).thenReturn(null);
    adapter.getKeyAsString(CursorStep.NO_STEP);
  }

  @Test
  public void getValueNextRecord() {
    byte[] value = adapter.getValue(CursorStep.NEXT_RECORD);
    verify(mockDelegate).get_value(true);
    assertThat(value, is(BYTE_ARRAY_VALUE));
  }

  @Test
  public void getValueNoStep() {
    byte[] value = adapter.getValue(CursorStep.NO_STEP);
    verify(mockDelegate).get_value(false);
    assertThat(value, is(BYTE_ARRAY_VALUE));
  }

  @Test(expected = KyotoException.class)
  public void getValueError() {
    when(mockDelegate.get_value(anyBoolean())).thenReturn(null);
    adapter.getValue(CursorStep.NO_STEP);
  }

  @Test
  public void getValueAsStringNextRecord() {
    String value = adapter.getValueAsString(CursorStep.NEXT_RECORD);
    verify(mockDelegate).get_value_str(true);
    assertThat(value, is(STRING_VALUE));
  }

  @Test
  public void getValueAsStringNoStep() {
    String value = adapter.getValueAsString(CursorStep.NO_STEP);
    verify(mockDelegate).get_value_str(false);
    assertThat(value, is(STRING_VALUE));
  }

  @Test(expected = KyotoException.class)
  public void getValueAsStringError() {
    when(mockDelegate.get_value_str(anyBoolean())).thenReturn(null);
    adapter.getValueAsString(CursorStep.NO_STEP);
  }

  @Test
  public void getEntryNextRecord() {
    byte[][] value = adapter.getEntry(CursorStep.NEXT_RECORD);
    verify(mockDelegate).get(true);
    assertThat(value, is(BYTE_2D_ARRAY_VALUE));
  }

  @Test
  public void getEntryNoStep() {
    byte[][] value = adapter.getEntry(CursorStep.NO_STEP);
    verify(mockDelegate).get(false);
    assertThat(value, is(BYTE_2D_ARRAY_VALUE));
  }

  @Test(expected = KyotoException.class)
  public void getEntryError() {
    when(mockDelegate.get(anyBoolean())).thenReturn(null);
    adapter.getEntry(CursorStep.NO_STEP);
  }

  @Test
  public void getEntryAsStringNextRecord() {
    String[] value = adapter.getEntryAsString(CursorStep.NEXT_RECORD);
    verify(mockDelegate).get_str(true);
    assertThat(value, is(STRING_ARRAY_VALUE));
  }

  @Test
  public void getEntryAsStringNoStep() {
    String[] value = adapter.getEntryAsString(CursorStep.NO_STEP);
    verify(mockDelegate).get_str(false);
    assertThat(value, is(STRING_ARRAY_VALUE));
  }

  @Test(expected = KyotoException.class)
  public void getEntryAsStringError() {
    when(mockDelegate.get_str(anyBoolean())).thenReturn(null);
    adapter.getEntryAsString(CursorStep.NO_STEP);
  }

  @Test
  public void scanForwardFromStart() {
    adapter.scanForwardFromStart();
    verify(mockDelegate).jump();
  }

  @Test(expected = KyotoException.class)
  public void scanForwardFromStartError() {
    when(mockDelegate.jump()).thenReturn(false);
    adapter.scanForwardFromStart();
  }

  @Test
  public void scanBackwardsFromEnd() {
    adapter.scanBackwardsFromEnd();
    verify(mockDelegate).jump_back();
  }

  @Test(expected = KyotoException.class)
  public void scanBackwardsFromEndError() {
    when(mockDelegate.jump_back()).thenReturn(false);
    adapter.scanBackwardsFromEnd();
  }

  @Test
  public void stepForwards() {
    adapter.stepForwards();
    verify(mockDelegate).step();
  }

  @Test(expected = KyotoException.class)
  public void stepForwardsError() {
    when(mockDelegate.step()).thenReturn(false);
    adapter.stepForwards();
  }

  @Test
  public void stepBackwards() {
    adapter.stepBackwards();
    verify(mockDelegate).step_back();
  }

  @Test(expected = KyotoException.class)
  public void stepBackwardsError() {
    when(mockDelegate.step_back()).thenReturn(false);
    adapter.stepBackwards();
  }

}
