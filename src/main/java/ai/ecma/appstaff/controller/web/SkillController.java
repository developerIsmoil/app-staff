package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.SkillDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping(path = SkillController.SKILL_CONTROLLER_PATH)
public interface SkillController {
    String SKILL_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/skill";

    String ADD_SKILL_PATH = "/add";
    String CRUD_SKILL_PATH = "/crud";
    String EDIT_SKILL_PATH = "/edit/{id}";
    String GET_ALL_SKILL_PATH = "/get-all";
    String GET_ONE_SKILL_PATH = "/get/{id}";
    String DELETE_SKILL_PATH = "/delete/{id}";

    /**
     * Tizimga qo'shiladigan skilllar bu faqat hisobot uchun kerak.
     * Navbarda MODULni ichida chiqadigan skilllarga aloqasi yo'q
     * Skill yaratish uchun faqat nomi bilan activligi berilsa bo'ldi
     *
     * @param skillDTO bunda name bilan activligi keladi
     * @return success or error
     */
    @PostMapping(path = CRUD_SKILL_PATH)
    ApiResult<?> crudSkill(
            @RequestBody SkillDTO skillDTO
    );

    @PostMapping(path = ADD_SKILL_PATH)
    ApiResult<?> addSkill(
            @RequestBody @Valid SkillDTO skillDTO
    );

    /**
     * Skillni nomi va activligi tahrirlanishi mumkin.
     * Bunda skillning id si bilan dto kelishi kerak
     *
     * @param id
     * @param skillDTO
     * @return
     */
    @PutMapping(path = EDIT_SKILL_PATH)
    ApiResult<?> editSkill(
            @PathVariable(name = "id") UUID id,
            @RequestBody @Valid SkillDTO skillDTO
    );

    /**
     * Barcha qobiliyatlarni olish uchun yo'l
     * <p>
     * //     * @param page
     * //     * @param size
     *
     * @return
     */
    @GetMapping(path = GET_ALL_SKILL_PATH)
    ApiResult<?> getAllSkill(
//            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
//            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    /**
     * Bitta qobiliyatni olish uchun yo'l
     *
     * @param id
     * @return
     */
    @GetMapping(path = GET_ONE_SKILL_PATH)
    ApiResult<?> getOneSkill(
            @PathVariable(name = "id") UUID id
    );

    /**
     * Qobiliyatni o'chirish uchun yo'l
     *
     * @param id
     * @return
     */
    @DeleteMapping(path = DELETE_SKILL_PATH)
    ApiResult<?> deleteSkill(
            @PathVariable(name = "id") UUID id
    );


}
