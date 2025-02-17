package mx.txalcala.spring_reactor_app.services;

import org.springframework.data.domain.Pageable;

import mx.txalcala.spring_reactor_app.pagination.PageSupport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICRUD<T, ID> {

    // Concurrencia, procesos no bloquantes, hilos, escalado vertical...
    Mono<T> save(T t);

    Mono<T> update(ID id, T t);

    Flux<T> findAll();

    Mono<T> findById(ID id);

    Mono<Boolean> delete(ID id);

    Mono<PageSupport<T>> getPage(Pageable pageable);

}
