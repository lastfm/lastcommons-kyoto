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

import java.io.IOException;
import java.rmi.UnexpectedException;

import kyotocabinet.Error;
import fm.last.commons.kyoto.KyotoException;

class ErrorHandler {

  interface ErrorSource {
    Error getError();
  }

  private final ErrorSource source;

  ErrorHandler(ErrorSource source) {
    this.source = source;
  }

  boolean wrapBooleanCall(boolean success) {
    if (!success) {
      processError();
    }
    return success;
  }

  long wrapLongCall(long result, long errorValue) {
    if (result == errorValue) {
      processError();
    }
    return result;
  }

  double wrapDoubleCall(double result, double errorValue) {
    if (result == errorValue) {
      processError();
    }
    return result;
  }

  void wrapVoidCall(boolean success) {
    if (!success) {
      processError();
    }
  }

  void wrapVoidCall(boolean success, String message) {
    if (!success) {
      processError(message);
    }
  }

  void wrapVoidIoCall(boolean success, String message) throws IOException {
    if (!success) {
      processIoError(message);
    }
  }

  <T> T wrapObjectCall(T result) {
    if (result == null) {
      processError();
    }
    return result;
  }

  void processError() {
    Error error = source.getError();
    if (error == null) {
      throw new KyotoException(new UnexpectedException("Could not read error code."));
    } else {
      ErrorType errorType = ErrorType.valueOf(error);
      if (errorType == null) {
        throw new KyotoException(new UnexpectedException("Unrecognized error code: " + error.code() + " : "
            + getErrorMessage(error)));
      }
      Throwable toThrow = errorType.newException(error);
      if (toThrow != null) {
        throw new KyotoException(toThrow);
      }
    }
  }

  private String getErrorMessage(Error error) {
    String message = error.getMessage();
    if (message == null) {
      message = "null - (this is often due to the database being closed)";
    }
    return message;
  }

  void processIoError(String message) throws IOException {
    Error error = source.getError();
    if (error == null) {
      throw new KyotoException(message, new UnexpectedException("Could not read error code."));
    } else {
      ErrorType errorType = ErrorType.valueOf(error);
      if (errorType == null) {
        throw new KyotoException(message, new UnexpectedException("Unrecognized error code: " + error.code() + " : "
            + getErrorMessage(error)));
      }
      Throwable toThrow = errorType.newException(error);
      if (toThrow != null) {
        if (toThrow instanceof IOException) {
          throw (IOException) toThrow;
        }
        throw new KyotoException(message, toThrow);
      }
    }
  }

  void processError(String message) {
    Error error = source.getError();
    if (error == null) {
      throw new KyotoException(message, new UnexpectedException("Could not read error code."));
    } else {
      ErrorType errorType = ErrorType.valueOf(error);
      if (errorType == null) {
        throw new KyotoException(message, new UnexpectedException("Unrecognized error code: " + error.code() + " : "
            + getErrorMessage(error)));
      }
      Throwable toThrow = errorType.newException(error);
      if (toThrow != null) {
        throw new KyotoException(message, toThrow);
      }
    }
  }

}
