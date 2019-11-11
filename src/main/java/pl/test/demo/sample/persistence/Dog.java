package pl.test.demo.sample.persistence;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "DOG")
@Getter
@Setter
@NoArgsConstructor
public class Dog {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "breed")
    private String breed;

}
