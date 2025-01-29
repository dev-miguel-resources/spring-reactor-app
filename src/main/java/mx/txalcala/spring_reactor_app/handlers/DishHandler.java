package mx.txalcala.spring_reactor_app.handlers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import mx.txalcala.spring_reactor_app.dtos.DishDTO;
import mx.txalcala.spring_reactor_app.models.Dish;
import mx.txalcala.spring_reactor_app.services.IDishService;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class DishHandler {

    private final IDishService service;

    @Qualifier("defaultMapper")
    private final ModelMapper modelMapper;

    private DishDTO convertToDto(Dish model) {
        return modelMapper.map(model, DishDTO.class);
    }

    private Dish convertToDocument(DishDTO dto) {
        return modelMapper.map(dto, Dish.class);
    }

    // enfoque funcional: ServerResponse
    // enfoque funcional: ServerRequest
    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll().map(this::convertToDto), DishDTO.class);
    }

    // swichIfEmpty vs el defaultIfEmpty
    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.findById(id)
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e)))
                .switchIfEmpty(ServerResponse.notFound().build());
        // .body(BodyInserters.fromValue(e)));
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<DishDTO> monoDishDTO = request.bodyToMono(DishDTO.class);

        return monoDishDTO
                // .flatMap(validarlo)
                .flatMap(e -> service.save(convertToDocument(e)))
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse
                        .created(URI.create(request.uri().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e)));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");

        return request.bodyToMono(DishDTO.class)
                .map(e -> {
                    e.setId(id);
                    return e;
                })
                // .flatMap(validacion)
                .flatMap(e -> service.update(id, convertToDocument(e)))
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e)));

    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.delete(id)
                .flatMap(result -> {
                    if (result) {
                        return ServerResponse.noContent().build();
                    } else {
                        return ServerResponse.notFound().build();
                    }
                });
    }

}
