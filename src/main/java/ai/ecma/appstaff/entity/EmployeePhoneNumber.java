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

// HODIMGA TEGISHLI BO'LGAN BARCHA TELEFON RAQAMLAR
@Entity
@Table(name = TableNameConstant.EMPLOYEE_PHONE_NUMBER)
@SQLDelete(sql = "update " + TableNameConstant.EMPLOYEE_PHONE_NUMBER + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePhoneNumber extends AbsUUIDUserAuditEntity {

    // QAYSI HODIMGA TEGISHLI EKANLIGI
    // MASALAN (Ixtiyor Xaitov)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.EMPLOYEE_ID)
    private Employee employee;

    // TELEFON RAQAM
    // MASALAN (+998 99 999 99 99)
    @Column(name = ColumnKey.PHONE_NUMBER, nullable = false)
    private String phoneNumber;

    // HODIMGA TEGISHLI BO'LGAN TELEFON RAQAM TURLARI
    // MASALAN (home)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.TYPE_ID)
    private PhoneNumberType type;

    // AGAR TELEFON RAQAM ASOSIY BO'LSA main = TRUE BO'LADI.
    // BITTA HODIMDA FAQAT BITTA ASOSIY TELEFON RAQAM BO'LISHI MUMKIN
    // (BUNDA DELETE BO'LGANLARI HISOBGA OLINMAYDI)
    @Column(name = ColumnKey.MAIN)
    private boolean main;

    public EmployeePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
