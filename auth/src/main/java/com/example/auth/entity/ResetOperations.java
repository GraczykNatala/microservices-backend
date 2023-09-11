package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Table(name = "resetoperations")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetOperations {

    @Id
    @GeneratedValue(generator = "resetoperations_id_seq",
            strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "resetoperations_id_seq",
            sequenceName = "resetoperations_id_seq",
            allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name = "users")
    private User user;

    @Column(name = "createdate")
    @Temporal(TemporalType.TIMESTAMP) // Określa, że to pole jest typu timestamp
    private Date createDate;

    private String uid;
}
