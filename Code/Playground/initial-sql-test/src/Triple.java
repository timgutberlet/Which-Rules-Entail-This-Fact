/**
 * @author tgutberl
 */
public class Triple {
  private StringBuffer v1, v2, relation;
  private int ID = -1;

  public Triple(int ID, String head_v1, String head_v2, String head_r){
    this.ID = ID;
    this.v1 = new StringBuffer(head_v1);
    this.v2 = new StringBuffer(head_v2);
    this.relation = new StringBuffer(head_r);
  }

  public Triple(String head_v1, String head_v2, String head_r){
    this.v1 = new StringBuffer(head_v1);
    this.v2 = new StringBuffer(head_v2);
    this.relation = new StringBuffer(head_r);
  }

  public String getRelation() {
    return relation.toString();
  }

  public String getV1() {
    return v1.toString();
  }

  public String getV2() {
    return v2.toString();
  }

  public int getID() {
    return ID;
  }

  @Override
  public String toString() {
    return "Triple{" +
        "v1=" + v1 +
        ", v2=" + v2 +
        ", relation=" + relation +
        ", ID=" + ID +
        '}';
  }
}
