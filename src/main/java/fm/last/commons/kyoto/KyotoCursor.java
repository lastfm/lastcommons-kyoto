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

import java.io.Closeable;
import java.io.IOException;

/**
 * Wrapper around {@link kyotocabinet.Cursor}.
 * 
 * @see kyotocabinet.Cursor
 */
public interface KyotoCursor extends Closeable {

  /**
   * Close the cursor. This method should be called explicitly when the cursor is no longer in use.
   * 
   * @throws IOException on failure.
   * @see kyotocabinet.Cursor#disable()
   */
  @Override
  void close() throws IOException;

  /**
   * Accept a read-only visitor to the current record.
   * 
   * @param visitor a visitor object which implements the {@link #IdempotentVisitor} interface.
   * @param step {@link CursorStep#NEXT_RECORD} to move the cursor to the next record, or {@link CursorStep#NO_STEP} for
   *          no move.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#accept(kyotocabinet.Visitor, boolean, boolean)
   */
  void accept(ReadOnlyVisitor visitor, CursorStep step);

  /**
   * Accept a record mutating visitor to the current record.
   * 
   * @param visitor a visitor object which implements the {@link #IdempotentVisitor} interface.
   * @param step {@link CursorStep#NEXT_RECORD} to move the cursor to the next record, or {@link CursorStep#NO_STEP} for
   *          no move.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#accept(kyotocabinet.Visitor, boolean, boolean)
   */
  void accept(WritableVisitor visitor, CursorStep step);

  /**
   * Set the value of the current record.
   * 
   * @param value new value.
   * @param step {@link CursorStep#NEXT_RECORD} to move the cursor to the next record, or {@link CursorStep#NO_STEP} to
   *          remain at the same location.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#set_value(byte[], boolean)
   */
  void setValue(byte[] value, CursorStep step);

  /**
   * Set the value of the current record.
   * 
   * @param value new value.
   * @param step {@link CursorStep#NEXT_RECORD} to move the cursor to the next record, or {@link CursorStep#NO_STEP} to
   *          remain at the same location.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#set_value(String, boolean)
   */
  void setValue(String value, CursorStep step);

  /**
   * Remove the current record. The cursor is moved to the next record implicitly.
   * 
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#remove()
   */
  void remove();

  /**
   * Get the key of the current record.
   * 
   * @param step {@link CursorStep#NEXT_RECORD} to move the cursor to the next record, or {@link CursorStep#NO_STEP} to
   *          remain at the same location.
   * @return the value of the current record or <code>null</code> if the cursor is invalid.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#get_key(boolean)
   */
  byte[] getKey(CursorStep step);

  /**
   * Get the key of the current record.
   * 
   * @param step {@link CursorStep#NEXT_RECORD} to move the cursor to the next record, or {@link CursorStep#NO_STEP} to
   *          remain at the same location.
   * @return the value of the current record or <code>null</code> if the cursor is invalid.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#get_key_str(boolean)
   */
  String getKeyAsString(CursorStep step);

  /**
   * Get the value of the current record.
   * 
   * @param step {@link CursorStep#NEXT_RECORD} to move the cursor to the next record, or {@link CursorStep#NO_STEP} to
   *          remain at the same location.
   * @return the value of the current record or <code>null</code> if the cursor is invalid.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#get_value(boolean)
   */
  byte[] getValue(CursorStep step);

  /**
   * Get the value of the current record.
   * 
   * @param step {@link CursorStep#NEXT_RECORD} to move the cursor to the next record, or {@link CursorStep#NO_STEP} to
   *          remain at the same location.
   * @return the value of the current record or <code>null</code> if the cursor is invalid.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#get_value_str(boolean)
   */
  String getValueAsString(CursorStep step);

  /**
   * Get a pair of the key and the value of the current record.
   * 
   * @param step {@link CursorStep#NEXT_RECORD} to move the cursor to the next record, or {@link CursorStep#NO_STEP} to
   *          remain at the same location.
   * @return a pair of the key and the value of the current record or <code>null</code> if the cursor is invalid.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#get(boolean)
   */
  byte[][] getEntry(CursorStep step);

  /**
   * Get a pair of the key and the value of the current record.
   * 
   * @param step {@link CursorStep#NEXT_RECORD} to move the cursor to the next record, or {@link CursorStep#NO_STEP} to
   *          remain at the same location.
   * @return a pair of the key and the value of the current record or <code>null</code> if the cursor is invalid.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#get_str(boolean)
   */
  String[] getEntryAsString(CursorStep step);

  /**
   * Jump the cursor to the first record for forward scan.
   * 
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#jump()
   */
  void scanForwardFromStart();

  /**
   * Jump the cursor to a record for forward scan.
   * 
   * @param key the key of the destination record.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#jump(byte[])
   */
  void scanForwardFromKey(byte[] key);

  /**
   * Jump the cursor to a record for forward scan.
   * 
   * @param key the key of the destination record.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#jump(String)
   */
  void scanForwardFromKey(String key);

  /**
   * Jump the cursor to the last record for backward scan. This method is provided for tree databases. Other database
   * types, including hash databases, may provide a dummy implementation.
   * 
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#jump_back()
   */
  void scanBackwardsFromEnd();

  /**
   * Jump the cursor to a record for backward scan. This method is provided for tree databases. Other database types,
   * including hash databases, may provide a dummy implementation.
   * 
   * @param key the key of the destination record.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#jump_back(byte[])
   */
  void scanBackwardsFromKey(byte[] key);

  /**
   * Jump the cursor to a record for backward scan. This method is provided for tree databases. Other database types,
   * including hash databases, may provide a dummy implementation.
   * 
   * @param key the key of the destination record.
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#jump_back(String)
   */
  void scanBackwardsFromKey(String key);

  /**
   * Step the cursor to the next record. This method is provided for tree databases. Other database types, including
   * hash databases, may provide a dummy implementation.
   * 
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#step()
   */
  void stepForwards();

  /**
   * Step the cursor to the previous record. This method is provided for tree databases. Other database types, including
   * hash databases, may provide a dummy implementation.
   * 
   * @throws KyotoException on failure.
   * @see kyotocabinet.Cursor#step_back()
   */
  void stepBackwards();

}
