package mx.txalcala.spring_reactor_app.services.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.txalcala.spring_reactor_app.models.Role;
import mx.txalcala.spring_reactor_app.repositories.IRoleRepo;
import mx.txalcala.spring_reactor_app.repositories.IGenericRepo;
import mx.txalcala.spring_reactor_app.services.IRoleService;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends CRUDImpl<Role, String> implements IRoleService {

    private final IRoleRepo repo;

    @Override
    protected IGenericRepo<Role, String> getRepo() {
        return repo;
    }

}
