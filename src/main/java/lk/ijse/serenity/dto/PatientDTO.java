package lk.ijse.serenity.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class PatientDTO {
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate dob;
    private String emergencyContact;
}
