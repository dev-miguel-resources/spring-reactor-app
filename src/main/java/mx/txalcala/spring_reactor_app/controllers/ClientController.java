package mx.txalcala.spring_reactor_app.controllers;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.txalcala.spring_reactor_app.dtos.ClientDTO;
import mx.txalcala.spring_reactor_app.models.Client;
import mx.txalcala.spring_reactor_app.pagination.PageSupport;
import mx.txalcala.spring_reactor_app.services.IClientService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.cloudinary.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final IClientService service;

    @Qualifier("clientMapper")
    private final ModelMapper modelMapper;

    private final Cloudinary cloudinary;

    private ClientDTO convertToDto(Client model) {
        return modelMapper.map(model, ClientDTO.class);
    }

    private Client convertToDocument(ClientDTO dto) {
        return modelMapper.map(dto, Client.class);
    }

    @GetMapping()
    public Mono<ResponseEntity<Flux<ClientDTO>>> findAll() {
        Flux<ClientDTO> fx = service.findAll().map(this::convertToDto); // e -> convertToDto(e)

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<ClientDTO>> save(@Valid @RequestBody ClientDTO dto, final ServerHttpRequest req) {
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
    public Mono<ResponseEntity<ClientDTO>> findById(@PathVariable("id") String id) {
        return service.findById(id) // Mono<Client>
                .map(this::convertToDto)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ClientDTO>> update(@Valid @PathVariable("id") String id, @RequestBody ClientDTO dto) {
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

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<ClientDTO>>> getPage(
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

    // MultipartFile: apps no reactivas (mvc)
    // FilePart: enfoque reactivo
    @PostMapping("/v1/upload/{id}")
    public Mono<ResponseEntity<ClientDTO>> uploadV1(@PathVariable("id") String id,
            @RequestPart("file") FilePart filePart) {
        return service.findById(id)
                .flatMap(client -> {
                    try {
                        File f = Files.createTempFile("temp", filePart.filename()).toFile();
                        filePart.transferTo(f).block();

                        @SuppressWarnings("unchecked")
                        Map<String, Object> response = cloudinary.uploader().upload(f,
                                ObjectUtils.asMap("resource_type", "auto"));
                        JSONObject json = new JSONObject(response);
                        String url = json.getString("url");

                        client.setUrlPhoto(url);

                        return service.update(id, client)
                                .map(this::convertToDto)
                                .map(e -> ResponseEntity.ok().body(e));
                    } catch (Exception e) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                });

    }

}
