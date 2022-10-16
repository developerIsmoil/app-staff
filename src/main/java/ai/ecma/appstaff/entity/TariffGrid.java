package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.enums.BonusType;
import ai.ecma.appstaff.enums.PaymentCriteriaTypeEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.ColumnKey;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Objects;

// TARIF SETKASI.
// SHU SETKAGA QARAB HODIMLARGA OYLIK MAOSH BELGILANADI
@Entity
@Table(name = TableNameConstant.TARIFF_GRID)
@SQLDelete(sql = "update " + TableNameConstant.TARIFF_GRID + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TariffGrid extends AbsUUIDUserAuditEntity {


    @Column(name = ColumnKey.COMPANY_ID)
    private Long companyId;

    // QAYSI FILIALGA TEGISHLI EKANLIGI
    @Column(name = ColumnKey.BRANCH_ID)
    private Long branchId;

    // QAYSI BO'LIMGA TEGISHLI EKANLIGI
    @JoinColumn(name = ColumnKey.DEPARTMENT_ID)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ToString.Exclude
    private Department department;

    // QAYSI LAVOZIMGA TEGISHLI EKANLIGI
    @JoinColumn(name = ColumnKey.POSITION_ID)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ToString.Exclude
    private Position position;

    // QAYSI HODIM KATEGORIYASIGA TEGISHLI EKANLIGI
    @JoinColumn(name = ColumnKey.EMPLOYEE_CATEGORY_ID)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ToString.Exclude
    private EmployeeCategory employeeCategory;

    // HODIMGA BERILADIGAN OYLIK KRITERIYASI.
    // YA'NI NIMA QILGANI UCHUN OYLIK BERILADI.
    // ISHLAGAN SOATIGAMI, QILGAN ISHIGAMI VA BOSHQALAR
    @Enumerated(EnumType.STRING)
    @Column(name = ColumnKey.PAYMENT_CRITERIA_TYPE)
    private PaymentCriteriaTypeEnum paymentCriteriaType;

    // BERILADIGAN FIX SUMMA
    @Column(name = ColumnKey.PAYMENT_AMOUNT, nullable = false)
    private Double paymentAmount;

    @Column(name = ColumnKey.DESCRIPTION)
    private String description;

    // AGAR KUNLIK PUL OLADIGAN HODIM BO'LSA TRUE BO'LADI
    @Column(name = ColumnKey.DAY)
    private boolean day;

    // ISHLAGAN KUNI UCHUN QANCHA OLISHI
    @Column(name = ColumnKey.DAY_PAYMENT_AMOUNT)
    private double dayPaymentAmount;

    // AGAR HODIM ISHLAGAN SOATIGA PUL OLSA TRUE BO'LADI
    @Column(name = ColumnKey.HOUR)
    private boolean hour;

    // SOATIGA QANCHA PUL OLISHI
    @Column(name = ColumnKey.HOUR_PAYMENT_AMOUNT)
    private double hourPaymentAmount;

    // QAYSI BONUS TURIDAN BONUS BERILADI
    @Column(name = ColumnKey.BONUS_TYPE)
    @Enumerated(EnumType.STRING)
    private BonusType bonusType;

    // BONUS SUMMASI
    @Column(name = ColumnKey.BONUS_PERCENT)
    private Double bonusPercent;

    // BU SETKANI HOLATI
    @Column(name = ColumnKey.ACTIVE)
    private boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TariffGrid that = (TariffGrid) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
