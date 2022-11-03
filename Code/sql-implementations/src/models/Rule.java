package models;

import java.util.List;

/**
 * @author tgutberl
 */
public class Rule {
  private Triple head;
  private List<Triple> body;

  public Rule(Triple head, List<Triple> body){
    this.head = head;
    this.body = body;
  }

  public List<Triple> getBody() {
    return body;
  }

  public Triple getHead() {
    return head;
  }

  public void setBody(List<Triple> body) {
    this.body = body;
  }

  public void setHead(Triple head) {
    this.head = head;
  }

  @Override
  public String toString() {
    return "Rule{" +
        "head=" + head +
        ", body=" + body +
        '}';
  }
}
