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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import kyotocabinet.Visitor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.commons.kyoto.WritableVisitor;

@RunWith(MockitoJUnitRunner.class)
public class WritableVisitorAdapterTest {

  private static final byte[] BYTE_ARRAY_VALUE = new byte[3];

  @Mock
  private WritableVisitor mockVisitor;

  private Visitor adapter;

  @Before
  public void setup() {
    adapter = new WritableVisitorAdapter(mockVisitor);
    when(mockVisitor.emptyRecord(BYTE_ARRAY_VALUE)).thenReturn(BYTE_ARRAY_VALUE);
    when(mockVisitor.record(BYTE_ARRAY_VALUE, BYTE_ARRAY_VALUE)).thenReturn(BYTE_ARRAY_VALUE);
  }

  @Test
  public void visitFull() {
    byte[] value = adapter.visit_full(BYTE_ARRAY_VALUE, BYTE_ARRAY_VALUE);
    verify(mockVisitor).record(BYTE_ARRAY_VALUE, BYTE_ARRAY_VALUE);
    assertThat(value, is(BYTE_ARRAY_VALUE));
  }

  @Test
  public void visitEmpty() {
    byte[] value = adapter.visit_empty(BYTE_ARRAY_VALUE);
    verify(mockVisitor).emptyRecord(BYTE_ARRAY_VALUE);
    assertThat(value, is(BYTE_ARRAY_VALUE));
  }

}
