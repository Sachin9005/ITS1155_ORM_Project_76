package lk.ijse.serenity.bo;

import lk.ijse.serenity.dao.UserDAOImpl;
import lk.ijse.serenity.dto.UserDTO;
import lk.ijse.serenity.entity.User;
import lk.ijse.serenity.exception.DuplicateRegistrationException;
import lk.ijse.serenity.exception.InvalidCredentialsException;
import lk.ijse.serenity.exception.SerenityException;
import lk.ijse.serenity.util.Converter;
import lk.ijse.serenity.util.Validator;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class UserBOImpl {

    private UserDAOImpl userDAOImpl = new UserDAOImpl();

    private UserDTO currentUser;

    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public boolean verifyPassword(String plainPassword, String hash) {
        return BCrypt.checkpw(plainPassword, hash);
    }

    public UserDTO login(String username, String password) {
        Optional<User> userOpt = userDAOImpl.findByUsername(username.trim());

        if (userOpt.isEmpty()) {
            throw new InvalidCredentialsException();
        }
        User user = userOpt.get();
        if (!verifyPassword(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
        this.currentUser = userDTO;
        return userDTO;
    }

    public boolean saveUser(UserDTO userDTO) {
        User user = User.builder()
                .username(userDTO.getUsername())
                .passwordHash(userDTO.getPasswordHash())
                .role(userDTO.getRole())
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .build();
        return userDAOImpl.save(user);
    }

    public boolean deleteUser(UserDTO userDTO) {
        User user = User.builder()
                .id(userDTO.getId())
                .build();
        return userDAOImpl.delete(user);
    }

    public void logout() {
        this.currentUser = null;
    }

    public UserDTO getCurrentUser() {
        if (currentUser == null) {
            return null;
        }
        return new UserDTO(currentUser.getId(), currentUser.getUsername(), currentUser.getPasswordHash(), currentUser.getRole(), currentUser.getFullName(), currentUser.getEmail());
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.Role.ADMIN;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userDAOImpl.getAll();
        return users.stream().map(u -> new UserDTO(u.getId(), u.getUsername(), u.getPasswordHash(), u.getRole(), u.getFullName(), u.getEmail())).toList();
    }

    public boolean existsByUsername(String username) {
        return userDAOImpl.existsByUsername(username);
    }

    public void changeUsername(String newUsername) {
        Validator.requireNonEmpty(newUsername, "Username");
        if (!Validator.isValidUsername(newUsername)) {
            throw new SerenityException(
                    "Username must be 4-30 chars, letters/digits/underscore only.");
        }
        if (userDAOImpl.existsByUsername(newUsername)) {
                throw new DuplicateRegistrationException("Username", newUsername);
        }
        currentUser.setUsername(newUsername);
        userDAOImpl.update(Converter.toUserEntity(currentUser));
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (!verifyPassword(oldPassword, currentUser.getPasswordHash())) {
            throw new SerenityException("Current password is incorrect.");
        }
        currentUser.setPasswordHash(hashPassword(newPassword));
        userDAOImpl.update(Converter.toUserEntity(currentUser));
    }

    public void ensureDefaultAdmin() {
        if (userDAOImpl.getAll().isEmpty()) {

            User admin = User.builder()
                    .username("admin")
                    .passwordHash(hashPassword("Admin@123"))
                    .role(User.Role.ADMIN)
                    .fullName("System Administrator")
                    .email("admin@serenity.lk")
                    .build();

            userDAOImpl.save(admin);

            User recep = User.builder()
                    .username("receptionist")
                    .passwordHash(hashPassword("Recep@123"))
                    .role(User.Role.RECEPTIONIST)
                    .fullName("Front Desk")
                    .email("reception@serenity.lk")
                    .build();
            userDAOImpl.save(recep);
        }
    }

}

