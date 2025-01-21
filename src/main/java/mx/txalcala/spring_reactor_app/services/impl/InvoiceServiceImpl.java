package mx.txalcala.spring_reactor_app.services.impl;

import mx.txalcala.spring_reactor_app.models.Invoice;

import mx.txalcala.spring_reactor_app.repositories.IInvoiceRepo;
import mx.txalcala.spring_reactor_app.repositories.IGenericRepo;
import mx.txalcala.spring_reactor_app.services.IInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl extends CRUDImpl<Invoice, String> implements IInvoiceService {

    // @Autowired
    private final IInvoiceRepo invoiceRepo;

    @Override
    protected IGenericRepo<Invoice, String> getRepo() {
        return invoiceRepo;
    }

}
