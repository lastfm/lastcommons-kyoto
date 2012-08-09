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

import kyotocabinet.FileProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.commons.kyoto.KyotoFileProcessor;

class FileProcessorAdapter implements FileProcessor {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final KyotoFileProcessor delegate;

  FileProcessorAdapter(KyotoFileProcessor delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean process(String dbFilePath, long recordCount, long availableRegionSize) {
    try {
      delegate.process(new File(dbFilePath), recordCount, availableRegionSize);
    } catch (Throwable e) {
      log.error("Error post processing file: " + dbFilePath, e);
      return false;
    }
    return true;
  }

}
