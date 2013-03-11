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
package fm.last.commons.kyoto.mapreduce;

import java.io.File;

import fm.last.commons.kyoto.KyotoDb;

public interface Job {

  Mapper getMapper();

  Reducer getReducer();

  File getTemporaryDbFolder();

  void setTemporaryDbFolder(File folder);

  void setUseLocks(boolean useLocks);

  boolean getUseLocks();

  void setCompressTemporaryDb(boolean compressTemporaryDb);

  boolean getCompressTemporaryDb();

  /**
   * Execute a MapReduce job.
   * 
   * @see {@link kyotocabinet.MapReduce#MapReduce#execute(kyotocabinet.DB, String, int)}
   */
  void executeWith(KyotoDb db);

}
