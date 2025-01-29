package mx.txalcala.spring_reactor_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import mx.txalcala.spring_reactor_app.handlers.ClientHandler;
import mx.txalcala.spring_reactor_app.handlers.DishHandler;
import mx.txalcala.spring_reactor_app.handlers.InvoiceHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    // Functional endpoints
    @Bean
    public RouterFunction<ServerResponse> routesDish(DishHandler handler) {
        return route(GET("/v2/dishes"), handler::findAll)
                .andRoute(GET("/v2/dishes/{id}"), handler::findById)
                .andRoute(POST("/v2/dishes"), handler::save)
                .andRoute(PUT("/v2/dishes/{id}"), handler::update)
                .andRoute(DELETE("/v2/dishes/{id}"), handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> routesClient(ClientHandler handler) {
        return route(GET("/v2/clients"), handler::findAll)
                .andRoute(GET("/v2/clients/{id}"), handler::findById)
                .andRoute(POST("/v2/clients"), handler::save)
                .andRoute(PUT("/v2/clients/{id}"), handler::update)
                .andRoute(DELETE("/v2/clients/{id}"), handler::delete);
    }

    @Bean
    public RouterFunction<ServerResponse> routesInvoice(InvoiceHandler handler) {
        return route(GET("/v2/invoices"), handler::findAll)
                .andRoute(GET("/v2/invoices/{id}"), handler::findById)
                .andRoute(POST("/v2/invoices"), handler::save)
                .andRoute(PUT("/v2/invoices/{id}"), handler::update)
                .andRoute(DELETE("/v2/invoices/{id}"), handler::delete);
    }

}
