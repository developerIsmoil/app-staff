package ai.ecma.appstaff.service.view;

import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.payload.feign.RoleFeignDTO;
import ai.ecma.appstaff.service.feign.FeignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SortAndSearchForOtherService {

    private final FeignService feignService;


    public List<String> searchingByBranchName(String search) {

        // TODO SHU YERDA REDISGA ULANIB  branch NI LISTINNI OLISH KK
        List<BranchFeignDTO> branchFeignDTOList = feignService.getBranchList();

        List<BranchFeignDTO> branchList;

        if (search != null && !search.isEmpty()) {
            branchList = branchFeignDTOList
                    .stream()
                    .filter(
                            branch -> branch
                                    .getName()
                                    .toLowerCase()
                                    .contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            branchList = branchFeignDTOList;
        }
        List<String> result = new ArrayList<>();
        for (BranchFeignDTO branchFeignDTO : branchFeignDTOList) {
            result.add(branchFeignDTO.getId().toString());
        }

        return result;
    }

    public List<String> sortingByBranch(int direction) {

        // TODO SHU YERDA REDISGA ULANIB COURSE NI LISTINNI OLISH KK
        List<BranchFeignDTO> branchFeignDTOList = feignService.getBranchList();


        if (direction == 1) {
            branchFeignDTOList.sort(Comparator.comparing(branch -> branch.getName().toLowerCase()));
        } else if (direction == -1) {

            branchFeignDTOList.sort((branch1, branch2) -> branch2.getName().toLowerCase().compareTo(branch1.getName().toLowerCase()));
        }

        List<String> result = new ArrayList<>();
        for (BranchFeignDTO branchFeignDTO : branchFeignDTOList) {
            result.add(branchFeignDTO.getId().toString());
        }

        return result;
    }

    public List<String> sortingByRole(Integer direction) {
        // TODO SHU YERDA REDISGA ULANIB COURSE NI LISTINNI OLISH KK
        List<RoleFeignDTO> roleFeignDTOList = feignService.getRoleList();


        if (direction == 1) {
            roleFeignDTOList.sort(Comparator.comparing(role -> role.getName().toLowerCase()));
        } else if (direction == -1) {

            roleFeignDTOList.sort((role1, role2) -> role2.getName().toLowerCase().compareTo(role1.getName().toLowerCase()));
        }

        List<String> result = new ArrayList<>();
        for (RoleFeignDTO roleFeignDTO : roleFeignDTOList) {
            result.add(roleFeignDTO.getId().toString());
        }

        return result;
    }

    public List<String> searchingByRoleName(String search) {

        // TODO SHU YERDA REDISGA ULANIB  branch NI LISTINNI OLISH KK
        List<RoleFeignDTO> roleFeignDTOList = feignService.getRoleList();

        List<RoleFeignDTO> roleList;

        if (search == null || search.isEmpty() || ("").equals(search.trim())) {
            roleList = roleFeignDTOList;
        } else {
            roleList = roleFeignDTOList
                    .stream()
                    .filter(
                            role -> role
                                    .getName()
                                    .toLowerCase()
                                    .contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }
        List<String> result = new ArrayList<>();
        for (RoleFeignDTO roleFeignDTO : roleFeignDTOList) {
            result.add(roleFeignDTO.getId().toString());
        }

        return result;
    }

    public List<String> sortingByCourse(Integer direction) {
        return null;
    }

    public List<String> searchingByCourse(String search) {
        return null;
    }
}