package lk.ijse.serenity.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder

public class TherapistDTO {
    private String name;
    private String specialization;
    private String email;
    private String phone;
    private String availability;
    private String qualification;
}
