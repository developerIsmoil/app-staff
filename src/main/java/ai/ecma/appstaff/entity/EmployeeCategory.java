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

// HODIM KATEGORIYASI
// OYLIK MAOSH, DARS O'TADIGAN GURUHLARI, SUPPORT QILINADIGAN MODULLARI VA HK LAR
// SHU HODIM KATEGORIYASIGA QARAB BELGILANADI
@Entity
@Table(name = TableNameConstant.EMPLOYEE_CATEGORY)
@SQLDelete(sql = "UPDATE " + TableNameConstant.EMPLOYEE_CATEGORY + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCategory extends AbsUUIDUserAuditEntity {

    // QAYSI BO'LIMGA TEGISHLI EKANLIGI
    // MASALAN (Academic)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.DEPARTMENT_ID)
    private Department department;

    // QAYSI LAVOZIMGA TEGISHLI EKANLIGI
    // MASALAN (Mentor)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.POSITION_ID)
    private Position position;

    // QAYSI HODIM KATEGORIYA TURI EKANLIGI
    // MASALAN (A1)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.EMPLOYEE_CATEGORY_TYPE_ID)
    private EmployeeCategoryType employeeCategoryType;

    // TALABLAR
    // MASALAN (A1 kategoriyadagi mentor uchun talablar)
    @Column(name = ColumnKey.REQUIREMENT, columnDefinition = "text")
    private String requirement;

    // IZOH
    // MASALAN (A1 kategoriyadagi mentor haqida izoh)
    @Column(name = ColumnKey.DESCRIPTION, columnDefinition = "text")
    private String description;

    // HOLATI
    // MASALAN (true)
    @Column(name = ColumnKey.ACTIVE)
    private boolean active;

    // view uchun faqat  (position -> name + employeeCategoryType -> name)
    @Column(name = ColumnKey.NAME)
    private String name;

    @Column(name = ColumnKey.COMPANY_ID)
    private Long companyId;

}