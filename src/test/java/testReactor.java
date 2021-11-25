import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;


public class testReactor {
    private static final Logger log = LoggerFactory.getLogger(testReactor.class);


    @Test
    public void test() {
        StepVerifier.create(Flux.just("foo", "bar"))
                .expectNext("foo", "bar")
                .verifyComplete();
    }

    /*@Test
    void expectFooBarError() {
        StepVerifier.create(Flux.just("foo","bar"))
                .expectNext("foo", "bar")
                .verifyError(RuntimeException.class);
    }*/

    @Test
    void expectSkylerJesseComplete() {
        Map<String, String> user = new HashMap<>();
        user.put("1", "Gomez");
        user.put("2", "Perez");
        Flux<Map<String, String>> users = Flux.just(user);
        StepVerifier.create(users)
                .expectNextMatches(user1 -> user1.get("1").equals("Gomez"))
                .expectNextMatches(user1 -> user1.get("2").equals("Perez"))
                .expectComplete();
    }

    @Test
    void expect10Elements() {

        StepVerifier.create(Flux.range(0, 10))
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void expectMonoError() {

        Mono<String> mono = Mono.just("Pepito")
                //.map(s -> Mono.error(new RuntimeException()));
                .map(s -> {
                    throw new RuntimeException();
                });


        mono.subscribe(s -> log.info("Name ", s), s -> log.error("Error"));

        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void expectDoOnError() {

        Mono<Object> mono = Mono.error(new IllegalArgumentException(("Illegal")))
                .doOnError(e -> log.error("Error message:", e.getMessage()))
                .doOnNext(s -> log.info("Executing this doOnNext"))
                .log();

        StepVerifier.create(mono)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void expectDoOnErrorResume() {
        String name = "Pepito";

        Mono<Object> mono = Mono.error(new IllegalArgumentException(("Illegal")))
                .doOnError(e -> log.error("Error message:", e.getMessage()))
                .onErrorResume(s -> {
                    log.info("Inside on Error resume");
                    return Mono.just(name);
                })
                .log();

        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    void expectDoOnErrorReturn() {
        String name = "Pepito";

        Mono<Object> mono = Mono.error(new IllegalArgumentException(("Illegal")))
                .onErrorReturn("Empty")
                .onErrorResume(s -> {
                    log.info("Inside on Error resume");
                    return Mono.just(name);
                })
                .doOnError(e -> log.error("Error message:", e.getMessage()))
                .log();

        StepVerifier.create(mono)
                .expectNext("Empty")
                .verifyComplete();
    }
}