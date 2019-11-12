package me.mingshan.reactive.flow;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompletableFutureTest {

  @Test
  public void test1() {
    CompletableFuture.supplyAsync(() -> "Hello")
      .thenApply(s -> s + "World")
      .thenApply(String::toUpperCase)
      .thenCombine(CompletableFuture.completedFuture("Java"), (s1, s2) -> s1 + s2)
      .thenAccept(System.out::println);
  }

  /**
   * 异步运算
   */
  @Test
  public void test2() throws ExecutionException, InterruptedException {
    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println(Thread.currentThread().getName());
    });

    future.get();
  }

  /**
   * 使用 supplyAsync() 运行一个异步任务并且返回结果
   */
  @Test
  public void test3() throws ExecutionException, InterruptedException {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      return Thread.currentThread().getName();
    });

    System.out.println(future.get());
  }

  /**
   * 使用 thenApply() 处理和改变CompletableFuture的结果（使用同一个线程）
   */
  @Test
  public void test4() throws ExecutionException, InterruptedException {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      return Thread.currentThread().getName();
    }).thenApply(item -> {
      return item + "-111111-" + Thread.currentThread().getName();
    }).thenApply(item -> {
      return item + "-222222-" + Thread.currentThread().getName();
    });

    System.out.println(future.get());
  }

  /**
   * 使用 thenApplyAsync() 异步处理和改变CompletableFuture的结果（使用不同的线程）
   */
  @Test
  public void test5() throws ExecutionException, InterruptedException {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      return Thread.currentThread().getName();
    }).thenApplyAsync(item -> {
      return item + "-111111-" + Thread.currentThread().getName();
    }).thenApplyAsync(item -> {
      return item + "-222222-" + Thread.currentThread().getName();
    });

    System.out.println(future.get());
  }

  /**
   * thenAccept() 处理结果
   */
  @Test
  public void test6() {
    CompletableFuture.supplyAsync(() -> {
      return Thread.currentThread().getName();
    }).thenAccept(System.out::println);
  }

  /**
   * thenRun 收尾处理
   */
  @Test
  public void test7() {
    CompletableFuture.supplyAsync(() -> {
      return Thread.currentThread().getName();
    }).thenRun(() -> {
      System.out.println("hhhhhhhhh");
    });
  }

  /*
   * 组合两个CompletableFuture
   */

  /**
   * 使用 thenCompose() 组合两个独立的future
   */
  @Test
  public void test8() {
    String userId = null;
    //

    CompletableFuture<CompletableFuture<Long>> future = getUser(userId).thenApply(this::getLLL);


    // 利用 thenCompose() 组合两个独立的future
    CompletableFuture<Long> result = getUser(userId)
      .thenCompose(user -> getLLL(user));

  }

  /**
   * 使用thenCombine()组合两个独立的 future
   */
  @Test
  public void test9() throws ExecutionException, InterruptedException {
    System.out.println("Retrieving weight.");
    CompletableFuture<Double> weightInKgFuture = CompletableFuture.supplyAsync(() -> {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        throw new IllegalStateException(e);
      }
      return 65.0;
    });

    System.out.println("Retrieving height.");
    CompletableFuture<Double> heightInCmFuture = CompletableFuture.supplyAsync(() -> {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        throw new IllegalStateException(e);
      }
      return 177.8;
    });

    System.out.println("Calculating BMI.");
    CompletableFuture<Double> combinedFuture = weightInKgFuture
      .thenCombine(heightInCmFuture, (weightInKg, heightInCm) -> {
        Double heightInMeter = heightInCm/100;
        return weightInKg/(heightInMeter*heightInMeter);
      });

    System.out.println("Your BMI is - " + combinedFuture.get());
  }

  /**
   * 异常处理1
   */
  @Test
  public void test10() throws ExecutionException, InterruptedException {
    int age = -1;

    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      if (age < 0) {
        throw new IllegalArgumentException("Age can not be negative");
      }
      if (age > 18) {
        return "Adult";
      } else {
        return "Child";
      }
    }).exceptionally(ex -> {
      System.out.println(ex.getMessage());
      return "Unknown!";
    });

    System.out.println(future.get());
  }

  /**
   * 异常处理2
   *
   * @throws ExecutionException
   * @throws InterruptedException
   */
  @Test
  public void test11() throws ExecutionException, InterruptedException {
    int age = -1;

    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      if (age < 0) {
        throw new IllegalArgumentException("Age can not be negative");
      }
      if (age > 18) {
        return "Adult";
      } else {
        return "Child";
      }
    }).handle((res, ex) -> {
      if(ex != null) {
        System.out.println("Oops! We have an exception - " + ex.getMessage());
        return "Unknown!";
      }
      return res;
    });

    System.out.println(future.get());
  }

  private CompletableFuture<User> getUser(String userId) {
    return CompletableFuture.supplyAsync(() -> {
      // DO ANYTHING
      return new User();
    });
  }

  private CompletableFuture<Long> getLLL(User user) {
    return CompletableFuture.supplyAsync(() -> {
      // DO ANYTHING
      return 2L;
    });
  }


  private static class User {
  }
}
