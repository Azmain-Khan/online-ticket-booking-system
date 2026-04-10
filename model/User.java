package model;

public class User {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private boolean isAdmin;

    public User(String username, String password, String email, String fullName, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.isAdmin = isAdmin;
    }

    // CSV format: username,password,email,fullName,isAdmin
    public String toCSV() {
        return username + "," + password + "," + email + "," + fullName + "," + isAdmin;
    }

    public static User fromCSV(String csv) {
        String[] parts = csv.split(",", 5);
        if (parts.length < 5) return null;
        return new User(parts[0], parts[1], parts[2], parts[3], Boolean.parseBoolean(parts[4]));
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public boolean isAdmin() { return isAdmin; }

    @Override
    public String toString() {
        return fullName + " (" + username + ")";
    }
}
