// user/Staff.java
package user;

public abstract class Staff implements IStaff {
    private static int nextId = 1;

    private final String staffId;
    private String fullName;
    private String phone;
    private final String username;
    private String password;
    private boolean active;
    protected double salary;

    protected Staff(
            String fullName,
            String phone,
            String username,
            String password) {
        this.staffId = String.valueOf(nextId);
        nextId++;
        setFullName(fullName);
        setPhone(phone);
        this.username = normalizeUsername(username, staffId);
        setPassword(password);
        this.active = true;
    }

    protected Staff(Staff source) {
        if (source == null) {
            this.staffId = String.valueOf(nextId);
            nextId++;
            setFullName(null);
            setPhone(null);
            this.username = normalizeUsername(null, staffId);
            setPassword(null);
            this.active = true;
        } else {
            this.staffId = source.getStaffId();
            setFullName(source.getFullName());
            setPhone(source.getPhone());
            this.username = normalizeUsername(source.getUsername(), staffId);
            setPassword(source.password);
            this.active = source.isActive();
            this.salary = source.getSalary();
        }
    }

    @Override
    public String getStaffId() {
        return staffId;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean checkPassword(String input) {
        return password != null && password.equals(input);
    }

    public void setFullName(String fullName) {
        if (isBlank(fullName)) {
            this.fullName = "No Name";
        } else {
            this.fullName = fullName.trim();
        }
    }

    public void setPhone(String phone) {
        String p = (phone == null) ? "" : phone.trim();
        if (!isDigits(p) || p.length() < 8 || p.length() > 15) {
            this.phone = "00000000";
        } else {
            this.phone = p;
        }
    }

    public void setPassword(String password) {
        String pw = (password == null) ? "" : password;
        if (pw.length() < 4) {
            this.password = "0000";
        } else {
            this.password = pw;
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getSalary() {
        return salary;
    }

    public abstract void setSalary(double salary);

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isDigits(String s) {
        if (isBlank(s)) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

    private String normalizeUsername(String username, String staffId) {
        if (isBlank(username)) {
            return "staff_" + staffId;
        }
        return username.trim();
    }

    @Override
    public abstract boolean can(String action);
    
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "staffId='" + staffId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Staff other = (Staff) obj;
        if (staffId == null) return other.staffId == null;
        return staffId.equals(other.staffId);
    }
}
