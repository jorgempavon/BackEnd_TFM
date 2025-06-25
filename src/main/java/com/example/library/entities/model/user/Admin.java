package com.example.library.entities.model.user;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "admin", schema = "library")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "userid", referencedColumnName = "id", unique = true)
    private User user;

    public Admin(){

    }
    public Admin(Long id,User user){
        this.id = id;
        this.user = user;
    }

}
