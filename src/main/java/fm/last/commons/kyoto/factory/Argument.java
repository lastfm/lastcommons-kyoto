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

import static fm.last.commons.kyoto.DbType.CACHE_HASH;
import static fm.last.commons.kyoto.DbType.CACHE_TREE;
import static fm.last.commons.kyoto.DbType.DIRECTORY_HASH;
import static fm.last.commons.kyoto.DbType.DIRECTORY_TREE;
import static fm.last.commons.kyoto.DbType.FILE_HASH;
import static fm.last.commons.kyoto.DbType.FILE_TREE;
import static fm.last.commons.kyoto.DbType.STASH;

import java.util.EnumSet;
import java.util.Set;

import fm.last.commons.kyoto.DbType;

enum Argument {
  /* */
  OPTIONS("opts", EnumSet.of(CACHE_HASH, CACHE_TREE, FILE_HASH, FILE_TREE, DIRECTORY_HASH, DIRECTORY_TREE)),
  /* */
  LOG_APPENDER("log", EnumSet.allOf(DbType.class)),
  /* */
  LOG_LEVEL("logkind", EnumSet.allOf(DbType.class)),
  /* */
  LOG_PREFIX("logpx", EnumSet.allOf(DbType.class)),
  /* */
  BUCKETS("bnum", EnumSet.of(STASH, CACHE_HASH, CACHE_TREE, FILE_HASH, FILE_TREE)),
  /* */
  COMPRESSOR("zcomp", EnumSet.of(CACHE_HASH, CACHE_TREE, FILE_HASH, FILE_TREE, DIRECTORY_HASH, DIRECTORY_TREE)),
  /* */
  CIPHER_KEY("zkey", EnumSet.of(CACHE_HASH, CACHE_TREE, FILE_HASH, FILE_TREE, DIRECTORY_HASH, DIRECTORY_TREE)),
  /* */
  PAGE_CACHE_SIZE("pccap", EnumSet.of(CACHE_TREE, FILE_TREE, DIRECTORY_TREE)),
  /* */
  PAGE_SIZE("psiz", EnumSet.of(CACHE_TREE, FILE_TREE, DIRECTORY_TREE)),
  /* */
  PAGE_COMPARATOR("rcomp", EnumSet.of(CACHE_TREE, FILE_TREE, DIRECTORY_TREE)),
  /* */
  MEMORY_MAP_SIZE("msiz", EnumSet.of(FILE_HASH, FILE_TREE)),
  /* */
  FREE_BLOCK_POOL_SIZE("fpow", EnumSet.of(FILE_HASH, FILE_TREE)),
  /* */
  DEFRAG_UNIT_SIZE("dfunit", EnumSet.of(FILE_HASH, FILE_TREE)),
  /* */
  ALIGNMENT_POWER("apow", EnumSet.of(FILE_HASH, FILE_TREE)),
  /* */
  MAXIMUM_RECORDS("capcnt", EnumSet.of(CACHE_HASH)),
  /* */
  MAXIMUM_MEMORY("capsiz", EnumSet.of(CACHE_HASH));

  private final String key;
  private final Set<DbType> supportedTypes;

  private Argument(String key, Set<DbType> supportedTypes) {
    this.key = key;
    this.supportedTypes = supportedTypes;
  }

  String key() {
    return key;
  }

  boolean isNotSupportedByDbType(DbType dbType) {
    return !supportedTypes.contains(dbType);
  }

}
