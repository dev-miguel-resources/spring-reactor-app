package mx.txalcala.spring_reactor_app.services;

import mx.txalcala.spring_reactor_app.models.Invoice;
import reactor.core.publisher.Mono;

public interface IInvoiceService extends ICRUD<Invoice, String> {

    Mono<byte[]> generateReport(String invoice);

}
