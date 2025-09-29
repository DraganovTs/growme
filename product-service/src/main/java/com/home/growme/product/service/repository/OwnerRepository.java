package com.home.growme.product.service.repository;

import com.home.growme.product.service.model.entity.Owner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, UUID> {

    @Query("select o from Owner o LEFT join o.products p group by o.ownerId order by count(p) desc ")
    List<Owner> findAllOrderByProductCountDesc(Pageable pageable);
}
