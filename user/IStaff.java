package user;

public interface IStaff {
    String getStaffId();
    String getFullName();
    String getPhone();
    String getUsername();
    String getPosition();
    
    boolean isActive();
    boolean checkPassword(String input);

    boolean can(String action);
}
