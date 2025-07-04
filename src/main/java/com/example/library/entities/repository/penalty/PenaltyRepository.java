package com.example.library.entities.repository.penalty;

import com.example.library.entities.model.penalty.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PenaltyRepository extends JpaRepository<Penalty, Long>, JpaSpecificationExecutor<Penalty> {
    Integer countByClientId(Long clientId);
    boolean existsByClientIdAndTypeAndForgived(Long clientId,String type,Boolean forgived);
    boolean existsByClientIdAndFulfilled(Long clientId,Boolean fulfilled);

    Optional<List<Penalty>> findByClientIdAndFulfilled(Long clientId, Boolean fulfilled);
    Optional<List<Penalty>> findByClientIdAndTypeAndForgived(Long clientId, String type, Boolean forgived);
}
