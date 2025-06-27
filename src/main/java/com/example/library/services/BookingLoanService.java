package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.ForbiddenException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.BookingPenaltyLookUpService;
import com.example.library.entities.TemporaryPenaltyLookUpService;
import com.example.library.entities.dto.bookingLoan.BookingLoanCreateDTO;
import com.example.library.entities.dto.bookingLoan.BookingLoanDTO;
import com.example.library.entities.dto.bookingLoan.BookingLoanUpdateDTO;
import com.example.library.entities.dto.penalty.BookingPeriodPenaltyExistenceDTO;
import com.example.library.entities.dto.penalty.TemporaryPeriodPenaltyExistenceDTO;
import com.example.library.entities.model.Book;
import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.repository.BookingLoanRepository;
import com.example.library.services.user.ClientService;
import com.example.library.util.ValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class BookingLoanService {
    private final BookingLoanRepository bookingLoanRepository;
    private final BookService bookService;
    private final ClientService clientService;
    private final TemporaryPenaltyLookUpService temporaryPeriodPenaltyService;
    private final BookingPenaltyLookUpService bookingPeriodPenaltyService;

    public BookingLoanService(BookingLoanRepository bookingLoanRepository,
                              BookService bookService,ClientService clientService,
                              BookingPenaltyLookUpService bookingPeriodPenaltyService,
                              TemporaryPenaltyLookUpService temporaryPeriodPenaltyService){
        this.bookingLoanRepository = bookingLoanRepository;
        this.bookService = bookService;
        this.clientService = clientService;
        this.bookingPeriodPenaltyService=bookingPeriodPenaltyService;
        this.temporaryPeriodPenaltyService = temporaryPeriodPenaltyService;
    }
    public String getBookTitleByBookingLoan(BookingLoan bookingLoan){
        if(!this.bookingLoanRepository.existsById(bookingLoan.getId())){
            throw new NotFoundException("No existe la reserva con el id proporcionado");
        }
        return this.bookService.getBookTitleByBook(bookingLoan.getBook());
    }

    public BookingLoanDTO findById(Long id,Long userId){
        if(!this.bookingLoanRepository.existsById(id)){
            throw new NotFoundException("No existe la reserva con el id proporcionado");
        }

        BookingLoan bookingLoan = this.bookingLoanRepository.findById(id).get();
        this.validateUserIsAdminOrOwnerOfBooking(bookingLoan.getClient(),userId);

        String bookTitle = this.getBookTitleByBookingLoan(bookingLoan);
        String clientName = this.clientService.getUserFullNameByClient(bookingLoan.getClient());
        return new BookingLoanDTO(
                bookingLoan.getBeginDate(),
                bookingLoan.getEndDate(),
                bookingLoan.getCollected(),
                bookingLoan.getReturned(),
                bookTitle,
                clientName
        );
    }
    @Transactional
    public BookingLoanDTO update(Long id, BookingLoanUpdateDTO bookingLoanUpdateDTO){
        if (!this.bookingLoanRepository.existsById(id)){
            throw new NotFoundException("No existe la reserva con el id proporcionado");
        }

        BookingLoan bookingLoan = this.updateBookingLoanData(id,bookingLoanUpdateDTO);
        this.bookingLoanRepository.save(bookingLoan);

        String bookTitle = this.getBookTitleByBookingLoan(bookingLoan);
        String clientName = this.clientService.getUserFullNameByClient(bookingLoan.getClient());
        return new BookingLoanDTO(
                bookingLoan.getBeginDate(),
                bookingLoan.getEndDate(),
                bookingLoan.getCollected(),
                bookingLoan.getReturned(),
                bookTitle,
                clientName
        );
    }
    public BookingLoan updateBookingLoanData(Long id, BookingLoanUpdateDTO bookingLoanUpdateDTO){
        BookingLoan bookingLoan = this.bookingLoanRepository.findById(id).get();
        if (ValidationUtils.isValidAndChangedDate(bookingLoanUpdateDTO.getBeginDate(),bookingLoan.getBeginDate())){
            bookingLoan.setBeginDate(bookingLoanUpdateDTO.getBeginDate());
        }
        if (ValidationUtils.isValidAndChangedDate(bookingLoanUpdateDTO.getEndDate(),bookingLoan.getEndDate())){
            bookingLoan.setEndDate(bookingLoanUpdateDTO.getEndDate());
        }

        if (ValidationUtils.isValidAndChangedBoolean(bookingLoanUpdateDTO.getReturned(),bookingLoan.getReturned())){
            bookingLoan.setReturned(bookingLoanUpdateDTO.getReturned());
        }
        if (ValidationUtils.isValidAndChangedBoolean(bookingLoanUpdateDTO.getCollected(),bookingLoan.getCollected())){
            bookingLoan.setCollected(bookingLoanUpdateDTO.getCollected());
        }
        return bookingLoan;
    }
    @Transactional
    public BookingLoanDTO create(BookingLoanCreateDTO createDTO){
        Book book = this.bookService.getBookByBookId(createDTO.getBookId());
        Client client = this.clientService.getClientByUserId(createDTO.getUserId());
        this.validateBookingLoanCreationDate(book,client,createDTO.getBeginDate());

        Date endDate = this.getEndDateForBooking(client.getId(),createDTO.getBeginDate());
        BookingLoan bookingLoan = new BookingLoan(
                createDTO.getBeginDate(),endDate,false,false,
                book,client
        );
        String bookTitle = this.getBookTitleByBookingLoan(bookingLoan);
        String clientName = this.clientService.getUserFullNameByClient(bookingLoan.getClient());

        return new BookingLoanDTO(bookingLoan.getBeginDate(),bookingLoan.getEndDate(),
                bookingLoan.getCollected(), bookingLoan.getReturned(), bookTitle, clientName
        );
    }
    @Transactional
    public void delete(Long id, Long userId){
        if (!this.bookingLoanRepository.existsById(id)){
            return;
        }
        BookingLoan bookingLoan = this.bookingLoanRepository.findById(id).get();
        Date bookingBeginDate = bookingLoan.getBeginDate();

        if(this.clientService.isClientByUserId(userId) && !isOneDayBeforeBeginDate(bookingBeginDate)){
            throw new ConflictException("Error, la reserva debe ser cancelada con un minimo de" +
                    " 24 horas respecto a la fecha inicial de reserva");
        }

        this.validateUserIsAdminOrOwnerOfBooking(bookingLoan.getClient(),userId);
        this.bookingLoanRepository.delete(bookingLoan);
    }

    public void validateBookingLoanCreationDate(Book book, Client client, Date beginDate){
        TemporaryPeriodPenaltyExistenceDTO temporaryExistenceDTO = this.temporaryPeriodPenaltyService
                .getTemporaryPeriodPenaltyByClientId(client.getId());
        String clientName = this.clientService.getUserFullNameByClient(client);

        if (temporaryExistenceDTO.getExistsPenalty()){
            throw new ConflictException("Error. Actualmente "+ clientName+" dispone de " +
                    "una penalización de reserva hasta el día "+temporaryExistenceDTO.getEndDate().toString());
        }

        if (book.getStock()<1){
            throw new BadRequestException("Error, actualmente no quedan existencias de este libro");
        }

        if(!isOneDayBeforeBeginDate(beginDate)){
            throw new ConflictException("Error, la reserva debe ser creada con un minimo de" +
                    " 24 horas respecto a la fecha inicial de reserva");
        }

    }
    public Date getEndDateForBooking(Long clientId, Date beginDate){
        Integer daysToAdd = 30;
        BookingPeriodPenaltyExistenceDTO bookingExistenceDTO = this.bookingPeriodPenaltyService
                .getBookingPeriodPenaltyByClientId((clientId));

        if(bookingExistenceDTO.getExistsPenalty()){
            daysToAdd = bookingExistenceDTO.getDays();
        }

        LocalDate localBeginDate = beginDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localEndDate = localBeginDate.plusDays(daysToAdd);

        return Date.from(localEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void validateUserIsAdminOrOwnerOfBooking(Client clientOwnerOfBooking, Long userId){
        Boolean isUserLoggedOwnerOfBooking =this.clientService.isClientEqualsByUserIdAndClient(clientOwnerOfBooking,userId);

        if(this.clientService.isClientByUserId(userId) && !isUserLoggedOwnerOfBooking){
            throw new ForbiddenException("No tienes permisos para eliminar esta reserva");
        }
    }

    public boolean isOneDayBeforeBeginDate(Date beginDate){
        LocalDate bookingBeginLocalDate = beginDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        return today.isBefore(bookingBeginLocalDate.minusDays(1));
    }

}
