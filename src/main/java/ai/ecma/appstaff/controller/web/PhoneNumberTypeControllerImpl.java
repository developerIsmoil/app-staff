package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.PhoneNumberTypeDTO;
import ai.ecma.appstaff.service.PhoneNumberTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PhoneNumberTypeControllerImpl implements PhoneNumberTypeController {

    private final PhoneNumberTypeService phoneNumberTypeService;

    @CheckAuth
    @Override
    public ApiResult<?> crudPhoneNumberType(PhoneNumberTypeDTO phoneNumberTypeDTO) {
        return phoneNumberTypeService.crudPhoneNumberType(phoneNumberTypeDTO);
    }

    @CheckAuth
    @Override
    public ApiResult<?> addPhoneNumberType(PhoneNumberTypeDTO phoneNumberTypeDTO) {
        return phoneNumberTypeService.addPhoneNumberType(phoneNumberTypeDTO);
    }

    @CheckAuth
    @Override
    public ApiResult<?> editPhoneNumberType(UUID id, PhoneNumberTypeDTO phoneNumberTypeDTO) {
        return phoneNumberTypeService.editPhoneNumberType(id, phoneNumberTypeDTO);
    }

    @CheckAuth
    @Override
    public ApiResult<?> deletePhoneNumberType(UUID id) {
        return phoneNumberTypeService.deletePhoneNumberType(id);
    }

    @CheckAuth
    @Override
    public ApiResult<?> getAllPhoneNumberType(Integer page, Integer size) {
        return phoneNumberTypeService.getAllPhoneNumberType(page, size);
    }
}
