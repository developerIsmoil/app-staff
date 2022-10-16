package ai.ecma.appstaff.service.view;

import ai.ecma.appstaff.enums.EntityColumnMapValuesKeyEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueryServiceInitialSum {
    private final QueryService queryService;

    /*
    with tempTable as (select * from time_sheet_employee as g where id = any('{5cc62e92-f1d1-4dc5-b2f3-fe1ac2be8682,885f3232-99e4-49b9-ae2e-b68ec8871be4,5ea2dc8c-0731-4421-bcbb-c8e3de6dd573}') )
    select CAST(JSON_BUILD_OBJECT(

            'salary', (select sum (ff.salary) from tempTable as ff),
            'bonus',(select sum(ff.bonus) from tempTable as ff),
            'premium',(select sum(ff.premium) from tempTable as ff),
            'advance_salary',(select sum(ff.advance_salary) from tempTable as ff),
            'retention_salary',(select sum(ff.retention_salary) from tempTable as ff),
            'addition_salary',(select sum(ff.addition_salary) from tempTable as ff),
            'total_salary',(select sum(ff.total_salary) from tempTable as ff),
            'tax_amount',(select sum(ff.tax_amount) from tempTable as ff),
            'paid_salary',(select count(ff.paid_salary) from tempTable as ff )

            ) as varchar) from time_sheet_employee as g  limit 1
      */
    public String mainQueryForTimeSheetEmployee(List<String> idList, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS) {

        StringBuilder query = new StringBuilder()
                .append(" with tempTable as (select ");


        StringBuilder tempTableQuery = new StringBuilder();
        StringBuilder jsonBuildObject = new StringBuilder();
        for (Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> stringMapEntry : ENTITY_FIELDS.entrySet()) {
            for (Map.Entry<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringEntry : stringMapEntry.getValue().entrySet()) {
                if (valuesKeyEnumStringEntry.getKey().equals(EntityColumnMapValuesKeyEnum.INLINE_SUM)) {
                    tempTableQuery.append(queryForTempTable(QueryService.makeSnakeCase(stringMapEntry.getKey())));
                    jsonBuildObject.append(queryForInlainSumColumns(QueryService.makeSnakeCase(stringMapEntry.getKey())));
                }
            }
        }

        query
                .append(tempTableQuery.substring(0, tempTableQuery.length() - 1))
                .append(" from timesheet_employee as g where id = any('{")
                .append(queryService.replaceBracketToEmpty(idList))
                .append("}') ) select CAST(JSON_BUILD_OBJECT(")
                .append(jsonBuildObject.substring(0,jsonBuildObject.length()-1))
                .append(") as varchar) from timesheet_employee as g where deleted=false limit 1");

        return query.toString();
    }

    //sum(g.total_salary) as total_salary
    private String queryForTempTable(String columnName) {
        return new StringBuilder()
                .append(" sum(g.")
                .append(columnName)
                .append(") as ")
                .append(columnName)
                .append(",").toString();
    }

    private String queryForInlainSumColumns(String columnName) {
        StringBuilder sql = new StringBuilder()
                .append("'")
                .append(QueryService.makeSnakeCase(columnName).trim())
                .append("', (select ff.")
                .append(QueryService.makeSnakeCase(columnName))
                .append(" from tempTable as ff),");

        return sql.toString();
    }


}
