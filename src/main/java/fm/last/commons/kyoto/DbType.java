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
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The database type.
 */
public enum DbType {
  /** Prototype hash database - in-memory database implemented with <code>std::unorderd_map</code> */
  PROTOTYPE_HASH(StorageType.MEMORY, "-"),
  /** Prototype tree database - in-memory database implemented with <code>std::map</code>. */
  PROTOTYPE_TREE(StorageType.MEMORY, "+"),
  /** Stash database - in-memory database with an optimized memory footprint. */
  STASH(StorageType.MEMORY, ":"),
  /** Cache hash database - in-memory database featuring least-recently used record expiration. */
  CACHE_HASH(StorageType.MEMORY, "*"),
  /** Cache tree database - in-memory B+ tree database: an ordered cache. */
  CACHE_TREE(StorageType.MEMORY, "%"),
  /** File hash database - hash table file database. DBM implementation. */
  FILE_HASH(StorageType.FILE, "kch"),
  /** Directory hash database - hash database with records stored in a file system directory structure. */
  DIRECTORY_HASH(StorageType.DIRECTORY, "kcd"),
  /** File tree database - B+ tree file database: Ordered DBM implementation. */
  FILE_TREE(StorageType.FILE, "kct"),
  /**
   * Directory tree database - B+ tree database with records stored in a file system directory structure. Ordered DBM
   * implementation.
   */
  DIRECTORY_TREE(StorageType.DIRECTORY, "kcf"),
  /** Plain text database - plain text file as a database. */
  PLAIN_TEXT(StorageType.FILE, "kcx");

  private static final Map<String, DbType> identifierToType;

  static {
    Map<String, DbType> build = new HashMap<String, DbType>();
    for (DbType type : DbType.values()) {
      build.put(type.identifier, type);
    }
    identifierToType = Collections.unmodifiableMap(build);
  }

  private final String identifier;
  private final StorageType storageType;

  private DbType(StorageType storageType, String identifier) {
    this.storageType = storageType;
    this.identifier = identifier;
  }

  public String identifier() {
    return identifier;
  }

  public StorageType storageType() {
    return storageType;
  }

  public void validateFileForType(File file) {
    switch (storageType) {
      case FILE:
        if (!file.isFile()) {
          throw new IllegalArgumentException("Database storage type is '" + StorageType.FILE + "' yet "
              + file.getAbsolutePath() + " is not a file.");
        }
        break;
      case DIRECTORY:
        if (!file.isDirectory()) {
          throw new IllegalArgumentException("Database storage type is '" + StorageType.DIRECTORY + "' yet "
              + file.getAbsolutePath() + " is not a directory.");
        }
        break;
      default:
        throw new IllegalArgumentException("Database storage type '" + storageType
            + " does not require file resources.");
    }
  }

  /**
   * Creates a database file/folder suitable for use with the {@link DbType}.
   * <p/>
   * Examples:
   * 
   * <pre>
   * FILE_HASH.createFile("db") -> new File("db.kch").createNewFile();
   * DIRECTORY_HASH.createFile("db") -> new File("db.kcd").mkdirs();
   * </pre>
   */
  public File createFile(String fileName) throws IOException {
    return storageType.createFile(this, newFile(fileName));
  }

  /**
   * Creates a database file/folder suitable for use with the {@link DbType}.
   * <p/>
   * Examples:
   * 
   * <pre>
   * FILE_HASH.createFile(parent, "db") -> new File(parent, "db.kch").createNewFile();
   * DIRECTORY_HASH.createFile(parent, "db") -> new File(parent, "db.kcd").mkdirs();
   * </pre>
   */
  public File createFile(File parent, String fileName) throws IOException {
    return storageType.createFile(this, newFile(parent, fileName));
  }

  /**
   * Returns a database file/folder descriptor ({@link File}) for use with the {@link DbType} - <b>note:</b> this method
   * does not create the file.
   * <p/>
   * Examples:
   * 
   * <pre>
   * FILE_HASH.createFile(parent, "db") -> new File(parent, "db.kch");
   * DIRECTORY_HASH.createFile(parent, "db") -> new File(parent, "db.kcd");
   * </pre>
   */
  public File newFile(String fileName) throws IOException {
    checkForNonFileBasedTypes();
    File file = new File(fileName + "." + identifier);
    if (file.exists()) {
      validateFileForType(file);
    }
    return file;
  }

  /**
   * Returns a database file/folder descriptor ({@link File}) for use with the {@link DbType} - <b>note:</b> this method
   * does not create the file.
   * <p/>
   * Examples:
   * 
   * <pre>
   * FILE_HASH.createFile(parent, "db") -> new File(parent, "db.kch");
   * DIRECTORY_HASH.createFile(parent, "db") -> new File(parent, "db.kcd");
   * </pre>
   */
  public File newFile(File parent, String fileName) throws IOException {
    checkForNonFileBasedTypes();
    File file = new File(parent, fileName + "." + identifier);
    if (file.exists()) {
      validateFileForType(file);
    }
    return file;
  }

  /**
   * Creates a temporary database file/folder suitable for use with the {@link DbType}.
   * 
   * @see #createFile(String)
   * @see File#createTempFile(String, String)
   */
  public File createTempFile(String prefix) throws IOException {
    return storageType.createTempFile(this, null, prefix);
  }

  /**
   * Creates a temporary database file/folder suitable for use with the {@link DbType}.
   * 
   * @see #createFile(String)
   * @see File#createTempFile(String, String, File)
   */
  public File createTempFile(File parent, String prefix) throws IOException {
    return storageType.createTempFile(this, parent, prefix);
  }

  /**
   * Determines the {@link DbType} from a file name.
   * 
   * @throws IllegalArgumentException if the {@link DbType} cannot be resolved from the file name.
   */
  public static DbType typeByFileName(String fileName) {
    String identifier = fileName;
    int suffixStart = fileName.lastIndexOf(".");
    if (suffixStart >= 0) {
      identifier = fileName.substring(suffixStart + 1);
    }
    DbType dbType = identifierToType.get(identifier);
    if (dbType != null) {
      return dbType;
    }
    throw new IllegalArgumentException("Could not determine " + DbType.class.getSimpleName() + " from file name: "
        + fileName);
  }

  /**
   * The environment in which the database is stored.
   */
  public enum StorageType {
    /** An in memory database. */
    MEMORY,
    /** A file system based database that uses a single file. */
    FILE() {
      @Override
      File createFile(DbType dbType, File file) throws IOException {
        file.createNewFile();
        return file;
      }

      @Override
      File createTempFile(DbType dbType, File parent, String prefix) throws IOException {
        return File.createTempFile(prefix, "." + dbType.identifier(), parent);
      }
    },
    /** A file system based database that uses a directory tree of files. */
    DIRECTORY() {
      @Override
      File createFile(DbType dbType, File file) throws IOException {
        if (!file.mkdirs()) {
          throw new IOException("Unable to create directory: " + file);
        }
        return file;
      }

      @Override
      File createTempFile(DbType dbType, File parent, String prefix) throws IOException {
        if (parent == null) {
          parent = new File(System.getProperty("java.io.tmpdir"));
        }
        String fileName = null;
        for (int counter = 0; counter < MAX_CREATE_FOLDER_ATTEMPTS; counter++) {
          fileName = System.currentTimeMillis() + "-" + counter + "." + dbType.identifier();
          File tempDir = new File(parent, fileName);
          if (tempDir.mkdir()) {
            return tempDir;
          }
        }
        throw new IllegalStateException("Failed to create temp directory within " + MAX_CREATE_FOLDER_ATTEMPTS
            + " - last name was: " + fileName);
      }
    };

    private static final int MAX_CREATE_FOLDER_ATTEMPTS = 10000;

    File createFile(DbType dbType, File file) throws IOException {
      throw new IllegalArgumentException("Database storage type '" + this + " does not require file resources.");
    }

    File createTempFile(DbType dbType, File parent, String prefix) throws IOException {
      throw new IllegalArgumentException("Database storage type '" + this + " does not require file resources.");
    }
  }

  private void checkForNonFileBasedTypes() {
    switch (storageType) {
      case FILE:
        break;
      case DIRECTORY:
        break;
      default:
        throw new IllegalArgumentException("Database storage type '" + storageType
            + " does not require file resources.");
    }
  }

}
