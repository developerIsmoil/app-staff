package ai.ecma.appstaff.entity.view;

import ai.ecma.appstaff.enums.FilterOperatorEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDNotUserAuditEntityWithoutUpdated;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@Getter
//@AllArgsConstructor
@NoArgsConstructor
@Entity(name = TableNameConstant.VIEW_FILTER)
@DynamicInsert
@DynamicUpdate
public class ViewFilter extends AbsUUIDNotUserAuditEntityWithoutUpdated {

    private UUID userId;

    private String tableName;

    private String name;

    @Column(name = "filter_option")
    @Enumerated(EnumType.STRING)
    private FilterOperatorEnum filterOperator = FilterOperatorEnum.AND;

    private String search = "";

    @OneToMany(mappedBy = "viewFilter")
    private List<ViewFilterSearchingColumn> viewFilterSearchingColumns = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "viewFilter")
    private List<ViewFilterField> fields = new ArrayList<>();
}