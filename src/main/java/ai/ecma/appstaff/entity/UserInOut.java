package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.entity.template.AbsUUIDNotUserAuditEntityWithoutUpdated;
import ai.ecma.appstaff.enums.UserInOutEnum;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity(name = "user_in_out")
@SQLDelete(sql = "UPDATE user_in_out SET deleted=true WHERE id=?")
@Where(clause = "deleted=false")
public class UserInOut extends AbsUUIDNotUserAuditEntityWithoutUpdated {

    @JoinColumn(insertable = false,updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private UserDailyInOut userDailyInOut;

    @Column(name = "user_daily_in_out_id")
    private UUID userDailyInOutId;//QAYSI KUNGA TEGISHLI

    @Enumerated(EnumType.STRING)
    private UserInOutEnum status;//KIRDI CHIQTI

    //TIMESTAMP
    private Timestamp timestamp;//TURNIKET BERGAN VAQT

}
