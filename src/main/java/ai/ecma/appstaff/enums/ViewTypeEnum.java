package ai.ecma.appstaff.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
@Getter
@AllArgsConstructor
public enum ViewTypeEnum {

    TABLE(1),
    BOARD(2),
    LIST(3);

    private final int orderIndex;

    public static List<ViewTypeEnum> getByOrderIndex() {
        List<ViewTypeEnum> viewTypeEnumList = Arrays.asList(ViewTypeEnum.values());
        viewTypeEnumList.sort(Comparator.comparingInt(ViewTypeEnum::getOrderIndex));
        return viewTypeEnumList;
    }
}

