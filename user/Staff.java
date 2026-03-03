package user;
public class Staff implements IStaff {
    private static int nextId = 1;

    private String staffId;
    private String fullName;
    private String phone;
    private String username;
    private String password;
    private boolean active;

    public Staff(
            String fullName,
            String phone,
            String username,
            String password,
            String idPrefix) {
        this.staffId = idPrefix + "-" + nextId;
        nextId++;
        setFullName(fullName);
        setPhone(phone);
        setUsername(username);
        setPassword(password);
        this.active = true;
    }

    public Staff(Staff source, String idPrefix) {
        this(
                source != null ? source.getFullName() : null,
                source != null ? source.getPhone() : null,
                source != null ? source.getUsername() : null,
                source != null ? source.password : null,
                idPrefix);
        if (source != null) {
            this.active = source.isActive();
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
    public String getPosition(){
        return "Staff";
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

    public void setUsername(String username) {
        if (isBlank(username)) {
            this.username = "staff_" + staffId;
        } else {
            this.username = username.trim();
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

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isDigits(String s) {
        if (isBlank(s)) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "staffId='" + staffId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Staff other = (Staff) obj;
        if (staffId == null) {
            if (other.staffId != null)
                return false;
        } else if (!staffId.equals(other.staffId))
            return false;
        return true;
    }

    @Override
    public boolean can(String action) {
        return false; 
    }
}
