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
import javax.persistence.Entity;
import javax.persistence.Table;

// HODIM KATEGORIYA TURI
// HODIM KATEGORIYASI UCHUN KERAK.
// HODIM KATEGORIYASINI YARATGANDA QAYTA QAYTA A1, A2 LARNI YOZMASLIK UCHUN KIRITIB QO'YILADI
@Entity
@Table(name = TableNameConstant.EMPLOYEE_CATEGORY_TYPE)
@SQLDelete(sql = "update " + TableNameConstant.EMPLOYEE_CATEGORY_TYPE + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCategoryType extends AbsUUIDUserAuditEntity {

    //KATEGORY NOMI
    // MASALAN (A1,A2,B3...)
    @Column(name = ColumnKey.NAME)
    private String name;

    //ACTIVE FALSE BO'LSA SELECTLARGA CHIQMAYDI
    // MASALAN (true)
    @Column(name = ColumnKey.ACTIVE)
    private boolean active;

    @Column(name = ColumnKey.COMPANY_ID)
    private Long companyId;
}
