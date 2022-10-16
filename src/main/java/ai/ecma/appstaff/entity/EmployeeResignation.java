package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.ColumnKey;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;


/**
 * HODIM. TIZIMDAGI BARCHA HODIMLAR MA'LUM BIR HUQUQI BO'LGAN USER TOMONIDAN TIZIMGA QO'SHILADI.
 * ALOHIDA RO'YXATDAN O'TISH DEGAN JOYI YO'Q
 * TIZIMDAN FOYDALANISHI YOKI FOYDALANMASLIGI HAM MUMKIN (access=false).
 */
@Entity
@Table(name = TableNameConstant.EMPLOYEE_RESIGNATION)
@SQLDelete(sql = "update " + TableNameConstant.EMPLOYEE_RESIGNATION + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class EmployeeResignation extends AbsUUIDUserAuditEntity {

    @ManyToOne(optional = false)
    private Employee employee;

    // HODIMNING ISHDAN BO'SHAGAN SANASI
    // QAYSIDIR BO'LIMDAN ISHDAN BO'SHASHI UCHUN
    @Column(name = ColumnKey.DATE)
    private Date date;

    @Column(name = ColumnKey.DESCRIPTION)
    private String description;

}
