package com.example.library.entities.dto.penalty;

import com.example.library.entities.model.rule.BookingPeriodRule;
import com.example.library.entities.model.rule.TemporaryPeriodRule;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BookingPeriodPenaltyCreateDTO {
    @NotNull(message = "Es obligatorio proporcionar los atributos de la penalización")
    private PenaltyCreateDTO penaltyCreateDTO;
    @NotNull(message = "Es obligatorio proporcionar los días de la penalización")
    private Integer days;
    @NotNull(message = "Es obligatorio proporcionar la penalización de intervalo de reserva")
    private BookingPeriodRule bookingPeriodRule;

    public BookingPeriodPenaltyCreateDTO(){

    }
    public BookingPeriodPenaltyCreateDTO(PenaltyCreateDTO penaltyCreateDTO,Integer days,BookingPeriodRule bookingPeriodRule){
        this.penaltyCreateDTO = penaltyCreateDTO;
        this.days = days;
        this.bookingPeriodRule = bookingPeriodRule;
    }
}
