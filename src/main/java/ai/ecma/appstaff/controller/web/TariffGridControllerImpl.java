package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.TariffGridDTO;
import ai.ecma.appstaff.service.TariffGridService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TariffGridControllerImpl implements TariffGridController {

    private final TariffGridService tariffGridService;

    @CheckAuth
    @Override
    public ApiResult<?> addTariffGrid(TariffGridDTO tariffGridDTO) {
        return tariffGridService.addTariffGrid(tariffGridDTO);
    }

    @CheckAuth
    @Override
    public ApiResult<?> editTariffGrid(UUID id, TariffGridDTO tariffGridDTO) {
        return tariffGridService.editTariffGrid(id, tariffGridDTO);
    }

    @CheckAuth
    @Override
    public ApiResult<?> getAllTariffGrid(Integer page, Integer size) {
        return tariffGridService.getAllTariffGrid(page, size);
    }

    @CheckAuth
    @Override
    public ApiResult<?> getAllTariffGridSelect(Integer page, Integer size) {
        return tariffGridService.getAllTariffGridSelect(page, size);
    }

    @CheckAuth
    @Override
    public ApiResult<?> getOneTariffGrid(UUID id) {
        if (id == null) {
            return tariffGridService.getFormTariffGrid();
        } else {
            return tariffGridService.getOneTariffGridById(id);
        }
    }

    @CheckAuth
    @Override
    public ApiResult<?> deleteTariffGridById(UUID id) {
        return tariffGridService.deleteTariffGridById(id);
    }


}
