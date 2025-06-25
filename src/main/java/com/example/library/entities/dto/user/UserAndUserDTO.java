package com.example.library.entities.dto.user;

import com.example.library.entities.model.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAndUserDTO {
    private User user;
    private UserDTO userDTO;

    public UserAndUserDTO(User user,UserDTO userDTO){
        this.user = user;
        this.userDTO = userDTO;
    }
}
