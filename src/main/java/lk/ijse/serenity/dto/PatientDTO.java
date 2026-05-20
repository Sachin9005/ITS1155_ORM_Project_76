package lk.ijse.serenity.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder

public class PatientDTO {
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate dob;
    private LocalDate registrationDate;
    private String emergencyContact;
}
