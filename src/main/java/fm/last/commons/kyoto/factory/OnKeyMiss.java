package fm.last.commons.kyoto.factory;

enum OnKeyMiss {
  RETURN_ERROR() {
    @Override
    public long asLong() {
      return Long.MIN_VALUE;
    }

    @Override
    public double asDouble() {
      return Double.NEGATIVE_INFINITY;
    }
  },
  USE_DEFAULT() {
    @Override
    public long asLong() {
      throw new IllegalStateException(this + " does not have a long representation.");
    }

    @Override
    public double asDouble() {
      throw new IllegalStateException(this + " does not have a double representation.");
    }
  },
  USE_DELTA() {
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
