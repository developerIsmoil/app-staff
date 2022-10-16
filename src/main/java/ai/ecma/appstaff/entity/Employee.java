package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.enums.GenderEnum;
import ai.ecma.appstaff.enums.MaritalStatusEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.ColumnKey;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.UUID;


/**
 * HODIM. TIZIMDAGI BARCHA HODIMLAR MA'LUM BIR HUQUQI BO'LGAN USER TOMONIDAN TIZIMGA QO'SHILADI.
 * ALOHIDA RO'YXATDAN O'TISH DEGAN JOYI YO'Q
 * TIZIMDAN FOYDALANISHI YOKI FOYDALANMASLIGI HAM MUMKIN (access=false).
 */
@Entity
@Table(name = TableNameConstant.EMPLOYEE)
@SQLDelete(sql = "update " + TableNameConstant.EMPLOYEE + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class Employee extends AbsUUIDUserAuditEntity {

    // HODIM USER BO'LISHI SHART.
    // HODIM YARATILAYOTGANDA AUTH-SERVICE GA BORIB USER YARATIB KELINADI VA HODIM YARATILADI
    @Column(name = ColumnKey.USER_ID)
    private UUID userId;

    // HODIMNING ISMI
    // MASALAN (Ixtiyor)
    @Column(name = ColumnKey.FIRST_NAME, nullable = false)
    private String firstName;

    // HODIMNING FAMILIYASI
    // MASALAN (Xaitov)
    @Column(name = ColumnKey.LAST_NAME, nullable = false)
    private String lastName;

    // HODIMNING OTASINING ISMI
    // MASALAN (Baxtiyor o'g'li)
    @Column(name = ColumnKey.MIDDLE_NAME)
    private String middleName;

    // HODIMNING RASMI
    // RASM BOSHQA SERVICEGA (ATTACHMENT-SERVICE) YUKLANGAN VA
    // UNING URL ADDRESI BU YERGA YOZIB QO'YILGAN BO'LADI
    // MASALAN ('https://attachment.pdp.uz/photo/ce3e7254-5c41-4294-b4cd-9ad188f8da65')
    @Column(name = ColumnKey.PHOTO_ID)
    private String photoId;

    // HODIMNING TUG'ILGAN SANASI
    // MASALAN (19/05/2000)
    @Column(name = ColumnKey.BIRTH_DATE, nullable = false)
    private Date birthDate;

    // HODIMNING OILAVIY HOLATI
    // MASALAN (SINGLE)
    @Column(name = ColumnKey.MARITAL_STATUS)
    @Enumerated(EnumType.STRING)
    private MaritalStatusEnum maritalStatus;

    // HODIMNING JINSI
    // MASALAN (MALE)
    @Column(name = ColumnKey.GENDER, nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    // HODIMING EMAILI
    // MASALAN (ikhtiyordev@gmail.com)
    @Column(name = ColumnKey.EMAIL)
    private String email;

    // PASSPORT MA'LUMOTLARI {SERIYASI}
    // MASALAN (AA)
    @Column(name = ColumnKey.PASSPORT_SERIAL)
    private String passportSerial;

    // PASSPORT MA'LUMOTLARI {RAQAMI}
    // MASALAN (1234567)
    @Column(name = ColumnKey.PASSPORT_NUMBER)
    private String passportNumber;

    // PASSPORT MA'LUMOTLARI {BERILGAN ORGAN NOMI}
    // MASALAN (IIB QARSHI)
    @Column(name = ColumnKey.PASSPORT_GIVEN_ORGANISATION)
    private String passportGivenOrganisation;

    // PASSPORT MA'LUMOTLARI {BERILGAN SANA}
    // MASALAN (01.01.2017)
    @Column(name = ColumnKey.PASSPORT_GIVEN_DATE)
    private Date passportGivenDate;

    // PASSPORT MA'LUMOTLARI {MUDDATI}
    // MASALAN (01.01.2027)
    @Column(name = ColumnKey.PASSPORT_EXPIRE_DATE)
    private Date passportExpireDate;

    // PASSPORT MA'LUMOTLARI {ASL YASHASH MANZILI}
    // MASALAN (Qarshi)
    @Column(name = ColumnKey.PERMANENT_ADDRESS)
    private String permanentAddress;

    // PASSPORT MA'LUMOTLARI {HOZIRDA YASHAYOTGAN MANZILI}
    // MASALAN (Toshkent)
    @Column(name = ColumnKey.CURRENT_ADDRESS)
    private String currentAddress;

    // JSHSHR
    // MASALAN (123455)
    @Column(name = ColumnKey.PERSONAL_NUMBER)
    private String personalNumber;

    // AGAR access TRUE BO'LSA TIZIMDAN FOYDALANA OLADI
    // access FALSE BO'LSA TIZIMDAN FOYDALANMAYDI SHUNCHAKI HISOBOT UCHUN KERAK
    // MASALAN (false)
    @Column(name = ColumnKey.ACCESS)
    private boolean access;

    // HODIMGA BERILGA IMTIYOZLARI
    // MASALAN (imtiyoz)
    @ManyToMany
    @JoinTable(
            name = ColumnKey.EMPLOYEE_PRIVILEGE_TYPE,
            joinColumns = {@JoinColumn(name = ColumnKey.EMPLOYEE_ID)},
            inverseJoinColumns = {@JoinColumn(name = ColumnKey.PRIVILEGE_TYPE_ID)})
    private Set<PrivilegeType> privilegeTypes;

    // HODIMNING QOBILYATLARI
    // MASALAN (java, spring boot)
    @ManyToMany
    @JoinTable(
            name = ColumnKey.EMPLOYEE_SKILL,
            joinColumns = {@JoinColumn(name = ColumnKey.EMPLOYEE_ID)},
            inverseJoinColumns = {@JoinColumn(name = ColumnKey.SKILL_ID)})
    private Set<Skill> skills;

    @Column(name = ColumnKey.ROLES, columnDefinition = "bigint[]")
    @Type(type = "ai.ecma.appstaff.type.GenericLongArrayType")
    private Long[] roles;

    // faqat view uchun kerak bu boshqa joyda ishlatilmaydi
    // TELEFON RAQAM
    // MASALAN (+998 99 999 99 99)
    @Column(name = ColumnKey.PHONE_NUMBER
//            , nullable = false
    )
    private String phoneNumber;


    // HODIMNING ISHDAN BO'SHAGAN SANASI
    // ishdan butunlay boshatish uchun
    @Column(name = ColumnKey.RESIGNATION_DATE)
    private Date resignationDate;

    // HODIMNING ISHDAN BO'SHAGAN SANASI
    // ishdan butunlay boshatish uchun
    @Column(name = ColumnKey.RESIGNATION_DESCRIPTION, columnDefinition = "text")
    private String resignationDescription;

    // ISHDAN BO'LGAN BO'LSA TRUE BO'LADI
    // ishdan butunlay boshatish uchun
    @Column(name = ColumnKey.RESIGNATION)
    private Boolean resignation;

    //XODIMNI TURNIKETDAGI ID SI KIRDI CHIQTI UCHUN
    @Column(name = ColumnKey.TURNIKET_ID)
    private Integer turniketId;
}
