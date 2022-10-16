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

// IMTIYOZ TURI
@Entity
@Table(name = TableNameConstant.PRIVILEGE_TYPE)
@SQLDelete(sql = "update " + TableNameConstant.PRIVILEGE_TYPE + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeType extends AbsUUIDUserAuditEntity {

    // NOMI
    @Column(name = ColumnKey.NAME, nullable = false)
    private String name;

    // HOLATI
    @Column(name = ColumnKey.ACTIVE)
    private boolean active;

    @Column(name = ColumnKey.COMPANY_ID)
    private Long companyId;

}
