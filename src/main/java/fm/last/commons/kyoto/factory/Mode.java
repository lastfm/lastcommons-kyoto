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

import kyotocabinet.DB;

/**
 * Database open mode.
 * 
 * @see KyotoDbBuilder#modes(Mode...)
 */
public enum Mode {
  /**
   * @see kyotocabinet.DB#OAUTOSYNC
   */
  AUTO_SYNC(DB.OAUTOSYNC),
  /**
   * Automatically manage transactions.
   * 
   * @see kyotocabinet.DB#OAUTOTRAN
   */
  AUTO_TRANSACTION(DB.OAUTOTRAN),
  /**
   * Work around for file systems that don't support file locking.
   * 
   * @see kyotocabinet.DB#ONOLOCK
   */
  NO_LOCK(DB.ONOLOCK),
  /**
   * @see kyotocabinet.DB#ONOREPAIR
   */
  NO_REPAIR(DB.ONOREPAIR),
  /**
   * Open the DB for reading only
   * 
   * @see kyotocabinet.DB#OREADER
   */
  READ_ONLY(DB.OREADER),
  /**
   * @see kyotocabinet.DB#OTRUNCATE
   */
  TRUNCATE(DB.OTRUNCATE),
  /**
   * @see kyotocabinet.DB#OTRYLOCK
   */
  TRY_LOCK(DB.OTRYLOCK),
  /**
   * Open the DB for writing
   * 
   * @see kyotocabinet.DB#OWRITER
   */
  READ_WRITE(DB.OWRITER),
  /**
   * Create the DB if it does not exist
   * 
   * @see kyotocabinet.DB#OCREATE
   */
  CREATE(DB.OCREATE);

  private final int bitIndex;

  private Mode(int bitIndex) {
    this.bitIndex = bitIndex;
  }

  public int value() {
    return bitIndex;
  }

}
