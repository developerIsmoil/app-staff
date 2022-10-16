package ai.ecma.appstaff.entity.view;

import ai.ecma.appstaff.enums.RowSizeEnum;
import ai.ecma.appstaff.enums.ViewTypeEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntityWithoutUpdated;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;



@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = TableNameConstant.VIEW_OBJECT)
@SQLDelete(sql = "update " + TableNameConstant.VIEW_OBJECT + " set deleted=true where id = ?")
@Where(clause = "deleted=false")
@DynamicInsert
@DynamicUpdate
public class ViewObject extends AbsUUIDUserAuditEntityWithoutUpdated {

    //USHBU VIEW QAYSI TABLE GA TEGISHLI
    @Column(name = "table_name", nullable = false)
    private String tableName;

    private String name = "Default";

    private boolean defaultView;

    private boolean autoSave;

    @OneToMany(mappedBy = "viewObject")
    @OrderBy(value = "pinned DESC, orderIndex")
    private List<ViewColumn> columnList;

    @OneToMany(mappedBy = "viewObject")
    private List<ViewSorting> sortingList;

    private boolean publicly = true;

    private boolean shared = false;

    @Column(name = "row_size")
    @Enumerated(EnumType.STRING)
    private RowSizeEnum rowSize;

    @JoinColumn(insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ViewFilter viewFilter;

    @Column(name = "view_filter_id")
    private UUID viewFilterId;

//    @OneToMany(mappedBy = "viewObject")
//    @Where(clause = "name is not null")
//    private List<ViewFilter> filters;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ViewTypeEnum type;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;

    public ViewObject(boolean autoSave, boolean publicly, RowSizeEnum rowSize, ViewTypeEnum type) {
        this.autoSave = autoSave;
        this.publicly = publicly;
        this.rowSize = rowSize;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ViewObject that = (ViewObject) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    //DEFAULT VIEW NI OCHIB OLISH UCHUN
    public ViewObject(String tableName, String name, boolean defaultView, boolean autoSave, boolean publicly, boolean shared, RowSizeEnum rowSize, ViewTypeEnum type) {
        this.tableName = tableName;
        this.name = name;
        this.defaultView = defaultView;
        this.autoSave = autoSave;
        this.publicly = publicly;
        this.shared = shared;
        this.rowSize = rowSize;
        this.type = type;
    }

    public ViewObject(String tableName, String name, boolean defaultView, boolean autoSave, List<ViewColumn> columnList, boolean publicly, boolean shared, RowSizeEnum rowSize, ViewTypeEnum type) {
        this.tableName = tableName;
        this.name = name;
        this.defaultView = defaultView;
        this.autoSave = autoSave;
        this.columnList = columnList;
        this.publicly = publicly;
        this.shared = shared;
        this.rowSize = rowSize;
        this.type = type;
    }
}
