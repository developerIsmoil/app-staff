package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.ColumnKey;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.Set;

// BAYRAMLAR. TIZIMGA BAYRAM KUNLARINI KIRITIB QO'YILSA
// BU KUNLAR HODIMLAR UCHUN ISH KUNI BO'LMASLIGI MUMKIN
@Entity
@Table(name = TableNameConstant.HOLIDAY)
@SQLDelete(sql = "update " + TableNameConstant.HOLIDAY + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Holiday extends AbsUUIDUserAuditEntity {

    // BAYRAM NOMI
    @Column(name = ColumnKey.NAME, nullable = false)
    private String name;

    // OYLIK HISOBLANADIMI YO'QMI. AGAR BU FALSE BO'LSA DEMAK HODIM BU KUNDA ISHLAMAYDI
    @Column(name = ColumnKey.CALC_MONTHLY_SALARY)
    private boolean calcMonthlySalary;

    // HOLATI
    @Column(name = ColumnKey.ACTIVE)
    private boolean active;

    // QAYSI KUNLAR EKANLIGI
    @ElementCollection
    private Set<Date> dateList;
}
