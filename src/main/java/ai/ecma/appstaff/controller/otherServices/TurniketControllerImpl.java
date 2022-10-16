package ai.ecma.appstaff.controller.otherServices;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.payload.*;
import ai.ecma.appstaff.service.otherService.TurniketService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TurniketControllerImpl implements TurniketController {
    private final TurniketService turniketService;

    @CheckAuth(permission = PermissionEnum.GET_EMPLOYEE_TURNIKET_HISTORY)
    @Override
    public ApiResult<TurniketInOutInfoDTO> getInOutData() {
        return turniketService.getInOutData();
    }

    @CheckAuth(permission = PermissionEnum.GET_EMPLOYEE_TURNIKET_HISTORY)
    @Override
    public ApiResult<TurniketInOutInfoDTO> getByFilter(List<TurniketFilterDTO> filters) {
        return turniketService.getByFilter(filters);
    }

    @CheckAuth(permission = PermissionEnum.GET_EMPLOYEE_TURNIKET_HISTORY)
    @Override
    public ApiResult<UserDailyInOutDTO> getOneDayDate(UUID userDailyInOutId) {
        return turniketService.getOneDayData(userDailyInOutId);
    }

    @CheckAuth(permission = PermissionEnum.GET_EMPLOYEE_TURNIKET_HISTORY)
    @Override
    public ApiResult<UserDailyInOutDTO> getOneDayData(OneDayTurniketDTO oneDayTurniketDTO) {
        return turniketService.getOneDayData(oneDayTurniketDTO);
    }

//    @CheckAuth(permission = PermissionEnum.GET_EMPLOYEE_TURNIKET_HISTORY)
    @Override
    public ResponseEntity<Resource> downloadExcel(String filters, String token) {
        return turniketService.downloadExcel(filters,token);
    }

}
