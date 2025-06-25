package com.example.library.services.penalty;

import com.example.library.api.exceptions.models.ForbiddenException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.penalty.PenaltyAndPenaltyDTO;
import com.example.library.entities.dto.penalty.PenaltyCreateDTO;
import com.example.library.entities.dto.penalty.PenaltyDTO;
import com.example.library.entities.dto.penalty.PenaltyJustificationDTO;
import com.example.library.entities.model.penalty.Penalty;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.repository.penalty.PenaltyRepository;
import com.example.library.services.BookingLoanService;
import com.example.library.services.EmailService;
import com.example.library.services.user.AdminService;
import com.example.library.services.user.ClientService;
import com.example.library.util.ValidationUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PenaltyService {
    private final PenaltyRepository penaltyRepository;
    private final ClientService clientService;
    private final AdminService adminService;
    private final BookingLoanService bookingLoanService;


    public PenaltyService(PenaltyRepository penaltyRepository,ClientService clientService,
                          BookingLoanService bookingLoanService,AdminService adminService,
                          EmailService emailService){
        this.penaltyRepository = penaltyRepository;
        this.clientService = clientService;
        this.bookingLoanService = bookingLoanService;
        this.adminService = adminService;
    }

    public PenaltyDTO findById(Long id,Long userId){
        if(!this.penaltyRepository.existsById(id)){
            throw new NotFoundException("No existe ninguna penalización con el id: "+id.toString());
        }
        Penalty penalty = this.penaltyRepository.findById(id).get();
        Client client =  penalty.getClient();
        Boolean isUserLoggedOwnerOfPenalty = this.clientService.isClientEqualsByUserIdAndClient(client,userId);

        if(!this.adminService.isAdminByUserId(userId) && !isUserLoggedOwnerOfPenalty){
            throw new ForbiddenException("No tienes permisos para  acceder a la penalización del id proporcionado");
        }

        String clientFullName = this.clientService.getUserFullNameByClient(penalty.getClient());
        String bookTitle = this.bookingLoanService.getBookTitleByBookingLoan(penalty.getBookingLoan());
        return new PenaltyDTO(penalty.getId(),penalty.getDescription(),penalty.getType(),
                penalty.getJustificationPenalty(),penalty.getFulfilled(),penalty.getForgived(),
                bookTitle,clientFullName);
    }

    public List<PenaltyDTO> findByUserAndFulfilled(Long userId, Boolean fulfilled) {
        Specification<Penalty> spec = Specification.where(null);
        Long clientId = this.clientService.getClientIdByUserId(userId);

        spec = ValidationUtils.buildQueryLongByField(spec,"clientId",clientId);
        spec = ValidationUtils.buildQueryBooleanByField(spec,"fulfilled",fulfilled);

        List<Penalty> penalties = this.penaltyRepository.findAll(spec);
        List<PenaltyDTO> responseList= new ArrayList<>();

        for (Penalty penalty : penalties) {
            String clientFullName = this.clientService.getUserFullNameByClient(penalty.getClient());
            String bookTitle = this.bookingLoanService.getBookTitleByBookingLoan(penalty.getBookingLoan());

            PenaltyDTO penaltyDTO = new PenaltyDTO(penalty.getId(),penalty.getDescription(),penalty.getType(),
                    penalty.getJustificationPenalty(),penalty.getFulfilled(),penalty.getForgived(),
                    bookTitle,clientFullName);
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
        String bookTitle = this.bookingLoanService.getBookTitleByBookingLoan(penalty.getBookingLoan());

        return new PenaltyDTO(penalty.getId(),penalty.getDescription(),penalty.getType(),
                penalty.getJustificationPenalty(),penalty.getFulfilled(),penalty.getForgived(),
                bookTitle,clientFullName);
    }
    @Transactional
    public PenaltyDTO fulfillPenalty(Long id, PenaltyJustificationDTO penaltyJustificationDTO,Long userId){
        if(!this.penaltyRepository.existsById(id)){
            throw new NotFoundException("No existe la penalizacion con el id proporcionado");
        }
        Penalty penalty = this.penaltyRepository.findById(id).get();
        Client client =  penalty.getClient();
        Boolean isUserLoggedOwnerOfPenalty = this.clientService.isClientEqualsByUserIdAndClient(client,userId);

        if(!this.adminService.isAdminByUserId(userId) && !isUserLoggedOwnerOfPenalty){
            throw new ForbiddenException("No tienes permisos para cumplimentar a la penalización del id proporcionado");
        }
        penalty.setJustificationPenalty(penaltyJustificationDTO.getJustificationPenalty());
        penalty.setFulfilled(true);
        this.penaltyRepository.save(penalty);

        String clientFullName = this.clientService.getUserFullNameByClient(penalty.getClient());
        String bookTitle = this.bookingLoanService.getBookTitleByBookingLoan(penalty.getBookingLoan());

        return new PenaltyDTO(penalty.getId(),penalty.getDescription(),penalty.getType(),
                penalty.getJustificationPenalty(),penalty.getFulfilled(),penalty.getForgived(),
                bookTitle,clientFullName);
    }
    @Transactional
    public PenaltyAndPenaltyDTO create(PenaltyCreateDTO penaltyCreateDTO){
        Penalty penalty = new Penalty(penaltyCreateDTO.getDescription(),
                "",false,false,penaltyCreateDTO.getType(),
                penaltyCreateDTO.getBookingLoan(), penaltyCreateDTO.getClient());
        this.penaltyRepository.save(penalty);

        String clientEmail = this.clientService.getUserEmailByClient(penalty.getClient());
        String clientFullName = this.clientService.getUserFullNameByClient(penalty.getClient());
        String bookTitle = this.bookingLoanService.getBookTitleByBookingLoan(penalty.getBookingLoan());
        PenaltyDTO penaltyDTO =  new PenaltyDTO(penalty.getId(),penalty.getDescription(),penalty.getType(),
                penalty.getJustificationPenalty(),penalty.getFulfilled(),penalty.getForgived(),
                bookTitle,clientFullName);
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
}
