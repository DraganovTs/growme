package com.home.order.service.repository;

import com.home.order.service.model.entity.Basket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketRepository extends CrudRepository<Basket,String > {
}
