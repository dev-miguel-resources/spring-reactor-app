package mx.txalcala.spring_reactor_app.services.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.txalcala.spring_reactor_app.models.Dish;
import mx.txalcala.spring_reactor_app.repositories.IDishRepo;
import mx.txalcala.spring_reactor_app.repositories.IGenericRepo;
import mx.txalcala.spring_reactor_app.services.IDishService;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends CRUDImpl<Dish, String> implements IDishService {

    private final IDishRepo repo;

    @Override
    protected IGenericRepo<Dish, String> getRepo() {
        return repo;
    }

}
