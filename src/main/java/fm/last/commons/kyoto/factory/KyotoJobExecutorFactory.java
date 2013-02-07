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

import fm.last.commons.kyoto.KyotoDb;
import fm.last.commons.kyoto.mapreduce.JobExecutor;
import fm.last.commons.kyoto.mapreduce.JobExecutorFactory;

public class KyotoJobExecutorFactory implements JobExecutorFactory {

  @Override
  public JobExecutor newExecutor(KyotoDb database) {
    if (!(database instanceof KyotoDbImpl)) {
      throw new IllegalArgumentException(database + " must be an instance of " + KyotoDbImpl.class.getSimpleName());
    }
    return new KyotoJobExecutor((KyotoDbImpl) database);
  }

}
