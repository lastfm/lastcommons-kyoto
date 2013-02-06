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
package fm.last.commons.kyoto.mapreduce;

import java.util.Iterator;

import kyotocabinet.ValueIterator;
import fm.last.commons.kyoto.KyotoException;

public interface Reducer {

  /**
   * Reduce a record data.
   * 
   * @param key specifies the key.
   * @param iter the iterator to get the values.
   * @note To avoid deadlock, any explicit database operation must not be performed in this method.
   * @throws KyotoException on failure.
   * @see {@link kyotocabinet.MapReduce#reduce(byte[], ValueIterator)}
   */
  void reduce(byte[] key, Iterator<byte[]> valueIterator);

}
