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

/**
 * Record visitor that cannot modify records. Wrapper around {@link kyotocabinet.Visitor} for the cases when methods
 * that accept visitors in {@link kyotocabinet.DB} are called with <code>'writable == false'</code>.
 * 
 * @see kyotocabinet.Visitor
 */
public interface ReadOnlyVisitor {

  /**
   * Visit a record.
   * 
   * @param key the record key.
   * @param value the record value.
   * @see kyotocabinet.Visitor#visit_full(byte[], byte[])
   */
  void record(byte[] key, byte[] value);

  /**
   * Visit an empty record.
   * 
   * @param key the record key.
   * @see kyotocabinet.Visitor#visit_empty(byte[])
   */
  void emptyRecord(byte[] key);

}
