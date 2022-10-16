package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.service.WorkDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class WorkDayControllerImpl implements WorkDayController {

    private final WorkDayService workDayService;

    @CheckAuth
    @Override
    public ApiResult<?> changeActive(UUID id) {
        return workDayService.changeActive(id);
    }

    @CheckAuth
    @Override
    public ApiResult<?> getAll() {
        return workDayService.getAll();
    }

    @CheckAuth
    @Override
    public ApiResult<?> getAllActive() {
        return workDayService.getAllActive();
    }
}
