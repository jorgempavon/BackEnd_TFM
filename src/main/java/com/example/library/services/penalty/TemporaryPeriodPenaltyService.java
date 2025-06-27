package com.example.library.services.penalty;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.TemporaryPenaltyLookUpService;
import com.example.library.entities.dto.penalty.*;
import com.example.library.entities.model.penalty.Penalty;
import com.example.library.entities.model.penalty.TemporaryPeriodPenalty;
import com.example.library.entities.model.rule.TemporaryPeriodRule;
import com.example.library.entities.repository.penalty.TemporaryPeriodPenaltyRepository;
import com.example.library.services.EmailService;
import com.example.library.services.rule.TemporaryPeriodRuleService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TemporaryPeriodPenaltyService implements TemporaryPenaltyLookUpService {
    private final TemporaryPeriodPenaltyRepository temporaryPeriodPenaltyRepository;
    private final PenaltyService penaltyService;
    private final EmailService emailService;

    private final TemporaryPeriodRuleService temporaryPeriodRuleService;

    public TemporaryPeriodPenaltyService(PenaltyService penaltyService,
                                         TemporaryPeriodPenaltyRepository temporaryPeriodPenaltyRepository,
                                         TemporaryPeriodRuleService temporaryPeriodRuleService,
                                         EmailService emailService
    ){
        this.penaltyService = penaltyService;
        this.temporaryPeriodPenaltyRepository = temporaryPeriodPenaltyRepository;
        this.temporaryPeriodRuleService = temporaryPeriodRuleService;
        this.emailService = emailService;
    }
    @Transactional
    public void deleteByPenaltyId(Long penaltyId){
        if(this.temporaryPeriodPenaltyRepository.existsByPenaltyId(penaltyId)){
            TemporaryPeriodPenalty temporaryPeriodPenalty = this.temporaryPeriodPenaltyRepository.findByPenaltyId(penaltyId).get();
            this.temporaryPeriodPenaltyRepository.delete(temporaryPeriodPenalty);
        }
        this.penaltyService.delete(penaltyId);
    }

    public TemporaryPeriodPenaltyDTO findByPenaltyId(Long penaltyId,Long userId){
        if(!this.temporaryPeriodPenaltyRepository.existsByPenaltyId(penaltyId)){
            throw new NotFoundException("No existe ninguna penalización con el id: "+penaltyId.toString());
        }

        TemporaryPeriodPenalty temporaryPeriodPenalty = this.temporaryPeriodPenaltyRepository.findByPenaltyId(penaltyId).get();
        TemporaryPeriodRule temporaryPeriodRule = temporaryPeriodPenalty.getTemporaryPeriodRule();

        PenaltyDTO penaltyDTO = this.penaltyService.findById(penaltyId,userId);
        String temporaryPeriodRuleName = this.temporaryPeriodRuleService.getRuleNameByTemporaryPeriodRule(temporaryPeriodRule);

        return new TemporaryPeriodPenaltyDTO(
                penaltyDTO,temporaryPeriodPenalty.getEndDate(),temporaryPeriodRuleName
        );
    }
    @Transactional
    public TemporaryPeriodPenaltyDTO create(TemporaryPeriodPenaltyCreateDTO temporaryPeriodPenaltyCreateDTO){
        PenaltyAndPenaltyDTO penaltyAndPenaltyDTO = this.penaltyService.create(
                temporaryPeriodPenaltyCreateDTO.getPenaltyCreateDTO()
        );
        Penalty penalty = penaltyAndPenaltyDTO.getPenalty();
        PenaltyDTO penaltyDTO = penaltyAndPenaltyDTO.getPenaltyDTO();
        String clientEmail = penaltyAndPenaltyDTO.getClientEmail();

        this.emailService.sendTemporaryPeriodPenaltyEmail(clientEmail,penaltyDTO.getClientName(),
                penaltyDTO.getBookTitle(),temporaryPeriodPenaltyCreateDTO.getEndDate());
        TemporaryPeriodRule temporaryPeriodRule = temporaryPeriodPenaltyCreateDTO.getTemporaryPeriodRule();
        String temporaryPeriodRuleName = this.temporaryPeriodRuleService.getRuleNameByTemporaryPeriodRule(temporaryPeriodRule);
        Date endDate = temporaryPeriodPenaltyCreateDTO.getEndDate();

        TemporaryPeriodPenalty temporaryPeriodPenalty = new TemporaryPeriodPenalty();
        temporaryPeriodPenalty.setTemporaryPeriodRule(temporaryPeriodRule);
        temporaryPeriodPenalty.setEndDate(endDate);
        temporaryPeriodPenalty.setPenalty(penalty);
        this.temporaryPeriodPenaltyRepository.save(temporaryPeriodPenalty);


        return new TemporaryPeriodPenaltyDTO(
                penaltyDTO,endDate,temporaryPeriodRuleName
        );
    }
    @Override
    public TemporaryPeriodPenaltyExistenceDTO getTemporaryPeriodPenaltyByClientId(Long ClientId){
        TemporaryPeriodPenaltyExistenceDTO response = new TemporaryPeriodPenaltyExistenceDTO(false,null);
        String type = "Temporal";

        PenaltyExistenceDTO responseExistsPenalty = this.penaltyService.getPenaltyByClientIdAndType(ClientId,type);
        if (!responseExistsPenalty.getExistsPenalty()){
            return response;
        }
        Long penaltyId = responseExistsPenalty.getPenaltyId();
        TemporaryPeriodPenalty bookingPeriodPenalty = this.temporaryPeriodPenaltyRepository.findByPenaltyId(penaltyId).get();
        Date today = new Date();

        if(bookingPeriodPenalty.getEndDate().before(today)){
           return response;
        }
        response.setExistsPenalty(true);
        response.setEndDate(bookingPeriodPenalty.getEndDate());
        return response;
    }
}
