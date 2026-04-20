package model;

/**
 * Abstract base class representing a generic user.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class User {

    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // ── Getters & Setters ──────────────────────────────

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ── Authentication ─────────────────────────────────

    /**
     * Validates credentials.
     * @return true if username and password match
     */
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public void logout() {
        System.out.println(username + " has been logged out.");
    }

    @Override
    public String toString() {
        return "User{username='" + username + "'}";
    }
}
