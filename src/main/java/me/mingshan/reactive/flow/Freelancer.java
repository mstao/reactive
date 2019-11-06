package me.mingshan.reactive.flow;

public class Freelancer extends Employee {
  private long fid;

  public Freelancer(long id, String name, long fid) {
    super(id, name);
    this.fid = fid;
  }

  public long getFid() {
    return fid;
  }

  public void setFid(long fid) {
    this.fid = fid;
  }

  @Override
  public String toString() {
    return "Freelancer{" +
      "id=" + super.getId() +
      ", name='" + super.getName() +
      ", age=" + super.getAge() +
      ", fid=" + fid +
      '}';
  }
}
