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

import kyotocabinet.MapReduce;
import kyotocabinet.ValueIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.commons.kyoto.mapreduce.Collector;
import fm.last.commons.kyoto.mapreduce.Job;
import fm.last.commons.kyoto.mapreduce.JobExecutor;

class KyotoJobExecutor implements JobExecutor {

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  private final KyotoDbImpl database;
  private final ErrorHandler errorHandler;

  KyotoJobExecutor(KyotoDbImpl database) {
    this.database = database;
    errorHandler = database.getErrorHandler();
  }

  @Override
  public void execute(final Job job) {

    MapReduce delegate = new MapReduce() {
      private final Collector collector = new Collector() {

        @Override
        public void collect(byte[] key, byte[] value) {
          errorHandler.wrapBooleanCall(emit(key, value));
        }
      };

      @Override
      public boolean map(byte[] key, byte[] value) {
        try {
          job.getMapper().map(key, value, collector);
        } catch (Throwable t) {
          LOG.error("Exception thrown in map invocation.", t);
          return false;
        }
        return true;
      }

      @Override
      public boolean reduce(byte[] key, ValueIterator valueIterator) {
        try {
          job.getReducer().reduce(key, new ValueIteratorAdapter(valueIterator));
        } catch (Throwable t) {
          LOG.error("Exception thrown in reduce invocation.", t);
          return false;
        }
        return true;
      }
    };
    errorHandler.wrapBooleanCall(delegate.execute(database.getDelegate(), getTemporaryDbFolder(job), getOptions(job)));
  }

  private int getOptions(Job job) {
    int options = 0;
    if (!job.getCompressTemporaryDb()) {
      options |= MapReduce.XNOCOMP;
    }
    if (!job.getUseLocks()) {
      options |= MapReduce.XNOLOCK;
    }
    return options;
  }

  private String getTemporaryDbFolder(Job job) {
    if (job.getTemporaryDbFolder() == null) {
      return null;
    }
    return job.getTemporaryDbFolder().getAbsolutePath();
  }

}
