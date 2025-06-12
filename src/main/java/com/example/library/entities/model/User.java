package com.example.library.entities.model;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.entities.dto.UserSaveDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users", schema = "library")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dni", nullable = false, unique = true, length = 9)
    private String dni;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "lastname", length = 100)
    private String lastName;

    public User(){
    }
    public User(String name,String dni,String email,String lastName){
        this.name=name;
        this.dni=dni;
        this.email=email;
        this.lastName=lastName;
    }
}
