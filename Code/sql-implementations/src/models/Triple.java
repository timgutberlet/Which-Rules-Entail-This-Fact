package models;

/**
 * @author tgutberl
 */
public class Triple {
  private Integer subject, predicate, object;

  public Triple(Integer subject, Integer predicate, Integer object){
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
  }

  public Integer getObject() {
    return object;
  }

  public Integer getSubject() {
    return subject;
  }

  public Integer getPredicate() {
    return predicate;
  }

  @Override
  public String toString() {
    return "Triple{" +
         subject +
        " " + predicate +
        " " + object;
  }
}
