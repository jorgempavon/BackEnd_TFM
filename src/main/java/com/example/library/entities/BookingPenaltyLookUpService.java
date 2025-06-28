package com.example.library.entities;

import com.example.library.entities.dto.penalty.BookingPeriodPenaltyCreateDTO;
import com.example.library.entities.dto.penalty.BookingPeriodPenaltyDTO;
import com.example.library.entities.dto.penalty.BookingPeriodPenaltyExistenceDTO;
import jakarta.transaction.Transactional;

public interface BookingPenaltyLookUpService {
    @Transactional
    BookingPeriodPenaltyDTO create(BookingPeriodPenaltyCreateDTO bookingPeriodPenaltyCreateDTO);

    BookingPeriodPenaltyExistenceDTO getBookingPeriodPenaltyByClientId(Long ClientId);
}
