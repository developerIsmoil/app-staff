package ai.ecma.appstaff.projection;

import java.util.UUID;

public interface EmploymentInfoProjection {

    String getEmployeeIdStr();

    String getDepartmentIdSTr();

    default UUID getEmployeeId(){
        return UUID.fromString(getEmployeeIdStr());
    }

    default UUID getDepartmentId(){
        return UUID.fromString(getDepartmentIdSTr());
    }

}
