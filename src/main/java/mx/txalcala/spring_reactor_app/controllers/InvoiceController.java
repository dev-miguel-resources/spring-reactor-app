package mx.txalcala.spring_reactor_app.controllers;

import java.net.URI;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.txalcala.spring_reactor_app.dtos.ClientDTO;
import mx.txalcala.spring_reactor_app.dtos.InvoiceDTO;
import mx.txalcala.spring_reactor_app.models.Invoice;
import mx.txalcala.spring_reactor_app.pagination.PageSupport;
import mx.txalcala.spring_reactor_app.services.IInvoiceService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);

    private final IInvoiceService service;

    @Qualifier("defaultMapper")
    private final ModelMapper modelMapper;

    private InvoiceDTO convertToDto(Invoice model) {
        return modelMapper.map(model, InvoiceDTO.class);
    }

    private Invoice convertToDocument(InvoiceDTO dto) {
        return modelMapper.map(dto, Invoice.class);
    }

    @GetMapping()
    public Mono<ResponseEntity<Flux<InvoiceDTO>>> findAll() {
        Flux<InvoiceDTO> fx = service.findAll().map(this::convertToDto); // e -> convertToDto(e)

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<InvoiceDTO>> save(@Valid @RequestBody InvoiceDTO dto, final ServerHttpRequest req) {
        return service.save(convertToDocument(dto))
                .map(this::convertToDto)
                .map(e -> ResponseEntity.created(
                        URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
        /*
         * .flatMap(e -> Mono.just(
         * ResponseEntity.created(
         * URI.create(req.getURI().toString().concat("/").concat(e.getId())))
         * .contentType(MediaType.APPLICATION_JSON).body(e)))
         */
        // .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<InvoiceDTO>> findById(@PathVariable("id") String id) {
        return service.findById(id) // Mono<Invoice>
                .map(this::convertToDto)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<InvoiceDTO>> update(@Valid @PathVariable("id") String id, @RequestBody InvoiceDTO dto) {
        return Mono.just(dto)
                .map(e -> {
                    e.setId(id);
                    return e;
                })
                .flatMap(e -> service.update(id, convertToDocument(dto)))
                .map(this::convertToDto)
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

    // hateoas -> reactividad = EntityModel
    @GetMapping("/hateoas/{id}")
    public Mono<EntityModel<InvoiceDTO>> getHateoas(@PathVariable("id") String id) {
        Mono<Link> monoLink = linkTo(methodOn(InvoiceController.class).findById(id)).withRel("invoice-link").toMono();

        // PRÁCTICA INTERMEDIA
        /*
         * return service.findById(id)
         * .map(this::convertToDto)
         * .flatMap(dto -> monoLink.map(link -> EntityModel.of(dto, link)));
         */

        return service.findById(id)
                .map(this::convertToDto)
                .zipWith(monoLink, EntityModel::of);

    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<InvoiceDTO>>> getPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "page", defaultValue = "2") int size) {
        return service.getPage(PageRequest.of(page, size))
                .map(pageSupport -> new PageSupport<>(
                        pageSupport.getContent().stream().map(this::convertToDto).toList(),
                        pageSupport.getPageNumber(),
                        pageSupport.getPageSize(),
                        pageSupport.getTotalElements()))
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @GetMapping("/generateReport/{id}")
    public Mono<ResponseEntity<byte[]>> generateReport(@PathVariable("id") String id) {
        return service.generateReport(id)
                .map(bytes -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(bytes))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
