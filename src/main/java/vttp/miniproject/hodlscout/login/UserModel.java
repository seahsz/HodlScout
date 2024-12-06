package vttp.miniproject.hodlscout.login;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserModel {

    // Username all set to Lower Case [Case Insensitive]

    @NotNull(message = "Username cannot be empty")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @NotNull(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$",
             message = "Password must contain at least one lowercase letter, one uppercase letter, and one number")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username.toLowerCase(); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
}
