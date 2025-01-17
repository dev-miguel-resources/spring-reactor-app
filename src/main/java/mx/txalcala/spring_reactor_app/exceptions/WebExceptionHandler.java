package mx.txalcala.spring_reactor_app.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
//import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

    // Constructor encargado de habilitar toda la escritura de excepciones en base a
    // nuesta configuración
    public WebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
            ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        setMessageWriters(configurer.getWriters());
    }

    // Este es un método para ejecutar una respuesta http
    // ServerResponse: esta definido para que la respuesta se resuelva de manera
    // reactiva
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        // inteceptar las rutas http de mi proyecto para resolver las exceptions de
        // manera reactiva
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    // Método para poder pintar los errores
    private Mono<ServerResponse> renderErrorResponse(ServerRequest req) {

        // Necesito capturar el mensaje de error x defecto que genera Spring
        Map<String, Object> generalError = getErrorAttributes(req, ErrorAttributeOptions.defaults());

        Map<String, Object> customError = new HashMap<>();

        // Extraemos el código de status de la llave "status"
        int statusCode = Integer.parseInt(String.valueOf(generalError.get("status")));

        // Obtengo la definición del error
        Throwable error = getError(req);

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        switch (statusCode) {
            case 400, 422 -> {
                customError.put("message", error.getMessage()); // ex.message
                customError.put("status", 400);
                httpStatus = HttpStatus.BAD_REQUEST;
            }
            case 404 -> {
                customError.put("message", error.getMessage()); // ex.message
                customError.put("status", 404);
                httpStatus = HttpStatus.NOT_FOUND;
            }
            case 401, 403 -> {
                customError.put("message", error.getMessage()); // ex.message
                customError.put("status", 401);
                httpStatus = HttpStatus.UNAUTHORIZED;
            }
            case 500 -> {
                customError.put("message", error.getMessage()); // ex.message
                customError.put("status", 500);
            }
            default -> {
                customError.put("message", error.getMessage()); // ex.message
                customError.put("status", 409);
                httpStatus = HttpStatus.CONFLICT;
            }
        }

        // retornar una salida dinámica con un formato de exception
        // custom error
        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(customError));

    }
}
