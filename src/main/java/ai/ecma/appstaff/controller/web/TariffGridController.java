package ai.ecma.appstaff.controller.web;


import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.TariffGridDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping(path = TariffGridController.TARIFF_GRID_CONTROLLER_PATH)
public interface TariffGridController {
    String TARIFF_GRID_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/tariff-grid";

    String ADD_TARIFF_GRID_PATH = "/add";
    String EDIT_TARIFF_GRID_PATH = "/edit/{id}";
    String GET_ALL_TARIFF_GRID_PATH = "/get-all";
    String GET_ALL_TARIFF_GRID_SELECT_PATH = "/get-all/select";
    String GET_ONE_TARIFF_GRID_PATH = "/get";
    String GET_ONE_TARIFF_GRID_ID_PATH = "/get/{id}";
    String DELETE_TARIFF_GRID_PATH = "/delete/{id}";

    /**
     * Tarif setkasini qo'shish uchun yo'l
     *
     * @param tariffGridDTO
     * @return
     */
    @PostMapping(path = ADD_TARIFF_GRID_PATH)
    ApiResult<?> addTariffGrid(@Valid @RequestBody TariffGridDTO tariffGridDTO);

    /**
     * Mavjud tarif setkasini tahrirlash uchun yo'l
     *
     * @param id
     * @param tariffGridDTO
     * @return
     */
    @PutMapping(path = EDIT_TARIFF_GRID_PATH)
    ApiResult<?> editTariffGrid(@PathVariable UUID id, @RequestBody TariffGridDTO tariffGridDTO);

    /**
     * Barcha tarif setkasini olish uchun yo'l
     * Tarif setkalari ro'yxati page uchun ishlatiladi
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = GET_ALL_TARIFF_GRID_PATH)
    ApiResult<?> getAllTariffGrid(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * Barcha active tarif setkalarini olish uchun yo'l
     * Selectlar uchun ishlatiladi
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = GET_ALL_TARIFF_GRID_SELECT_PATH)
    ApiResult<?> getAllTariffGridSelect(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * Bitta tarif setkasini olish uchun yo'l
     *
     * @param id
     * @return
     */
    @GetMapping(path = {GET_ONE_TARIFF_GRID_PATH, GET_ONE_TARIFF_GRID_ID_PATH})
    ApiResult<?> getOneTariffGrid(
            @PathVariable(name = "id", required = false) UUID id
    );

    /**
     * Tarif setkasini o'chirish uchun yo'l
     *
     * @param id
     * @return
     */
    @DeleteMapping(path = DELETE_TARIFF_GRID_PATH)
    ApiResult<?> deleteTariffGridById(
            @PathVariable("id") UUID id
    );

}
