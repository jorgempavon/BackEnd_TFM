package com.example.library.services.penalty;

import com.example.library.api.exceptions.models.ForbiddenException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.penalty.*;
import com.example.library.entities.model.penalty.Penalty;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.repository.penalty.PenaltyRepository;
import com.example.library.services.booking_loan.BookingLoanInfoService;
import com.example.library.services.user.ClientService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PenaltyService {
    private final PenaltyRepository penaltyRepository;
    private final ClientService clientService;
    private final BookingLoanInfoService bookingLoanInfoService;


    public PenaltyService(PenaltyRepository penaltyRepository,ClientService clientService,
                          BookingLoanInfoService bookingLoanInfoService){
        this.penaltyRepository = penaltyRepository;
        this.clientService = clientService;
        this.bookingLoanInfoService = bookingLoanInfoService;
    }

    public PenaltyDTO findById(Long id,Long userId){
        if(!this.penaltyRepository.existsById(id)){
            throw new NotFoundException("No existe ninguna penalización con el id: "+id.toString());
        }
        Penalty penalty = this.penaltyRepository.findById(id).get();
        Client client =  penalty.getClient();
        Boolean isUserLoggedOwnerOfPenalty = this.clientService.isClientEqualsByUserIdAndClient(client,userId);

        if(this.clientService.isClientByUserId(userId) && !isUserLoggedOwnerOfPenalty){
            throw new ForbiddenException("No tienes permisos para  acceder a la penalización del id proporcionado");
        }

        String clientFullName = this.clientService.getUserFullNameByClient(penalty.getClient());
        String bookTitle = this.bookingLoanInfoService.getBookTitleByBookingLoan(penalty.getBookingLoan());
        return new PenaltyDTO(penalty.getId(),penalty.getDescription(),penalty.getType(),
                penalty.getJustificationPenalty(),penalty.getFulfilled(),penalty.getForgived(),
                bookTitle,clientFullName,penalty.getCreationDate());
    }

    public List<PenaltyDTO> findByUserAndFulfilled(Long userId, Boolean fulfilled) {
        Long clientId = this.clientService.getClientIdByUserId(userId);
        List<PenaltyDTO> responseList= new ArrayList<>();

        if (!this.penaltyRepository.existsByClientIdAndFulfilled(clientId,fulfilled)){
            return responseList;
        }
        List<Penalty> penalties = this.penaltyRepository.findByClientIdAndFulfilled(clientId,fulfilled).get();

        for (Penalty penalty : penalties) {
            String bookTitle = this.bookingLoanInfoService.getBookTitleByBookingLoan(penalty.getBookingLoan());
            String clientName = this.clientService.getUserFullNameByClient(penalty.getClient());
            PenaltyDTO penaltyDTO = new PenaltyDTO(penalty.getId(),penalty.getDescription(),penalty.getType(),
                    penalty.getJustificationPenalty(),penalty.getFulfilled(),penalty.getForgived()
                    ,bookTitle,clientName,penalty.getCreationDate());
            responseList.add(penaltyDTO);
        }
        return responseList;
    }
    @Transactional
    public PenaltyDTO forgivePenalty(Long id, PenaltyJustificationDTO penaltyJustificationDTO){
        if(!this.penaltyRepository.existsById(id)){
            throw new NotFoundException("No existe la penalizacion con el id proporcionado");
        }
        Penalty penalty = this.penaltyRepository.findById(id).get();
        penalty.setJustificationPenalty(penaltyJustificationDTO.getJustificationPenalty());
        penalty.setForgived(true);
        penalty.setFulfilled(true);
        this.penaltyRepository.save(penalty);

        String clientFullName = this.clientService.getUserFullNameByClient(penalty.getClient());
        String bookTitle = this.bookingLoanInfoService.getBookTitleByBookingLoan(penalty.getBookingLoan());

        return new PenaltyDTO(penalty.getId(),penalty.getDescription(),penalty.getType(),
                penalty.getJustificationPenalty(),penalty.getFulfilled(),penalty.getForgived(),
                bookTitle,clientFullName,penalty.getCreationDate());
    }
    @Transactional
    public PenaltyDTO fulfillPenalty(Long id, PenaltyJustificationDTO penaltyJustificationDTO,Long userId){
        if(!this.penaltyRepository.existsById(id)){
            throw new NotFoundException("No existe la penalizacion con el id proporcionado");
        }
        Penalty penalty = this.penaltyRepository.findById(id).get();
        Client client =  penalty.getClient();
        Boolean isUserLoggedOwnerOfPenalty = this.clientService.isClientEqualsByUserIdAndClient(client,userId);

        if(this.clientService.isClientByUserId(userId) && !isUserLoggedOwnerOfPenalty){
            throw new ForbiddenException("No tienes permisos para cumplimentar a la penalización del id proporcionado");
        }
        penalty.setJustificationPenalty(penaltyJustificationDTO.getJustificationPenalty());
        penalty.setFulfilled(true);
        this.penaltyRepository.save(penalty);

        String clientFullName = this.clientService.getUserFullNameByClient(penalty.getClient());
        String bookTitle = this.bookingLoanInfoService.getBookTitleByBookingLoan(penalty.getBookingLoan());

        return new PenaltyDTO(penalty.getId(),penalty.getDescription(),penalty.getType(),
                penalty.getJustificationPenalty(),penalty.getFulfilled(),penalty.getForgived(),
                bookTitle,clientFullName,penalty.getCreationDate());
    }
    @Transactional
    public PenaltyAndPenaltyDTO create(PenaltyCreateDTO penaltyCreateDTO){
        System.out.println(penaltyCreateDTO.getBookingLoan());
        Penalty penalty = new Penalty(penaltyCreateDTO.getDescription(),
                "",false,false,penaltyCreateDTO.getType(),
                penaltyCreateDTO.getBookingLoan(), penaltyCreateDTO.getClient());
        this.penaltyRepository.save(penalty);

        String clientEmail = this.clientService.getUserEmailByClient(penalty.getClient());
        String clientFullName = this.clientService.getUserFullNameByClient(penalty.getClient());
        String bookTitle = this.bookingLoanInfoService.getBookTitleByBookingLoan(penalty.getBookingLoan());
        PenaltyDTO penaltyDTO =  new PenaltyDTO(penalty.getId(),penalty.getDescription(),penalty.getType(),
                penalty.getJustificationPenalty(),penalty.getFulfilled(),penalty.getForgived(),
                bookTitle,clientFullName,penalty.getCreationDate());
        return new PenaltyAndPenaltyDTO(
                penalty,
                penaltyDTO,
                clientEmail
        );
    }
    @Transactional
    public void delete(Long id){
        if(this.penaltyRepository.existsById(id)){
            Penalty penalty = this.penaltyRepository.findById(id).get();
            this.penaltyRepository.delete(penalty);
        }
    }
    public PenaltyExistenceDTO getPenaltyByClientIdAndType(Long clientId, String type){
        PenaltyExistenceDTO response = new PenaltyExistenceDTO(false,null);
        if(!this.penaltyRepository.existsByClientIdAndTypeAndForgived(clientId,type,false)){
            return response;
        }
        List<Penalty> penalties = this.penaltyRepository.findByClientIdAndTypeAndForgived(clientId, type, false).get();

        LocalDate today = LocalDate.now();
        Penalty closestPenalty = penalties.stream()
                .min(Comparator.comparing(p -> {
                    LocalDate penaltyDate = p.getCreationDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return Math.abs(ChronoUnit.DAYS.between(penaltyDate, today));
                })).orElse(null);

        response.setExistsPenalty(true);
        response.setPenaltyId(closestPenalty.getId());
        return response;
    }

    public Integer getNumPenaltiesOfClient(Client client){
        return this.penaltyRepository.countByClientId(client.getId());
    }

}
