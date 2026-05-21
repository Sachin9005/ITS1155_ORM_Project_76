package lk.ijse.serenity.dto;

import lk.ijse.serenity.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class UserDTO {
    private Long id;
    private String username;
    private String passwordHash;
    private User.Role role;
    private String fullName;
    private String email;

}
