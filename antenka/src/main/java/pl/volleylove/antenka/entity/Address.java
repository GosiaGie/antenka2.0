package pl.volleylove.antenka.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.volleylove.antenka.enums.AddressType;
import pl.volleylove.antenka.map.resultcomponents.Location;

import java.util.Objects;
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addressID", nullable = false)
    private Long addressID;

    @Column(name = "address_type")
    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    @Column(name = "street")
    private String street;

    @Column(name = "number")
    private String number;

    @Column(name = "flat_number")
    private String flatNumber;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "locality")
    private String locality;

    @Embedded
    private Location location;

    @Column(name = "description")
    private String description;

    @Override
    public String toString() {
        return "Address{" +
                "addressID=" + addressID +
                ", addressType=" + addressType +
                ", street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", flatNumber='" + flatNumber + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", locality='" + locality + '\'' +
                ", location=" + location +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(addressID, address.addressID) && addressType == address.addressType && Objects.equals(street, address.street) && Objects.equals(number, address.number) && Objects.equals(flatNumber, address.flatNumber) && Objects.equals(zipCode, address.zipCode) && Objects.equals(locality, address.locality) && Objects.equals(location, address.location) && Objects.equals(description, address.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressID, addressType, street, number, flatNumber, zipCode, locality, location, description);
    }
}
