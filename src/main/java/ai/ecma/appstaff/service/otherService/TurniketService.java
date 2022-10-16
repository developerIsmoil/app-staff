package ai.ecma.appstaff.service.otherService;

import ai.ecma.appstaff.entity.Employee;
import ai.ecma.appstaff.payload.*;
import ai.ecma.appstaff.payload.feign.turniket.TurniketDTOForMessage;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface TurniketService {
    ApiResult<TurniketInOutInfoDTO> getInOutData();

    ApiResult<TurniketInOutInfoDTO> getByFilter(List<TurniketFilterDTO> filters);

    ApiResult<UserDailyInOutDTO> getOneDayData(UUID userDailyInOutId);

    ApiResult<UserDailyInOutDTO> getOneDayData(OneDayTurniketDTO oneDayTurniketDTO);

    ResponseEntity<Resource> downloadExcel(String filters, String token);

    void addUserInTurniket(Employee employee, EmployeeInfoDTO employeeInfoDTO);

    void editUserInTurniket(Employee employee, EmployeeInfoDTO employeeInfoDTO);

    void deleteUserFromTurniket(Employee employeeFromDB);

    void getUpdates(TurniketDTOForMessage message);

}
