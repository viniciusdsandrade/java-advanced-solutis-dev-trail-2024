package com.mightyjava.model;

import java.util.Set;
import javax.persistence.*;

import org.springframework.data.jpa.domain.AbstractPersistable;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "roles") // Especificar o nome correto da tabela
public class Role extends AbstractPersistable<Long> {

    private static final long serialVersionUID = -2716348754532601761L;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "role", fetch = LAZY, cascade = ALL)
    private Set<Users> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
