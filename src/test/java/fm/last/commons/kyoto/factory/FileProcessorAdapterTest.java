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
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.File;

import kyotocabinet.FileProcessor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.commons.kyoto.KyotoFileProcessor;

@RunWith(MockitoJUnitRunner.class)
public class FileProcessorAdapterTest {

  @Mock
  private KyotoFileProcessor mockDelegate;

  private FileProcessor adapter;

  @Before
  public void setup() {
    adapter = new FileProcessorAdapter(mockDelegate);
  }

  @Test
  public void process() {
    boolean value = adapter.process("myfile", 10L, 10L);
    verify(mockDelegate).process(eq(new File("myfile")), eq(10L), eq(10L));
    assertThat(value, is(true));
  }

  @Test
  public void processError() {
    doThrow(new RuntimeException()).when(mockDelegate).process(any(File.class), anyLong(), anyLong());
    boolean value = adapter.process("myfile", 10L, 10L);
    verify(mockDelegate).process(eq(new File("myfile")), eq(10L), eq(10L));
    assertThat(value, is(false));
  }

}
