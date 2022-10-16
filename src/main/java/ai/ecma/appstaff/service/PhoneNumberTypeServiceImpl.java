package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.PhoneNumberType;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.PhoneNumberTypeDTO;
import ai.ecma.appstaff.payload.PhoneNumberTypeInfoDTO;
import ai.ecma.appstaff.repository.PhoneNumberTypeRepository;
  
import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PhoneNumberTypeServiceImpl   implements PhoneNumberTypeService {

    private final PhoneNumberTypeRepository phoneNumberTypeRepository;
    private final EmployeeService employeeService;

    @Autowired
    public PhoneNumberTypeServiceImpl(
            PhoneNumberTypeRepository phoneNumberTypeRepository,
            @Lazy EmployeeService employeeService) {
        this.phoneNumberTypeRepository = phoneNumberTypeRepository;
        this.employeeService = employeeService;
    }

    @Override
    public ApiResult<List<PhoneNumberTypeDTO>> addPhoneNumberType(PhoneNumberTypeDTO phoneNumberTypeDTO) {

//        checkStrIsEmptyOrNull(phoneNumberTypeDTO.getName(), ResponseMessage.REQUIRED_PHONE_NUMBER_TYPE_NAME);

        checkPhoneNumberTypeExist(phoneNumberTypeDTO, Optional.empty());

        PhoneNumberType phoneNumberType = new PhoneNumberType();

        phoneNumberType.setName(phoneNumberTypeDTO.getName());
        phoneNumberType.setColor(phoneNumberTypeDTO.getColorCode());

        try {
            phoneNumberTypeRepository.save(phoneNumberType);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_PHONE_NUMBER_TYPE_SAVING);
        }

        return ApiResult.successResponse(getPhoneNumberTypeDTOListFromDB());
    }

    @Override
    public ApiResult<List<PhoneNumberTypeDTO>> editPhoneNumberType(UUID id, PhoneNumberTypeDTO phoneNumberTypeDTO) {

//        checkStrIsEmptyOrNull(phoneNumberTypeDTO.getName(), ResponseMessage.REQUIRED_PHONE_NUMBER_TYPE_NAME);

        checkPhoneNumberTypeExist(phoneNumberTypeDTO, Optional.of(id));

        PhoneNumberType phoneNumberType = getPhoneNumberTypeFromDB(id);

        phoneNumberType.setName(phoneNumberTypeDTO.getName());
        phoneNumberType.setColor(phoneNumberTypeDTO.getColorCode());

        try {
            phoneNumberTypeRepository.save(phoneNumberType);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_PHONE_NUMBER_TYPE_EDITING);
        }
        return ApiResult.successResponse(getPhoneNumberTypeDTOListFromDB(), ResponseMessage.SUCCESS_PHONE_NUMBER_TYPE_EDITED);

    }

    @Override
    public ApiResult<?> deletePhoneNumberType(UUID id) {


        checkCanDeletePhoneNumberTypeOrThrow(id);

        try {
            phoneNumberTypeRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_PHONE_NUMBER_TYPE_DELETING);
        }
        return ApiResult.successResponse(getPhoneNumberTypeDTOListFromDB());
    }

    private void checkCanDeletePhoneNumberTypeOrThrow(UUID id) {

        employeeService.existsByPhoneNumberTypeId(id);

    }

    @Override
    public ApiResult<?> getAllPhoneNumberType(Integer page, Integer size) {
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<PhoneNumberType> phoneNumberTypeList = phoneNumberTypeRepository.findAll(sortByColumn);

        return ApiResult.successResponse(
                phoneNumberTypeList
                        .stream()
                        .map(PhoneNumberTypeDTO::fromPhoneNumberType)
                        .collect(Collectors.toList()));
    }

    @Override
    public PhoneNumberType getPhoneNumberTypeFromDB(UUID id) {
        Optional<PhoneNumberType> optionalPhoneNumberType = phoneNumberTypeRepository.findById(id);
        if (optionalPhoneNumberType.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_NOT_FOUND_PHONE_NUMBER_TYPE);
        }
        return optionalPhoneNumberType.get();
    }

    @Override
    public List<PhoneNumberType> getPhoneNumberTypeListFromDB(List<UUID> uuidList) {
        return phoneNumberTypeRepository.findAllById(uuidList);
    }

    @Override
    public List<PhoneNumberTypeDTO> getPhoneNumberTypeDTOListFromDB() {
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<PhoneNumberType> phoneNumberTypeList = phoneNumberTypeRepository.findAll(sortByColumn);

        return phoneNumberTypeList
                .stream()
                .map(PhoneNumberTypeDTO::fromPhoneNumberType)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhoneNumberTypeInfoDTO> getPhoneNumberTypeInfoDTOListFromDB() {
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<PhoneNumberType> phoneNumberTypeList = phoneNumberTypeRepository.findAll(sortByColumn);

        return phoneNumberTypeList
                .stream()
                .map(PhoneNumberTypeInfoDTO::fromPhoneNumberType)
                .collect(Collectors.toList());
    }

    @Override
    public ApiResult<?> crudPhoneNumberType(PhoneNumberTypeDTO phoneNumberTypeDTO) {

        switch (phoneNumberTypeDTO.getMethod()) {
            case CREATE:
                return addPhoneNumberType(phoneNumberTypeDTO);
            case EDIT:
                return editPhoneNumberType(phoneNumberTypeDTO.getId(), phoneNumberTypeDTO);
            case DELETE:
                return deletePhoneNumberType(phoneNumberTypeDTO.getId());
            default:
                throw RestException.restThrow(ResponseMessage.ERROR_INVALID_ACTION_TYPE);
        }

    }

    private void checkPhoneNumberTypeExist(PhoneNumberTypeDTO phoneNumberTypeDTO, Optional<UUID> optionalId) {

        boolean exists;

        if (optionalId.isEmpty()) {

            exists = phoneNumberTypeRepository.existsByNameEqualsIgnoreCaseAndColorEqualsIgnoreCase(phoneNumberTypeDTO.getName(), phoneNumberTypeDTO.getColorCode());

        } else {

            exists = phoneNumberTypeRepository.existsByNameEqualsIgnoreCaseAndColorEqualsIgnoreCaseAndIdNot(phoneNumberTypeDTO.getName(), phoneNumberTypeDTO.getColorCode(), optionalId.get());

        }

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_PHONE_NUMBER_TYPE_ALREADY_EXIST);
        }

    }


}
