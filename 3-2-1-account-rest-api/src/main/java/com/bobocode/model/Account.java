package com.bobocode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

import static java.lang.Double.compare;

@NoArgsConstructor
@Setter
@Getter
public class Account implements Cloneable {

    // Getters and Setters
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private double balance;

    // Construtor de cópia
    public Account(Account other) {
        this.id = other.id;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.email = other.email;
        this.balance = other.balance;
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Account clone() {
        Account clone = null;
        try {
            clone = new Account(this);
        } catch (Exception ignored) {
        }
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;

        Account that = (Account) o;

        if (compare(this.balance, that.balance) != 0) return false;
        if (!Objects.equals(this.id, that.id)) return false;
        if (!Objects.equals(this.firstName, that.firstName)) return false;
        if (!Objects.equals(this.lastName, that.lastName)) return false;

        return Objects.equals(this.email, that.email);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = id != null ? id.hashCode() : 0;

        hash *= prime + (firstName != null ? firstName.hashCode() : 0);
        hash *= prime + (lastName != null ? lastName.hashCode() : 0);
        hash *= prime + (email != null ? email.hashCode() : 0);
        hash *= prime + Double.hashCode(balance);

        if (hash < 0) hash = -hash;

        return hash;
    }

    @Override
    public String toString() {
        return "Account{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", balance=" + balance +
               '}';
    }
}