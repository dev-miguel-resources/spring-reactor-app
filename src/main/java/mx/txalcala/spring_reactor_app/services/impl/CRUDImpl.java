package mx.txalcala.spring_reactor_app.services.impl;

import org.springframework.data.domain.Pageable;

import mx.txalcala.spring_reactor_app.pagination.PageSupport;
import mx.txalcala.spring_reactor_app.repositories.IGenericRepo;
import mx.txalcala.spring_reactor_app.services.ICRUD;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class CRUDImpl<T, ID> implements ICRUD<T, ID> {

    protected abstract IGenericRepo<T, ID> getRepo();

    @Override
    public Mono<T> save(T t) {
        return getRepo().save(t);
    }

    @Override
    public Mono<T> update(ID id, T t) {
        return getRepo().findById(id).flatMap(e -> getRepo().save(t));
    }

    @Override
    public Flux<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public Mono<T> findById(ID id) {
        return getRepo().findById(id);
    }

    @Override
    public Mono<Boolean> delete(ID id) {
        return getRepo().findById(id)
                .hasElement()
                .flatMap(result -> {
                    if (result) {
                        return getRepo().deleteById(id).thenReturn(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    @Override
    public Mono<PageSupport<T>> getPage(Pageable pageable) {
        return getRepo().findAll()
                .collectList()
                .map(list -> new PageSupport<>(
                        // 1,2,3,4,5,6,7,8,9,10
                        // pageNumber = 0
                        // pageSize = 2
                        list.stream()
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                .limit(pageable.getPageSize()).toList(),
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        list.size()));
    }

}
