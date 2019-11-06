package me.mingshan.reactive.flow;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

public class MyProcessor extends SubmissionPublisher<Freelancer> implements Flow.Processor<Employee, Freelancer> {
  private Flow.Subscription subscription;
  private Function<Employee,Freelancer> function;

  public MyProcessor(Function<Employee,Freelancer> function) {
    super();
    this.function = function;
  }

  @Override
  public void onSubscribe(Flow.Subscription subscription) {
    System.out.println("Subscribed");
    this.subscription = subscription;
    //subscription.request(1);
    System.out.println("onSubscribe requested 1 item");
  }

  @Override
  public void onNext(Employee item) {
    submit((Freelancer) function.apply(item));
    subscription.request(1);
  }

  @Override
  public void onError(Throwable e) {
    System.out.println("Some error happened");
    e.printStackTrace();
  }

  @Override
  public void onComplete() {
    System.out.println("All Processing Done");
  }
}
