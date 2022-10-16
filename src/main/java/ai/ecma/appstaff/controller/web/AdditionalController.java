package ai.ecma.appstaff.controller.web;


import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EnumDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * TIZIMDAGI ENUMLARNI FRONTENDCHI SELECTLARGA CHIQARISHI UCHUN OLIB KETADIGAN YO'LLAR
 */
@RequestMapping(path = AdditionalController.ADDITIONAL_CONTROLLER_PATH)
public interface AdditionalController {
    String ADDITIONAL_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/additional";

    String GET_BONUS_TYPE_PATH = "/bonus-type";
    String GET_CONTRACT_FORM_PATH = "/contract-form";
    String GET_EMPLOYEE_MODE_PATH = "/employee-mode";
    String GET_EMPLOYER_STATUS_PATH = "/employer-status";
    String GET_GENDER_PATH = "/gender";
    String GET_MARITAL_STATUS_PATH = "/marital-status";
    String GET_PAYMENT_CRITERIA_TYPE_PATH = "/payment-criteria-type";
    String GET_STUDY_DEGREE_PATH = "/study-degree";
    String GET_WEEK_DAY_PATH = "/week-day";

    /**
     * BONUS TURLARINI OLIB KETADIGAN YO'L
     *
     * @return List<EnumDTO>
     */
    @GetMapping(path = GET_BONUS_TYPE_PATH)
    ApiResult<List<EnumDTO>> getBonusTypes();

    /**
     * SHARTNOMA TURLARINI OLIB KETADIGAN YO'L
     *
     * @return List<EnumDTO>
     */
    @GetMapping(path = GET_CONTRACT_FORM_PATH)
    ApiResult<List<EnumDTO>> getContractForms();

    /**
     * HODIMLARNING ISHLASH STAFKALARINI OLIB KELADIGAN YO'L
     *
     * @return List<EnumDTO>
     */
    @GetMapping(path = GET_EMPLOYEE_MODE_PATH)
    ApiResult<List<EnumDTO>> getEmployeeModes();

    /**
     * HODIMNING STATUSLARI. TIZIMDAGI HOLATLARINI OLIB KETADIGAN YO'L
     *
     * @return List<EnumDTO>
     */
    @GetMapping(path = GET_EMPLOYER_STATUS_PATH)
    ApiResult<List<EnumDTO>> getEmployerStatus();

    /**
     * @return List<EnumDTO>
     */
    @GetMapping(path = GET_GENDER_PATH)
    ApiResult<List<EnumDTO>> getGenders();

    /**
     * @return List<EnumDTO>
     */
    @GetMapping(path = GET_MARITAL_STATUS_PATH)
    ApiResult<List<EnumDTO>> getMaritalStatuses();

    /**
     * @return List<EnumDTO>
     */
    @GetMapping(path = GET_PAYMENT_CRITERIA_TYPE_PATH)
    ApiResult<List<EnumDTO>> getPaymentCriteriaType();

    /**
     * @return List<EnumDTO>
     */
    @GetMapping(path = GET_STUDY_DEGREE_PATH)
    ApiResult<List<EnumDTO>> getStudyDegrees();

    /**
     * @return List<EnumDTO>
     */
    @GetMapping(path = GET_WEEK_DAY_PATH)
    ApiResult<List<EnumDTO>> getWeekDays();

}


