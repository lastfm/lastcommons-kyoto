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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import kyotocabinet.Error;

enum ErrorType {

  /** error code: success */
  SUCCESS() {
    @Override
    int code() {
      return Error.SUCCESS;
    }

    @Override
    Throwable newException(Error error) {
      return null;
    }
  },
  /** error code: not implemented */
  NOT_IMPLEMENTED {
    @Override
    int code() {
      return Error.NOIMPL;
    }

    @Override
    Throwable newException(Error error) {
      return new UnsupportedOperationException("Not implemented: " + error.getMessage());
    }
  },
  /** error code: invalid operation */
  INVALID_OPERATION {
    @Override
    int code() {
      return Error.INVALID;
    }

    @Override
    Throwable newException(Error error) {
      return new IllegalStateException("Invalid operation: " + error.getMessage());
    }
  },
  /** error code: no repository. */
  NO_REPOSITORY {
    @Override
    int code() {
      return Error.NOREPOS;
    }

    @Override
    Throwable newException(Error error) {
      return new IOException("No repository: " + error.getMessage());
    }
  },
  /** error code: no permission */
  NO_PERMISSION {
    @Override
    int code() {
      return Error.NOPERM;
    }

    @Override
    Throwable newException(Error error) {
      return new SecurityException("No permission: " + error.getMessage());
    }
  },
  /** error code: broken file */
  BROKEN_FILE {
    @Override
    int code() {
      return Error.BROKEN;
    }

    @Override
    Throwable newException(Error error) {
      return new IOException("Broken file: " + error.getMessage());
    }
  },
  /** error code: record duplication */
  RECORD_DUPLICATION {
    @Override
    int code() {
      return Error.DUPREC;
    }

    @Override
    Throwable newException(Error error) {
      return null;
    }
  },
  /** error code: no record */
  NO_RECORD {
    @Override
    int code() {
      return Error.NOREC;
    }

    @Override
    Throwable newException(Error error) {
      return null;
    }
  },
  /** error code: logical inconsistency */
  LOGICAL_INCONSISTENCY {
    @Override
    int code() {
      return Error.LOGIC;
    }

    @Override
    Throwable newException(Error error) {
      return new IllegalStateException("Logical inconsistency: " + error.getMessage());
    }
  },
  /** error code: system error */
  SYSTEM_ERROR {
    @Override
    int code() {
      return Error.SYSTEM;
    }

    @Override
    Throwable newException(Error error) {
      return new RuntimeException("System error: " + error.getMessage());
    }
  },
  /** error code: miscellaneous error */
  UNKNOWN_ERROR {
    @Override
    int code() {
      return Error.MISC;
    }

    @Override
    Throwable newException(Error error) {
      return new UnknownError("Miscellaneous error: " + error.getMessage());
    }
  };

  private static Map<Integer, ErrorType> errorTypeByCode;

  static {
    Map<Integer, ErrorType> build = new HashMap<Integer, ErrorType>();
    for (ErrorType error : ErrorType.values()) {
      build.put(error.code(), error);
    }
    errorTypeByCode = Collections.unmodifiableMap(build);
  }

  abstract int code();

  abstract Throwable newException(Error error);

  static ErrorType valueOf(Error error) {
    return errorTypeByCode.get(error.code());
  }

}
