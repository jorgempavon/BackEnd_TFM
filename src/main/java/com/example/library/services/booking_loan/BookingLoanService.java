package com.example.library.services.bookingLoan;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.ForbiddenException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.BookingPenaltyLookUpService;
import com.example.library.entities.TemporaryPenaltyLookUpService;
import com.example.library.entities.dto.bookingLoan.BookingLoanCreateDTO;
import com.example.library.entities.dto.bookingLoan.BookingLoanDTO;
import com.example.library.entities.dto.bookingLoan.BookingLoanUpdateDTO;
import com.example.library.entities.dto.penalty.*;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.dto.rule.RuleExistenceDTO;
import com.example.library.entities.dto.user.UserDTO;
import com.example.library.entities.model.Book;
import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.model.rule.BookingPeriodRule;
import com.example.library.entities.model.rule.TemporaryPeriodRule;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.BookingLoanRepository;
import com.example.library.services.BookService;
import com.example.library.services.EmailService;
import com.example.library.services.penalty.PenaltyService;
import com.example.library.services.rule.BookingPeriodRuleInfoService;
import com.example.library.services.rule.TemporaryPeriodRuleInfoService;
import com.example.library.services.user.ClientService;
import com.example.library.util.ValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookingLoanService {
    private final BookingLoanRepository bookingLoanRepository;
    private final BookService bookService;
    private final ClientService clientService;
    private final PenaltyService penaltyService;
    private final TemporaryPenaltyLookUpService temporaryPeriodPenaltyService;
    private final BookingPenaltyLookUpService bookingPeriodPenaltyService;
    private final BookingPeriodRuleInfoService bookingPeriodRuleInfoService;
    private final TemporaryPeriodRuleInfoService temporaryPeriodRuleInfoService;

    private final EmailService emailService;

    public BookingLoanService(BookingLoanRepository bookingLoanRepository,
                              BookService bookService,ClientService clientService,
                              BookingPenaltyLookUpService bookingPeriodPenaltyService,
                              TemporaryPenaltyLookUpService temporaryPeriodPenaltyService,
                              BookingPeriodRuleInfoService bookingPeriodRuleInfoService,
                              TemporaryPeriodRuleInfoService temporaryPeriodRuleInfoService,
                              EmailService emailService,PenaltyService penaltyService){
        this.bookingLoanRepository = bookingLoanRepository;
        this.bookService = bookService;
        this.clientService = clientService;
        this.emailService = emailService;
        this.bookingPeriodPenaltyService = bookingPeriodPenaltyService;
        this.bookingPeriodRuleInfoService = bookingPeriodRuleInfoService;
        this.temporaryPeriodRuleInfoService = temporaryPeriodRuleInfoService;
        this.penaltyService = penaltyService;
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
    public List<BookingLoanDTO> findByUserId(Long userId){
        Long clientId = this.clientService.getClientIdByUserId(userId);
        List<BookingLoanDTO> responseList= new ArrayList<>();

        if (!this.bookingLoanRepository.existsByClientId(clientId)){
            return responseList;
        }

        List<BookingLoan> bookingLoanList = this.bookingLoanRepository.findByClientId(clientId).get();
        for (BookingLoan bookingLoan : bookingLoanList) {
            String bookTitle = this.getBookTitleByBookingLoan(bookingLoan);
            String clientName = this.clientService.getUserFullNameByClient(bookingLoan.getClient());
            BookingLoanDTO bookingLoanDTO = new BookingLoanDTO(
                    bookingLoan.getBeginDate(),
                    bookingLoan.getEndDate(),
                    bookingLoan.getCollected(),
                    bookingLoan.getReturned(),
                    bookTitle,
                    clientName
            );
            responseList.add(bookingLoanDTO);
        }
        return responseList;
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

        if (ValidationUtils.isValidAndChangedBoolean(bookingLoanUpdateDTO.getReturned(),bookingLoan.getReturned())){
            if (bookingLoanUpdateDTO.getReturned()){
                this.checkReturnedBookingHasPenalties(bookingLoan);
            }
            bookingLoan.setReturned(bookingLoanUpdateDTO.getReturned());
        }
        if (ValidationUtils.isValidAndChangedDate(bookingLoanUpdateDTO.getBeginDate(),bookingLoan.getBeginDate())){
            bookingLoan.setBeginDate(bookingLoanUpdateDTO.getBeginDate());
        }
        if (ValidationUtils.isValidAndChangedDate(bookingLoanUpdateDTO.getEndDate(),bookingLoan.getEndDate())){
            bookingLoan.setEndDate(bookingLoanUpdateDTO.getEndDate());
        }
        if (ValidationUtils.isValidAndChangedBoolean(bookingLoanUpdateDTO.getCollected(),bookingLoan.getCollected())){
            bookingLoan.setCollected(bookingLoanUpdateDTO.getCollected());
        }
        return bookingLoan;
    }
    public void checkReturnedBookingHasPenalties(BookingLoan bookingLoan){
        LocalDate bookingEndLocalDate = bookingLoan.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        if (!today.isAfter(bookingEndLocalDate)){
            return;
        }
        Integer numPenaltiesOfClient = this.penaltyService.getNumPenaltiesOfClient(bookingLoan.getClient());
        this.checkBookingPeriodPenalties(numPenaltiesOfClient,bookingLoan);
        this.checkTemporaryPeriodPenalties(numPenaltiesOfClient,bookingLoan);
    }
    public void checkBookingPeriodPenalties(Integer numPenalties,BookingLoan bookingLoan){
        RuleExistenceDTO existsBookingRuleDTO = this.bookingPeriodRuleInfoService.findByNumPenalties(numPenalties);
        if(existsBookingRuleDTO.getExistsRule()){
            RuleDTO ruleDTO = existsBookingRuleDTO.getRuleDTO();
            PenaltyCreateDTO penaltyCreateDTO = new PenaltyCreateDTO(ruleDTO.getName(),ruleDTO.getType(),
                    bookingLoan,bookingLoan.getClient());

            BookingPeriodRule bookingPeriodRule = this.bookingPeriodRuleInfoService.findById(ruleDTO.getId());
            BookingPeriodPenaltyCreateDTO bookingPenaltyCreateDTO = new BookingPeriodPenaltyCreateDTO(
                    penaltyCreateDTO,ruleDTO.getDays(),bookingPeriodRule
            );
            this.bookingPeriodPenaltyService.create(bookingPenaltyCreateDTO);
        }
    }
    public void checkTemporaryPeriodPenalties(Integer numPenalties,BookingLoan bookingLoan){
        RuleExistenceDTO existsTemporaryRuleDTO = this.temporaryPeriodRuleInfoService.findByNumPenalties(numPenalties);
        if(existsTemporaryRuleDTO.getExistsRule()){
            RuleDTO ruleDTO = existsTemporaryRuleDTO.getRuleDTO();
            PenaltyCreateDTO penaltyCreateDTO = new PenaltyCreateDTO(ruleDTO.getName(),ruleDTO.getType(),
                    bookingLoan,bookingLoan.getClient());

            TemporaryPeriodRule temporaryPeriodRule = this.temporaryPeriodRuleInfoService.findById(ruleDTO.getId());
            LocalDate localDate = LocalDate.now().plusDays(ruleDTO.getDays());
            Date endDatePenalty = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            TemporaryPeriodPenaltyCreateDTO temporaryPeriodPenaltyCreateDTO = new TemporaryPeriodPenaltyCreateDTO(
                    penaltyCreateDTO,endDatePenalty,temporaryPeriodRule
            );
            this.temporaryPeriodPenaltyService.create(temporaryPeriodPenaltyCreateDTO);
        }
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

        String bookTitle = this.bookService.getBookTitleByBook(book);
        String clientName = this.clientService.getUserFullNameByClient(bookingLoan.getClient());
        String clientEmail = this.clientService.getUserEmailByClient(client);
        this.emailService.sendBookingLoanPenaltyEmail(clientEmail,clientName,bookTitle,
                createDTO.getBeginDate(),endDate);

        this.bookingLoanRepository.save(bookingLoan);
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

        if(this.clientService.isClientByUserId(userId) && !isTodayDayBeforeBeginDate(bookingBeginDate)){
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

        if(!isTodayDayBeforeBeginDate(beginDate)){
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

    public boolean isTodayDayBeforeBeginDate(Date beginDate){
        LocalDate bookingBeginLocalDate = beginDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        return today.isBefore(bookingBeginLocalDate.minusDays(1));
    }

}
