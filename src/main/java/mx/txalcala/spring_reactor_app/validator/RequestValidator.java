package mx.txalcala.spring_reactor_app.validator;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

// Clase de request validator para los handlers (controladores)
// Permite validator cualquier tipo de dato
@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public <T> Mono<T> validate(T t) {
        if (t == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        }

        Set<ConstraintViolation<T>> constraints = validator.validate(t);

        if (constraints == null || constraints.isEmpty()) {
            return Mono.just(t);
        }

        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

}
