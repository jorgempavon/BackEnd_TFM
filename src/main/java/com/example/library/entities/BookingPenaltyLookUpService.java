package com.example.library.entities;

import com.example.library.entities.dto.penalty.BookingPeriodPenaltyExistenceDTO;

public interface BookingPenaltyLookUpService {
    BookingPeriodPenaltyExistenceDTO getBookingPeriodPenaltyByClientId(Long ClientId);
}
