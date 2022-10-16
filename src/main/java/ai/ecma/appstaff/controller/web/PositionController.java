package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.PositionDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping(path = PositionController.POSITION_CONTROLLER_PATH)
public interface PositionController {
    String POSITION_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/position";

    String ADD_POSITION_PATH = "/add";
    String EDIT_POSITION_PATH = "/edit/{id}";
    String GET_ALL_POSITION_PATH = "/get-all";
    String GET_ALL_POSITION_FOR_SELECT_PATH = "/get-all/select";
    String GET_ONE_POSITION_PATH = "/get";
    String GET_ONE_POSITION_ID_PATH = "/get/{id}";
    String DELETE_POSITION_PATH = "/delete/{id}";

    /**
     * Lavozim qo'shish uchun yo'l
     *
     * @param positionDTO
     * @return
     */
    @PostMapping(path = ADD_POSITION_PATH)
    ApiResult<PositionDTO> addPosition(
            @RequestBody PositionDTO positionDTO
    );

    /**
     * Mavjud lavozimni tahrirlash uchun yo'l
     *
     * @param id
     * @param positionDTO
     * @return
     */
    @PutMapping(path = EDIT_POSITION_PATH)
    ApiResult<PositionDTO> editPosition(
            @PathVariable(name = "id") UUID id,
            @RequestBody PositionDTO positionDTO
    );

    /**
     * Barcha lavozimlarni olish uchun yo'l.
     * Lavozimlar ro'yxati pageda chaqiriladi
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = GET_ALL_POSITION_PATH)
    ApiResult<List<PositionDTO>> getAllPosition(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * Avtive true bo'lgan barcha lavozimlarni olish uchun yo'l
     * Selectlarda chaqiriladi
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = GET_ALL_POSITION_FOR_SELECT_PATH)
    ApiResult<List<PositionDTO>> getAllPositionForSelect(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * Bitta lavozimni olish uchun yo'l
     *
     * @param id
     * @return
     */
    @GetMapping(path = {GET_ONE_POSITION_PATH, GET_ONE_POSITION_ID_PATH})
    ApiResult<PositionDTO> getOnePosition(
            @PathVariable(name = "id", required = false) UUID id
    );

    /**
     * Lavozimni o'chirish uchun yo'l
     *
     * @param id
     * @return
     */
    @DeleteMapping(path = DELETE_POSITION_PATH)
    ApiResult<?> deletePosition(
            @PathVariable(name = "id") UUID id
    );


}
