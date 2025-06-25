package com.example.library.entities.dto.penalty;

import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.model.user.Client;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class PenaltyCreateDTO {
    @NotBlank(message = "La descripcion es obligatoria")
    private String description;
    @NotBlank(message = "El tipo es obligatorio")
    private String type;
    @NotBlank(message = "La reserva es obligatoria")
    private BookingLoan bookingLoan;
    @NotBlank(message = "El cliente es obligatorio")
    private Client client;

    public PenaltyCreateDTO(){

    }

    public PenaltyCreateDTO(String description,String type, BookingLoan bookingLoan, Client client){
        this.description = description;
        this.bookingLoan = bookingLoan;
        this.client = client;
        this.type = type;
    }
}
