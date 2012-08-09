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

import kyotocabinet.Visitor;

/**
 * Record visitor that can modify records. Wrapper around {@link kyotocabinet.Visitor} for the cases when methods that
 * accept visitors in {@link kyotocabinet.DB} are called with <code>'writable == true'</code>.
 * 
 * @see kyotocabinet.Visitor
 */
public interface WritableVisitor {

  /** No operation */
  public static final byte[] NOP = Visitor.NOP;
  /** Remove record */
  public static final byte[] REMOVE = Visitor.REMOVE;

  /**
   * Visit a record.
   * 
   * @param key the record key.
   * @param value the record value.
   * @return {@link #REMOVE} to remove the record, {@link #NOP} to leave the record unchanged, or a <code>byte[]</code>
   *         value to replace the current record value.
   * @see kyotocabinet.Visitor#visit_full(byte[], byte[])
   */
  byte[] record(byte[] key, byte[] value);

  /**
   * Visit an empty record.
   * 
   * @param key the record key.
   * @return {@link #REMOVE} to remove the record, {@link #NOP} to leave the record unchanged, or a <code>byte[]</code>
   *         value to replace the current record value.
   * @see kyotocabinet.Visitor#visit_empty(byte[])
   */
  byte[] emptyRecord(byte[] key);

}
