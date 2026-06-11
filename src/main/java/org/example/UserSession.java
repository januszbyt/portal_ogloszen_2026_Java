package org.example;

public class UserSession {
    private static UserSession instance;

    private final int userId;
    private final String email;
    private final String role;
    private final String firstName;

    private UserSession(int userId, String email, String role, String firstName) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
    }

    public static UserSession init(int userId, String email, String role, String firstName) {
        // Zawsze tworzymy nową sesję — nigdy nie zwracamy starej instancji
        instance = new UserSession(userId, email, role, firstName);
        return instance;
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getFirstName() {
        return firstName;
    }
}
//naprawa