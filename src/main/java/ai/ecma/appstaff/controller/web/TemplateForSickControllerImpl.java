package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.TemplateForSickDTO;
import ai.ecma.appstaff.service.TemplateForSickService;
import ai.ecma.appstaff.service.TemplateForSickServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TemplateForSickControllerImpl implements TemplateForSickController {

    private final TemplateForSickService templateForSickService;


    /**
     * Tizimga qo'shiladigan templateForSicklar bu faqat hisobot uchun kerak.
     * Navbarda MODULni ichida chiqadigan templateForSicklarga aloqasi yo'q
     * TemplateForSick yaratish uchun faqat nomi bilan activligi berilsa bo'ldi
     *
     * @param templateForSickDTO bunda name bilan activligi keladi
     * @return success or error
     */
    @CheckAuth
    @Override
    public ApiResult<?> addTemplateForSick(TemplateForSickDTO templateForSickDTO) {
        return templateForSickService.addTemplateForSick(templateForSickDTO);
    }


    /**
     * TemplateForSickni nomi va activligi tahrirlanishi mumkin.
     * Bunda templateForSickning id si bilan dto kelishi kerak
     *
     * @param id
     * @param templateForSickDTO
     * @return
     */
    @CheckAuth
    @Override
    public ApiResult<?> editTemplateForSick(UUID id, TemplateForSickDTO templateForSickDTO) {
        return templateForSickService.editTemplateForSick(id, templateForSickDTO);
    }


    /**
     * Tizimda mavjud barcha templateForSicklarni olish uchun.
     * Bunda delete=false bo'lganlari olinadi faqat
     *
     * @param page
     * @param size
     * @return
     */
    @CheckAuth
    @Override
    public ApiResult<?> getAllTemplateForSick(Integer page, Integer size) {
        return templateForSickService.getAllTemplateForSick(page, size);
    }

    @CheckAuth
    @Override
    public ApiResult<?> getAllTemplateForSickForSelect(Integer page, Integer size) {
        return templateForSickService.getAllTemplateForSickForSelect(page, size);
    }


    /**
     * ID bo'yicha bitta templateForSickni olish uchun
     *
     * @param id
     * @return
     */
    @CheckAuth
    @Override
    public ApiResult<?> getOneTemplateForSick(UUID id) {

        if (id == null) {
            return templateForSickService.getFormTemplateForSick();
        } else {
            return templateForSickService.getOneTemplateForSick(id);
        }

    }


    /**
     * TemplateForSickni o'chirish uchun
     *
     * @param id
     * @return
     */
    @CheckAuth
    @Override
    public ApiResult<?> deleteTemplateForSick(UUID id) {
        return templateForSickService.deleteTemplateForSick(id);
    }

}
