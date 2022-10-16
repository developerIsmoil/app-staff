package ai.ecma.appstaff.payload.view;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class GenericViewResultDTO {


    private Object genericResult;

    private Object statusId;

    private int page;

    private Object count;

    private Long durationTime;

    private String query;

    private Object initialSum;

    public GenericViewResultDTO(String query) {
        this.query = query;
    }

}
