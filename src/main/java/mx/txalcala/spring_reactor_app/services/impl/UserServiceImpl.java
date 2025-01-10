package mx.txalcala.spring_reactor_app.services.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.txalcala.spring_reactor_app.models.User;
import mx.txalcala.spring_reactor_app.repositories.IUserRepo;
import mx.txalcala.spring_reactor_app.repositories.IGenericRepo;
import mx.txalcala.spring_reactor_app.services.IUserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends CRUDImpl<User, String> implements IUserService {

    private final IUserRepo repo;

    @Override
    protected IGenericRepo<User, String> getRepo() {
        return repo;
    }

}