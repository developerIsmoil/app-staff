package ai.ecma.appstaff.entity.template;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class AbsUUIDUserAuditEntityWithoutUpdated extends AbsUserAuditWithoutUpdated {
    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AbsUUIDUserAuditEntityWithoutUpdated that = (AbsUUIDUserAuditEntityWithoutUpdated) o;
        return id != null && Objects.equals(id.hashCode(), that.id.hashCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
