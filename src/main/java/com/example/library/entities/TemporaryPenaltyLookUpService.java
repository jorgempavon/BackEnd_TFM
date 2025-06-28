package com.example.library.entities;

import com.example.library.entities.dto.penalty.TemporaryPeriodPenaltyCreateDTO;
import com.example.library.entities.dto.penalty.TemporaryPeriodPenaltyDTO;
import com.example.library.entities.dto.penalty.TemporaryPeriodPenaltyExistenceDTO;
import jakarta.transaction.Transactional;

public interface TemporaryPenaltyLookUpService {
    @Transactional
    TemporaryPeriodPenaltyDTO create(TemporaryPeriodPenaltyCreateDTO temporaryPeriodPenaltyCreateDTO);

    TemporaryPeriodPenaltyExistenceDTO getTemporaryPeriodPenaltyByClientId(Long ClientId);
}
