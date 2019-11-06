package me.mingshan.reactive.flow;

import java.util.concurrent.Flow;

public class MySubscriber implements Flow.Subscriber<Employee> {
  private Flow.Subscription subscription;

  private int counter = 0;

  @Override
  public void onSubscribe(Flow.Subscription subscription) {
    System.out.println("Subscribed");
    this.subscription = subscription;
    subscription.request(10);
    System.out.println("onSubscribe requested 1 item");
  }

  @Override
  public void onNext(Employee item) {
    System.out.println("Processing Employee "+item);
    counter++;
    this.subscription.request(1);
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

  public int getCounter() {
    return counter;
  }

}
