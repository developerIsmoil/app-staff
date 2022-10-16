package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.SkillDTO;
import ai.ecma.appstaff.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SkillControllerImpl implements SkillController {

    private final SkillService skillService;

    @CheckAuth
    @Override
    public ApiResult<?> crudSkill(SkillDTO skillDTO) {
        return skillService.crudSkill(skillDTO);
    }

    /**
     * Tizimga qo'shiladigan skilllar bu faqat hisobot uchun kerak.
     * Navbarda MODULni ichida chiqadigan skilllarga aloqasi yo'q
     * Skill yaratish uchun faqat nomi bilan activligi berilsa bo'ldi
     *
     * @param skillDTO bunda name bilan activligi keladi
     * @return success or error
     */
    @CheckAuth
    @Override
    public ApiResult<?> addSkill(SkillDTO skillDTO) {
        return skillService.addSkill(skillDTO);
    }


    /**
     * Skillni nomi va activligi tahrirlanishi mumkin.
     * Bunda skillning id si bilan dto kelishi kerak
     *
     * @param id
     * @param skillDTO
     * @return
     */
    @CheckAuth
    @Override
    public ApiResult<?> editSkill(UUID id, SkillDTO skillDTO) {
        return skillService.editSkill(id, skillDTO);
    }


    /**
     * Tizimda mavjud barcha skilllarni olish uchun.
     * Bunda delete=false bo'lganlari olinadi faqat
     *
//     * @param page
//     * @param size
     * @return
     */
    @CheckAuth
    @Override
    public ApiResult<?> getAllSkill(
//            Integer page, Integer size
    ) {
        return skillService.getAllSkill();
    }

    /**
     * ID bo'yicha bitta skillni olish uchun
     *
     * @param id
     * @return
     */
    @CheckAuth
    @Override
    public ApiResult<?> getOneSkill(UUID id) {
        return skillService.getOneSkill(id);
    }


    /**
     * Skillni o'chirish uchun
     *
     * @param id
     * @return
     */
    @CheckAuth
    @Override
    public ApiResult<?> deleteSkill(UUID id) {
        return skillService.deleteSkill(id);
    }

}
