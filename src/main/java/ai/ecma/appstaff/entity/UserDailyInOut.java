package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.entity.template.AbsUUIDNotUserAuditEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity(name = "user_daily_in_out")
@SQLDelete(sql = "UPDATE user_daily_in_out SET deleted=true WHERE id=?")
@Where(clause = "deleted=false")
public class UserDailyInOut extends AbsUUIDNotUserAuditEntity {

    @JoinColumn(insertable = false,updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Employee employee;//QAYSI EMPLOYEE

    @Column(name = "employee_id")
    private UUID employeeId;

    private boolean isLate;//KECHIKKANMI

    private long workingTimes;//QANCHA BINODA BO'LGAN SONIYADA

    private Timestamp firstEnterTimeStamp;//ISHXONAGA 1-KECHIKMAY KELGAN VAQTI

    private Timestamp lastExitTimeStamp;//ENG OXIRGI CHIQIB KETGAN VAQTI

    private Timestamp lastEnterTimeStamp;//ENG OXIRGI KIRIB KELGAN VAQTI

    private Date date;//QAYSI KUN UCHUN

    private boolean inOffice;//OFFICEDAMI SHU ISHCHI

    @OneToMany(mappedBy = "userDailyInOut")
    @ToString.Exclude
    @OrderBy(value = "timestamp")
    private List<UserInOut> userInOuts = new ArrayList<>();

}
