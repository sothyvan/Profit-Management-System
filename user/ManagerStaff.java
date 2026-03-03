package user;
public class ManagerStaff extends Staff {

    private double salary;

    public ManagerStaff(Staff staff, double salary) {
        super(staff, "MANAGER");
        setSalary(salary);
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if(salary < 1000)
        {
            System.out.println("error: need more salary");
        }else
        {
            this.salary = salary;
        }
    }

    @Override
    public boolean can(String action) {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        ManagerStaff other = (ManagerStaff) obj;

        if (!super.equals(obj))
        {
            return false;
        }else{
            if (salary != other.salary){
                return false;
            }
        }
        return true;
    }

        // ManagerStaff.java
    @Override
    public String getPosition() {
        return "Manager";
    }


    @Override
    public String toString() {
        return super.toString()+"ManagerStaff [\"Position: Manager salary=" + salary + "]";
    }
}
