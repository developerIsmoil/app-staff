package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.PrivilegeTypeDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping(path = PrivilegeTypeController.PRIVILEGE_TYPE_CONTROLLER_PATH)
public interface PrivilegeTypeController {
    String PRIVILEGE_TYPE_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/privilege-type";

    String ADD_PRIVILEGE_TYPE_PATH = "/add";
    String EDIT_PRIVILEGE_TYPE_PATH = "/edit/{id}";
    String GET_ALL_PRIVILEGE_TYPE_PATH = "/get-all";
    String GET_ALL_PRIVILEGE_TYPE_FOR_SELECT_PATH = "/get-all/select";
    String GET_ONE_PRIVILEGE_TYPE_PATH = "/get/{id}";
    String DELETE_PRIVILEGE_TYPE_PATH = "/delete/{id}";

    /**
     * Imtiyoz turi qo'shish uchun yo'l
     *
     * @param privilegeTypeDTO
     * @return
     */
    @PostMapping(path = ADD_PRIVILEGE_TYPE_PATH)
    ApiResult<?> create(
            @RequestBody @Valid PrivilegeTypeDTO privilegeTypeDTO
    );

    /**
     * Mavjud imtiyoz turini tahrirlash uchun yo'l
     *
     * @param id
     * @param privilegeTypeDTO
     * @return
     */
    @PutMapping(path = EDIT_PRIVILEGE_TYPE_PATH)
    ApiResult<?> edit(
            @PathVariable(name = "id") UUID id,
            @RequestBody @Valid PrivilegeTypeDTO privilegeTypeDTO
    );

    /**
     * Barcha imtiyoz turlarini olish uchun yo'l
     * Imtiyoz turlari ro'xati pageda chaqiriladi
     *
     * @return
     */
    @GetMapping(path = GET_ALL_PRIVILEGE_TYPE_PATH)
    ApiResult<?> getAll();

    /**
     * Barcha active true bo'lgan imtiyoz turlarini olish uchun yo'l
     * Selectlarda chaqiriladi
     *
     * @return
     */
    @GetMapping(path = GET_ALL_PRIVILEGE_TYPE_FOR_SELECT_PATH)
    ApiResult<?> getAllForSelect();

    /**
     * Bitta imtiyoz turini olish uchun yo'l
     *
     * @param id
     * @return
     */
    @GetMapping(path = GET_ONE_PRIVILEGE_TYPE_PATH)
    ApiResult<?> getById(
            @PathVariable(name = "id") UUID id
    );

    /**
     * Imtiyoz turini o'chirish uchun yo'l
     *
     * @param id
     * @return
     */
    @DeleteMapping(path = DELETE_PRIVILEGE_TYPE_PATH)
    ApiResult<?> delete(
            @PathVariable(name = "id") UUID id
    );


}
