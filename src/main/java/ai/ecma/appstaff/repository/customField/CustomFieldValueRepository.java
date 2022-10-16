package ai.ecma.appstaff.repository.customField;

import ai.ecma.appstaff.entity.customField.CustomFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomFieldValueRepository extends JpaRepository<CustomFieldValue, UUID> {

    List<CustomFieldValue> findAllByOwnerId(String ownerId);
    List<CustomFieldValue> findAllByOwnerIdIn(Collection<String> ownerId);

//    Optional<CustomFieldValue> findFirstByOwnerIdAndCustomFieldId(Long ownerId, UUID customField_id);


    //BERILGAN OWNER ID VA CUSTOM_FIELD_ID GA TENG CUSTOM_FIELD_VALUE NI QAYTARADI
    Optional<CustomFieldValue> findFirstByOwnerIdAndCustomFieldId(String ownerId, UUID customField_id);


    //CUSTOM_FIELD_ID LIST VA OWNER_ID_LIST QAY TARTIB DA BO'LSA SHU KETMA-KETLIKDAGI CUSTOM_FIELD_VALUE LARNI DB DAN OLIB KELADI
    @Query(value = "select *\n" +
            "from custom_field_value\n" +
            "where deleted = false\n" +
            "  and custom_field_id in :customFieldIdLinkedList\n" +
            "  and owner_id in :ownerIdLinkedList",nativeQuery = true)
    List<CustomFieldValue> findAllCustomFieldValueListByCustomFieldIdLinkedListAndOwnerIdLinkedList(@Param("customFieldIdLinkedList") List<UUID> customFieldIdLinkedList,
                                                                                                    @Param("ownerIdLinkedList")List<String> ownerIdLinkedList);

    //CUSTOM FIELD LARNING QIYMATLARINI QAYTARADI
    @Query(value = "select *\n" +
            "from custom_field_value cfv\n" +
            "where owner_id =:ownerId\n" +
            "  and custom_field_id in :customFieldIds\n" +
            "  and deleted = false", nativeQuery = true)
    List<CustomFieldValue> findAllByOwnerAndCustomFieldIds(@Param("ownerId") String ownerId,
                                                           @Param("customFieldIds") List<UUID> customFieldIds);

}
