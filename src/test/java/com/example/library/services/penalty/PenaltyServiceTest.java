package com.example.library.services.penalty;

import com.example.library.api.exceptions.models.ForbiddenException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.penalty.PenaltyAndPenaltyDTO;
import com.example.library.entities.dto.penalty.PenaltyCreateDTO;
import com.example.library.entities.dto.penalty.PenaltyDTO;
import com.example.library.entities.dto.penalty.PenaltyJustificationDTO;
import com.example.library.entities.model.Book;
import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.model.penalty.Penalty;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.penalty.PenaltyRepository;
import com.example.library.services.BookingLoanService;
import com.example.library.services.admin.AdminService;
import com.example.library.services.client.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PenaltyServiceTest {
    @Mock
    private PenaltyRepository penaltyRepository;
    @Mock
    private ClientService clientService;
    @Mock
    private AdminService adminService;
    @Mock
    private BookingLoanService bookingLoanService;
    @InjectMocks
    private PenaltyService penaltyService;
    private static final Long PENALTY_ID = 2L;
    private static final String PENALTY_DESCRIPTION = "penalty description";
    private static final String PENALTY_JUSTIFICATION = "penalty justification";
    private static final String PENALTY_TYPE = "temporal";
    private static final String USER_NAME = "user name";
    private static final String CLIENT_FULL_NAME = "client full name";
    private static final String USER_LAST_NAME = "User last name";
    private static final String USER_DNI = "11112222Y";
    private static final String USER_EMAIL = "test@example.com";
    private static final Long USER_ID = 7L;

    private static final User USER = new User(
            USER_ID,
            USER_NAME,
            USER_DNI,
            USER_EMAIL,
            USER_LAST_NAME
    );
    private static final Long CLIENT_ID = 5L;
    private static final Client CLIENT = new Client(
            CLIENT_ID,
            USER
    );
    private static final String BOOK_TITLE = "Titulo de libro";
    private static final Book BOOK = new Book("9781234567890", BOOK_TITLE,"Qr",new  Date()
            ,5, "Fantasía", "Lorenzo");
    private static final BookingLoan BOOKING_LOAN = new BookingLoan(
            1L,new Date(),new Date(System.currentTimeMillis() + 86400000), false, false,BOOK,CLIENT
    );

    private static final Penalty FIND_PENALTY = new Penalty(
            PENALTY_ID,
            PENALTY_DESCRIPTION,
            PENALTY_JUSTIFICATION,
            false,
            false,
            PENALTY_TYPE,
            BOOKING_LOAN,
            CLIENT

    );
    private static final Penalty PENALTY = new Penalty(
            PENALTY_ID,
            PENALTY_DESCRIPTION,
            PENALTY_JUSTIFICATION,
            false,
            false,
            PENALTY_TYPE,
            BOOKING_LOAN,
            CLIENT

    );
    private static final PenaltyCreateDTO PENALTY_CREATE_DTO = new PenaltyCreateDTO(
            PENALTY_DESCRIPTION,PENALTY_TYPE,BOOKING_LOAN,CLIENT
    );

    private static final PenaltyJustificationDTO PENALTY_JUSTIFICATION_DTO = new PenaltyJustificationDTO(
            PENALTY_JUSTIFICATION
    );
    @Test
    void findPenaltyById_UserIsAdmin_successful(){
        when(this.penaltyRepository.existsById(PENALTY_ID)).thenReturn(true);
        when(this.penaltyRepository.findById(PENALTY_ID)).thenReturn(Optional.of(FIND_PENALTY));
        when(this.adminService.isAdminByUserId(USER_ID)).thenReturn(true);
        when(this.clientService.getUserFullNameByClient(CLIENT)).thenReturn(CLIENT_FULL_NAME);
        when(this.bookingLoanService.getBookTitleByBookingLoan(BOOKING_LOAN)).thenReturn(BOOK_TITLE);

        PenaltyDTO responsePenaltyDTO = this.penaltyService.findById(PENALTY_ID,USER_ID);

        assertEquals(responsePenaltyDTO.getId(),PENALTY_ID);
        assertEquals(responsePenaltyDTO.getDescription(),PENALTY_DESCRIPTION);
        assertEquals(responsePenaltyDTO.getType(),PENALTY_TYPE);
        assertEquals(responsePenaltyDTO.getJustificationPenalty(),PENALTY_JUSTIFICATION);
        assertEquals(responsePenaltyDTO.getFulfilled(),false);
        assertEquals(responsePenaltyDTO.getForgived(),false);
    }

    @Test
    void findPenaltyById_UserIsNotAdmin_successful(){
        when(this.penaltyRepository.existsById(PENALTY_ID)).thenReturn(true);
        when(this.penaltyRepository.findById(PENALTY_ID)).thenReturn(Optional.of(FIND_PENALTY));
        when(this.adminService.isAdminByUserId(USER_ID)).thenReturn(false);
        when(this.clientService.isClientEqualsByUserIdAndClient(CLIENT,USER_ID)).thenReturn(true);
        when(this.clientService.getUserFullNameByClient(CLIENT)).thenReturn(CLIENT_FULL_NAME);
        when(this.bookingLoanService.getBookTitleByBookingLoan(BOOKING_LOAN)).thenReturn(BOOK_TITLE);

        PenaltyDTO responsePenaltyDTO = this.penaltyService.findById(PENALTY_ID,USER_ID);

        assertEquals(responsePenaltyDTO.getId(),PENALTY_ID);
        assertEquals(responsePenaltyDTO.getDescription(),PENALTY_DESCRIPTION);
        assertEquals(responsePenaltyDTO.getType(),PENALTY_TYPE);
        assertEquals(responsePenaltyDTO.getJustificationPenalty(),PENALTY_JUSTIFICATION);
        assertEquals(responsePenaltyDTO.getFulfilled(),false);
        assertEquals(responsePenaltyDTO.getForgived(),false);
    }
    @Test
    void findPenaltyById_whenNotExistsId_throwNotFoundException(){
        when(this.penaltyRepository.existsById(PENALTY_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            penaltyService.findById(PENALTY_ID,USER_ID);
        });
    }
    @Test
    void findPenaltyById_whenUserIsNotAdminAndNotOwner_throwForbiddenException(){
        when(this.penaltyRepository.existsById(PENALTY_ID)).thenReturn(true);
        when(this.penaltyRepository.findById(PENALTY_ID)).thenReturn(Optional.of(FIND_PENALTY));
        when(this.adminService.isAdminByUserId(USER_ID)).thenReturn(false);
        when(this.clientService.isClientEqualsByUserIdAndClient(CLIENT,USER_ID)).thenReturn(false);
        assertThrows(ForbiddenException.class, () -> {
            penaltyService.findById(PENALTY_ID,USER_ID);
        });
    }
    @Test
    void findPenaltiesByUserAndFulfilled(){
        List<Penalty> mockPenalties = List.of(FIND_PENALTY);

        when(this.penaltyRepository.findAll(any(Specification.class))).thenReturn(mockPenalties);
        when(this.clientService.getUserFullNameByClient(CLIENT)).thenReturn(CLIENT_FULL_NAME);
        when(this.bookingLoanService.getBookTitleByBookingLoan(BOOKING_LOAN)).thenReturn(BOOK_TITLE);
        List<PenaltyDTO> result = penaltyService.findByUserAndFulfilled(USER_ID, false);

        assertEquals(1, result.size());
        assertEquals(result.get(0).getId(),PENALTY_ID);
        assertEquals(result.get(0).getDescription(),PENALTY_DESCRIPTION);
        assertEquals(result.get(0).getType(),PENALTY_TYPE);
        assertEquals(result.get(0).getJustificationPenalty(),PENALTY_JUSTIFICATION);
        assertEquals(result.get(0).getFulfilled(),false);
        assertEquals(result.get(0).getForgived(),false);
        assertEquals(result.get(0).getBookTitle(),BOOK_TITLE);
        assertEquals(result.get(0).getClientName(),CLIENT_FULL_NAME);
    }

    @Test
    void forgivePenalty_successful(){
        when(this.penaltyRepository.existsById(PENALTY_ID)).thenReturn(true);
        when(this.penaltyRepository.findById(PENALTY_ID)).thenReturn(Optional.of(PENALTY));
        when(this.clientService.getUserFullNameByClient(CLIENT)).thenReturn(CLIENT_FULL_NAME);
        when(this.bookingLoanService.getBookTitleByBookingLoan(BOOKING_LOAN)).thenReturn(BOOK_TITLE);

        PenaltyDTO responsePenaltyDTO = this.penaltyService.forgivePenalty(PENALTY_ID,PENALTY_JUSTIFICATION_DTO);

        assertEquals(responsePenaltyDTO.getId(),PENALTY_ID);
        assertEquals(responsePenaltyDTO.getDescription(),PENALTY_DESCRIPTION);
        assertEquals(responsePenaltyDTO.getType(),PENALTY_TYPE);
        assertEquals(responsePenaltyDTO.getJustificationPenalty(),PENALTY_JUSTIFICATION);
        assertEquals(responsePenaltyDTO.getFulfilled(),true);
        assertEquals(responsePenaltyDTO.getForgived(),true);
    }

    @Test
    void forgivePenalty_whenPenaltyNotExists_NotFoundException(){
        when(this.penaltyRepository.existsById(PENALTY_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            penaltyService.forgivePenalty(PENALTY_ID,PENALTY_JUSTIFICATION_DTO);
        });
    }

    @Test
    void fulfillPenalty_whenUserIsAdmin_successful(){
        when(this.penaltyRepository.existsById(PENALTY_ID)).thenReturn(true);
        when(this.penaltyRepository.findById(PENALTY_ID)).thenReturn(Optional.of(PENALTY));
        when(this.adminService.isAdminByUserId(USER_ID)).thenReturn(true);
        when(this.clientService.getUserFullNameByClient(CLIENT)).thenReturn(CLIENT_FULL_NAME);
        when(this.bookingLoanService.getBookTitleByBookingLoan(BOOKING_LOAN)).thenReturn(BOOK_TITLE);

        PenaltyDTO responsePenaltyDTO = this.penaltyService.fulfillPenalty(PENALTY_ID,PENALTY_JUSTIFICATION_DTO,USER_ID);

        assertEquals(responsePenaltyDTO.getId(),PENALTY_ID);
        assertEquals(responsePenaltyDTO.getDescription(),PENALTY_DESCRIPTION);
        assertEquals(responsePenaltyDTO.getType(),PENALTY_TYPE);
        assertEquals(responsePenaltyDTO.getJustificationPenalty(),PENALTY_JUSTIFICATION);
        assertEquals(responsePenaltyDTO.getFulfilled(),true);
        assertEquals(responsePenaltyDTO.getForgived(),false);
    }
    @Test
    void fulfillPenalty_whenClientIsOwnerOfPenalty_successful(){
        when(this.penaltyRepository.existsById(PENALTY_ID)).thenReturn(true);
        when(this.penaltyRepository.findById(PENALTY_ID)).thenReturn(Optional.of(PENALTY));
        when(this.adminService.isAdminByUserId(USER_ID)).thenReturn(false);
        when(this.clientService.isClientEqualsByUserIdAndClient(CLIENT,USER_ID)).thenReturn(true);
        when(this.clientService.getUserFullNameByClient(CLIENT)).thenReturn(CLIENT_FULL_NAME);
        when(this.bookingLoanService.getBookTitleByBookingLoan(BOOKING_LOAN)).thenReturn(BOOK_TITLE);

        PenaltyDTO responsePenaltyDTO = this.penaltyService.fulfillPenalty(PENALTY_ID,PENALTY_JUSTIFICATION_DTO,USER_ID);

        assertEquals(responsePenaltyDTO.getId(),PENALTY_ID);
        assertEquals(responsePenaltyDTO.getDescription(),PENALTY_DESCRIPTION);
        assertEquals(responsePenaltyDTO.getType(),PENALTY_TYPE);
        assertEquals(responsePenaltyDTO.getJustificationPenalty(),PENALTY_JUSTIFICATION);
        assertEquals(responsePenaltyDTO.getFulfilled(),true);
        assertEquals(responsePenaltyDTO.getForgived(),false);
    }
    @Test
    void fulfillPenalty_whenUserIsNotAdminAndNotOwner_throwForbiddenException(){
        when(this.penaltyRepository.existsById(PENALTY_ID)).thenReturn(true);
        when(this.penaltyRepository.findById(PENALTY_ID)).thenReturn(Optional.of(PENALTY));
        when(this.adminService.isAdminByUserId(USER_ID)).thenReturn(false);
        when(this.clientService.isClientEqualsByUserIdAndClient(CLIENT,USER_ID)).thenReturn(false);
        assertThrows(ForbiddenException.class, () -> {
            penaltyService.fulfillPenalty(PENALTY_ID,PENALTY_JUSTIFICATION_DTO,USER_ID);
        });
    }
    @Test
    void fulfillPenalty_whenPenaltyNotExists_NotFoundException(){
        when(this.penaltyRepository.existsById(PENALTY_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            penaltyService.fulfillPenalty(PENALTY_ID,PENALTY_JUSTIFICATION_DTO,USER_ID);
        });
    }

    @Test
    void createPenalty_successful(){
        when(this.clientService.getUserFullNameByClient(CLIENT)).thenReturn(CLIENT_FULL_NAME);
        when(this.clientService.getUserEmailByClient(CLIENT)).thenReturn(USER_EMAIL);
        when(this.bookingLoanService.getBookTitleByBookingLoan(BOOKING_LOAN)).thenReturn(BOOK_TITLE);

        PenaltyAndPenaltyDTO responseCreatePenalty = this.penaltyService.create(PENALTY_CREATE_DTO);
        PenaltyDTO penaltyDTO = responseCreatePenalty.getPenaltyDTO();
        Penalty penalty = responseCreatePenalty.getPenalty();

        assertEquals(penaltyDTO.getDescription(),PENALTY_DESCRIPTION);
        assertEquals(penaltyDTO.getType(),PENALTY_TYPE);
        assertEquals(penaltyDTO.getJustificationPenalty(),"");
        assertEquals(penaltyDTO.getFulfilled(),false);
        assertEquals(penaltyDTO.getForgived(),false);

        assertEquals(penalty.getDescription(),PENALTY_DESCRIPTION);
        assertEquals(penalty.getType(),PENALTY_TYPE);
        assertEquals(penalty.getJustificationPenalty(),"");
        assertEquals(penalty.getFulfilled(),false);
        assertEquals(penalty.getForgived(),false);
    }
    @Test
    void delete_NotExistsPenalty_successful(){
        when(this.penaltyRepository.existsById(PENALTY_ID)).thenReturn(false);
        this.penaltyService.delete(PENALTY_ID);
    }
    @Test
    void delete_ExistsPenalty_successful(){
        when(this.penaltyRepository.existsById(PENALTY_ID)).thenReturn(true);
        when(this.penaltyRepository.findById(PENALTY_ID)).thenReturn(Optional.of(PENALTY));
        this.penaltyService.delete(PENALTY_ID);
    }
}
