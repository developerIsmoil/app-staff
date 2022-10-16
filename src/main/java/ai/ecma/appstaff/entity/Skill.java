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

// QOBILYATLARI
@Entity
@Table(name = TableNameConstant.SKILL)
@SQLDelete(sql = "update " + TableNameConstant.SKILL + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Skill extends AbsUUIDUserAuditEntity {

    // NOMI
    @Column(name = ColumnKey.NAME, nullable = false)
    private String name;

    // RANGI
    @Column(name = ColumnKey.COLOR, nullable = false)
    private String color;

    @Column(name = ColumnKey.COMPANY_ID)
    private Long companyId;

}
