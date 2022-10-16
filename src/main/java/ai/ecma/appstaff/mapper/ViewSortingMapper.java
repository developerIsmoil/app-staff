package ai.ecma.appstaff.mapper;

import ai.ecma.appstaff.entity.view.ViewSorting;
import ai.ecma.appstaff.payload.view.ViewSortingDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ViewSortingMapper {

    //ViewSorting NI -> ViewSortingDTO GA PARSE QILADI
    ViewSortingDTO mapViewSortingToDTO(ViewSorting viewSorting);

    //List<ViewSorting> NI -> List<ViewSortingDTO> GA PARSE QILADI
    List<ViewSortingDTO> mapViewSortingToDTOList(List<ViewSorting> viewSortingList);
}
