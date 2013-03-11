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

import kyotocabinet.MapReduce;
import kyotocabinet.ValueIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.commons.kyoto.KyotoDb;
import fm.last.commons.kyoto.mapreduce.Context;
import fm.last.commons.kyoto.mapreduce.Job;
import fm.last.commons.kyoto.mapreduce.Mapper;
import fm.last.commons.kyoto.mapreduce.Reducer;

public class MapReduceJob implements Job {

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  private final Mapper mapper;
  private final Reducer reducer;
  private File temporaryDbFolder;
  private boolean useLocks;
  private boolean compressTemporaryDb;

  public MapReduceJob(Mapper mapper, Reducer reducer) {
    this.mapper = mapper;
    this.reducer = reducer;
  }

  @Override
  public void executeWith(KyotoDb database) {
    if (!(database instanceof KyotoDbImpl)) {
      throw new IllegalArgumentException(database + " must be an instance of " + KyotoDbImpl.class.getSimpleName());
    }
    KyotoDbImpl databaseImpl = (KyotoDbImpl) database;
    final ErrorHandler errorHandler = databaseImpl.getErrorHandler();
    MapReduce mapDelegate = new MapReduce() {
      private final Context context = new Context() {
        @Override
        public void write(byte[] key, byte[] value) {
          errorHandler.wrapBooleanCall(emit(key, value));
        }
      };

      @Override
      public boolean map(byte[] key, byte[] value) {
        try {
          mapper.map(key, value, context);
        } catch (Throwable t) {
          LOG.error("Exception thrown in map invocation.", t);
          return false;
        }
        return true;
      }

      @Override
      public boolean reduce(byte[] key, ValueIterator valueIterator) {
        try {
          reducer.reduce(key, new ValueIteratorAdapter(valueIterator));
        } catch (Throwable t) {
          LOG.error("Exception thrown in reduce invocation.", t);
          return false;
        }
        return true;
      }
    };
    errorHandler.wrapBooleanCall(mapDelegate.execute(databaseImpl.getDelegate(), resolveTemporaryDbFolder(),
        resolveOptions()));
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

  private int resolveOptions() {
    int options = 0;
    if (!getCompressTemporaryDb()) {
      options |= MapReduce.XNOCOMP;
    }
    if (!getUseLocks()) {
      options |= MapReduce.XNOLOCK;
    }
    return options;
  }

  private String resolveTemporaryDbFolder() {
    if (getTemporaryDbFolder() == null) {
      return null;
    }
    return getTemporaryDbFolder().getAbsolutePath();
  }

}
