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

import java.io.File;

import fm.last.commons.kyoto.mapreduce.Job;
import fm.last.commons.kyoto.mapreduce.Mapper;
import fm.last.commons.kyoto.mapreduce.Reducer;

public class KyotoJob implements Job {

  private final Mapper mapper;
  private final Reducer reducer;
  private File temporaryDbFolder;
  private boolean useLocks;
  private boolean compressTemporaryDb;

  public KyotoJob(Mapper mapper, Reducer reducer) {
    this.mapper = mapper;
    this.reducer = reducer;
  }

  @Override
  public Mapper getMapper() {
    return mapper;
  }

  @Override
  public Reducer getReducer() {
    return reducer;
  }

  @Override
  public File getTemporaryDbFolder() {
    return temporaryDbFolder;
  }

  @Override
  public void setTemporaryDbFolder(File temporaryDbFolder) {
    this.temporaryDbFolder = temporaryDbFolder;
  }

  @Override
  public void setUseLocks(boolean useLocks) {
    this.useLocks = useLocks;
  }

  @Override
  public boolean getUseLocks() {
    return useLocks;
  }

  @Override
  public void setCompressTemporaryDb(boolean compressTemporaryDb) {
    this.compressTemporaryDb = compressTemporaryDb;
  }

  @Override
  public boolean getCompressTemporaryDb() {
    return compressTemporaryDb;
  }

}
