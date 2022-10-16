package ai.ecma.appstaff.feign;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.feign.turniket.TurniketUserDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = RestConstants.TURNIKET_SERVICE, configuration = FeignConfig.class)
public interface TurniketFeign {

    String CRUD_USER_IN_TURNIKET = "/turniket/employee";

    //add user
    @PostMapping(CRUD_USER_IN_TURNIKET)
    ApiResult<Integer> addUserInTurniket(@RequestHeader("Authorization") String token,
                                         @RequestBody TurniketUserDTO turniketUserDTO);

    //edit user
    @PutMapping(CRUD_USER_IN_TURNIKET)
    ApiResult<Integer> editUserInTurniket(@RequestHeader("Authorization") String token,
                                          @RequestBody TurniketUserDTO turniketUserDTO);

    //delete user
    @DeleteMapping(CRUD_USER_IN_TURNIKET + "/{empId}")
    ApiResult<Integer> deleteUserInTurniket(@RequestHeader("Authorization") String token,
                                            @PathVariable Integer empId);


}
