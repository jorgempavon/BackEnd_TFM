package com.example.library.entities.dto.penalty;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PenaltyDTO {
    private Long id;

    private String description;

    private String justificationPenalty;

    private Boolean fulfilled;

    private Boolean forgived;

    private Date creationDate;

    private String type;

    private String bookTitle;

    private String clientName;

    public PenaltyDTO(){

    }

    public PenaltyDTO(String description,String type,String justificationPenalty,Boolean fulfilled,
                      Boolean forgived, String bookTitle, String clientName,Date creationDate){
        this.description = description;
        this.justificationPenalty = justificationPenalty;
        this.fulfilled = fulfilled;
        this.forgived = forgived;
        this.bookTitle = bookTitle;
        this.clientName = clientName;
        this.type = type;
        this.creationDate = creationDate;
    }

    public PenaltyDTO(Long id, String description,String type,String justificationPenalty,
                      Boolean fulfilled, Boolean forgived, String bookTitle, String clientName,Date creationDate){
        this.id = id;
        this.description = description;
        this.justificationPenalty = justificationPenalty;
        this.fulfilled = fulfilled;
        this.forgived = forgived;
        this.bookTitle = bookTitle;
        this.clientName = clientName;
        this.type = type;
        this.creationDate = creationDate;
    }
}
