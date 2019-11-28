package me.mingshan.reactive.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.net.URI;

public class ExampleClient {

  @Test
  public void test1() {
    WebsocketClientTransport ws = WebsocketClientTransport.create(URI.create("ws://rsocket-demo.herokuapp.com/ws"));
    RSocket client = RSocketFactory.connect().keepAlive().transport(ws).start().block();

    try {
      Flux<Payload> s = client.requestStream(DefaultPayload.create("peace"));

      s.take(10).doOnNext(p -> System.out.println(p.getDataUtf8())).blockLast();
    } finally {
      client.dispose();
    }
  }
}
