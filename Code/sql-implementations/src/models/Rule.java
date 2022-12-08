package models;

import java.util.List;

/**
 * Class that represents a rule
 *
 * @author tgutberl
 */
public class Rule {
  private Triple head;
  private List<Triple> body;
  private Integer id;

  private Integer bound;

  public Integer getBound() {
    return bound;
  }

  public void setBound(Integer bound) {
    this.bound = bound;
  }

  /**
   * Costructor for setting
   * @param head Triple representation of Head
   * @param body Triple Array representation of Body
   */
  public Rule(Triple head, List<Triple> body){
    this.head = head;
    this.body = body;
  }

  /**
   * Costructor for setting
   * @param head Triple representation of Head
   * @param body Triple Array representation of Body
   */
  public Rule(Triple head, List<Triple> body, Integer id){
    this.head = head;
    this.body = body;
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Returns the body
   *
   * @return Body list representation
   */
  public List<Triple> getBody() {
    return body;
  }

  /**
   * Returns the head
   *
   * @return Returns the Head triple representation
   */
  public Triple getHead() {
    return head;
  }

  /**
   * Sets the body
   * @param body Arraylist representation
   */
  public void setBody(List<Triple> body) {
    this.body = body;
  }

  /**
   * Sets the Head representsation
   * @param head Head triple representation
   */
  public void setHead(Triple head) {
    this.head = head;
  }

  /**
   * Returns a String representation
   * @return String
   */
  @Override
  public String toString() {
    return "Rule{" +
        "head=" + head +
        ", body=" + body +
        '}';
  }
}
