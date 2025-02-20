package mx.txalcala.spring_reactor_app.services.impl;

import mx.txalcala.spring_reactor_app.models.Invoice;
import mx.txalcala.spring_reactor_app.models.InvoiceDetail;
import mx.txalcala.spring_reactor_app.repositories.IInvoiceRepo;
import mx.txalcala.spring_reactor_app.repositories.IClientRepo;
import mx.txalcala.spring_reactor_app.repositories.IDishRepo;
import mx.txalcala.spring_reactor_app.repositories.IGenericRepo;
import mx.txalcala.spring_reactor_app.services.IInvoiceService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl extends CRUDImpl<Invoice, String> implements IInvoiceService {

    // @Autowired
    private final IInvoiceRepo invoiceRepo;
    private final IClientRepo clientRepo;
    private final IDishRepo dishRepo;

    @Override
    protected IGenericRepo<Invoice, String> getRepo() {
        return invoiceRepo;
    }

    private Mono<Invoice> populateClient(Invoice invoice) {
        return clientRepo.findById(invoice.getClient().getId())
                .map(client -> {
                    invoice.setClient(client);
                    return invoice;
                })
                .delaySubscription(Duration.ofSeconds(2));
    }

    private Mono<Invoice> populateItems(Invoice invoice) {
        List<Mono<InvoiceDetail>> list = invoice.getItems().stream()
                .map(item -> dishRepo.findById(item.getDish().getId())
                        .map(dish -> {
                            item.setDish(dish);
                            return item;
                        }))
                .toList();

        // when: gatillador
        // then: objeto resultante a partir del primero
        return Mono.when(list).then(Mono.just(invoice)).delaySubscription(Duration.ofSeconds(3));
    }

    private byte[] generatePDF(Invoice invoice) {
        try (InputStream stream = getClass().getResourceAsStream("/facturas.jrxml")) {
            Map<String, Object> params = new HashMap<>();
            params.put("txt_client", invoice.getClient().getFirstName() + " " + invoice.getClient().getLastName());

            JasperReport report = JasperCompileManager.compileReport(stream);
            JasperPrint print = JasperFillManager.fillReport(report, params,
                    new JRBeanCollectionDataSource(invoice.getItems()));
            return JasperExportManager.exportReportToPdf(print);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public Mono<byte[]> generateReport(String idInvoice) {
        long startTime = System.currentTimeMillis();

        return invoiceRepo.findById(idInvoice)
                .subscribeOn(Schedulers.single()) // esto lo resuelvo con un hilo single
                .publishOn(Schedulers.newSingle("th-data")) // nuevo hilo
                .flatMap(invoice -> Mono.zip(
                        populateClient(invoice),
                        populateItems(invoice),
                        (populatedClient, populatedItems) -> populatedItems)) // resultado combinado en una sola fuente
                .publishOn(Schedulers.boundedElastic())
                .map(this::generatePDF)
                .onErrorResume(e -> Mono.empty())
                // una vez completada la ejecución haremos algo más
                .doOnSuccess(inv -> {
                    long endTime = System.currentTimeMillis(); // tiempo de termino
                    System.out.println("Total time: " + (endTime - startTime) + " ms");
                });

        /*
         * return invoiceRepo.findById(idInvoice)
         * .flatMap(this::populateClient)
         * .flatMap(this::populateItems)
         * .map(this::generatePDF)
         * .onErrorResume(e -> Mono.empty());
         */

    }

}
