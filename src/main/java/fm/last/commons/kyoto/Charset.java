package fm.last.commons.kyoto;

public enum Charset {
  UTF_8(true),
  DEFAULT(false);

  private final boolean value;

  private Charset(boolean value) {
    this.value = value;
  }

  public boolean value() {
    return value;
  }
}
