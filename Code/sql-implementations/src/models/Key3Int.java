package models;

import java.util.Objects;

/**
 * @author tgutberl
 */
public final class Key3Int {
  final int a;
  final int b;
  final int c;

  public Key3Int(int a, int b, int c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  @Override
  public String toString() {
    return String.valueOf(hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Key3Int)) {
      return false;
    }
    Key3Int that = (Key3Int) obj;
    return (this.a == that.a)
        && (this.b == that.b)
        && (this.c == that.c);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.a, this.b, this.c);
  }

}
