package models;

/**
 * @author tgutberl
 */
public class Rule {
  private Triple head;
  private Triple body;

  public Rule(Triple head, Triple body){
    this.head = head;
    this.body = body;
  }

  public Triple getBody() {
    return body;
  }

  public Triple getHead() {
    return head;
  }

  public void setBody(Triple body) {
    this.body = body;
  }

  public void setHead(Triple head) {
    this.head = head;
  }
}
