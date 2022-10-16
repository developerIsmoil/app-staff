package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.DateCompareOperatorTypeEnum;
import ai.ecma.appstaff.enums.DateFilterTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterFieldValueDTO {

    //DATE UCHUN START
    private DateFilterTypeEnum dateFilterType;

    private DateCompareOperatorTypeEnum dateCompareOperatorType;//MUAJBURIY(DOIM DATE TANLANSA KELADI)

    private Long starDate;

    //BU
    private boolean starDateTime;

    private Long endDate;

    private boolean endDateTime;

    private Integer dateXValue=0;
    //DATE UCHUN FINISH


    //RATING, NUMBER, MONEY LAR UCHUN START -> BULAR CompareOperatorTypeEnum BILAN ISHLAYDI
    private String minValue;//REQURED(RAQAMLAR BILAN FILTER QILINSA DOIM KELADI)

    private String maxValue;
    //RATING, NUMBER, MONEY UCHUN FINISH


    //DROPDOWN, LABELS LAR UCHUN BULAR -> CompareOperatorTypeEnum BILAN
    private String[] optionsSelected;//REQUIRED (AGAR OPTION TANLANSA KELADI)


    //EMAIL, PHONE, SHORT_TEXT, LONG_TEXT LAR UCHUN
    private String searchingValue = "";//REQUIRED(STRING GA BOG'LIQ HAMMA NARSADA)

    public FilterFieldValueDTO(DateFilterTypeEnum dateFilterType, DateCompareOperatorTypeEnum dateCompareOperatorType, Long starDate, boolean starDateTime, Long endDate, boolean endDateTime, Integer dateXValue, String minValue, String maxValue, String[] optionsSelected, String searchingValue) {
        this.dateFilterType = dateFilterType;
        this.dateCompareOperatorType = dateCompareOperatorType;
        this.starDate = starDate;
        this.starDateTime = starDateTime;
        this.endDate = endDate;
        this.endDateTime = endDateTime;
        this.dateXValue = dateXValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.optionsSelected = optionsSelected;
        this.searchingValue = searchingValue;
    }
}
