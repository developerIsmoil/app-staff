package ai.ecma.appstaff.service;

import ai.ecma.appstaff.payload.EnumDTO;

import java.util.List;

public interface AdditionalService {

    List<EnumDTO> getBonusTypes();

    List<EnumDTO> getContractForms();

    List<EnumDTO> getEmployeeModes();

    List<EnumDTO> getEmployerStatus();

    List<EnumDTO> getGenders();

    List<EnumDTO> getMaritalStatuses();

    List<EnumDTO> getPaymentCriteriaType();

    List<EnumDTO> getStudyDegrees();

    List<EnumDTO> getWeekDays();

}
