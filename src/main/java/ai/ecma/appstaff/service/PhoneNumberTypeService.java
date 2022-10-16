package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.PhoneNumberType;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.PhoneNumberTypeDTO;
import ai.ecma.appstaff.payload.PhoneNumberTypeInfoDTO;

import java.util.List;
import java.util.UUID;

public interface PhoneNumberTypeService {
    ApiResult<List<PhoneNumberTypeDTO>> addPhoneNumberType(PhoneNumberTypeDTO phoneNumberTypeDTO);

    ApiResult<List<PhoneNumberTypeDTO>> editPhoneNumberType(UUID id, PhoneNumberTypeDTO phoneNumberTypeDTO);

    ApiResult<?> deletePhoneNumberType(UUID id);

    ApiResult<?> getAllPhoneNumberType(Integer page, Integer size);

    PhoneNumberType getPhoneNumberTypeFromDB(UUID id);

    List<PhoneNumberType> getPhoneNumberTypeListFromDB(List<UUID> uuidList);

    List<PhoneNumberTypeDTO> getPhoneNumberTypeDTOListFromDB();

    List<PhoneNumberTypeInfoDTO> getPhoneNumberTypeInfoDTOListFromDB();

    ApiResult<?> crudPhoneNumberType(PhoneNumberTypeDTO phoneNumberTypeDTO);
}
