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

import static fm.last.commons.kyoto.factory.Argument.ALIGNMENT_POWER;
import static fm.last.commons.kyoto.factory.Argument.BUCKETS;
import static fm.last.commons.kyoto.factory.Argument.CIPHER_KEY;
import static fm.last.commons.kyoto.factory.Argument.COMPRESSOR;
import static fm.last.commons.kyoto.factory.Argument.DEFRAG_UNIT_SIZE;
import static fm.last.commons.kyoto.factory.Argument.FREE_BLOCK_POOL_SIZE;
import static fm.last.commons.kyoto.factory.Argument.LOG_APPENDER;
import static fm.last.commons.kyoto.factory.Argument.LOG_LEVEL;
import static fm.last.commons.kyoto.factory.Argument.LOG_PREFIX;
import static fm.last.commons.kyoto.factory.Argument.MAXIMUM_MEMORY;
import static fm.last.commons.kyoto.factory.Argument.MAXIMUM_RECORDS;
import static fm.last.commons.kyoto.factory.Argument.MEMORY_MAP_SIZE;
import static fm.last.commons.kyoto.factory.Argument.OPTIONS;
import static fm.last.commons.kyoto.factory.Argument.PAGE_CACHE_SIZE;
import static fm.last.commons.kyoto.factory.Argument.PAGE_COMPARATOR;
import static fm.last.commons.kyoto.factory.Argument.PAGE_SIZE;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import kyotocabinet.DB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.commons.kyoto.DbType;
import fm.last.commons.kyoto.DbType.StorageType;
import fm.last.commons.kyoto.KyotoDb;
import fm.last.commons.lang.units.ByteUnit;
import fm.last.commons.lang.units.MetricUnit;

/**
 * Builds {@link KyotoDb} instances. For file based databases the {@link DbType} is inferred from the file extension,
 * otherwise the type must be specified when constructing the builder. Will throw an {@link IllegalArgumentException} if
 * you try to set database options not supported by the {@link DbType}.
 */
public class KyotoDbBuilder {

  private static final File NO_FILE = null;

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final Set<Option> options;
  private final Set<Mode> modes;
  private final Map<Argument, String> arguments;
  private final InternalKyotoFactory dbFactory;
  private final DbType dbType;
  private final File file;

  /**
   * Create a builder for a memory based {@link KyotoDb}.
   * 
   * @see StorageType#MEMORY
   */
  public KyotoDbBuilder(DbType dbType) {
    this(DefaultInternalKyotoFactory.INSTANCE, dbType, NO_FILE);
    if (dbType.storageType() != StorageType.MEMORY) {
      throw new IllegalArgumentException("Type " + dbType + " requires a file system resource.");
    }
  }

  /**
   * Create a builder for a file based {@link KyotoDb}.
   * 
   * @see StorageType#FILE
   * @see StorageType#DIRECTORY
   */
  public KyotoDbBuilder(String fileName) {
    this(DefaultInternalKyotoFactory.INSTANCE, DbType.typeByFileName(fileName), new File(fileName));
  }

  /**
   * Create a builder for a file based {@link KyotoDb}.
   * 
   * @see StorageType#FILE
   * @see StorageType#DIRECTORY
   */
  public KyotoDbBuilder(File parent, String fileName) {
    this(DefaultInternalKyotoFactory.INSTANCE, DbType.typeByFileName(fileName), new File(parent, fileName));
  }

  /**
   * Create a builder for a file based {@link KyotoDb}.
   * 
   * @see StorageType#FILE
   * @see StorageType#DIRECTORY
   */
  public KyotoDbBuilder(File file) {
    this(DefaultInternalKyotoFactory.INSTANCE, DbType.typeByFileName(file.getAbsolutePath()), file);
  }

  KyotoDbBuilder(InternalKyotoFactory dbFactory, DbType dbType) {
    this(dbFactory, dbType, NO_FILE);
  }

  KyotoDbBuilder(InternalKyotoFactory dbFactory, String fileName) {
    this(dbFactory, DbType.typeByFileName(fileName), new File(fileName));
  }

  KyotoDbBuilder(InternalKyotoFactory dbFactory, File parent, String fileName) {
    this(dbFactory, DbType.typeByFileName(fileName), new File(parent, fileName));
  }

  KyotoDbBuilder(InternalKyotoFactory dbFactory, File file) {
    this(dbFactory, DbType.typeByFileName(file.getAbsolutePath()), file);
  }

  private KyotoDbBuilder(InternalKyotoFactory dbFactory, DbType dbType, File file) {
    this.dbFactory = dbFactory;
    this.dbType = dbType;
    if (file != NO_FILE) {
      dbType.validateFileForType(file);
      this.file = file;
    } else {
      this.file = NO_FILE;
    }
    modes = EnumSet.noneOf(Mode.class);
    options = EnumSet.noneOf(Option.class);
    arguments = new TreeMap<Argument, String>();
  }

  /**
   * @return an unopened {@link KyotoDb}.
   */
  public final KyotoDb build() {
    String descriptor = buildDbDescriptor();
    log.info("Creating Kyoto '{}' DB with descriptor '{}' to open with modes '{}'", new Object[] { dbType, descriptor,
      modes });
    DB delegate = dbFactory.newDb();
    KyotoDb db = new KyotoDbImpl(dbType, delegate, descriptor, modes, file);
    return db;
  }

  /**
   * Builds and opens the {@link KyotoDb} described by this builder.
   * 
   * @return an open {@link KyotoDb}.
   * @throws IOException on failure.
   * @see kyotocabinet.DB#open(String, int)
   */
  public final KyotoDb buildAndOpen() throws IOException {
    KyotoDb db = build();
    db.open();
    return db;
  }

  /**
   * Database options: <b>#opts</b>. Supported by {@link DbType#CACHE_HASH}, {@link DbType#CACHE_TREE},
   * {@link DbType#FILE_HASH}, {@link DbType#FILE_TREE}, {@link DbType#DIRECTORY_HASH}, {@link DbType#DIRECTORY_TREE}.
   * 
   * @see kyotocabinet.DB#open(String, int)
   */
  public KyotoDbBuilder options(Option... options) {
    this.options.addAll(Arrays.asList(options));
    StringBuilder optionList = new StringBuilder();
    for (Option option : this.options) {
      optionList.append(option.value());
    }
    arguments.put(OPTIONS, optionList.toString());
    return this;
  }

  /**
   * Open modes
   * 
   * @see kyotocabinet.DB#open(String, int)
   */
  public KyotoDbBuilder modes(Mode... modes) {
    this.modes.addAll(Arrays.asList(modes));
    return this;
  }

  /**
   * Where to append log entries: <b>#log</b>. Supported by all {@link DbType}s.
   */
  public KyotoDbBuilder logAppender(LogAppender appender) {
    addArgument(LOG_APPENDER, appender.value());
    return this;
  }

  /**
   * Logging level: <b>#logkind</b>. Supported by all {@link DbType}s.
   */
  public KyotoDbBuilder logLevel(LogLevel level) {
    addArgument(LOG_LEVEL, level.value());
    return this;
  }

  /**
   * Logging prefix: <b>#logpx</b>. Supported by all {@link DbType}s.
   */
  public KyotoDbBuilder logPrefix(String prefix) {
    addArgument(LOG_PREFIX, prefix);
    return this;
  }

  /**
   * Number of buckets: <b>#bnum</b>. Supported by {@link DbType#STASH}, {@link DbType#CACHE_HASH},
   * {@link DbType#CACHE_TREE}, {@link DbType#FILE_HASH}, {@link DbType#FILE_TREE}.
   */
  public KyotoDbBuilder buckets(long value, MetricUnit unit) {
    return buckets(unit.toLong(value));
  }

  /**
   * Number of buckets: <b>#bnum</b>. Supported by {@link DbType#STASH}, {@link DbType#CACHE_HASH},
   * {@link DbType#CACHE_TREE}, {@link DbType#FILE_HASH}, {@link DbType#FILE_TREE}.
   */
  public KyotoDbBuilder buckets(double value, MetricUnit unit) {
    return buckets(unit.toLong(value));
  }

  /**
   * Number of buckets: <b>#bnum</b>. Supported by {@link DbType#STASH}, {@link DbType#CACHE_HASH},
   * {@link DbType#CACHE_TREE}, {@link DbType#FILE_HASH}, {@link DbType#FILE_TREE}.
   */
  public KyotoDbBuilder buckets(long buckets) {
    addArgument(BUCKETS, String.valueOf(buckets));
    return this;
  }

  /**
   * Record compressor: <b>#zcomp</b>. Supported by {@link DbType#CACHE_HASH}, {@link DbType#CACHE_TREE},
   * {@link DbType#FILE_HASH}, {@link DbType#FILE_TREE}, {@link DbType#DIRECTORY_HASH}, {@link DbType#DIRECTORY_TREE}.
   */
  public KyotoDbBuilder compressor(Compressor compressor) {
    addArgument(COMPRESSOR, compressor.value());
    return this;
  }

  /**
   * Record cipher key: <b>#zkey</b>. Supported by {@link DbType#CACHE_HASH}, {@link DbType#CACHE_TREE},
   * {@link DbType#FILE_HASH}, {@link DbType#FILE_TREE}, {@link DbType#DIRECTORY_HASH}, {@link DbType#DIRECTORY_TREE}.
   */
  public KyotoDbBuilder cipherKey(String cipherKey) {
    addArgument(CIPHER_KEY, cipherKey);
    return this;
  }

  /**
   * Page cache size: <b>#pccap</b>. Supported by {@link DbType#CACHE_TREE}, {@link DbType#FILE_TREE},
   * {@link DbType#DIRECTORY_TREE}.
   */
  public KyotoDbBuilder pageCacheSize(long value, ByteUnit unit) {
    return pageCacheSize(unit.toBytes(value));
  }

  /**
   * Page cache size: <b>#pccap</b>. Supported by {@link DbType#CACHE_TREE}, {@link DbType#FILE_TREE},
   * {@link DbType#DIRECTORY_TREE}.
   */
  public KyotoDbBuilder pageCacheSize(double value, ByteUnit unit) {
    return pageCacheSize(unit.toBytes(value));
  }

  /**
   * Page cache size: <b>#pccap</b>. Supported by {@link DbType#CACHE_TREE}, {@link DbType#FILE_TREE},
   * {@link DbType#DIRECTORY_TREE}.
   */
  public KyotoDbBuilder pageCacheSize(long size) {
    addArgument(PAGE_CACHE_SIZE, String.valueOf(size));
    return this;
  }

  /**
   * Page size: <b>#psiz</b>. Supported by {@link DbType#CACHE_TREE}, {@link DbType#FILE_TREE},
   * {@link DbType#DIRECTORY_TREE}.
   */
  public KyotoDbBuilder pageSize(long value, ByteUnit unit) {
    return pageSize(unit.toBytes(value));
  }

  /**
   * Page size: <b>#psiz</b>. Supported by {@link DbType#CACHE_TREE}, {@link DbType#FILE_TREE},
   * {@link DbType#DIRECTORY_TREE}.
   */
  public KyotoDbBuilder pageSize(double value, ByteUnit unit) {
    return pageSize(unit.toBytes(value));
  }

  /**
   * Page size: <b>#psiz</b>. Supported by {@link DbType#CACHE_TREE}, {@link DbType#FILE_TREE},
   * {@link DbType#DIRECTORY_TREE}.
   */
  public KyotoDbBuilder pageSize(long size) {
    addArgument(PAGE_SIZE, String.valueOf(size));
    return this;
  }

  /**
   * Page comparator: <b>#pcom</b>. Supported by {@link DbType#CACHE_TREE}, {@link DbType#FILE_TREE},
   * {@link DbType#DIRECTORY_TREE}.
   */
  public KyotoDbBuilder pageComparator(PageComparator comparator) {
    addArgument(PAGE_COMPARATOR, comparator.value());
    return this;
  }

  /**
   * Memory map size: <b>#msiz</b>. Supported by {@link DbType#FILE_HASH}, {@link DbType#FILE_TREE}.
   */
  public KyotoDbBuilder memoryMapSize(long value, ByteUnit unit) {
    return memoryMapSize(unit.toBytes(value));
  }

  /**
   * Memory map size: <b>#msiz</b>. Supported by {@link DbType#FILE_HASH}, {@link DbType#FILE_TREE}.
   */
  public KyotoDbBuilder memoryMapSize(double value, ByteUnit unit) {
    return memoryMapSize(unit.toBytes(value));
  }

  /**
   * Memory map size: <b>#msiz</b>. Supported by {@link DbType#FILE_HASH}, {@link DbType#FILE_TREE}.
   */
  public KyotoDbBuilder memoryMapSize(long size) {
    addArgument(MEMORY_MAP_SIZE, String.valueOf(size));
    return this;
  }

  /**
   * Memory map size - derived from the existing file size: <b>#msiz</b>. Supported by {@link DbType#FILE_HASH},
   * {@link DbType#FILE_TREE}.
   */
  public KyotoDbBuilder memoryMapSizeFromFile() {
    validateArgumentForType(MEMORY_MAP_SIZE);
    return memoryMapSize(file.length());
  }

  /**
   * Free block pool size: <b>#fpow</b>. Supported by {@link DbType#FILE_HASH}, {@link DbType#FILE_TREE}.
   */
  public KyotoDbBuilder freeBlockPoolSize(long poolSize) {
    addArgument(FREE_BLOCK_POOL_SIZE, String.valueOf(poolSize));
    return this;
  }

  /**
   * Defrag unit size: <b>#dfunit</b>. Supported by {@link DbType#FILE_HASH}, {@link DbType#FILE_TREE}.
   */
  public KyotoDbBuilder defragUnitSize(long defragSize) {
    addArgument(DEFRAG_UNIT_SIZE, String.valueOf(defragSize));
    return this;
  }

  /**
   * Alignment power: <b>#apow</b>. Supported by {@link DbType#FILE_HASH}, {@link DbType#FILE_TREE}.
   */
  public KyotoDbBuilder alignmentPower(long alignment) {
    addArgument(ALIGNMENT_POWER, String.valueOf(alignment));
    return this;
  }

  /**
   * Maximum records: <b>#capcnt</b>. Supported by {@link DbType#CACHE_HASH}.
   */
  public KyotoDbBuilder maximumRecords(long value, MetricUnit unit) {
    return maximumRecords(unit.toLong(value));
  }

  /**
   * Maximum records: <b>#capcnt</b>. Supported by {@link DbType#CACHE_HASH}.
   */
  public KyotoDbBuilder maximumRecords(double value, MetricUnit unit) {
    return maximumRecords(unit.toLong(value));
  }

  /**
   * Maximum records: <b>#capcnt</b>. Supported by {@link DbType#CACHE_HASH}.
   */
  public KyotoDbBuilder maximumRecords(long maxRecords) {
    addArgument(MAXIMUM_RECORDS, String.valueOf(maxRecords));
    return this;
  }

  /**
   * Maximum memory: <b>#capsiz</b>. Supported by {@link DbType#CACHE_HASH}.
   */
  public KyotoDbBuilder maximumMemory(long value, ByteUnit unit) {
    maximumMemory(unit.toBytes(value));
    return this;
  }

  /**
   * Maximum memory: <b>#capsiz</b>. Supported by {@link DbType#CACHE_HASH}.
   */
  public KyotoDbBuilder maximumMemory(double value, ByteUnit unit) {
    maximumMemory(unit.toBytes(value));
    return this;
  }

  /**
   * Maximum memory: <b>#capsiz</b>. Supported by {@link DbType#CACHE_HASH}.
   */
  public KyotoDbBuilder maximumMemory(long maxMemory) {
    addArgument(MAXIMUM_MEMORY, String.valueOf(maxMemory));
    return this;
  }

  private void addArgument(Argument argument, String value) {
    validateArgumentForType(argument);
    arguments.put(argument, value);
  }

  private void validateArgumentForType(Argument argument) {
    if (argument.isNotSupportedByDbType(dbType)) {
      throw new IllegalStateException("Argument " + argument + " is not supported by DB type: " + dbType);
    }
  }

  private String buildDbDescriptor() {
    StringBuilder argsList = new StringBuilder();
    for (Map.Entry<Argument, String> entry : arguments.entrySet()) {
      argsList.append('#').append(entry.getKey().key()).append('=').append(entry.getValue());
    }
    return buildFilePath() + argsList.toString();
  }

  private String buildFilePath() {
    if (dbType.storageType() == StorageType.MEMORY) {
      return dbType.identifier();
    }
    return file.getAbsolutePath();
  }

}
