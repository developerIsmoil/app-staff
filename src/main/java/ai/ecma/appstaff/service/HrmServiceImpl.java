package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.EmploymentInfo;
import ai.ecma.appstaff.entity.Position;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.ChildrenDTO;
import ai.ecma.appstaff.payload.EmployeeHrmDTO;
import ai.ecma.appstaff.repository.EmploymentInfoRepository;
import ai.ecma.appstaff.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrmServiceImpl implements HrmService {


    private final PositionRepository positionRepository;
    private final EmploymentInfoRepository employeeInfoRepository;


    @Override
    public ApiResult<?> getOrgStructure() {
        List<ChildrenDTO> childrenDTOList = mapChildrenDTO();

        return ApiResult.successResponse(childrenDTOList);
    }

    public List<ChildrenDTO> mapChildrenDTO() {

        List<EmploymentInfo> employmentInfoList = employeeInfoRepository.findAll();

        List<Position> positions = positionRepository.findAll();

        List<Position> parentPositionList = positions.stream().filter(position -> position.getParent() == null).collect(Collectors.toList());

        return mapChildrenDTO(parentPositionList, positions, employmentInfoList);

    }

    private List<ChildrenDTO> mapChildrenDTO(List<Position> parentPositionList, List<Position> positions, List<EmploymentInfo> employmentInfoList) {

        List<ChildrenDTO> result = new ArrayList<>();
        for (Position parentPosition : parentPositionList) {

            List<Position> childrenPositionList = positions.stream()
                    .filter(position -> Objects.equals(position.getParent().getId(), parentPosition.getId()))
                    .collect(Collectors.toList());


            ChildrenDTO childrenDTO = new ChildrenDTO(
                    parentPosition != null ? parentPosition.getId() : null,
                    mapEmployeeHrmDTO(parentPosition, employmentInfoList),
                    !childrenPositionList.isEmpty(),
                    parentPosition.getName(),
                    mapChildrenDTO(childrenPositionList, positions, employmentInfoList)
            );

            result.add(childrenDTO);
        }

        return result;
    }

    private List<EmployeeHrmDTO> mapEmployeeHrmDTO(Position position, List<EmploymentInfo> employmentInfoList) {
        if (employmentInfoList == null || employmentInfoList.isEmpty())
            return new ArrayList<>();

        return employmentInfoList.stream()
                .filter(employmentInfo -> Objects.equals(employmentInfo.getPosition().getId(), position.getId()))
                .map(EmployeeHrmDTO::mapDTO)
                .collect(Collectors.toList());

    }


}


//  for (String id : parentIdList) {
//
//            for (Position position : positions) {
//
//                if (Objects.equals(UUID.fromString(id), position.getParent().getId())) {
//
//
//                    List<EmployeeHrmDTO> employeeHrmDTOS = employmentInfoList
//                            .stream()
//                            .filter(employmentInfo -> Objects.equals(employmentInfo.getPosition().getId(), position.getId()))
//                            .map(employmentInfo ->
//                                    new EmployeeHrmDTO(
//                                            employmentInfo.getId(),
//                                            employmentInfo.getEmployee().getFirstName(),
//                                            employmentInfo.getEmployee().getLastName(),
//                                            employmentInfo.getEmployee().getPhotoId()
//                                    )
//                            )
//                            .collect(Collectors.toList());
//
//                    ChildrenDTO childrenDTO = new ChildrenDTO();
//
//                    childrenDTO.setEmployeeHrmList(employeeHrmDTOS);
//                    childrenDTO.setPositionName(position.getParent().getName());
//
////                    childrenDTO.setChildrenList();
//                }
//            }
//
//        }
//
//        for (PositionProjection positionProjection : positionList) {
//
//            List<Position> objects = positionProjection.getObjects();
//
//            for (Position object : objects) {
//
//                List<EmployeeHrmDTO> employeeHrmDTOS = employmentInfoList
//                        .stream()
//                        .filter(employmentInfo -> Objects.equals(employmentInfo.getPosition().getId(), object.getId()))
//                        .map(employmentInfo ->
//                                new EmployeeHrmDTO(
//                                        employmentInfo.getId(),
//                                        employmentInfo.getEmployee().getFirstName(),
//                                        employmentInfo.getEmployee().getLastName(),
//                                        employmentInfo.getEmployee().getPhotoId()
//                                )
//                        )
//                        .collect(Collectors.toList());
//
//                ChildrenDTO childrenDTO = new ChildrenDTO();
//
//                childrenDTO.setEmployeeHrmList(employeeHrmDTOS);
//
//            }
//        }