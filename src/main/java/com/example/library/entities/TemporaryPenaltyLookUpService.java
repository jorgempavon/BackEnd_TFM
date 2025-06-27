package com.example.library.entities;

import com.example.library.entities.dto.penalty.TemporaryPeriodPenaltyExistenceDTO;

public interface TemporaryPenaltyLookUpService {
    TemporaryPeriodPenaltyExistenceDTO getTemporaryPeriodPenaltyByClientId(Long ClientId);
}
