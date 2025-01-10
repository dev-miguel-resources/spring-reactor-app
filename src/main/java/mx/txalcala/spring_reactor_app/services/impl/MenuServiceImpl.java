package mx.txalcala.spring_reactor_app.services.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.txalcala.spring_reactor_app.models.Menu;
import mx.txalcala.spring_reactor_app.repositories.IMenuRepo;
import mx.txalcala.spring_reactor_app.repositories.IGenericRepo;
import mx.txalcala.spring_reactor_app.services.IMenuService;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends CRUDImpl<Menu, String> implements IMenuService {

    private final IMenuRepo repo;

    @Override
    protected IGenericRepo<Menu, String> getRepo() {
        return repo;
    }

}
