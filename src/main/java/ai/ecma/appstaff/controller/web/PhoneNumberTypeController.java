package ai.ecma.appstaff.controller.web;


import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.PhoneNumberTypeDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(path = PhoneNumberTypeController.PHONE_NUMBER_TYPE_CONTROLLER_PATH)
public interface PhoneNumberTypeController {
    String PHONE_NUMBER_TYPE_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/phone-number-type";

    String PHONE_NUMBER_TYPE_CRUD_PATH = "/crud";
    String ADD_PHONE_NUMBER_TYPE_PATH = "/add";
    String EDIT_PHONE_NUMBER_TYPE_PATH = "/edit/{id}";
    String DELETE_PHONE_NUMBER_TYPE_PATH = "/delete/{id}";
    String GET_ALL_PHONE_NUMBER_TYPE_PATH = "/get";


    @PostMapping(path = PHONE_NUMBER_TYPE_CRUD_PATH)
    ApiResult<?> crudPhoneNumberType(
            @RequestBody PhoneNumberTypeDTO phoneNumberTypeDTO
    );


    /**
     * Telefon raqam turini qo'shish uchun yo'l
     *
     * @param phoneNumberTypeDTO
     * @return
     */
    @PostMapping(path = ADD_PHONE_NUMBER_TYPE_PATH)
    ApiResult<?> addPhoneNumberType(
            @RequestBody PhoneNumberTypeDTO phoneNumberTypeDTO
    );

    /**
     * Telefon raqam turini tahrirlash uchun yo'l
     *
     * @param id
     * @param phoneNumberTypeDTO
     * @return
     */
    @PutMapping(path = EDIT_PHONE_NUMBER_TYPE_PATH)
    ApiResult<?> editPhoneNumberType(
            @PathVariable(name = "id") UUID id,
            @RequestBody PhoneNumberTypeDTO phoneNumberTypeDTO
    );

    /**
     * Telefon raqam turini o'chirish uchun yo'l
     *
     * @param id
     * @return
     */
    @DeleteMapping(path = DELETE_PHONE_NUMBER_TYPE_PATH)
    ApiResult<?> deletePhoneNumberType(
            @PathVariable(name = "id") UUID id
    );

    /**
     * Barcha telefon raqam turlarini olish uchun yo'l
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(path = GET_ALL_PHONE_NUMBER_TYPE_PATH)
    ApiResult<?> getAllPhoneNumberType(
            @RequestParam(name = "page", defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

}


