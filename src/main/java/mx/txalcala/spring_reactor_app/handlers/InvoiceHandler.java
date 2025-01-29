package mx.txalcala.spring_reactor_app.handlers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import mx.txalcala.spring_reactor_app.dtos.InvoiceDTO;
import mx.txalcala.spring_reactor_app.models.Invoice;
import mx.txalcala.spring_reactor_app.services.IInvoiceService;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class InvoiceHandler {

    private final IInvoiceService service;

    @Qualifier("defaultMapper")
    private final ModelMapper modelMapper;

    private InvoiceDTO convertToDto(Invoice model) {
        return modelMapper.map(model, InvoiceDTO.class);
    }

    private Invoice convertToDocument(InvoiceDTO dto) {
        return modelMapper.map(dto, Invoice.class);
    }

    // enfoque funcional: ServerResponse
    // enfoque funcional: ServerRequest
    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll().map(this::convertToDto), InvoiceDTO.class);
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
        Mono<InvoiceDTO> monoInvoiceDTO = request.bodyToMono(InvoiceDTO.class);

        return monoInvoiceDTO
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

        return request.bodyToMono(InvoiceDTO.class)
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
