package com.mightyjava.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.data.jpa.domain.AbstractPersistable;

import com.mightyjava.validator.Validate;

import static com.mightyjava.utils.ConstantUtils.CHAR_PATTERN;
import static com.mightyjava.utils.ConstantUtils.CODE_PATTERN;

@Entity
public class Address extends AbstractPersistable<Long> {

    private static final long serialVersionUID = -4863536267672915815L;

    @NotNull
    @Size(min = 10, max = 100, message = "Please enter between {min}-{max} characters")
    private String addressLine;

    @NotNull
    @Size(min = 3, max = 15, message = "Please enter between {min}-{max} characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Please enter only characters")
    private String city;

    @NotNull
    @Size(min = 3, max = 15, message = "Please enter between {min}-{max} characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Please enter only characters")
    private String state;

    @NotNull
    @Size(min = 3, max = 15, message = "Please enter between {min}-{max} characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Please enter only characters")
    private String country;

    @NotNull
    @Size(min = 6, max = 6, message = "Please enter exactly {min} digits")
    @Pattern(regexp = "^\\d{6}$", message = "Please enter exactly {min} digits")
    private String pinCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    public String getAddressLine() {
        return addressLine;
    }
    public String getCity() {
        return city;
    }
    public String getState() {
        return state;
    }
    public String getCountry() {
        return country;
    }
    public Users getUser() {
        return user;
    }
    public String getPinCode() {
        return pinCode;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }
    public void setUser(Users user) {
        this.user = user;
    }
}
