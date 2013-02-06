package fm.last.commons.kyoto;

public enum LockType {
  READ() {
    @Override
    public boolean value() {
      return false;
    }
  },
  WRITE() {
    @Override
    public boolean value() {
      return true;
    }
  };

  public abstract boolean value();
}
