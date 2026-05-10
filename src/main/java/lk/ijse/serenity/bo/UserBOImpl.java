package lk.ijse.serenity.bo;

import javafx.scene.control.Alert;
import lk.ijse.serenity.dao.UserDAOImpl;
import lk.ijse.serenity.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class UserBOImpl {

    private UserDAOImpl userDAOImpl = new UserDAOImpl();

    private User currentUser;

    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public boolean verifyPassword(String plainPassword, String hash) {
        return BCrypt.checkpw(plainPassword, hash);
    }

    public User login(String username, String password) {
        Optional<User> userOpt = userDAOImpl.findByUsername(username.trim());

        if (userOpt.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Invalid UserName !").show();
        }

        User user = userOpt.get();
        if (!verifyPassword(password, user.getPasswordHash())) {
            new Alert(Alert.AlertType.ERROR, "Invalid Password !").show();
        }
        this.currentUser = user;
        return user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.Role.ADMIN;
    }
}
