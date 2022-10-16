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
import java.util.UUID;


// HODIMGA TEGISHLI BO'LSA ATTACHMENTLAR (PASSPORT RASMLARI, DIPLOM, SERTIFICAT, ...)
@Entity
@Table(name = TableNameConstant.EMPLOYEE_ATTACHMENT)
@SQLDelete(sql = "update " + TableNameConstant.EMPLOYEE_ATTACHMENT + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAttachment extends AbsUUIDUserAuditEntity {

    // ATTACHMENT TIZIMDA MAVJUD AYNAN BIRON BIR HODIMGA TEGISHLI BO'LISHI SHART
    // MASALAN (Ixtiyor Xaitov)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.EMPLOYEE_ID)
    private Employee employee;

    // FILE BOSHQA SERVICEGA (ATTACHMENT-SERVICE) YUKLANGAN VA
    // UNING IDSI BU YERGA YOZIB QO'YILGAN BO'LADI
    // MASALAN ('ce3e7254-5c41-4294-b4cd-9ad188f8da65')
    @Column(name = ColumnKey.FILE_ID)
    private String fileId;

    // ATTACHMENT HAQIDA MA'LUMOT
    // BUNDA BU FILE NIMA EKANLIGI VA BU FILE HAQIDA TO'LIQ MA'LUMOT BERILISHI MUMKIN
    // MASALAN (passport rasmi)
    @Column(name = ColumnKey.DESCRIPTION, columnDefinition = "text")
    private String description;

    @Column(name = ColumnKey.ACTION)
    private Boolean action = Boolean.TRUE;

    public EmployeeAttachment(Employee employee, String fileId, String description) {
        this.employee = employee;
        this.fileId = fileId;
        this.description = description;
    }
}
