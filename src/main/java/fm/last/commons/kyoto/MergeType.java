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

import kyotocabinet.DB;

/**
 * Strategy to use when merging databases with {@link KyotoDb#mergeWith(MergeType, KyotoDb...)}.
 * 
 * @see kyotocabinet.DB#merge(DB[], int)
 * @see kyotocabinet.DB#MADD
 * @see kyotocabinet.DB#MAPPEND
 * @see kyotocabinet.DB#MREPLACE
 * @see kyotocabinet.DB#MSET
 */
public enum MergeType {
  /**
   * Overwrite the existing value
   * 
   * @see kyotocabinet.DB#MSET
   */
  SET(DB.MSET),
  /**
   * Keep the existing value
   * 
   * @see kyotocabinet.DB#MADD
   */
  ADD(DB.MADD),
  /**
   * Append the new value
   * 
   * @see kyotocabinet.DB#MAPPEND
   */
  APPEND(DB.MAPPEND),
  /**
   * Modify the existing record only
   * 
   * @see kyotocabinet.DB#MREPLACE
   */
  REPLACE(DB.MREPLACE);

  private final int value;

  private MergeType(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }

}
