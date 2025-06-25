package com.example.library.services.penalty;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.penalty.BookingPeriodPenaltyCreateDTO;
import com.example.library.entities.dto.penalty.BookingPeriodPenaltyDTO;
import com.example.library.entities.dto.penalty.PenaltyAndPenaltyDTO;
import com.example.library.entities.dto.penalty.PenaltyDTO;
import com.example.library.entities.model.penalty.BookingPeriodPenalty;
import com.example.library.entities.model.penalty.Penalty;
import com.example.library.entities.model.rule.BookingPeriodRule;
import com.example.library.entities.repository.penalty.BookingPeriodPenaltyRepository;
import com.example.library.services.EmailService;
import com.example.library.services.rule.BookingPeriodRuleService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BookingPeriodPenaltyService {
    private final BookingPeriodPenaltyRepository bookingPeriodPenaltyRepository;
    private final PenaltyService penaltyService;
    private final EmailService emailService;

    private final BookingPeriodRuleService bookingPeriodRuleService;

    public BookingPeriodPenaltyService(BookingPeriodPenaltyRepository bookingPeriodPenaltyRepository,
                                       PenaltyService penaltyService,
                                       BookingPeriodRuleService bookingPeriodRuleService,
                                       EmailService emailService
    ){
        this.penaltyService = penaltyService;
        this.bookingPeriodPenaltyRepository = bookingPeriodPenaltyRepository;
        this.bookingPeriodRuleService = bookingPeriodRuleService;
        this.emailService = emailService;
    }
    @Transactional
    public void deleteByPenaltyId(Long penaltyId){
        if(this.bookingPeriodPenaltyRepository.existsByPenaltyId(penaltyId)){
            BookingPeriodPenalty bookingPeriodPenalty = this.bookingPeriodPenaltyRepository.findByPenaltyId(penaltyId).get();
            this.bookingPeriodPenaltyRepository.delete(bookingPeriodPenalty);
        }
        this.penaltyService.delete(penaltyId);
    }

    public BookingPeriodPenaltyDTO findByPenaltyId(Long penaltyId,Long userId){
        if(!this.bookingPeriodPenaltyRepository.existsByPenaltyId(penaltyId)){
            throw new NotFoundException("No existe ninguna penalización con el id: "+penaltyId.toString());
        }
        BookingPeriodPenalty bookingPeriodPenalty = this.bookingPeriodPenaltyRepository.findByPenaltyId(penaltyId).get();
        BookingPeriodRule bookingPeriodRule = bookingPeriodPenalty.getBookingPeriodRule();

        PenaltyDTO penaltyDTO = this.penaltyService.findById(penaltyId,userId);
        String bookingPeriodRuleName = this.bookingPeriodRuleService.getRuleNameByBookingPeriodRule(bookingPeriodRule);

        return new BookingPeriodPenaltyDTO(
                penaltyDTO,bookingPeriodPenalty.getDays(),bookingPeriodRuleName
        );
    }
    @Transactional
    public BookingPeriodPenaltyDTO create(BookingPeriodPenaltyCreateDTO bookingPeriodPenaltyCreateDTO){
        PenaltyAndPenaltyDTO penaltyAndPenaltyDTO = this.penaltyService.create(
                bookingPeriodPenaltyCreateDTO.getPenaltyCreateDTO()
        );
        Penalty penalty = penaltyAndPenaltyDTO.getPenalty();
        PenaltyDTO penaltyDTO = penaltyAndPenaltyDTO.getPenaltyDTO();
        String clientEmail = penaltyAndPenaltyDTO.getClientEmail();

        this.emailService.sendBookingPeriodPenaltyEmail(clientEmail,penaltyDTO.getClientName(),
                penaltyDTO.getBookTitle(),bookingPeriodPenaltyCreateDTO.getDays());
        BookingPeriodRule bookingPeriodRule = bookingPeriodPenaltyCreateDTO.getBookingPeriodRule();
        String bookingPeriodRuleName = this.bookingPeriodRuleService.getRuleNameByBookingPeriodRule(bookingPeriodRule);
        Integer days = bookingPeriodPenaltyCreateDTO.getDays();

        BookingPeriodPenalty bookingPeriodPenalty = new BookingPeriodPenalty();
        bookingPeriodPenalty.setBookingPeriodRule(bookingPeriodRule);
        bookingPeriodPenalty.setDays(days);
        bookingPeriodPenalty.setPenalty(penalty);
        this.bookingPeriodPenaltyRepository.save(bookingPeriodPenalty);

        return new BookingPeriodPenaltyDTO(
                penaltyDTO,days,bookingPeriodRuleName
        );
    }
}
