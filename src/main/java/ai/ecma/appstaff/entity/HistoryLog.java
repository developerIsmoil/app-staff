package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.enums.EntityFieldNameEnum;
import ai.ecma.appstaff.enums.EntityNameEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.ColumnKey;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

// ENTITYLARDA BO'LGAN O'ZGARISHLARNI LOG QILIB HISTORYGA YOZIB BORISH UCHUN
@Entity
@Table(name = TableNameConstant.HISTORY_LOG)
@SQLDelete(sql = "update " + TableNameConstant.HISTORY_LOG + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryLog extends AbsUUIDUserAuditEntity {

    // QAYSI FIELDDA O'ZGARISH BO'LGANLIGI
    @Column(name = ColumnKey.FIELD_NAME, nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityFieldNameEnum fieldName;

    // O'ZGARISHDAN AVVALGI QIYMATI
    @Column(name = ColumnKey.OLD_VALUE)
    private String oldValue;

    // O'ZGARTIRILGAN YANGI QIYMAT
    @Column(name = ColumnKey.NEW_VALUE)
    private String newValue;

    // QAYSI ENTITYDA O'ZGARISH BO'LGANLIGI
    @Column(name = ColumnKey.ENTITY_NAME, nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityNameEnum entityName;


}
