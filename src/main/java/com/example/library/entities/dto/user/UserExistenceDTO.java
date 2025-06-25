package com.example.library.entities.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserExistenceDTO {
    private Boolean statusEmail;
    private Boolean status;
    private Boolean statusDni;
    private String message;
    private Long idUserByEmail;
    private Long idUserByDni;

    public UserExistenceDTO(){
        this.message = "";
        this.statusEmail = false;
        this.statusDni = false;
        this.status = false;
    }
}
