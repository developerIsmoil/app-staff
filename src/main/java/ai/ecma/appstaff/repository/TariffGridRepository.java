package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.TariffGrid;
import ai.ecma.appstaff.enums.PaymentCriteriaTypeEnum;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TariffGridRepository extends JpaRepository<TariffGrid, UUID> {

    Optional<TariffGrid> findByDepartment_IdAndBranchIdAndCompanyIdAndPosition_IdAndPaymentCriteriaTypeAndEmployeeCategory_IdAndActiveIsTrue(
            UUID department_id,
            Long branchId,
            Long companyId,
            UUID position_id,
            PaymentCriteriaTypeEnum paymentCriteriaType,
            UUID employeeCategory_id);

    boolean existsByDepartment_IdAndBranchIdAndCompanyIdAndPosition_IdAndPaymentCriteriaTypeAndEmployeeCategory_IdAndActiveIsTrue(
            UUID department_id,
            Long branchId,
            Long companyId,
            UUID position_id,
            PaymentCriteriaTypeEnum paymentCriteriaType,
            UUID employeeCategory_id);

    boolean existsByDepartment_IdAndBranchIdAndCompanyIdAndPosition_IdAndPaymentCriteriaTypeAndEmployeeCategory_Id(
            UUID department_id,
            Long branchId,
            Long companyId,
            UUID position_id,
            PaymentCriteriaTypeEnum paymentCriteriaType,
            UUID employeeCategory_id);

    boolean existsByDepartment_IdAndBranchIdAndCompanyIdAndPosition_IdAndPaymentCriteriaTypeAndEmployeeCategory_IdAndIdNot(
            UUID department_id,
            Long branchId,
            Long companyId,
            UUID position_id,
            PaymentCriteriaTypeEnum paymentCriteriaType,
            UUID employeeCategory_id,
            UUID id);


    List<TariffGrid> findAllByActiveTrue(Sort sort);

    boolean existsByDepartmentId(UUID department_id);

    boolean existsByPositionId(UUID position_id);

    boolean existsByEmployeeCategoryId(UUID employeeCategory_id);

    @Query(
            value = "select count(*) > 0 as exists\n" +
                    "from tariff_grid tg\n" +
                    "where tg.deleted = false\n" +
                    "  and tg.active = true\n" +
                    "  and (case\n" +
                    "           when (:companyId) isnull then tg.company_id isnull\n" +
                    "           else cast(tg.company_id as varchar) = cast((:companyId) as varchar) end)\n" +
                    "  and (case\n" +
                    "           when (:branchId) isnull then tg.branch_id isnull\n" +
                    "           else cast(tg.branch_id as varchar) = cast((:branchId) as varchar) end)\n" +
                    "  and cast(tg.employee_category_id as varchar) = cast((:employeeCategoryId) as varchar)\n" +
                    "  and cast(tg.department_id as varchar) = cast((:departmentId) as varchar)\n" +
                    "  and cast(tg.position_id as varchar) = cast((:positionId) as varchar)\n" +
                    "  and cast(tg.payment_criteria_type as varchar) = cast((:paymentCriteriaType) as varchar);",
            nativeQuery = true
    )
    Boolean existsByDepartmentIdBranchIdCompanyIdPositionIdPaymentCriteriaTypeEmployeeCategoryId(
            @Param("departmentId") UUID departmentId,
            @Param("branchId") Long branchId,
            @Param("companyId") Long companyId,
            @Param("positionId") UUID positionId,
            @Param("paymentCriteriaType") PaymentCriteriaTypeEnum paymentCriteriaType,
            @Param("employeeCategoryId") UUID employeeCategoryId
    );


    @Query(
            value = "select count(*) > 0 as exists\n" +
                    "from tariff_grid tg\n" +
                    "where tg.deleted = false\n" +
                    "  and tg.active = true\n" +
                    "  and (case\n" +
                    "           when (:companyId) isnull then tg.company_id isnull\n" +
                    "           else cast(tg.company_id as varchar) = cast((:companyId) as varchar) end)\n" +
                    "  and (case\n" +
                    "           when (:branchId) isnull then tg.branch_id isnull\n" +
                    "           else cast(tg.branch_id as varchar) = cast((:branchId) as varchar) end)\n" +
                    "  and cast(tg.employee_category_id as varchar) = cast((:employeeCategoryId) as varchar)\n" +
                    "  and cast(tg.department_id as varchar) = cast((:departmentId) as varchar)\n" +
                    "  and cast(tg.position_id as varchar) = cast((:positionId) as varchar)\n" +
                    "  and cast(tg.payment_criteria_type as varchar) = cast((:paymentCriteriaType) as varchar)\n" +
                    "  and cast(tg.id as varchar) <> cast((:id) as varchar);",
            nativeQuery = true
    )
    Boolean existsByDepartmentIdBranchIdCompanyIdPositionIdPaymentCriteriaTypeEmployeeCategoryIdIdNot(
            @Param("departmentId") UUID departmentId,
            @Param("branchId") Long branchId,
            @Param("companyId") Long companyId,
            @Param("positionId") UUID positionId,
            @Param("paymentCriteriaType") PaymentCriteriaTypeEnum paymentCriteriaType,
            @Param("employeeCategoryId") UUID employeeCategoryId,
            @Param("id") UUID id);
}
