package com.home.preorder.service.repository;

import com.home.preorder.service.model.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID>, JpaSpecificationExecutor<Bid> {


    @Query("SELECT b FROM Bid b WHERE b.task.taskId = :taskId ORDER BY b.createdAt DESC")
    List<Bid> findAllByTaskId(@Param("taskId") UUID taskId);
}
