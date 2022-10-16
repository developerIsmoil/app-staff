package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.ColumnKey;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BO'LIM BU TIZIMDA HODIM QO'SHISH VA HISOBOTLAR UCHUN KERAK
 * HAR BIR HODIM QAYSIDIR BO'LIMGA BIRIKTIRILGAN BO'LISHI SHART
 */
@Entity
@Table(name = TableNameConstant.DEPARTMENT)
@SQLDelete(sql = "update " + TableNameConstant.DEPARTMENT + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department extends AbsUUIDUserAuditEntity {

    // BO'LIMNIG NOMI
    // BO'LIM NOMI TIZIMDA TAKRORLANMAS BO'LISHI KERAK.
    // unique = true QO'YILMAGANLIGINING SABABI AGAR 2 TA deleted = true BO'LIB QOLSA
    // YA'NI BITTA NOMLI BO'LIM IKKI MARTA O'CHIRILSA XATOLIKKA TUSHAMIZ YA'NI UNIKALLIK BUZILADI
    // SHUNING UCHUN DEPARTMENT QO'SHISHDAN OLDIN TEKSHIRIB OLGANMIZ
    // MASALAN (Academic)
    @Column(name = ColumnKey.NAME, nullable = false)
    private String name;

    // AGAR active FALSE BO'LSA SELECTLARDA KO'RINMAYDI.
    // BO'LIMLAR TABLITSASIDA active ning HAR QANDAY HOLATIDAHAM BO'LIMLAR KO'RINADI
    // MASALAN (true)
    @Column(name = ColumnKey.ACTIVE)
    private boolean active;

    @Column(name = ColumnKey.COMPANY_ID)
    private Long companyId;
}
