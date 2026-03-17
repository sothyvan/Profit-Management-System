// user/ManagerStaff.java
package user;

public class ManagerStaff extends Staff {

    public ManagerStaff(Staff staff, double salary) {
        super(staff);
        setSalary(salary);
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public void setSalary(double salary) {
        if (salary < 1000) {
            System.out.println("Error: salary too low!");
        } else {
            this.salary = salary;
        }
    }

    @Override
    public boolean can(String action) {
        return action != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ManagerStaff)) return false;
        ManagerStaff other = (ManagerStaff) obj;
        return super.equals(other) && Double.compare(salary, other.salary) == 0;
    }

    @Override
    public String toString() {
        return super.toString() + ", salary=" + salary;
    }
}
