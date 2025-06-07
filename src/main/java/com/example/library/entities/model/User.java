package com.example.library.entities.model;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "users", schema = "library")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Dni", nullable = false, unique = true, length = 9)
    private String dni;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "Password", nullable = false, length = 100)
    private String password;

    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Column(name = "LastName", length = 100)
    private String lastName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserDTO getUserDTO(){
        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setId(this.id);
        newUserDTO.setDni(this.dni);
        newUserDTO.setEmail(this.email);
        newUserDTO.setName(this.name);
        newUserDTO.setLastName(this.lastName);
        return newUserDTO;
    }

    public void updateFromUserDTO(UserDTO userDTO){
        this.dni = userDTO.getDni();
        this.email = userDTO.getEmail();
        this.name = userDTO.getName();
        this.lastName = userDTO.getLastName();
    }
    public void updateFromUserRegisterDTO(UserRegisterDTO userRegisterDTO){
        this.dni = userRegisterDTO.getDni();
        this.email = userRegisterDTO.getEmail();
        this.name = userRegisterDTO.getName();
        this.lastName = userRegisterDTO.getLastName();
        this.password = userRegisterDTO.getPassword();
    }
}
