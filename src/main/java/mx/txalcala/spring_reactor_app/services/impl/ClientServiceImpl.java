package mx.txalcala.spring_reactor_app.services.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.txalcala.spring_reactor_app.models.Client;
import mx.txalcala.spring_reactor_app.repositories.IClientRepo;
import mx.txalcala.spring_reactor_app.repositories.IGenericRepo;
import mx.txalcala.spring_reactor_app.services.IClientService;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl extends CRUDImpl<Client, String> implements IClientService {

    private final IClientRepo repo;

    @Override
    protected IGenericRepo<Client, String> getRepo() {
        return repo;
    }

}
