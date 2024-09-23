package br.com.agilizeware.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
//@Table(name="user_profile", catalog="agilize_security")
@Table(name="user_profile", catalog="tv8lo3nb8etoit7u")
@PrimaryKeyJoinColumn(name = "fk_user", referencedColumnName = "id")
public class Profile extends User {
	
	private static final long serialVersionUID = -804515587807469287L;
	
	//userId, email, firstName, lastName, name, username, imageUrl
	@Column(name="user_id")
    private String userId;
    @Column(name="first_name")
    private String firstName;
    @Column(name="last_name")
    private String lastName;
    @Column(name="image_url")
    private String imageUrl;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}