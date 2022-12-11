package models;

/**
 * Class that represents a triple
 *
 * @author tgutberl
 */
public class Triple {
  private Integer subject, predicate, object;
  String subString, preString, objString;

  /**
   * Constructor for setting a Triple
   * @param subject Integer
   * @param predicate Integer
   * @param object Integer
   */
  public Triple(Integer subject, Integer predicate, Integer object, String subString, String preString, String objString){
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
    this.subString = subString;
    this.preString = preString;
    this.objString = objString;
  }

  /**
   * Returns the object
   * @return Integer
   */
  public Integer getObject() {
    return object;
  }

  /**
   * Returns the Subject
   * @return Integer
   */
  public Integer getSubject() {
    return subject;
  }

  /**
   * Returns the predicate
   * @return Integer
   */
  public Integer getPredicate() {
    return predicate;
  }

  /**
   * Returns the String representation
   * @return String
   */
  @Override
  public String toString() {
    return "Triple: " +
         subject +
        " " + predicate +
        " " + object;
  }

  public String toText() {
    return "Triple: " +
            subString +
            " " + preString +
            " " + objString;
  }
}
