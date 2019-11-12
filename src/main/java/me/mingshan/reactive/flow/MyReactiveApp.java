package me.mingshan.reactive.flow;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SubmissionPublisher;

public class MyReactiveApp {

  /**
   * 发布订阅模式
   *
   * @throws InterruptedException
   */
  @Test
  public void PsTest() throws InterruptedException {
    SubmissionPublisher<Employee> publisher = new SubmissionPublisher<>();
    MySubscriber subscriber = new MySubscriber();
    publisher.subscribe(subscriber);
    List<Employee> emps = generateEmps();
    // Publish items
    System.out.println("Publishing Items to Subscriber");
    emps.forEach(publisher::submit);

    // logic to wait till processing of all messages are over
    while (emps.size() != subscriber.getCounter()) {
      Thread.sleep(10);
    }
    // close the Publisher
    //publisher.close();

    System.out.println("Exiting the app");

  }

  @Test
  public void ProcessorTest() throws InterruptedException {
    // Create End Publisher
    SubmissionPublisher<Employee> publisher = new SubmissionPublisher<>();

    MyFreelancerSubscriber subscriber = new MyFreelancerSubscriber();
    MyProcessor processor = new MyProcessor(s -> {
      return new Freelancer(s.getId(), s.getName(), s.getId() + 100);
    });

    processor.subscribe(subscriber);
    publisher.subscribe(processor);

    List<Employee> emps = generateEmps();

    // Publish items
    System.out.println("Publishing Items to Subscriber");
    emps.forEach(publisher::submit);

    // logic to wait till processing of all messages are over
    while (emps.size() != subscriber.getCounter()) {
      Thread.sleep(10);
    }
    // close the Publisher
    publisher.close();

    System.out.println("Exiting the app");
  }


  private static List<Employee> generateEmps() {
    Employee e1 = new Employee(1, "Pankaj");
    Employee e2 = new Employee(2, "David");
    Employee e3 = new Employee(3, "Lisa");
    Employee e4 = new Employee(4, "Ram");
    Employee e5 = new Employee(5, "Anupam");

    List<Employee> emps = new ArrayList<>();
    emps.add(e1);
    emps.add(e2);
    emps.add(e3);
    emps.add(e4);
    emps.add(e5);

    return emps;
  }
}
