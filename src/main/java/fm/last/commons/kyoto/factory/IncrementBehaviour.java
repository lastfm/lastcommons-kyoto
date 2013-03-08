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

enum IncrementBehaviour {
  ERROR_ON_KEY_MISS() {
    @Override
    public long asLong() {
      return Long.MIN_VALUE;
    }

    @Override
    public double asDouble() {
      return Double.NEGATIVE_INFINITY;
    }
  },
  APPLY_DEFAULT_ON_KEY_MISS() {
    @Override
    public long asLong() {
      throw new IllegalStateException(this + " does not have a long representation.");
    }

    @Override
    public double asDouble() {
      throw new IllegalStateException(this + " does not have a double representation.");
    }
  },
  ALWAYS_SET_VALUE() {
    @Override
    public long asLong() {
      return Long.MAX_VALUE;
    }

    @Override
    public double asDouble() {
      return Double.POSITIVE_INFINITY;
    }
  };

  abstract long asLong();

  abstract double asDouble();
}
