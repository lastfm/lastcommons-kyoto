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
package fm.last.commons.kyoto;

import java.io.File;

/**
 * Wrapper around {@link kyotocabinet.FileProcessor}.
 */
public interface KyotoFileProcessor {

  /**
   * Process the database file.
   * 
   * @param dbFile the underlying database file.
   * @param recordCount the number of records in the file.
   * @param availableRegionSize the size of the available region.
   * @see kyotocabinet.FileProcessor#process(String, long, long)
   * @throws KyotoException on failure
   */
  void process(File dbFile, long recordCount, long availableRegionSize);

}
