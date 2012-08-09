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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

import fm.last.commons.test.file.TemporaryFolder;

public class DbTypeTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void fileHashValidateFileForTypeOk() throws IOException {
    DbType.FILE_HASH.validateFileForType(temporaryFolder.newFile("fileHash1.kch"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void fileHashValidateFileForTypeFolderFail() throws IOException {
    DbType.FILE_HASH.validateFileForType(temporaryFolder.newFolder("fileHash2.kch"));
  }

  @Test
  public void fileTreeValidateFileForTypeOk() throws IOException {
    DbType.FILE_TREE.validateFileForType(temporaryFolder.newFile("fileTree1.kct"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void fileTreeValidateFileForTypeFolderFail() throws IOException {
    DbType.FILE_TREE.validateFileForType(temporaryFolder.newFolder("fileTree2.kct"));
  }

  @Test
  public void plainTextValidateFileForTypeOk() throws IOException {
    DbType.PLAIN_TEXT.validateFileForType(temporaryFolder.newFile("plainText1.kcx"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void plainTextValidateFileForTypeFolderFail() throws IOException {
    DbType.PLAIN_TEXT.validateFileForType(temporaryFolder.newFolder("plainText2.kcx"));
  }

  @Test
  public void directoryTreeValidateFileForTypeOk() throws IOException {
    DbType.DIRECTORY_TREE.validateFileForType(temporaryFolder.newFolder("directoryTree2.kcf"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void directoryTreeValidateFileForTypeFileFail() throws IOException {
    DbType.DIRECTORY_TREE.validateFileForType(temporaryFolder.newFile("directoryTree1.kcf"));
  }

  @Test
  public void directoryHashValidateFileForTypeOk() throws IOException {
    DbType.DIRECTORY_HASH.validateFileForType(temporaryFolder.newFolder("directoryHash2.kcd"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void directoryHashValidateFileForTypeFileFail() throws IOException {
    DbType.DIRECTORY_HASH.validateFileForType(temporaryFolder.newFile("directoryHash1.kcd"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeHashValidateFileForTypeFail() throws IOException {
    DbType.PROTOTYPE_HASH.validateFileForType(temporaryFolder.newFile("x"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeTreeValidateFileForTypeFail() throws IOException {
    DbType.PROTOTYPE_TREE.validateFileForType(temporaryFolder.newFile("x"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void stashValidateFileForTypeFail() throws IOException {
    DbType.STASH.validateFileForType(temporaryFolder.newFile("x"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheHashValidateFileForTypeFail() throws IOException {
    DbType.CACHE_HASH.validateFileForType(temporaryFolder.newFile("x"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheTreeValidateFileForTypeFail() throws IOException {
    DbType.CACHE_TREE.validateFileForType(temporaryFolder.newFile("x"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void typeByFileNameFail() {
    DbType.typeByFileName("notAKyotoFile.xxx");
  }

  @Test
  public void typeByFileNameKCHS() {
    assertThat(DbType.typeByFileName("notAKyotoFile.kch"), is(DbType.FILE_HASH));
  }

  @Test
  public void typeByFileNameKCT() {
    assertThat(DbType.typeByFileName("notAKyotoFile.kct"), is(DbType.FILE_TREE));
  }

  @Test
  public void typeByFileNameKCD() {
    assertThat(DbType.typeByFileName("notAKyotoFile.kcf"), is(DbType.DIRECTORY_TREE));
  }

  @Test
  public void typeByFileNameKCF() {
    assertThat(DbType.typeByFileName("notAKyotoFile.kcf"), is(DbType.DIRECTORY_TREE));
  }

  @Test
  public void typeByFileNameProtoHash() {
    assertThat(DbType.typeByFileName("-"), is(DbType.PROTOTYPE_HASH));
  }

  @Test
  public void typeByFileNameProtoTree() {
    assertThat(DbType.typeByFileName("+"), is(DbType.PROTOTYPE_TREE));
  }

  @Test
  public void typeByFileNameCacheHash() {
    assertThat(DbType.typeByFileName("*"), is(DbType.CACHE_HASH));
  }

  @Test
  public void typeByFileNameCacheTree() {
    assertThat(DbType.typeByFileName("%"), is(DbType.CACHE_TREE));
  }

  @Test
  public void typeByFileNameStash() {
    assertThat(DbType.typeByFileName(":"), is(DbType.STASH));
  }

  @Test(expected = IllegalArgumentException.class)
  public void typeByFileNameUnknown() {
    DbType.typeByFileName("?");
  }

  @Test
  public void fileHashCreateFile() throws IOException {
    File dbFile = DbType.FILE_HASH.createFile(getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isFile(), is(true));
    assertThat(dbFile.getName().endsWith(".kch"), is(true));
    dbFile.delete();
  }

  @Test
  public void fileHashCreateFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("createFileParent");
    File dbFile = DbType.FILE_HASH.createFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isFile(), is(true));
    assertThat(dbFile.getName().endsWith(".kch"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
    dbFile.delete();
  }

  @Test
  public void fileHashCreateTempFile() throws IOException {
    File dbFile = DbType.FILE_HASH.createTempFile(getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isFile(), is(true));
    assertThat(dbFile.getName().endsWith(".kch"), is(true));
    dbFile.delete();
  }

  @Test
  public void fileHashCreateTempFileAtLocation() throws IOException {
    File parent = temporaryFolder.newFolder("createTempFileParent");
    File dbFile = DbType.FILE_HASH.createTempFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isFile(), is(true));
    assertThat(dbFile.getName().endsWith(".kch"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
    dbFile.delete();
  }

  @Test
  public void fileTreeCreateFile() throws IOException {
    File dbFile = DbType.FILE_TREE.createFile(getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isFile(), is(true));
    assertThat(dbFile.getName().endsWith(".kct"), is(true));
    dbFile.delete();
  }

  @Test
  public void fileTreeCreateFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("createFileParent");
    File dbFile = DbType.FILE_TREE.createFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isFile(), is(true));
    assertThat(dbFile.getName().endsWith(".kct"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
    dbFile.delete();
  }

  @Test
  public void fileTreeCreateTempFile() throws IOException {
    File dbFile = DbType.FILE_TREE.createTempFile(getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isFile(), is(true));
    assertThat(dbFile.getName().endsWith(".kct"), is(true));
    dbFile.delete();
  }

  @Test
  public void fileTreeCreateTempFileAtLocation() throws IOException {
    File parent = temporaryFolder.newFolder("createTempFileParent");
    File dbFile = DbType.FILE_TREE.createTempFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isFile(), is(true));
    assertThat(dbFile.getName().endsWith(".kct"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
    dbFile.delete();
  }

  @Test
  public void plainTextCreateFile() throws IOException {
    File dbFile = DbType.PLAIN_TEXT.createFile(getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isFile(), is(true));
    assertThat(dbFile.getName().endsWith(".kcx"), is(true));
    dbFile.delete();
  }

  @Test
  public void plainTextCreateFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("createFileParent");
    File dbFile = DbType.PLAIN_TEXT.createFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isFile(), is(true));
    assertThat(dbFile.getName().endsWith(".kcx"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
    dbFile.delete();
  }

  @Test
  public void plainTextCreateTempFile() throws IOException {
    File dbFile = DbType.PLAIN_TEXT.createTempFile(getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isFile(), is(true));
    assertThat(dbFile.getName().endsWith(".kcx"), is(true));
    dbFile.delete();
  }

  @Test
  public void plainTextCreateTempFileAtLocation() throws IOException {
    File parent = temporaryFolder.newFolder("createTempFileParent");
    File dbFile = DbType.PLAIN_TEXT.createTempFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isFile(), is(true));
    assertThat(dbFile.getName().endsWith(".kcx"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
    dbFile.delete();
  }

  @Test
  public void directoryHashCreateFile() throws IOException {
    File dbFile = DbType.DIRECTORY_HASH.createFile(getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isDirectory(), is(true));
    assertThat(dbFile.getName().endsWith(".kcd"), is(true));
    dbFile.delete();
  }

  @Test
  public void directoryHashCreateFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("createFileParent");
    File dbFile = DbType.DIRECTORY_HASH.createFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isDirectory(), is(true));
    assertThat(dbFile.getName().endsWith(".kcd"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
    dbFile.delete();
  }

  @Test
  public void directoryHashCreateTempFile() throws IOException {
    File dbFile = DbType.DIRECTORY_HASH.createTempFile(getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isDirectory(), is(true));
    assertThat(dbFile.getName().endsWith(".kcd"), is(true));
    dbFile.delete();
  }

  @Test
  public void directoryHashCreateTempFileAtLocation() throws IOException {
    File parent = temporaryFolder.newFolder("createTempFileParent");
    File dbFile = DbType.DIRECTORY_HASH.createTempFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isDirectory(), is(true));
    assertThat(dbFile.getName().endsWith(".kcd"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
    dbFile.delete();
  }

  @Test
  public void directoryTreeCreateFile() throws IOException {
    File dbFile = DbType.DIRECTORY_TREE.createFile(getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isDirectory(), is(true));
    assertThat(dbFile.getName().endsWith(".kcf"), is(true));
    dbFile.delete();
  }

  @Test
  public void directoryTreeCreateFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("createFileParent");
    File dbFile = DbType.DIRECTORY_TREE.createFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isDirectory(), is(true));
    assertThat(dbFile.getName().endsWith(".kcf"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
    dbFile.delete();
  }

  @Test
  public void directoryTreeCreateTempFile() throws IOException {
    File dbFile = DbType.DIRECTORY_TREE.createTempFile(getClass().getSimpleName() + System.currentTimeMillis());
    dbFile.deleteOnExit();
    assertThat(dbFile.exists(), is(true));
    assertThat(dbFile.isDirectory(), is(true));
    assertThat(dbFile.getName().endsWith(".kcf"), is(true));
    dbFile.delete();
  }

  @Test(expected = IllegalArgumentException.class)
  public void stashCreateFile() throws IOException {
    DbType.STASH.createFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void stashCreateFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("createFileParent");
    DbType.STASH.createFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void stashCreateTempFile() throws IOException {
    DbType.STASH.createTempFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void stashCreateTempFileAtLocation() throws IOException {
    File parent = temporaryFolder.newFolder("createTempFileParent");
    DbType.STASH.createTempFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeHashCreateFile() throws IOException {
    DbType.PROTOTYPE_HASH.createFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeHashCreateFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("createFileParent");
    DbType.PROTOTYPE_HASH.createFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeHashCreateTempFile() throws IOException {
    DbType.PROTOTYPE_HASH.createTempFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeHashCreateTempFileAtLocation() throws IOException {
    File parent = temporaryFolder.newFolder("createTempFileParent");
    DbType.PROTOTYPE_HASH.createTempFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeTreeCreateFile() throws IOException {
    DbType.PROTOTYPE_TREE.createFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeTreeCreateFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("createFileParent");
    DbType.PROTOTYPE_TREE.createFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeTreeCreateTempFile() throws IOException {
    DbType.PROTOTYPE_TREE.createTempFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeTreeCreateTempFileAtLocation() throws IOException {
    File parent = temporaryFolder.newFolder("createTempFileParent");
    DbType.PROTOTYPE_TREE.createTempFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheHashCreateFile() throws IOException {
    DbType.CACHE_HASH.createFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheHashCreateFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("createFileParent");
    DbType.CACHE_HASH.createFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheHashCreateTempFile() throws IOException {
    DbType.CACHE_HASH.createTempFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheHashCreateTempFileAtLocation() throws IOException {
    File parent = temporaryFolder.newFolder("createTempFileParent");
    DbType.CACHE_HASH.createTempFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheTreeCreateFile() throws IOException {
    DbType.CACHE_TREE.createFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheTreeCreateFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("createFileParent");
    DbType.CACHE_TREE.createFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheTreeCreateTempFile() throws IOException {
    DbType.CACHE_TREE.createTempFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheTreeCreateTempFileAtLocation() throws IOException {
    File parent = temporaryFolder.newFolder("createTempFileParent");
    DbType.CACHE_TREE.createTempFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test
  public void fileHashNewFile() throws IOException {
    File dbFile = DbType.FILE_HASH.newFile(getClass().getSimpleName() + System.currentTimeMillis());
    assertThat(dbFile.exists(), is(false));
    assertThat(dbFile.getName().endsWith(".kch"), is(true));
  }

  @Test
  public void fileHashNewFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("newFileParent");
    File dbFile = DbType.FILE_HASH.newFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    assertThat(dbFile.exists(), is(false));
    assertThat(dbFile.getName().endsWith(".kch"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
  }

  @Test
  public void fileTreeNewFile() throws IOException {
    File dbFile = DbType.FILE_TREE.newFile(getClass().getSimpleName() + System.currentTimeMillis());
    assertThat(dbFile.exists(), is(false));
    assertThat(dbFile.getName().endsWith(".kct"), is(true));
  }

  @Test
  public void fileTreeNewFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("newFileParent");
    File dbFile = DbType.FILE_TREE.newFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    assertThat(dbFile.exists(), is(false));
    assertThat(dbFile.getName().endsWith(".kct"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
  }

  @Test
  public void plainTextNewFile() throws IOException {
    File dbFile = DbType.PLAIN_TEXT.newFile(getClass().getSimpleName() + System.currentTimeMillis());
    assertThat(dbFile.exists(), is(false));
    assertThat(dbFile.getName().endsWith(".kcx"), is(true));
  }

  @Test
  public void plainTextNewFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("newFileParent");
    File dbFile = DbType.PLAIN_TEXT.newFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    assertThat(dbFile.exists(), is(false));
    assertThat(dbFile.getName().endsWith(".kcx"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
  }

  @Test
  public void directoryHashNewFile() throws IOException {
    File dbFile = DbType.DIRECTORY_HASH.newFile(getClass().getSimpleName() + System.currentTimeMillis());
    assertThat(dbFile.exists(), is(false));
    assertThat(dbFile.getName().endsWith(".kcd"), is(true));
  }

  @Test
  public void directoryHashNewFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("newFileParent");
    File dbFile = DbType.DIRECTORY_HASH.newFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    assertThat(dbFile.exists(), is(false));
    assertThat(dbFile.getName().endsWith(".kcd"), is(true));
    assertThat(dbFile.getParentFile(), is(parent));
  }

  @Test
  public void directoryTreeNewFile() throws IOException {
    File dbFile = DbType.DIRECTORY_TREE.newFile(getClass().getSimpleName() + System.currentTimeMillis());
    assertThat(dbFile.exists(), is(false));
    assertThat(dbFile.getName().endsWith(".kcf"), is(true));
  }

  @Test
  public void directoryTreeNewFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("newFileParent");
    File dbFile = DbType.DIRECTORY_TREE.newFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
    assertThat(dbFile.exists(), is(false));
    assertThat(dbFile.getName().endsWith(".kcf"), is(true));
  }

  @Test(expected = IllegalArgumentException.class)
  public void stashNewFile() throws IOException {
    DbType.STASH.newFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void stashNewFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("newFileParent");
    DbType.STASH.newFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeHashNewFile() throws IOException {
    DbType.PROTOTYPE_HASH.newFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeHashNewFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("newFileParent");
    DbType.PROTOTYPE_HASH.newFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeTreeNewFile() throws IOException {
    DbType.PROTOTYPE_TREE.newFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void prototypeTreeNewFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("newFileParent");
    DbType.PROTOTYPE_TREE.newFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheHashNewFile() throws IOException {
    DbType.CACHE_HASH.newFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheHashNewFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("newFileParent");
    DbType.CACHE_HASH.newFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheTreeNewFile() throws IOException {
    DbType.CACHE_TREE.newFile(getClass().getSimpleName() + System.currentTimeMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void cacheTreeNewFileWithParent() throws IOException {
    File parent = temporaryFolder.newFolder("newFileParent");
    DbType.CACHE_TREE.newFile(parent, getClass().getSimpleName() + System.currentTimeMillis());
  }

}
