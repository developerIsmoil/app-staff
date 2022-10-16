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

import javax.persistence.*;

// KASALLIK VARAQASI UCHUN SHABLON
@Entity
@Table(name = TableNameConstant.TEMPLATE_FOR_SICK)
@SQLDelete(sql = "update " + TableNameConstant.TEMPLATE_FOR_SICK + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateForSick extends AbsUUIDUserAuditEntity {

    // IMTIYOZ UCHUNMI
    // AGAT privilege = FALSE BO'LSA FROM BILAN TO NULL BO'LADI VA privilegeType TANLANADI
    // privilege = TRUE BO'LSA FROM BILAN TO GA QIYMAT YOZILADI VA privilegeType NULL BO'LADI
    @Column(name = ColumnKey.PRIVILEGE)
    private boolean privilege;

    // IMTIYOZ.
    // KASALLIK VARAQASI UCHUN IMTIYOZ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ColumnKey.PRIVILEGE_TYPE_ID)
    private PrivilegeType privilegeType;

    // ISHLASH DAVRI MASALAN: FROM GA 2 TO GA 5 BO'LSA 2-5 YIL ORALIQDA ISH TAJRIBASI BOR HISOBLANADI
    @Column(name = ColumnKey.FROM_COUNT)
    private Integer fromCount;

    // ISHLASH DAVRI MASALAN: FROM GA 2 TO GA 5 BO'LSA 2-5 YIL ORALIQDA ISH TAJRIBASI BOR HISOBLANADI
    @Column(name = ColumnKey.TO_COUNT)
    private Integer toCount;

    // FIX OYLIGINING NECHA FOIZI BERILSIN
    @Column(name = ColumnKey.PERCENT)
    private Double percent;

    // HOLATI
    @Column(name = ColumnKey.ACTIVE)
    private boolean active;

    @Column(name = ColumnKey.COMPANY_ID)
    private Long companyId;

}
