package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EnumDTO;
import ai.ecma.appstaff.service.AdditionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdditionalControllerImpl implements AdditionalController {

    private final AdditionalService additionalService;

    @CheckAuth
    @Override
    public ApiResult<List<EnumDTO>> getBonusTypes() {

        List<EnumDTO> enumDTOList = additionalService.getBonusTypes();
        return ApiResult.successResponse(enumDTOList);
    }

    @CheckAuth
    @Override
    public ApiResult<List<EnumDTO>> getContractForms() {

        List<EnumDTO> enumDTOList = additionalService.getContractForms();
        return ApiResult.successResponse(enumDTOList);
    }

    @CheckAuth
    @Override
    public ApiResult<List<EnumDTO>> getEmployeeModes() {

        List<EnumDTO> enumDTOList = additionalService.getEmployeeModes();
        return ApiResult.successResponse(enumDTOList);
    }

    @CheckAuth
    @Override
    public ApiResult<List<EnumDTO>> getEmployerStatus() {

        List<EnumDTO> enumDTOList = additionalService.getEmployerStatus();
        return ApiResult.successResponse(enumDTOList);
    }

    @CheckAuth
    @Override
    public ApiResult<List<EnumDTO>> getGenders() {

        List<EnumDTO> enumDTOList = additionalService.getGenders();
        return ApiResult.successResponse(enumDTOList);
    }

    @CheckAuth
    @Override
    public ApiResult<List<EnumDTO>> getMaritalStatuses() {

        List<EnumDTO> enumDTOList = additionalService.getMaritalStatuses();
        return ApiResult.successResponse(enumDTOList);
    }

    @CheckAuth
    @Override
    public ApiResult<List<EnumDTO>> getPaymentCriteriaType() {

        List<EnumDTO> enumDTOList = additionalService.getPaymentCriteriaType();
        return ApiResult.successResponse(enumDTOList);
    }

    @CheckAuth
    @Override
    public ApiResult<List<EnumDTO>> getStudyDegrees() {

        List<EnumDTO> enumDTOList = additionalService.getStudyDegrees();
        return ApiResult.successResponse(enumDTOList);
    }

    @CheckAuth
    @Override
    public ApiResult<List<EnumDTO>> getWeekDays() {

        List<EnumDTO> enumDTOList = additionalService.getWeekDays();
        return ApiResult.successResponse(enumDTOList);
    }
}
