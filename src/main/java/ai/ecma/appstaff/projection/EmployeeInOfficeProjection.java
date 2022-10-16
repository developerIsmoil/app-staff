package ai.ecma.appstaff.projection;

import java.util.Objects;
import java.util.UUID;

public interface EmployeeInOfficeProjection {

    String getEmployeeIdStr();

    default UUID getEmployeeId(){
        return Objects.nonNull(getEmployeeIdStr()) ? UUID.fromString(getEmployeeIdStr()) : null;
    }

    boolean getInOffice();

}
