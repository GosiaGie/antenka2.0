package pl.volleylove.antenka.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import pl.volleylove.antenka.enums.Role;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "user")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userID;
    private String email;
    @JsonIgnore
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private LocalDate birthday;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne
            (cascade = CascadeType.ALL, mappedBy = "user")
    private PlayerProfile playerProfile;

    @OneToMany(mappedBy = "organizer")
    private Set<Event> eventsOrganized;

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthday=" + birthday +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userID, user.userID) && Objects.equals(email, user.email)
                && Objects.equals(password, user.password) && Objects.equals(firstName, user.firstName)
                && Objects.equals(lastName, user.lastName) && Objects.equals(birthday, user.birthday) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, email, password, firstName, lastName, birthday, role);
    }
}




