package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.PrivilegeType;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EmployeeDTO;
import ai.ecma.appstaff.payload.EmployeeResignationDTO;
import ai.ecma.appstaff.payload.UserDTO;
import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.payload.feign.EmploymentInfoFeignDTO;
import ai.ecma.appstaff.projection.IEmployee;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The interface Employee service.
 */
public interface EmployeeService {

    /**
     * Add employee api result.
     *
     * @param employeeDTO the employee dto
     * @return the api result
     */
    ApiResult<?> addEmployee(EmployeeDTO employeeDTO);

    /**
     * Edit employee api result.
     *
     * @param id          the id
     * @param employeeDTO the employee dto
     * @return the api result
     */
    ApiResult<?> editEmployee(UUID id, EmployeeDTO employeeDTO);

    /**
     * Gets all employee.
     *
     * @param page the page
     * @param size the size
     * @return the all employee
     */
    ApiResult<?> getAllEmployee(Integer page, Integer size);

    /**
     * Gets all employee.
     *
     * @return the all employee
     */
    ApiResult<?> getAllEmployee();

    /**
     * Gets one employee.
     *
     * @param id the id
     * @return the one employee
     */
    ApiResult<EmployeeDTO> getOneEmployee(UUID id);

    /**
     * Gets employee form id.
     *
     * @param id the id
     * @return the employee form id
     */
    ApiResult<?> getEmployeeFormById(UUID id);

    /**
     * Delete employee api result.
     *
     * @param id the id
     * @return the api result
     */
    ApiResult<?> deleteEmployee(UUID id);

    /**
     * Gets employee form.
     *
     * @return the employee form
     */
    ApiResult<?> getEmployeeForm();

    /**
     * Edit view row data api result.
     *
     * @param viewId the view id
     * @param rowId  the row id
     * @param map    the map
     * @return the api result
     */
    ApiResult<?> editViewRowData(UUID viewId, UUID rowId, Map<String, Object> map);

    /**
     * Gets branches by user id.
     *
     * @param userId the user id
     * @return the branches by user id
     */
    List<BranchFeignDTO> getBranchesByUserId(UUID userId);

    /**
     * Exists by department id.
     *
     * @param departmentId the department id
     */
    void existsByDepartmentId(UUID departmentId);

    /**
     * Exists by employee category id.
     *
     * @param employeeCategoryId the employee category id
     */
    void existsByEmployeeCategoryId(UUID employeeCategoryId);

    /**
     * Exists by employee category type id.
     *
     * @param id the id
     */
    void existsByEmployeeCategoryTypeId(UUID id);

    /**
     * Exists by phone number type id.
     *
     * @param id the id
     */
    void existsByPhoneNumberTypeId(UUID id);

    /**
     * Exists by position id.
     *
     * @param id the id
     */
    void existsByPositionId(UUID id);

    /**
     * Exists by employee privilege type id.
     *
     * @param privilegeType the privilege type
     */
    void existsByEmployeePrivilegeTypeId(PrivilegeType privilegeType);

    /**
     * Gets employment info by id.
     *
     * @param uuidList the uuid list
     * @return the employment info by id
     */
    List<EmploymentInfoFeignDTO> getEmploymentInfoById(List<UUID> uuidList);

    /**
     * Gets employment info full info by id.
     *
     * @param uuidList the uuid list
     * @return the employment info full info by id
     */
    List<EmploymentInfoFeignDTO> getEmploymentInfoFullInfoById(List<UUID> uuidList);

    /**
     * Gets all operators.
     *
     * @return the all operators
     */
    List<UserDTO> getAllOperators();

    /**
     * Gets all mentors.
     *
     * @return the all mentors
     */
    List<UserDTO> getAllMentors();

    List<IEmployee> getAllMentorsByBranch(Integer id);

    ApiResult<?> resignationEmployee(EmployeeResignationDTO employeeResignationDTO);

    ApiResult<?> resignationEmployment(EmployeeResignationDTO employeeResignationDTO);

    ApiResult<?> editViewRowDataForMentorView(UUID viewId, UUID rowId, Map<String, Object> map);

}
