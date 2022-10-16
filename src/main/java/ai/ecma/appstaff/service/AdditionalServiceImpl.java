package ai.ecma.appstaff.service;

 
import ai.ecma.appstaff.enums.*;
import ai.ecma.appstaff.payload.EnumDTO;
  
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdditionalServiceImpl   implements AdditionalService {

    @Override
    public List<EnumDTO> getBonusTypes() {

        List<String> enums = Arrays
                .stream(BonusType.values())
                .map(Enum::toString)
                .collect(Collectors.toList());

        return convertEnumValuesToEnumDTO(enums);
    }

    @Override
    public List<EnumDTO> getContractForms() {

        List<String> enums = Arrays
                .stream(ContractFormEnum.values())
                .map(Enum::toString)
                .collect(Collectors.toList());

        return convertEnumValuesToEnumDTO(enums);


    }

    @Override
    public List<EnumDTO> getEmployeeModes() {

        List<String> enums = Arrays
                .stream(EmployeeModeEnum.values())
                .map(Enum::toString)
                .collect(Collectors.toList());

        return convertEnumValuesToEnumDTO(enums);


    }

    @Override
    public List<EnumDTO> getEmployerStatus() {

        List<String> enums = Arrays
                .stream(EmployerStatusEnum.values())
                .map(Enum::toString)
                .collect(Collectors.toList());

        return convertEnumValuesToEnumDTO(enums);


    }

    @Override
    public List<EnumDTO> getGenders() {

        List<String> enums = Arrays
                .stream(GenderEnum.values())
                .map(Enum::toString)
                .collect(Collectors.toList());

        return convertEnumValuesToEnumDTO(enums);


    }

    @Override
    public List<EnumDTO> getMaritalStatuses() {

        List<String> enums = Arrays
                .stream(MaritalStatusEnum.values())
                .map(Enum::toString)
                .collect(Collectors.toList());

        return convertEnumValuesToEnumDTO(enums);


    }

    @Override
    public List<EnumDTO> getPaymentCriteriaType() {

        List<String> enums = Arrays
                .stream(PaymentCriteriaTypeEnum.values())
                .map(Enum::toString)
                .collect(Collectors.toList());

        return convertEnumValuesToEnumDTO(enums);


    }

    @Override
    public List<EnumDTO> getStudyDegrees() {

        List<String> enums = Arrays
                .stream(StudyDegreeEnum.values())
                .map(Enum::toString)
                .collect(Collectors.toList());

        return convertEnumValuesToEnumDTO(enums);


    }

    @Override
    public List<EnumDTO> getWeekDays() {

        List<String> enums = Arrays
                .stream(WeekDayEnum.values())
                .map(Enum::toString)
                .collect(Collectors.toList());

        return convertEnumValuesToEnumDTO(enums);

    }

    private List<EnumDTO> convertEnumValuesToEnumDTO(List<String> stringList) {
        List<EnumDTO> enumDTOList = new ArrayList<>();

        for (String enumValue : stringList) {
            enumDTOList.add(
                    new EnumDTO(
                            enumValue,
                            enumValue
                    )
            );
        }

        return enumDTOList;
    }
}
