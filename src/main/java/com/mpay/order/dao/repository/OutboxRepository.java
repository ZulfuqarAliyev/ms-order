package com.mpay.order.dao.repository;

import com.mpay.order.dao.entity.OutboxEntity;
import com.mpay.order.model.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {
    List<OutboxEntity> findTop10ByStatusInOrderByIdAsc(List<OutboxStatus> statusList);
}