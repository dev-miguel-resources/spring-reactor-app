package mx.txalcala.spring_reactor_app.controllers;

import java.net.URI;

import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.txalcala.spring_reactor_app.models.Dish;
import mx.txalcala.spring_reactor_app.services.IDishService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    private static final Logger log = LoggerFactory.getLogger(DishController.class);

    private final IDishService service;

    @GetMapping()
    public Mono<ResponseEntity<Flux<Dish>>> findAll() {
        Flux<Dish> fx = service.findAll();

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Dish>> save(@RequestBody Dish dish, final ServerHttpRequest req) {
        return service.save(dish)
                .flatMap(e -> Mono.just(
                        ResponseEntity.created(
                                URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                                .contentType(MediaType.APPLICATION_JSON).body(e)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Dish>> findById(@PathVariable("id") String id) {
        return service.findById(id) // Mono<Dish>
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Dish>> update(@PathVariable("id") String id, @RequestBody Dish dish) {
        return Mono.just(dish)
                .map(e -> {
                    e.setId(id);
                    return e;
                })
                .flatMap(e -> service.update(id, dish))
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .doOnNext(e -> log.info("Element: " + e))
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id) {
        return service.delete(id)
                .flatMap(result -> { // Mono<Boolean>
                    if (result) {
                        return Mono.just(ResponseEntity.noContent().build()); // Mono<ResponseEntity<Void>>
                    } else {
                        return Mono.just(ResponseEntity.notFound().build()); // Mono<ResponseEntity<Void>>
                    }
                });
    }

}
