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

// HODIMGA TEGISHLI BO'LGAN TELEFON RAQAM TURLARI
@Entity
@Table(name = TableNameConstant.PHONE_NUMBER_TYPE)
@SQLDelete(sql = "update " + TableNameConstant.PHONE_NUMBER_TYPE + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNumberType extends AbsUUIDUserAuditEntity {

    // TELEFON RAQAM TURI
    // MASALAN (home)
    @Column(name = ColumnKey.NAME, nullable = false)
    private String name;

    // FRONTENDDA CHIQADIGAN RANGI
    @Column(name = ColumnKey.COLOR)
    private String color;

    @Column(name = ColumnKey.COMPANY_ID)
    private Long companyId;

}
