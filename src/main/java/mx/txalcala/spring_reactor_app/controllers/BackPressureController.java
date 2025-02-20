package mx.txalcala.spring_reactor_app.controllers;

import java.time.Duration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.txalcala.spring_reactor_app.models.Dish;
import mx.txalcala.spring_reactor_app.services.IDishService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/backpressure")
@RequiredArgsConstructor
public class BackPressureController {

    private final IDishService service;

    @GetMapping(value = "/json", produces = "application/json")
    public Flux<Dish> json() {
        return Flux.interval(Duration.ofMillis(100))
                .map(t -> new Dish("1", "Soda", 5.90, true));
    }

    @GetMapping(value = "/event", produces = "text/event-stream")
    public Flux<Dish> eventStream() {
        return service.findAll().repeat(10000);
    }

    @GetMapping("/limitRate")
    public Flux<Integer> limitRate() {
        return Flux.range(1, 5000000)
                .log()
                // hightide: solicita los 10 primeros
                // lowtide: solicita los siguientes 8 y así sucesivamente
                .limitRate(10) // utiliza el 75% de la capacidad total de los siguientes elementos requeridos
                // .limitRate(10, 8)
                .delayElements(Duration.ofMillis(1));
    }

}
