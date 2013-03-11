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

public enum Option {
  /**
   * Use 32-bit addressing - only use this if your database will be less than 4GiBytes in size.
   */
  SMALL('s'),
  /** */
  LINEAR('l'),
  /**
   * The key and the value of each record is compressed implicitly when stored in the file. If the value is bigger than
   * 1KB or more, compression is effective.
   */
  COMPRESS('c');

  private final char value;

  private Option(char value) {
    this.value = value;
  }

  char value() {
    return value;
  }

}
