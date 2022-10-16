package ai.ecma.appstaff.entity.view;

import ai.ecma.appstaff.enums.DateCompareOperatorTypeEnum;
import ai.ecma.appstaff.enums.DateFilterTypeEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDNotUserAuditEntityWithoutUpdated;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = TableNameConstant.VIEW_FILTER_FIELD_VALUE)
@DynamicInsert
@DynamicUpdate
public class ViewFilterFieldValue extends AbsUUIDNotUserAuditEntityWithoutUpdated {

    @JoinColumn(insertable = false,updatable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private ViewFilterField viewFilterField;

    @Column(name = "view_filter_field_id",nullable = false)
    private UUID viewFilterFieldId;

//========DATE START====================

    @Column(name = "date_filter_type")
    @Enumerated(EnumType.STRING)
    private DateFilterTypeEnum dateFilterType;

    @Column(name = "date_compare_operator_type")
    @Enumerated(EnumType.STRING)
    private DateCompareOperatorTypeEnum dateCompareOperatorType;

    @Column(name = "start_date")
    private Timestamp starDate;

    //AGAR BU TRUE BO'LSA starDate NI TIME_STAMPGA PARSE QILAMIZ AKS HODLA FAQAT DATE GA
    @Column(name = "start_date_time")
    private boolean starDateTime;

    @Column(name = "end_date")
    private Timestamp endDate;

    //AGAR BU TRUE BO'LSA endDate NI TIME_STAMPGA PARSE QILAMIZ AKS HODLA FAQAT DATE GA
    @Column(name = "end_date_time")
    private boolean endDateTime;

    @Column(name = "date_x_value")
    private Integer dateXValue;
//========DATE FINISH====================


    //RATING, NUMBER, MONEY LAR UCHUN START -> BULAR FILTER_FIELD NI ICHIDAGI CompareOperatorTypeEnum BILAN SOLISHTIRILADI
    @Column(name = "min_value")//REQURED(RAQAMLAR BILAN FILTER QILINSA DOIM KELADI)
    private String minValue;

    @Column(name = "max_value")
    private String maxValue;
    //RATING, NUMBER, MONEY UCHUN FINISH



    @Column(name = "selected_options", columnDefinition = "varchar[]")
    @Type(type = "ai.ecma.appstaff.type.GenericStringArrayType")
    private String[] selectedOptions;

    @Column(name = "searching_value")
    private String searchingValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ViewFilterFieldValue that = (ViewFilterFieldValue) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}