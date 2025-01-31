package mx.txalcala.spring_reactor_app.handlers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import mx.txalcala.spring_reactor_app.dtos.ClientDTO;
import mx.txalcala.spring_reactor_app.models.Client;
import mx.txalcala.spring_reactor_app.services.IClientService;
import mx.txalcala.spring_reactor_app.validator.RequestValidator;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class ClientHandler {

    private final IClientService service;

    @Qualifier("clientMapper")
    private final ModelMapper modelMapper;

    private final RequestValidator requestValidator;

    private ClientDTO convertToDto(Client model) {
        return modelMapper.map(model, ClientDTO.class);
    }

    private Client convertToDocument(ClientDTO dto) {
        return modelMapper.map(dto, Client.class);
    }

    // enfoque funcional: ServerResponse
    // enfoque funcional: ServerRequest
    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll().map(this::convertToDto), ClientDTO.class);
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
        Mono<ClientDTO> monoClientDTO = request.bodyToMono(ClientDTO.class);

        return monoClientDTO
                .flatMap(requestValidator::validate)
                .flatMap(e -> service.save(convertToDocument(e)))
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse
                        .created(URI.create(request.uri().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e)));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");

        return request.bodyToMono(ClientDTO.class)
                .map(e -> {
                    e.setId(id);
                    return e;
                })
                .flatMap(requestValidator::validate)
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
