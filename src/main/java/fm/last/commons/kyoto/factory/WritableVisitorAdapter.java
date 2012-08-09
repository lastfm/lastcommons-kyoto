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

import kyotocabinet.Visitor;
import fm.last.commons.kyoto.WritableVisitor;

class WritableVisitorAdapter implements Visitor {

  private final WritableVisitor delegate;

  WritableVisitorAdapter(WritableVisitor delegate) {
    this.delegate = delegate;
  }

  @Override
  public byte[] visit_empty(byte[] key) {
    byte[] result = delegate.emptyRecord(key);
    if (result == WritableVisitor.NOP) {
      return Visitor.NOP;
    }
    if (result == WritableVisitor.REMOVE) {
      return Visitor.REMOVE;
    }
    return result;
  }

  @Override
  public byte[] visit_full(byte[] key, byte[] value) {
    byte[] result = delegate.record(key, value);
    if (result == WritableVisitor.NOP) {
      return Visitor.NOP;
    }
    if (result == WritableVisitor.REMOVE) {
      return Visitor.REMOVE;
    }
    return result;
  }

}
