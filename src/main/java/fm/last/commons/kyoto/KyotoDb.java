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
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import fm.last.commons.kyoto.factory.Mode;

/**
 * Wrapper around {@link kyotocabinet.DB} that provides exception based error handling and a Java style API.
 * 
 * @see kyotocabinet.DB
 */
public interface KyotoDb extends Closeable {

  /**
   * Specifies no limit for the number of results returned from {@link #matchKeysByPrefix(String, long)},
   * {@link #matchKeysByRegex(String, long)}, {@link #matchKeysByLevenshtein(String, long, boolean, long)}.
   */
  public static final int NO_LIMIT = -1;

  /**
   * @param key
   * @return
   * @see kyotocabinet.DB#check(byte[])
   */
  public boolean exists(byte[] key);

  /**
   * @param key
   * @return
   * @see kyotocabinet.DB#check(String)
   */
  public boolean exists(String key);

  /**
   * Check the existence of a record.
   * 
   * @param key the key.
   * @return the size of the value, or -1 on failure.
   * @see kyotocabinet.DB#check(byte[])
   */
  public int valueSize(byte[] key);

  /**
   * @param key
   * @return
   * @see kyotocabinet.DB#check(String)
   */
  public int valueSize(String key);

  /**
   * Retrieve the value of a record and remove it atomically.
   * 
   * @param key the key.
   * @return the value of the corresponding record, or null on failure.
   * @see kyotocabinet.DB#seize(String)
   */
  public byte[] getAndRemove(byte[] key);

  /**
   * Retrieve the value of a record and remove it atomically.
   * 
   * @note Equal to the original DB.seize method except that the parameter and the return value are String.
   * @see kyotocabinet.DB#seize(byte[])
   */
  public String getAndRemove(String key);

  /**
   * Occupy database by locking and do something meanwhile.
   * 
   * @param writable true to use writer lock, or false to use reader lock.
   * @param proc a processor object which implements the FileProcessor interface. If it is null, no processing is
   *          performed.
   * @return true on success, or false on failure.
   * @note The operation of the processor is performed atomically and other threads accessing the same record are
   *       blocked. To avoid deadlock, any explicit database operation must not be performed in this method.
   */
  public boolean occupy(AccessType accessType, KyotoFileProcessor fileProcessor);

  /**
   * Get keys similar to a string in terms of the levenshtein distance.
   * 
   * @param query the query string.
   * @param maxLevenshteinDistance the maximum distance of keys to adopt.
   * @param utf flag to treat keys as UTF-8 strings.
   * @param limit the maximum number to retrieve. If it is negative, no limit is specified.
   * @return a list object of matching keys, or null on failure.
   * @see kyotocabinet.DB#match_similar(String, long, boolean, long)
   */
  public List<String> matchKeysByLevenshtein(String query, long maxLevenshteinDistance, boolean utf);

  public List<String> matchKeysByLevenshtein(String query, long maxLevenshteinDistance, boolean utf, long limit);

  /**
   * Accept a read-only visitor to a single record. The visit operation on the record is performed atomically and other
   * threads accessing the same record are blocked.
   * 
   * @param key the record key.
   * @param visitor a read-only visitor that implements {@link ReadOnlyVisitor}.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#accept(byte[], kyotocabinet.Visitor, boolean)
   */
  void accept(byte[] key, ReadOnlyVisitor visitor);

  /**
   * Accept a read-only visitor to a batch of records. The operation on each record is performed atomically and other
   * threads accessing the same record are blocked.
   * 
   * @param keys the record keys.
   * @param visitor a read-only visitor that implements {@link ReadOnlyVisitor}.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#accept_bulk(byte[][], kyotocabinet.Visitor, boolean)
   */
  void accept(byte[][] keys, ReadOnlyVisitor visitor);

  /**
   * Accept a read-only visitor to a single record. The visit operation on the record is performed atomically and other
   * threads accessing the same record are blocked.
   * 
   * @param key the record key.
   * @param visitor a read-only visitor that implements {@link ReadOnlyStringVisitor}.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#accept(byte[], kyotocabinet.Visitor, boolean)
   */
  void accept(String key, ReadOnlyStringVisitor visitor);

  /**
   * Accept a read-only visitor to a batch of records. The operation on each record is performed atomically and other
   * threads accessing the same record are blocked.
   * 
   * @param keys the record keys.
   * @param visitor a read-only visitor that implements {@link ReadOnlyStringVisitor}.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#accept_bulk(byte[][], kyotocabinet.Visitor, boolean)
   */
  void accept(List<String> keys, ReadOnlyStringVisitor visitor);

  /**
   * Accept a read-write visitor to a single record. The visit operation on the record is performed atomically and other
   * threads accessing the same record are blocked.
   * 
   * @param key the record key.
   * @param visitor a read-write visitor that implements {@link WritableVisitor}.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#accept(byte[], kyotocabinet.Visitor, boolean)
   */
  void accept(byte[] key, WritableVisitor visitor);

  /**
   * Accept a read-write visitor to a batch of records. The operation on each record is performed atomically and other
   * threads accessing the same record are blocked.
   * 
   * @param keys the record keys.
   * @param visitor a read-write visitor that implements {@link WritableVisitor}.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#accept_bulk(byte[][], kyotocabinet.Visitor, boolean)
   */
  void accept(byte[][] keys, WritableVisitor visitor);

  /**
   * Accept a read-write visitor to a single record. The visit operation on the record is performed atomically and other
   * threads accessing the same record are blocked.
   * 
   * @param key the record key.
   * @param visitor a read-write visitor that implements {@link WritableStringVisitor}.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#accept(byte[], kyotocabinet.Visitor, boolean)
   */
  void accept(String key, WritableStringVisitor visitor);

  /**
   * Accept a read-write visitor to a batch of records. The operation on each record is performed atomically and other
   * threads accessing the same record are blocked.
   * 
   * @param keys the record keys.
   * @param visitor a read-write visitor that implements {@link WritableStringVisitor}.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#accept_bulk(byte[][], kyotocabinet.Visitor, boolean)
   */
  void accept(List<String> keys, WritableStringVisitor visitor);

  /**
   * Create a record if it does not already exist in the database. If the record already exists, the record is not
   * modified and {@code false} is returned.
   * 
   * @param key the key.
   * @param value the value.
   * @return true if the record already exists.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#add(byte[], byte[])
   */
  boolean putIfAbsent(byte[] key, byte[] value);

  /**
   * Create a record if it does not already exist in the database. If the record already exists, the record is not
   * modified and {@code false} is returned.
   * 
   * @param key the key.
   * @param value the value.
   * @return true if the record already exists.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#add(String, String)
   */
  boolean putIfAbsent(String key, String value);

  /**
   * Append a value to a record. If the record does not exist, a new record is created. If the record exists, the given
   * value is appended at the end of the existing value.
   * 
   * @param key the record key.
   * @param value the value to append.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#append(byte[], byte[])
   */
  void append(byte[] key, byte[] value);

  /**
   * Append a value to a record. If the record does not exist, a new record is created. If the record exists, the given
   * value is appended at the end of the existing value.
   * 
   * @param key the record key.
   * @param value the value to append.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#append(String, String)
   */
  void append(String key, String value);

  /**
   * Starts a transaction.
   * 
   * @param synchronization Sets the {@link Synchronization} type for the transaction.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#begin_transaction(boolean)
   */
  void begin(Synchronization synchronization);

  /**
   * Remove all records in the database.
   * 
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#clear()
   */
  void clear();

  /**
   * Close the database file.
   * 
   * @throws IllegalStateException if the connection has already been closed.
   * @throws IOException on failure.
   * @see kyotocabinet.DB#close()
   */
  @Override
  void close() throws IOException;

  /**
   * Checks the value of a record and replaces it with a new value if it was the equal.
   * 
   * @param key the record key.
   * @param oldValue the value to compare with the record value.
   * @param newValue the value to replace the current record value.
   * @return true if the record value was changed to the new value.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#cas(byte[], byte[], byte[])
   * @note Take care: The documentation isn't that clear on what {@code null} parameters represent, or the meaning of
   *       the return value.
   */
  boolean compareAndSwap(byte[] key, byte[] oldValue, byte[] newValue);

  /**
   * Checks the value of a record and replaces it with a new value if it was the equal.
   * 
   * @param key the record key.
   * @param oldValue the value to compare with the record value.
   * @param newValue the value to replace the current record value.
   * @return true if the record value was changed to the new value.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#cas(byte[], byte[], byte[])
   * @note Take care: The documentation isn't that clear on what {@code null} parameters represent, or the meaning of
   *       the return value.
   */
  boolean compareAndSwap(String key, String oldValue, String newValue);

  /**
   * Create a copy of the database file.
   * 
   * @param destination the destination file.
   * @throws IOException on failure.
   * @see kyotocabinet.DB#copy(String)
   */
  void copyTo(File destination) throws IOException;

  /**
   * Get the number of records in the database.
   * 
   * @return the number of records.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#count()
   */
  long recordCount();

  /**
   * Create a cursor object.
   * 
   * @return the return value is the created cursor object. Each cursor should be closed with the
   *         {@link KyotoCursor#close()} method when it is no longer in use.
   * @see kyotocabinet.DB#cursor()
   */
  KyotoCursor cursor();

  /**
   * Dump all records into a snapshot file.
   * 
   * @param destination the destination file.
   * @throws IOException on failure.
   * @see kyotocabinet.DB#dump_snapshot(String)
   */
  void dumpSnapshotTo(File destination) throws IOException;

  /**
   * Commit the current transaction.
   * 
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#end_transaction(boolean)
   */
  void commit();

  /**
   * Rollback the current transaction.
   * 
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#end_transaction(boolean)
   */
  void rollback();

  /**
   * Retrieve the value of a record.
   * 
   * @param key the record key.
   * @return the value of the record.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#get(byte[])
   */
  byte[] get(byte[] key);

  /**
   * Bulk retrieve record values.
   * 
   * @param keys the keys to retrieve.
   * @param atomicity Should the fetch occur atomically.
   * @return the value of the corresponding records.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#get_bulk(byte[][], boolean)
   */
  byte[][] get(byte[][] keys, Atomicity atomicity);

  /**
   * Bulk retrieve record values.
   * 
   * @param keys the keys to retrieve.
   * @param atomicity Should the fetch occur atomically.
   * @return the value of the corresponding records.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#get_bulk(List, boolean)
   */
  Map<String, String> get(List<String> keys, Atomicity atomicity);

  /**
   * Retrieve the value of a record.
   * 
   * @param key the record key.
   * @return the value of the record.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#get(String)
   */
  public String get(String key);

  /**
   * The underlying database file.
   * 
   * @return The {@link File} where this database is stored, or {@code null} if the {@link DbType} does not require file
   *         system storage.
   * @see kyotocabinet.DB#path()
   */
  File getFile();

  /**
   * @return The {@link DbType} of this database instance.
   */
  DbType getType();

  /**
   * Increment the record value by a delta throwing a {@link KyotoException} if the key record does not exist.
   * 
   * @param key the record key.
   * @param delta amount to increment the record value.
   * @return the resulting value after the increment has been applied.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#increment_double(byte[], double, double)
   */
  double increment(byte[] key, double delta);

  /**
   * Increment the record value by a delta, using the delta value as the default value if the key record does not exist.
   * 
   * @param key the record key.
   * @param delta amount to increment the record value or the default value if the record doesn't exist.
   * @return the resulting value after the increment has been applied.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#increment_double(byte[], double, double)
   */
  double incrementOrSet(byte[] key, double delta);

  /**
   * Increment the record value by a delta, using a default value if the key record does not exist.
   * 
   * @param key the record key.
   * @param delta amount to increment the record value if it exists.
   * @param defaultValue the default value to set if the record doesn't exist.
   * @return the resulting value after the increment has been applied.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#increment_double(byte[], double, double)
   */
  double incrementOrSetDefault(byte[] key, double delta, double defaultValue);

  /**
   * Increment the record value by a delta throwing a {@link KyotoException} if the key record does not exist.
   * 
   * @param key the record key.
   * @param delta amount to increment the record value.
   * @return the resulting value after the increment has been applied.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#increment(byte[], long, long)
   */
  long increment(byte[] key, long delta);

  /**
   * Increment the record value by a delta, using the delta value as the default value if the key record does not exist.
   * 
   * @param key the record key.
   * @param delta amount to increment the record value or the default value if the record doesn't exist.
   * @return the resulting value after the increment has been applied.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#increment(byte[], long, long)
   */
  long incrementOrSet(byte[] key, long delta);

  /**
   * Increment the record value by a delta, using a default value if the key record does not exist.
   * 
   * @param key the record key.
   * @param delta amount to increment the record value if it exists.
   * @param defaultValue the default value to set if the record doesn't exist.
   * @return the resulting value after the increment has been applied.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#increment(byte[], long, long)
   */
  long incrementOrSetDefault(byte[] key, long delta, long defaultValue);

  /**
   * Increment the record value by a delta throwing a {@link KyotoException} if the key record does not exist.
   * 
   * @param key the record key.
   * @param delta amount to increment the record value.
   * @return the resulting value after the increment has been applied.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#increment_double(String, double, double)
   */
  double increment(String key, double delta);

  /**
   * Increment the record value by a delta, using the delta value as the default value if the key record does not exist.
   * 
   * @param key the record key.
   * @param delta amount to increment the record value or the default value if the record doesn't exist.
   * @return the resulting value after the increment has been applied.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#increment_double(String, double, double)
   */
  double incrementOrSet(String key, double delta);

  /**
   * Increment the record value by a delta, using a default value if the key record does not exist.
   * 
   * @param key the record key.
   * @param delta amount to increment the record value if it exists.
   * @param defaultValue the default value to set if the record doesn't exist.
   * @return the resulting value after the increment has been applied.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#increment_double(String, double, double)
   */
  double incrementOrSetDefault(String key, double delta, double defaultValue);

  /**
   * Increment the record value by a delta throwing a {@link KyotoException} if the key record does not exist.
   * 
   * @param key the record key.
   * @param delta amount to increment the record value.
   * @return the resulting value after the increment has been applied.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#increment(String, long, long)
   */
  long increment(String key, long delta);

  /**
   * Increment the record value by a delta, using the delta value as the default value if the key record does not exist.
   * 
   * @param key the record key.
   * @param delta amount to increment the record value or the default value if the record doesn't exist.
   * @return the resulting value after the increment has been applied.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#increment(String, long, long)
   */
  long incrementOrSet(String key, long delta);

  /**
   * Increment the record value by a delta, using a default value if the key record does not exist.
   * 
   * @param key the record key.
   * @param delta amount to increment the record value if it exists.
   * @param defaultValue the default value to set if the record doesn't exist.
   * @return the resulting value after the increment has been applied.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#increment(String, long, long)
   */
  long incrementOrSetDefault(String key, long delta, long defaultValue);

  /**
   * Visit all records with a read-only {@link ReadOnlyVisitor}. All records are visited in a single atomic block and
   * other threads are blocked until the operation completes. The {@link ReadOnlyVisitor} cannot mutate records.
   * 
   * @param visitor a read-only visitor that implements {@link ReadOnlyVisitor}.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#iterate(kyotocabinet.Visitor, boolean)
   */
  void iterate(ReadOnlyVisitor visitor);

  /**
   * Visit all records with a read-only {@link ReadOnlyStringVisitor}. All records are visited in a single atomic block
   * and other threads are blocked until the operation completes. The {@link ReadOnlyStringVisitor} cannot mutate
   * records.
   * 
   * @param visitor a read-only visitor that implements {@link ReadOnlyStringVisitor}.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#iterate(kyotocabinet.Visitor, boolean)
   */
  void iterate(ReadOnlyStringVisitor visitor);

  /**
   * Visit all records with a read-write {@link WritableVisitor}. All records are visited in a single atomic block and
   * other threads are blocked until the operation completes. The {@link WritableVisitor} can mutate records.
   * 
   * @param visitor a read-write visitor that implements {@link WritableVisitor}.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#iterate(kyotocabinet.Visitor, boolean)
   */
  void iterate(WritableVisitor visitor);

  /**
   * Visit all records with a read-write {@link WritableStringVisitor}. All records are visited in a single atomic block
   * and other threads are blocked until the operation completes. The {@link WritableStringVisitor} can mutate records.
   * 
   * @param visitor a read-write visitor that implements {@link WritableStringVisitor}.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#iterate(kyotocabinet.Visitor, boolean)
   */
  void iterate(WritableStringVisitor visitor);

  /**
   * Load records from a snapshot file.
   * 
   * @param source the source file.
   * @throws IOException on failure.
   * @see kyotocabinet.DB#load_snapshot(String)
   */
  void loadSnapshotFrom(File source) throws IOException;

  /**
   * Get keys matching a prefix.
   * 
   * @param prefix the key prefix to match
   * @param limit the records returned. If limit is negative then all matching keys are returned.
   * @return the matching keys.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#match_prefix(String, long)
   */
  List<String> matchKeysByPrefix(String prefix, long limit);

  /**
   * Get all keys matching a prefix.
   * 
   * @param prefix the key prefix to match
   * @return the matching keys.
   * @throws KyotoException on failure.
   * @see #matchKeysByPrefix(String, long)
   * @see kyotocabinet.DB#match_prefix(String, long)
   */
  List<String> matchKeysByPrefix(String prefix);

  /**
   * Get keys matching a regular expression.
   * 
   * @param prefix the key regular expression to match
   * @param limit the records returned. If limit is negative then all matching keys are returned.
   * @return the matching keys.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#match_regex(String, long)
   */
  List<String> matchKeysByRegex(String regex, long limit);

  /**
   * Get all keys matching a regular expression.
   * 
   * @param prefix the key regular expression to match
   * @return the matching keys.
   * @throws KyotoException on failure.
   * @see #matchKeysByRegex(String, long)
   * @see kyotocabinet.DB#match_regex(String, long)
   */
  List<String> matchKeysByRegex(String regex);

  /**
   * Merge records from other {@link KyotoDb} databases.
   * 
   * @param mergeType the {@link MergeType} strategy for combining records that have the same key. Specify
   *          {@link MergeType#REPLACE} to modify the existing record only, {@link MergeType#ADD} to keep the existing
   *          value, {@link MergeType#APPEND} to append the new value to the existing record value,
   *          {@link MergeType#SET} to overwrite the old value with the new value.
   * @param dbs source databases to merge into the current database.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#merge(kyotocabinet.DB[], int)
   */
  void mergeWith(MergeType mergeType, KyotoDb... dbs);

  /**
   * Open a database file. Uses the {@link Mode} and database descriptor flags specified when this {@link KyotoDb} was
   * instantiated.
   * 
   * @throws IllegalStateException if the connection has already been opened.
   * @throws IOException on failure.
   * @see kyotocabinet.DB#open(String, int)
   */
  void open() throws IOException;

  /**
   * Removes a record.
   * 
   * @param key the record key.
   * @return false if the record doesn't exist.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#remove(byte[])
   */
  boolean remove(byte[] key);

  /**
   * Removes a record.
   * 
   * @param key the record key.
   * @return false if the record doesn't exist.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#remove(String)
   */
  boolean remove(String key);

  /**
   * Bulk remove records.
   * 
   * @param keys Keys to remove.
   * @param atomicity Should the mutation occur atomically.
   * @return Number of records removed.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#remove_bulk(byte[][], boolean)
   */
  long remove(byte[][] keys, Atomicity atomicity);

  /**
   * Bulk remove records.
   * 
   * @param keys Keys to remove.
   * @param atomicity Should the mutation occur atomically.
   * @return Number of records removed.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#remove_bulk(List, boolean)
   */
  long remove(List<String> keys, Atomicity atomicity);

  /**
   * Replace the value of a record. If no record exists with the given key then no new record is created and false is
   * returned.
   * 
   * @param key the record key.
   * @param newValue the new value.
   * @return false if the record doesn't exist.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#replace(byte[], byte[])
   */
  boolean replace(byte[] key, byte[] newValue);

  /**
   * Replace the value of a record. If no record exists with the given key then no new record is created and false is
   * returned.
   * 
   * @param key the record key.
   * @param newValue the new value.
   * @return false if the record doesn't exist.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#replace(String, String)
   */
  boolean replace(String key, String newValue);

  /**
   * Set the value of a record. If no record exists with the given key then a new record is created. If the record
   * already exists then the value is overwritten.
   * 
   * @param key the record key.
   * @param value the value.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#set(byte[], byte[])
   */
  void set(byte[] key, byte[] value);

  /**
   * Bulk set a number or records.
   * 
   * @param keyValues record entries.
   * @param atomicity Should the mutation occur atomically.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#set_bulk(byte[][], boolean)
   */
  long set(byte[][] keyValues, Atomicity atomicity);

  /**
   * Bulk set a number or records.
   * 
   * @param keyValues record entries.
   * @param atomicity Should the mutation occur atomically.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#set_bulk(Map, boolean)
   */
  long set(Map<String, String> keyValues, Atomicity atomicity);

  /**
   * Set the value of a record. If no record exists with the given key then a new record is created. If the record
   * already exists then the value is overwritten.
   * 
   * @param key the record key.
   * @param value the value.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#set(String, String)
   */
  void set(String key, String value);

  /**
   * Get the size of the database file.
   * 
   * @return the size of the database file in bytes.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#size()
   */
  long sizeInBytes();

  /**
   * Get the miscellaneous status information.
   * 
   * @return status information.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#status()
   */
  Map<String, String> status();

  /**
   * Synchronize the database file with the underlying storage device.
   * 
   * @param synchronization The {@link Synchronization} semantics.
   * @param fileProcessor A post processor that implements {@link KyotoFileProcessor} or {@code null} if no
   *          post-processing is required.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#synchronize(boolean, kyotocabinet.FileProcessor)
   */
  void synchronize(Synchronization synchronization, KyotoFileProcessor fileProcessor);

  /**
   * Set the encoding of external strings. The default encoding is UTF-8.
   * 
   * @param encoding the name of the encoding.
   * @throws KyotoException on failure.
   * @see kyotocabinet.DB#tune_encoding(String)
   */
  void setEncoding(String encoding);

}
