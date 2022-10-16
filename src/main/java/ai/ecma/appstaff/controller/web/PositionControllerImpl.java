package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.PositionDTO;
import ai.ecma.appstaff.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PositionControllerImpl implements PositionController {

    private final PositionService positionService;

    /**
     * Tizimga yangi lavozim qo'shish uchun
     *
     * @param positionDTO
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_ADD_POSITION})
    @Override
    public ApiResult<PositionDTO> addPosition(PositionDTO positionDTO) {
        return positionService.addPosition(positionDTO);
    }

    /**
     * Tizimda mavjud lavozimni tahrirlash uchun
     *
     * @param id
     * @param positionDTO
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_EDIT_POSITION})
    @Override
    public ApiResult<PositionDTO> editPosition(UUID id, PositionDTO positionDTO) {
        return positionService.editPosition(id, positionDTO);
    }

    /**
     * Tizimdagi barcha lavozimlarni olish uchun
     *
     * @param page
     * @param size
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_GET_ALL_POSITION})
    @Override
    public ApiResult<List<PositionDTO>> getAllPosition(Integer page, Integer size) {
        return positionService.getAllPosition(page, size);
    }

    @CheckAuth
    @Override
    public ApiResult<List<PositionDTO>> getAllPositionForSelect(Integer page, Integer size) {
        return positionService.getAllPositionForSelect(page, size);
    }

    /**
     * ID bo'yicha bitta lavozimni olish uchun
     *
     * @param id
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_GET_ONE_POSITION})
    @Override
    public ApiResult<PositionDTO> getOnePosition(UUID id) {
        if (id == null) {
            return positionService.getFormPosition();
        } else {

            return positionService.getOnePosition(id);
        }
    }

    /**
     * ID bo'yicha lavozimni o'chirish uchun
     *
     * @param id
     * @return
     */
    @CheckAuth(permission = {PermissionEnum.HRM_DELETE_POSITION})
    @Override
    public ApiResult<?> deletePosition(UUID id) {
        return positionService.deletePosition(id);
    }

}
