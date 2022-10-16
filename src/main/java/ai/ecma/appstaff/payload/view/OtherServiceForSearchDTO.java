package ai.ecma.appstaff.payload.view;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface OtherServiceForSearchDTO {
    String getTableName();

    @JsonIgnore
    String getIdList();

    default List<String> getIds() {
        if (getIdList() != null) {
            return Arrays.asList(getIdList().split(","));
        }
        return new ArrayList<>();
    }

}
