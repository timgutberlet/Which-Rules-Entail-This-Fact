package models;

import java.util.Objects;

/**
 * @author tgutberl
 */
public final class Key2Int {
  final int a;
  final int b;

  public Key2Int(int a, int b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Key2Int)) {
      return false;
    }
    Key2Int that = (Key2Int) obj;
    return (this.a == that.a)
        && (this.b == that.b);
  }

  @Override
  public String toString() {
    return "Key2Int{" +
        "a=" + a +
        ", b=" + b +
        '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.a, this.b);
  }

}
