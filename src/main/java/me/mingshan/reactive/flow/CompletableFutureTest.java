package me.mingshan.reactive.flow;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
    }, Executors.newFixedThreadPool(5));

    System.out.println(future.join());
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

    //System.out.println(future.get());
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

    CompletableFuture<CompletableFuture<User>> future = getUser(userId).thenApply(this::fillRes);


    // 利用 thenCompose() 组合两个独立的future
    CompletableFuture<User> result = getUser(userId)
      .thenCompose(user -> fillRes(user));

    result.join();
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

    CompletableFuture<String> walker = CompletableFuture.supplyAsync(() -> {
      return ", Walker";
    });

    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      return "Hello";
    }).thenApply(s -> s + " World")
      .thenCombine(walker, (s1, s2) -> {
        return s1 + s2;
      });

    System.out.println(future.get());


    System.out.println("Your BMI is - " + combinedFuture.get());
  }

  /**
   * 异常处理1
   */
  @Test
  public void test10() throws ExecutionException, InterruptedException {
    int age = -1;

    CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
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
    }).thenAccept((r) -> {
      System.out.println("result = " + r);
      System.out.println("Done.");
    });

    future.join();
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

    CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
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
    }).thenAccept((r) -> {
      System.out.println(r);
      System.out.println("Done.");
    });

    future.join();
  }

  @Test
  public void test111() {
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
    }).whenComplete((res, ex) -> {
      if (ex == null) {
        System.out.println(res);
      } else {
        throw new RuntimeException(ex);
      }
    }).exceptionally(e -> {
      System.out.println(e.getMessage());
      return "hello world";
    });

    future.join();
  }

  @Test
  public void test12() {
    long startTime = System.currentTimeMillis();
    List<String> webPageLinks = Arrays.asList("1", "2", "3", "4", "5");

    // ①
    List<CompletableFuture<String>> futures = webPageLinks.stream()
      .map(this::downloadWebPage).collect(Collectors.toList());

    System.out.println("下载中1");

    // ②
    // 注意这里返回泛型的是空
    CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));

    System.out.println("下载中2");

    // ③
    CompletableFuture<List<String>> allFuture = allOf.thenApply(v -> {
      return futures.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toList());
    });

    System.out.println("下载中3");

    // ④
    List<String> strings = allFuture.join();

    long endTime = System.currentTimeMillis();
    System.out.println("总耗时长：" + (endTime - startTime) / 1000);

    strings.forEach(System.out::println);
  }

  @Test
  public void test13() {
    long startTime = System.currentTimeMillis();
    List<String> webPageLinks = Arrays.asList("1", "2", "3", "4", "5");

    List<CompletableFuture<String>> futures = webPageLinks.stream()
      .map(this::downloadWebPage).collect(Collectors.toList());

    // 注意这里返回
    CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futures.toArray(new CompletableFuture[futures.size()]));

    Object result = anyOf.join();

    System.out.println(result);
    long endTime = System.currentTimeMillis();
    System.out.println("总耗时长：" + (endTime - startTime) / 1000);
  }

  @Test
  public void test14() {
    long startTime = System.currentTimeMillis();
    List<String> webPageLinks = Arrays.asList("1", "2", "3", "4", "5");

    // ①
    List<CompletableFuture<String>> futures = webPageLinks.stream()
      .map(this::downloadWebPage).collect(Collectors.toList());

    System.out.println("下载中1");

    // ②
    // 注意这里返回泛型的是空
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
  }

  private CompletableFuture<String> downloadWebPage(String webPageLink) {
    return CompletableFuture.supplyAsync(() -> {
      Random random = new Random();
      int i = random.nextInt(10) + 1;
      System.out.println(webPageLink + " - " + i);
      try {
        Thread.sleep(i * 1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println(webPageLink + " - done." );
      return webPageLink + " - http://www.baidu.com";
    });
  }

  private CompletableFuture<User> getUser(String userId) {
    return CompletableFuture.supplyAsync(() -> {
      // DO ANYTHING
      return new User();
    });
  }

  private CompletableFuture<User> fillRes(User user) {
    return CompletableFuture.supplyAsync(() -> {
      // 获取权限信息，填充到用户信息里面
      return user;
    });
  }


  private static class User {
    List<String> res;
  }

  @Test
  public void test() {
    System.out.println(ForkJoinPool.getCommonPoolParallelism());
  }
}
