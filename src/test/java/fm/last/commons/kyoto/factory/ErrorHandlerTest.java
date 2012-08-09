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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.rmi.UnexpectedException;

import kyotocabinet.Error;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fm.last.commons.kyoto.KyotoException;
import fm.last.commons.kyoto.factory.ErrorHandler.ErrorSource;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlerTest {

  @Mock
  private ErrorSource mockErrorSource;

  private ErrorHandler errorHandler;

  @Before
  public void setup() {
    errorHandler = new ErrorHandler(mockErrorSource);
    when(mockErrorSource.getError()).thenReturn(new Error(Error.MISC, "misc"));
  }

  @Test
  public void wrapBooleanCallOk() {
    assertThat(errorHandler.wrapBooleanCall(true), is(true));
  }

  @Test(expected = KyotoException.class)
  public void wrapBooleanCallFail() {
    errorHandler.wrapBooleanCall(false);
  }

  @Test
  public void wrapLongCallOk() {
    assertThat(errorHandler.wrapLongCall(1L, -1L), is(1L));
  }

  @Test(expected = KyotoException.class)
  public void wrapLongCallFail() {
    errorHandler.wrapLongCall(-1L, -1L);
  }

  @Test
  public void wrapDoubleCallOk() {
    assertThat(errorHandler.wrapDoubleCall(0.1d, -1d), is(0.1d));
  }

  @Test(expected = KyotoException.class)
  public void wrapDoubleCallFail() {
    errorHandler.wrapDoubleCall(-1d, -1d);
  }

  @Test
  public void wrapObjectCallOk() {
    assertThat(errorHandler.wrapObjectCall("hello"), is("hello"));
  }

  @Test(expected = KyotoException.class)
  public void wrapObjectCallFail() {
    errorHandler.wrapObjectCall(null);
  }

  @Test
  public void wrapVoidCallOk() {
    errorHandler.wrapVoidCall(true);
  }

  @Test(expected = KyotoException.class)
  public void wrapVoidCallFail() {
    errorHandler.wrapVoidCall(false);
  }

  @Test
  public void wrapVoidCallMessageOk() {
    errorHandler.wrapVoidCall(true, "m");
  }

  @Test(expected = KyotoException.class)
  public void wrapVoidCallMessageFail() {
    errorHandler.wrapVoidCall(false, "m");
  }

  @Test
  public void processNullError() {
    when(mockErrorSource.getError()).thenReturn(null);
    try {
      errorHandler.processError();
      fail();
    } catch (KyotoException e) {
      assertThat(e.getCause(), is(instanceOf(UnexpectedException.class)));
    }
  }

  @Test
  public void processUnknownCodeError() {
    when(mockErrorSource.getError()).thenReturn(new Error(883787832, "<- not a real code"));
    try {
      errorHandler.processError();
      fail();
    } catch (KyotoException e) {
      assertThat(e.getCause(), is(instanceOf(UnexpectedException.class)));
    }
  }

  @Test
  public void processSuccess() {
    when(mockErrorSource.getError()).thenReturn(new Error(Error.SUCCESS, ""));
    errorHandler.processError();
  }

  @Test
  public void processDuplicateRecord() {
    when(mockErrorSource.getError()).thenReturn(new Error(Error.DUPREC, ""));
    errorHandler.processError();
  }

  @Test
  public void processNoRecord() {
    when(mockErrorSource.getError()).thenReturn(new Error(Error.NOREC, ""));
    errorHandler.processError();
  }

  @Test
  public void processNotImplementedError() {
    when(mockErrorSource.getError()).thenReturn(new Error(Error.NOIMPL, ""));
    try {
      errorHandler.processError();
      fail();
    } catch (KyotoException e) {
      assertThat(e.getCause(), is(instanceOf(UnsupportedOperationException.class)));
    }
  }

  @Test
  public void processInvalidOperationError() {
    when(mockErrorSource.getError()).thenReturn(new Error(Error.INVALID, ""));
    try {
      errorHandler.processError();
      fail();
    } catch (KyotoException e) {
      assertThat(e.getCause(), is(instanceOf(IllegalStateException.class)));
    }
  }

  @Test
  public void processNoRepositoryError() {
    when(mockErrorSource.getError()).thenReturn(new Error(Error.NOREPOS, ""));
    try {
      errorHandler.processError();
      fail();
    } catch (KyotoException e) {
      assertThat(e.getCause(), is(instanceOf(IOException.class)));
    }
  }

  @Test
  public void processNoPermissionError() {
    when(mockErrorSource.getError()).thenReturn(new Error(Error.NOPERM, ""));
    try {
      errorHandler.processError();
      fail();
    } catch (KyotoException e) {
      assertThat(e.getCause(), is(instanceOf(SecurityException.class)));
    }
  }

  @Test
  public void processBrokenFileError() {
    when(mockErrorSource.getError()).thenReturn(new Error(Error.BROKEN, ""));
    try {
      errorHandler.processError();
      fail();
    } catch (KyotoException e) {
      assertThat(e.getCause(), is(instanceOf(IOException.class)));
    }
  }

  @Test
  public void processLogicalInconsistencyError() {
    when(mockErrorSource.getError()).thenReturn(new Error(Error.LOGIC, ""));
    try {
      errorHandler.processError();
      fail();
    } catch (KyotoException e) {
      assertThat(e.getCause(), is(instanceOf(IllegalStateException.class)));
    }
  }

  @Test
  public void processSystemError() {
    when(mockErrorSource.getError()).thenReturn(new Error(Error.SYSTEM, ""));
    try {
      errorHandler.processError();
      fail();
    } catch (KyotoException e) {
      assertThat(e.getCause(), is(instanceOf(RuntimeException.class)));
    }
  }

  @Test
  public void processUnknownError() {
    when(mockErrorSource.getError()).thenReturn(new Error(Error.MISC, ""));
    try {
      errorHandler.processError();
      fail();
    } catch (KyotoException e) {
      assertThat(e.getCause(), is(instanceOf(UnknownError.class)));
    }
  }

}
