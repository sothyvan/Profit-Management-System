// user/CashierStaff.java
package user;

public class CashierStaff extends Staff {

    public CashierStaff(Staff staff, double salary) {
        super(staff);
        setSalary(salary);
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public void setSalary(double salary) {
        if (salary > 700) {
            System.out.println("Error: salary too high!");
        } else {
            this.salary = salary;
        }
    }

    @Override
    public boolean can(String action) {
        if (action == null) return false;
        switch (action.trim().toUpperCase()) {
            case "CREATE_TRANSACTION":
            case "EDIT_TRANSACTION":
            case "CREATE_CUSTOMER":
            case "VIEW_TRANSACTIONS":
            case "VIEW_PRODUCTS":
            case "VIEW_CUSTOMERS":
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CashierStaff)) return false;
        CashierStaff other = (CashierStaff) obj;
        return super.equals(other) && Double.compare(salary, other.salary) == 0;
    }

    @Override
    public String toString() {
        return super.toString() + ", salary=" + salary;
    }
}
