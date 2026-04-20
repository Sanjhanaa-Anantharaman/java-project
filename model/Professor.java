package model;

/**
 * Represents a professor who can manage student records.
 * Demonstrates Inheritance (extends User).
 * Actual CRUD operations are delegated to ResultManager; this class
 * exists to enforce role-based design and hold professor-specific state.
 */
public class Professor extends User {

    public Professor(String username, String password) {
        super(username, password);
    }

    @Override
    public String toString() {
        return "Professor{username='" + getUsername() + "'}";
    }
}
