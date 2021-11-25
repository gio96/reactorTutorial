import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class test1 {
    private static final Logger log = LoggerFactory.getLogger(test1.class);

    public static void main(String[] args) throws InterruptedException {

        Flux<Integer> just = Flux.just(1, 2, 3);
        Mono<Integer> integerMono = Mono.just(1);
        Publisher<String> publisher = Mono.just("foo");

        List<Integer> elements = new ArrayList<>();

        /*Flux.just(1,2,3,4)
                .log()
                .subscribe(elements::add);

        System.out.println(elements);*/

        /*Flux.fromIterable(Arrays.asList("1","2","3"))
                .subscribe(s -> log.info(s));*/

        //Ejemplo de flujo deberia salir fooA
        /*Flux<String> flux = Flux.just("A");
        flux.map(s -> "foo" + s);
        flux.subscribe(System.out::println);*/


        //Flux.empty();
        //Flux.error(new IllegalStateException());


        // diferentes metodos que tiene
        /*Flux.just(1,2,3,4)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                    //request unbounded ilimitado
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        elements.add(integer);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        System.out.println(elements);*/


        //backpressure le decimos que envie dos elementos para no abrumar al suscriptos
        /*Flux.just(1,2,3,4)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    private Subscription s;
                    int onNextAmount;

                    @Override
                    public void onSubscribe(Subscription s) {
                        this.s = s;
                        // request (2)
                        s.request(2);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        elements.add(integer);
                        onNextAmount++;
                        if(onNextAmount % 2 == 0){
                            s.request(2);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/


        //Otra forma de ver los estados
        String name = "Pepito";

        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(longNumber -> log.info("Request Received"))
                .doOnNext(s -> log.info("Value is here",s))
                .doOnNext(s -> log.info("Value is here2",s))
                //.flatMap(s -> Mono.empty())
                .doOnSuccess(s -> log.info("DoOnSucess executed",s));

        Mono<Object> mono2 = Mono.just(name)
                .log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(longNumber -> log.info("Request Received"))
                .doOnNext(s -> log.info("Value is here",s))
                .flatMap(s -> Mono.empty())
                .doOnNext(s -> log.info("Value is here2",s))
                .doOnSuccess(s -> log.info("DoOnSucess executed",s));

        mono.subscribe(s -> log.info("Value {}", s));

        //buffer and windowing
        /*
        ¡El flujo de datos podría ser ilimitado / sin fin! En la tubería reactiva, podríamos tener algunas operaciones
        que deban ejecutarse para cada elemento. A veces, en lugar de ejecutar las operaciones para cada artículo uno
        por uno, podríamos recolectar el artículo periódicamente y ejecutar las operaciones para todos los artículos
        recolectados a la vez o podríamos querer realizar algunas operaciones agregadas en el conjunto.
        * */
        // buffer acumula valores pra luego mandarselos al onNext
        Flux.range(5,3)
                //Flux.range(5,10)
                .map(i -> i + 3)
                .filter(j -> j % 2 == 0)
                .buffer(3)
                .log()
                .subscribe();

        Flux<List<Integer>> test = Flux.range(5,3)
                .map(i -> i + 3)
                .filter(j -> j % 2 == 0)
                .buffer(3);

        test.map(i -> i)
                .subscribe(System.out::println);


        //Windowings habre un flux por cada 5 elementos y resultara un FLux<Flux<T>>
        /*Flux.range(1, 10)
                .window(5)
                .doOnNext(flux -> flux.collectList().subscribe(l -> System.out.println("Received :: " + l)))
                .subscribe();*/


        //Hemos estado trabajando con cold streams, "flujos estaticos" de longitud fija que
        //son faciles de manejar; un caso de unso mas realista seria tener un flujo de un mouse
        //constatemente y reacionar a un feed de twitter

        ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
            while (true){
                fluxSink.next(System.currentTimeMillis());
            }
        }).publish();

        //Obtenes un connectable flux lo cual significa que llamar a un subscribe no hara que comience a emitir
        // lo que nos permitira agregar multiples suscripciones

        publish.subscribe(System.out::println);
        publish.subscribe(System.out::println);

        publish.connect();

        //Con lo anterior nuestra consola se vera demasiado saturada con lo que podemos evitar esto con el throttling
        /*ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
            while (true){
                fluxSink.next(System.currentTimeMillis());
            }
        }).sample(Duration.ofSeconds(2))
                .publish();

        publish.subscribe(System.out::println);
        publish.subscribe(System.out::println);

        publish.connect();*/


        List<String> listaPrueba = Arrays.asList("1","2","3");

        /*Flux.just("prueba","cosa","animal")
                .log()
                .flatMap(test1::asyncCapitalize)
                .subscribe(System.out::println);*/

        //Merge
        Flux<String> flux1 = Flux.just("1", "2", "3", "4");
        Flux<String> flux2 = Flux.just("a", "b", "c", "d");


        List<String> elements2 = new ArrayList<>();

        Flux.just(1,2,3,4)
                .log()
                .map(i -> i * 2)
                .zipWith(Flux.range(0,Integer.MAX_VALUE),
                        (one, two) -> String.format("First flux: %d, Second Flux: %d",one,two))
                .subscribe(elements2::add);

        System.out.println(elements2);


        flux1.zipWith(flux2,
                (one,two) ->
                        String.format("first: %s , second: %s",one,two))
                .subscribe(System.out::println);

        //merge with interleave
        //Flux.merge(flux1,flux2)
        //        .subscribe(System.out::println);

        //no interleave
        //Flux.mergeSequential(flux1, flux2)
        //        .subscribe(System.out::println);


        // Convertir un flux to Mono<List>
        /*Mono<List<String>> monolist = Flux.just("1","2","3")
                .collectList();

        monolist
                .subscribe(System.out::println);*/


        // map is for synchronous, non-blocking, 1-to-1 transformations
        // flatMap is for asynchronous (non-blocking) 1-to-N transformations

        //-----------------------------------------
        // Validar el synchonismo en el map
        Flux.just(1,2,3,4)
                .log()
                .map(i -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    return i *2;
                }).subscribe(e -> System.out.println("get:{}" + e));

        //validar lo asyncrono del flux
        Flux<Integer> flujoFlat  = Flux.just(1,2,3,4)
                .log()
                .flatMap(e -> {
                    return Flux.just(e*2);
                    //return Flux.just(e*2).delayElements(Duration.ofSeconds(1));
                });

        flujoFlat.subscribe(System.out::println);
        //TimeUnit.SECONDS.sleep(5);


        //block
        //Mono.just("hola").block();




    }

    static Mono<String> asyncCapitalize(String value) {
        return Mono.just(value)
                .map(user -> user.substring(0, 1).toUpperCase() + user.substring(1));
    }

    ////Flux containing the value of monos
    //        Mono<String> mono1 = Mono.just("1");
    //        Mono<String> mono2 = Mono.just("a");
    //
    //        Flux<String> adfg = Flux.concat(mono1,mono2)
    //                .subscribe(System.out::println);


     /*Flux<String> letters = Flux.just("A", "B", "C", "D", "E", "F")
                //.flatMap(letter -> {
                //    if(letter.equals("F")){
                //        //return Mono.error(new IllegalArgumentException(""));
                //        return Mono.just(letter);
                //    }else  return Mono.just(letter);
                //})
                .map(l -> {
                    if (l.equals("F"))
                        //throw new IllegalArgumentException();
                        return l;
                    return l;
                })
                //.checkpoint("I want a error to show this message", false)
                .delayElements(Duration.ofSeconds(1));


        Consumer<String> consumer = letter -> System.out.println("new letter " + letter);

        letters.subscribe(consumer);
        Thread.sleep(4 * 1000);*/
}
