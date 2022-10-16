package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping(path = WorkDayController.WORK_DAY_CONTROLLER_PATH)
public interface WorkDayController {
    String WORK_DAY_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/work-day";

    String CHANGE_ACTIVE_PATH = "/change/active/{id}";
    String GET_ALL_PATH = "/get-all";
    String GET_ALL_ACTIVE_PATH = "/get-all/active";

    /**
     * Havta kunini holatini o'zgartirish uchun yo'l
     *
     * @param id
     * @return
     */
    @GetMapping(path = CHANGE_ACTIVE_PATH)
    ApiResult<?> changeActive(
            @PathVariable(name = "id") UUID id
    );

    /**
     * Barcha havfta kunlarini olish uchun yo'l
     *
     * @return
     */
    @GetMapping(path = GET_ALL_PATH)
    ApiResult<?> getAll();

    @GetMapping(path = GET_ALL_ACTIVE_PATH)
    ApiResult<?> getAllActive();
}
