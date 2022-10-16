package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.TemplateForSickDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping(path = TemplateForSickController.TEMPLATE_FOR_SICK_CONTROLLER_PATH)
public interface TemplateForSickController {
    String TEMPLATE_FOR_SICK_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/template-for-sick";

    String ADD_TEMPLATE_FOR_SICK_PATH = "/add";
    String EDIT_TEMPLATE_FOR_SICK_PATH = "/edit/{id}";
    String GET_ALL_TEMPLATE_FOR_SICK_PATH = "/get-all";
    String GET_ALL_TEMPLATE_FOR_SICK_FOR_SELECT_PATH = "/get-all/select";
    String GET_ONE_TEMPLATE_FOR_SICK_PATH = "/get";
    String GET_ONE_TEMPLATE_FOR_SICK_ID_PATH = "/get/{id}";
    String DELETE_TEMPLATE_FOR_SICK_PATH = "/delete/{id}";

    /**
     * Kasallik varaqasi yaratish uchun yo'l
     *
     * @param templateForSickDTO
     * @return
     */
    @PostMapping(path = ADD_TEMPLATE_FOR_SICK_PATH)
    ApiResult<?> addTemplateForSick(
            @RequestBody @Valid TemplateForSickDTO templateForSickDTO
    );

    /**
     * Mavjud kasallik varaqasini tahrirlash uchun yo'l
     *
     * @param id
     * @param templateForSickDTO
     * @return
     */
    @PutMapping(path = EDIT_TEMPLATE_FOR_SICK_PATH)
    ApiResult<?> editTemplateForSick(
            @PathVariable(name = "id") UUID id,
            @RequestBody TemplateForSickDTO templateForSickDTO
    );

    /**
     * Barcha kasallik varaqalarini olish uchun yo'l
     * Kasallik varaqalari ro'yxati chiqadigan page uchun ishlatiladi
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = GET_ALL_TEMPLATE_FOR_SICK_PATH)
    ApiResult<?> getAllTemplateForSick(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * Barcha active true bo'lgan kasallik varaqalarini olish uchun yo'l
     * Selectlar uchun ishlatiladi
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = GET_ALL_TEMPLATE_FOR_SICK_FOR_SELECT_PATH)
    ApiResult<?> getAllTemplateForSickForSelect(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * Bitta kasallik varaqasini olish uchun yo'l
     *
     * @param id
     * @return
     */
    @GetMapping(path = {GET_ONE_TEMPLATE_FOR_SICK_PATH, GET_ONE_TEMPLATE_FOR_SICK_ID_PATH})
    ApiResult<?> getOneTemplateForSick(
            @PathVariable(name = "id", required = false) UUID id
    );

    /**
     * Kasallik varaqasini o'chirish uchun yo'l
     *
     * @param id
     * @return
     */
    @DeleteMapping(path = DELETE_TEMPLATE_FOR_SICK_PATH)
    ApiResult<?> deleteTemplateForSick(
            @PathVariable(name = "id") UUID id
    );

}
