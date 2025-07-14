package com.home.preorder.service.repository;

import com.home.preorder.service.model.entity.Bid;
import com.home.preorder.service.model.enums.BidStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID>, JpaSpecificationExecutor<Bid> {


    @Query("SELECT b FROM Bid b WHERE b.task.taskId = :taskId")
    Page<Bid> findAllByTaskId(@Param("taskId") UUID taskId, Pageable pageable);

    @Query("SELECT b FROM Bid b WHERE b.userId = :userId")
    Page<Bid> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT b FROM Bid b WHERE b.task.userId = :userId AND b.status IN :statuses")
    Page<Bid> findAllByTaskUserIdAndStatusIn(
            @Param("userId") UUID userId,
            @Param("statuses") List<BidStatus> statuses,
            Pageable pageable);


    @Query("SELECT COUNT(b) > 0 FROM Bid b WHERE b.task.taskId = :taskId AND b.userId = :userId")
    boolean existsByTaskIdAndUserId(@Param("taskId") UUID taskId, @Param("userId") UUID userId);
}
