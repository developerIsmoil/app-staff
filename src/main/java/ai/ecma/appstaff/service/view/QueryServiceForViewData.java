package ai.ecma.appstaff.service.view;


import ai.ecma.appstaff.entity.customField.CustomField;
import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.enums.EntityColumnMapValuesKeyEnum;
import ai.ecma.appstaff.entity.view.ViewObject;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.repository.customField.CustomFieldRepository;
import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableMapList;
import ai.ecma.appstaff.utils.TableNameConstant;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

import static ai.ecma.appstaff.enums.EntityColumnMapValuesKeyEnum.*;


@Service
public class QueryServiceForViewData {
    private final ViewService viewService;
    private final CustomFieldRepository customFieldRepository;
    private final QueryService queryService;

    public QueryServiceForViewData(@Lazy ViewService viewService, CustomFieldRepository customFieldRepository, QueryService queryService) {
        this.viewService = viewService;
        this.customFieldRepository = customFieldRepository;
        this.queryService = queryService;
    }

    public String mainQuery(UUID viewId, List<String> idList) {

        if (idList == null)
            throw RestException.restThrow("IdList bo'sh bo'lmasin", HttpStatus.BAD_REQUEST);


        ViewObject viewObject = viewService.getViewObjectByIdIfNotThrow(viewId);


        String tableName = viewObject.getTableName();


        Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS = TableMapList.ENTITY_FIELDS.get(tableName);


        List<CustomField> customFieldList = customFieldRepository.findAllByTableName(tableName);

        StringBuilder selectQuery = new StringBuilder().append(" SELECT ")
                .append("CAST(JSON_BUILD_OBJECT(");

        StringBuilder withQuery = new StringBuilder();
        withQuery.append(withQueryForCustomFields(customFieldList, idList));

        Map<String, String> withQueryMap = new HashMap<>();

        StringBuilder whereQuery = new StringBuilder().append(" WHERE ");

        tableName = CommonUtils.getTableName(tableName);

        StringBuilder fromQueryForJoin = new StringBuilder()
                .append(" FROM ")
                .append(QueryService.makeSnakeCase(tableName))
                .append(" AS g ");

        for (Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry : ENTITY_FIELDS.entrySet()) {
            Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();
            String columnName = mapEntry.getKey();

            // AGAR MAPGA NOT_FROM DB YOZILGAN BO'LSA U COLUMNNI TASHLAB KETISH KK KETISH KK
            if (value.get(NOT_FROM_DB) != null && !value.get(NOT_FROM_DB).isBlank() && value.get(NOT_FROM_DB).equals(RestConstants.YES))
                continue;

            if (value.get(DIRECTIONAL_FOREIGN_TABLE) != null) {

                if (value.get(COUNT) != null) {
                    String mapValue = withQueryMap.getOrDefault(value.get(DIRECTIONAL_FOREIGN_TABLE), "");
                    if (mapValue.isEmpty()) {
                        mapValue += value.get(DIRECTIONAL_OURS_COLUMN);
                    }
                    mapValue += "," + COUNT + "(" + (value.get(DIRECTIONAL_COLUMN)) + ") AS " + columnName;
                    withQueryMap.put(value.get(DIRECTIONAL_FOREIGN_TABLE), mapValue);
                    selectQuery.append(queryForCountAndSumSelect(mapEntry));
                } else if (value.get(SUM) != null) {
                    String mapValue = withQueryMap.getOrDefault(value.get(DIRECTIONAL_FOREIGN_TABLE), "");
                    if (mapValue.isEmpty()) {
                        mapValue += value.get(DIRECTIONAL_OURS_COLUMN);
                    }
                    mapValue += "," + SUM + "(" + (value.get(DIRECTIONAL_COLUMN)) + ") AS " + columnName;
                    withQueryMap.put(value.get(DIRECTIONAL_FOREIGN_TABLE), mapValue);

                    selectQuery.append(queryForCountAndSumSelect(mapEntry));
                } else if (value.get(TYPE).equals(CustomFieldTypeEnum.SPECIAL_LABEL.name())) {
                    String tempTableName = createTableName();
                    withQuery.append(withQueryForSuperLabel(idList, mapEntry, tempTableName));
                    selectQuery.append(queryForSelectSuperLabels(tempTableName, mapEntry));
                } else {
                    String mapValue = withQueryMap.getOrDefault(value.get(DIRECTIONAL_FOREIGN_TABLE), "");
                    if (mapValue.isEmpty()) {
                        mapValue += value.get(DIRECTIONAL_OURS_COLUMN);
                    }
                    mapValue += "," + columnName;
                    withQueryMap.put(value.get(DIRECTIONAL_FOREIGN_TABLE), mapValue);
                    selectQuery.append(queryForSelectForDirectionTable(value, mapEntry));
                }

//                fromQueryForJoin.append(queryForCountAndSumForFrom(mapEntry));
            } else if (value.get(OTHER_TABLE) != null && value.get(OTHER_SERVICE) == null) {
                selectQuery.append(queryForSelectLabels(mapEntry));
                fromQueryForJoin.append(queryForOtherTable(mapEntry));
            } else if (value.get(OTHER_TABLE) != null && value.get(OTHER_SERVICE) != null && value.get(NOT_FROM_DB) == null) {
                selectQuery.append(queryForSelectLabels(mapEntry));
                fromQueryForJoin.append(queryForOtherTableForBranch(mapEntry));
            } else if (value.get(RELATIONAL_TABLE) != null) {

                queryMethodForRelationalTable(selectQuery, mapEntry);

            } else if (value.get(FOREIGN_TABLE) != null && !(value.get(TYPE).equals(CustomFieldTypeEnum.DROPDOWN.name()) || value.get(TYPE).equals(CustomFieldTypeEnum.LABELS.name()))) {
                selectQuery.append(queryForSelectForForeignTable(mapEntry));
            } else if (value.get(NOT_FROM_DB) != null) {
            } else if (value.get(TYPE).equals(CustomFieldTypeEnum.DATE.name()) ||
                    value.get(TYPE).equals(CustomFieldTypeEnum.DATE_TIME.name())
                    || value.get(TYPE).equals(CustomFieldTypeEnum.TIME.name())) {
                selectQuery.append(queryForSelectForDate(columnName, mapEntry.getKey()));

            } else {
                selectQuery.append(queryForSelect(columnName, mapEntry.getKey()));
            }
        }

        for (CustomField customFieldId : customFieldList) {
            selectQuery.append(queryForSelectCustomFields(customFieldId));

        }
        whereQuery.append(queryForWhereEnd(idList));

        String withQueryString = withQuery.toString();

        if (!withQueryString.isEmpty()) {
            withQueryString = withQueryString.substring(0, withQueryString.length() - 1);
        }

        String result = "WITH " + mapToWithQueryFromWithQueryMap(withQueryMap, idList) + withQueryString;

        result += selectQuery.toString();
        result = result.substring(0, result.length() - 1);
        result += " ) AS VARCHAR) ";

        result += fromQueryForJoin.toString();
        if (fromQueryForJoin.toString().length() > 5)
            result += ",";
        result = result.substring(0, result.length() - 1);

        result += whereQuery.toString();

        result += " GROUP BY  g.id ORDER BY array_position('{" + queryService.replaceBracketToEmpty(idList) + "}',g.id);";

        System.out.println(result);
        return result;
    }

    //   'deadline', (SELECT ff.description
    //                     from expense_proposition as ff
    //                     where ff.id = g.expense_proposition_id limit 1),
    private String queryForSelectForForeignTable(Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry) {
        String columnName = mapEntry.getKey();
        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();

        return new StringBuilder()
                .append("'")
                .append(columnName)
                .append("', (select ff.")
                .append(Objects.nonNull(value.get(SEARCHING_COLUMN)) ? value.get(SEARCHING_COLUMN) : Objects.nonNull(value.get(ORDERING_COLUMN)) ? value.get(ORDERING_COLUMN) : columnName)
                .append(" from ")
                .append(value.get(FOREIGN_TABLE))
                .append(" as ff where ff.deleted=false and ff.id=g.")
                .append(value.get(FOREIGN_TABLE_ID) == null ? columnName : value.get(FOREIGN_TABLE_ID))
                .append(" limit 1 ),").toString();

    }

    private String queryForSelectForDirectionTable(Map<EntityColumnMapValuesKeyEnum, String> value, Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry) {

        String tempTableName = getWithTableName(value.get(DIRECTIONAL_FOREIGN_TABLE));

        return new StringBuilder()
                .append("'")
                .append(mapEntry.getKey())
                .append("', (SELECT array_agg(ff.")
                .append(mapEntry.getKey())
                .append("::varchar ) FROM ")
                .append(tempTableName)
                .append(" AS ff where ff.")
                .append(QueryService.makeSnakeCase(value.get(DIRECTIONAL_OURS_COLUMN)))
                .append("=g.id),").toString();

    }

    //EXTRACT(epoch FROM l.lesson_date) * 1000
    private String queryForSelectForDate(String columnName, String key) {
        return new StringBuilder().append("'")
                .append(key)
                .append("', EXTRACT(epoch FROM g.")
                .append(QueryService.makeSnakeCase(columnName))
                .append(") * 1000 ,").toString();
    }

    private void queryMethodForRelationalTable(StringBuilder selectQuery, Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry) {
        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();

        if (value.get(TYPE).equals(CustomFieldTypeEnum.DROPDOWN.name())
//                || value.get(TYPE).equals(CustomFieldTypeEnum.ENUM_DROPDOWN.name())
        ) {

            selectQuery.append(queryForRelationalTableTypeDropDown(mapEntry));

        } else if (value.get(TYPE).equals(CustomFieldTypeEnum.SHORT_TEXT.name()) ||
                value.get(TYPE).equals(CustomFieldTypeEnum.LONG_TEXT.name()) ||
                value.get(TYPE).equals(CustomFieldTypeEnum.PHONE.name()) ||
                value.get(TYPE).equals(CustomFieldTypeEnum.EMAIL.name())) {

            selectQuery.append(queryForRelationalTable(mapEntry));
        }

    }

    // 'departmentId', (select ff.department_id from employment_info as ff where ff.id = g.employment_info_id),
    private String queryForRelationalTableTypeDropDown(Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry) {

        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();

        return new StringBuilder()
                .append("'")
                .append(mapEntry.getKey())
                .append("', (SELECT ff.")
                .append(QueryService.makeSnakeCase(value.get(RELATIONAL_TABLE_FOREIGN_TABLE_ID)))
                .append(" from ")
                .append(QueryService.makeSnakeCase(value.get(RELATIONAL_TABLE)))
                .append(" as ff where ff.id=g.")
                .append(QueryService.makeSnakeCase(value.get(RELATIONAL_TABLE_ID)))
                .append("), ").toString();
    }

    //(select hh.first_name from employee as hh where hh.id = (select ff.employee_id from employment_info ff where ff.id = g.employment_info_id))
    private String queryForRelationalTable(Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry) {
        String key = mapEntry.getKey();
        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();

        return new StringBuilder()
                .append("'")
                .append(mapEntry.getKey())
                .append("', (SELECT hh.")
                .append(QueryService.makeSnakeCase(key))
                .append(" from ")
                .append(QueryService.makeSnakeCase(value.get(RELATIONAL_TABLE_FOREIGN_TABLE)))
                .append(" as hh where hh.id=(select ff.")
                .append(QueryService.makeSnakeCase(value.get(RELATIONAL_TABLE_FOREIGN_TABLE_ID)))
                .append(" from ")
                .append(QueryService.makeSnakeCase(value.get(RELATIONAL_TABLE)))
                .append(" as ff where ff.id=g.")
                .append(QueryService.makeSnakeCase(value.get(RELATIONAL_TABLE_ID)))
                .append(")), ").toString();
    }

    //  'aggggggggr', (select kk.agg from superLabelTemp as kk where kk.id = g.id),
    private String queryForSelectSuperLabels(String tempTableName, Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry) {
        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();
        return new StringBuilder()
                .append("'")
                .append(mapEntry.getKey())
                .append("', (SELECT ff.agg FROM ")
                .append(tempTableName)
                .append(" AS ff where ff.")
                .append(QueryService.makeSnakeCase(value.get(DIRECTIONAL_OURS_COLUMN)))
                .append("=g.id),").toString();
    }

    //     superLabelTemp as (select array_agg(phone_number::varchar) as agg, ff.human_id  from human_additional_number ff where ff.human_id = any ('{1,2,3,4,55}') group by ff.human_id)
    private String withQueryForSuperLabel(List<String> idList, Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry, String tempTableName) {
        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();
        return new StringBuilder()
                .append(tempTableName)
                .append(" AS (SELECT ARRAY_AGG(")
                .append(QueryService.makeSnakeCase(value.get(DIRECTIONAL_COLUMN)))
                .append("::VARCHAR) AS agg, ff.")
                .append(QueryService.makeSnakeCase(value.get(DIRECTIONAL_OURS_COLUMN)))
                .append(" FROM ")
                .append(QueryService.makeSnakeCase(value.get(DIRECTIONAL_FOREIGN_TABLE)))
                .append(" AS ff WHERE ff.")
                .append(QueryService.makeSnakeCase(value.get(DIRECTIONAL_OURS_COLUMN)))
                .append("=ANY('{")
                .append(queryService.replaceBracketToEmpty(idList))
                .append("}') GROUP BY ff.")
                .append(QueryService.makeSnakeCase(value.get(DIRECTIONAL_OURS_COLUMN)))
                .append("),").toString();
    }

    private String queryForSelectCustomFields(CustomField customField) {

        if (customField.getType().equals(CustomFieldTypeEnum.CHECKBOX))
            return queryForCustomFieldCheckbox(customField);
        else if (customField.getType().equals(CustomFieldTypeEnum.DATE)) {
            return queryForCustomFieldDate(customField);
        } else if (customField.getType().equals(CustomFieldTypeEnum.NUMBER)) {
            return queryForCustomFieldNumber(customField);
        } else {
            return queryForCustomFieldOtherTypes(customField);

        }

    }

    private String queryForCustomFieldOtherTypes(CustomField customField) {
        return new StringBuilder()
                .append(" '")
                .append(customField.getId())
                .append("',")
                .append(isCustomFielTypeSelectable(customField.getType()) ? " string_to_array(string_agg(" : "")
                .append("(SELECT value FROM ")
                .append(getCustomFieldWithTableName())
                .append(" WHERE ")
                .append(getCustomFieldWithTableName())
                .append(".custom_field_id = '")
                .append(customField.getId())
                .append("' AND ")
                .append(getCustomFieldWithTableName())
                .append(".owner_id=g.id::VARCHAR  LIMIT 1 )")
                .append(isCustomFielTypeSelectable(customField.getType()) ? " ,','),',') " : "")
                .append(",").toString();

    }

    private String queryForCustomFieldNumber(CustomField customField) {
        return new StringBuilder()
                .append(" '")
                .append(customField.getId())
                .append("',")
                .append(isCustomFielTypeSelectable(customField.getType()) ? " string_to_array(string_agg(" : "")
                .append("(SELECT cast(value as numeric) FROM ")
                .append(getCustomFieldWithTableName())
                .append(" WHERE ")
                .append(getCustomFieldWithTableName())
                .append(".custom_field_id = '")
                .append(customField.getId())
                .append("' AND ")
                .append(getCustomFieldWithTableName())
                .append(".owner_id=g.id::VARCHAR  LIMIT 1)")
                .append(isCustomFielTypeSelectable(customField.getType()) ? " ,','),',') " : "")
                .append(",").toString();
    }

    private String queryForCustomFieldDate(CustomField customField) {
        return new StringBuilder()
                .append(" '")
                .append(customField.getId())
                .append("',")
                .append(isCustomFielTypeSelectable(customField.getType()) ? " string_to_array(string_agg(" : "")
                .append("(SELECT EXTRACT(epoch FROM cast(value as date)) * 1000 FROM ")
                .append(getCustomFieldWithTableName())
                .append(" WHERE ")
                .append(getCustomFieldWithTableName())
                .append(".custom_field_id = '")
                .append(customField.getId())
                .append("' AND ")
                .append(getCustomFieldWithTableName())
                .append(".owner_id=g.id::VARCHAR  LIMIT 1 )")
                .append(isCustomFielTypeSelectable(customField.getType()) ? " ,','),',') " : "")
                .append(",").toString();

    }

    private String queryForCustomFieldCheckbox(CustomField customField) {
        return new StringBuilder()
                .append(" '")
                .append(customField.getId())
                .append("',")
                .append(isCustomFielTypeSelectable(customField.getType()) ? " string_to_array(string_agg(" : "")
                .append("(SELECT CASE WHEN value = 'true' THEN true ELSE false END FROM ")
                .append(getCustomFieldWithTableName())
                .append(" WHERE ")
                .append(getCustomFieldWithTableName())
                .append(".custom_field_id = '")
                .append(customField.getId())
                .append("' AND ")
                .append(getCustomFieldWithTableName())
                .append(".owner_id=g.id::VARCHAR  LIMIT 1 )")
                .append(isCustomFielTypeSelectable(customField.getType()) ? " ,','),',') " : "")
                .append(",").toString();


    }

    private String createTableName() {
        Integer random = (int) (Math.random() * 10000);
        return ("tab" + random + "Temp");
    }

    private boolean isCustomFielTypeSelectable(CustomFieldTypeEnum type) {
        if (CustomFieldTypeEnum.LABELS.equals(type) || CustomFieldTypeEnum.DROPDOWN.equals(type) || CustomFieldTypeEnum.TREE.equals(type)) {
            return true;
        }
        return false;
    }

    // tempTablef as (select value, custom_field_id, owner_id from custom_field_value where custom_field_id = any ('{cskjnlms}') and owner_id = any ('{1,3,4,55}'))
    private String withQueryForCustomFields(List<CustomField> customFieldList, List<String> userIdList) {
        List<String> strings = customFieldListTOcustomFieldIdList(customFieldList);

        return new StringBuilder()
                .append(getCustomFieldWithTableName())
                .append(" AS (SELECT value, custom_field_id, owner_id FROM custom_field_value WHERE owner_id=ANY('{")
                .append(queryService.replaceBracketToEmpty(userIdList))
                .append("}') AND custom_field_id=ANY('{")
                .append(queryService.replaceBracketToEmpty(strings))
                .append("}')),").toString();
    }

    private List<String> customFieldListTOcustomFieldIdList(List<CustomField> customFieldList) {
        List<String> result = new ArrayList<>();
        for (CustomField customField : customFieldList) {
            result.add(customField.getId().toString());
        }
        return result;
    }

    //---------------------------------------------------METHODS---------------------------------------------------------------------//
    private String getCustomFieldWithTableName() {
        return " customFieldTemp ";
    }

    private String getWithTableName(String directionForeignTable) {

        return " " + directionForeignTable + "Temp ";

    }

    private String mapToWithQueryFromWithQueryMap(Map<String, String> withQueryMap, List<String> idList) {
        StringBuilder stringBuilder = new StringBuilder();

        String idListWithoutBrackets = queryService.replaceBracketToEmpty(idList);

        for (Map.Entry<String, String> stringStringEntry : withQueryMap.entrySet()) {

            String ourColumnId = stringStringEntry.getValue().split(",")[0];

            String tableName = getWithTableName(stringStringEntry.getKey());

            stringBuilder
                    .append(tableName)
                    .append(" AS (SELECT ")
                    .append(stringStringEntry.getValue())
                    .append(" FROM ")
                    .append(stringStringEntry.getKey())
                    .append(" WHERE deleted=false and ")
                    .append(ourColumnId)
                    .append("=ANY('{")
                    .append(idListWithoutBrackets)
                    .append("}')),");
        }
        return stringBuilder.toString();
    }

    // AND g.id = any ('{1,2,3,4,44}')
    private String queryForWhereEnd(List<String> idList) {
        return new StringBuilder()
                .append(" g.id = ANY ('{")
                .append(queryService.replaceBracketToEmpty(idList))
                .append("}')").toString();
    }


    // concat('[', string_agg(hc.course_id::varchar, ','), ']'),
    private String queryForSelectLabels(Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry) {
        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();
        return new StringBuilder()
                .append("'" + mapEntry.getKey() + "', ")
                .append(" CONCAT('[', STRING_AGG(")
                .append(QueryService.makeSnakeCase(value.get(OTHER_TABLE)))
                .append(".")
                .append(QueryService.makeSnakeCase(value.get(IN_OTHER_TABLE_SEARCHING_COLUMN_NAME)))
                .append("::VARCHAR, ','), ']'),").toString();

    }

    // left join human_course as humanCourse on g.id = humanCourse.human_id,
    private String queryForOtherTable(Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry) {
        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();
        return new StringBuilder()
                .append(" LEFT JOIN ")
                .append(value.get(OTHER_TABLE))
                .append(" ON g.id=")
                .append(value.get(OTHER_TABLE))
                .append(".")
                .append(QueryService.makeSnakeCase(mapEntry.getValue().get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME)))
                .append(" ").toString();
    }

    // left join human_course as humanCourse on g.id = humanCourse.human_id,
    private String queryForOtherTableForBranch(Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry) {
        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();
        return new StringBuilder()
                .append(" LEFT JOIN ")
                .append(value.get(OTHER_TABLE))
                .append(" ON g.")
                .append(QueryService.makeSnakeCase(mapEntry.getValue().get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME)))
                .append("=")
                .append(value.get(OTHER_TABLE))
                .append(".id ").toString();
//                .append(QueryService.makeSnakeCase(mapEntry.getValue().get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME)))
    }

    // g.first_name,
    private String queryForSelect(String columnName, String key) {
        return new StringBuilder()
                .append("'")
                .append(key)
                .append("', g.")
                .append(QueryService.makeSnakeCase(columnName))
                .append(",").toString();

    }

    // LEFT JOIN TSum ON g.id = paySumSum.human_id
//    private String queryForCountAndSumForFrom(Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry, boolean isCount) {
//        StringBuilder sql = new StringBuilder()
//                .append(" LEFT JOIN ")
//                .append(mapEntry.getKey())
//                .append(isCount ? COUNT + " " : SUM + " ")
//                .append(" ON g.id=")
//                .append(mapEntry.getKey())
//                .append(isCount ? COUNT : SUM)
//                .append(".")
//                .append(mapEntry.getValue().get())
//
//        return sql.toString();
//    }

    //g.id = paymentCount.humanId AND
    //g.id = paymentSum.humanId AND
    private String queryForCountAndSumWhere(Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry, boolean COUNT) {
        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();
        StringBuilder sql = new StringBuilder()
                .append(" g.id=")
                .append(mapEntry.getKey())
                .append(COUNT ? "Count" : "Sum")
                .append(COUNT ? ".count " : ".sum ")
                .append(" AND ");
        return sql.toString();
    }

    //(select paySum from paymentTemp where human_id = g.id limit 1),
    private String queryForCountAndSumSelect(Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry) {
        String key = mapEntry.getKey();
        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();
        StringBuilder sql = new StringBuilder()
                .append("'" + key + "',(SELECT ")
                .append(key)
                .append(" FROM ")
                .append(value.get(DIRECTIONAL_FOREIGN_TABLE))
                .append("Temp")
                .append(" WHERE ")
                .append(value.get(DIRECTIONAL_OURS_COLUMN))
                .append("=g.id limit 1),");
        return sql.toString();

    }

    // left join human_course as humanCourse on g.id = humanCourse.human_id,
    private String queryForCountAndSumForFrom(Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry) {

        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();
        return new StringBuilder()
                .append(" LEFT JOIN ")
                .append(value.get(DIRECTIONAL_FOREIGN_TABLE))
                .append("Temp")
                .append(" ON g.id=")
                .append(value.get(DIRECTIONAL_FOREIGN_TABLE))
                .append("Temp")
                .append(".")
                .append(QueryService.makeSnakeCase(mapEntry.getValue().get(DIRECTIONAL_OURS_COLUMN)))
                .append(" ").toString();

    }

    //paymentCount as (select count(id) as count, human_id as humanId from payment group by human_id),
    // paymentSum as (select sum(amount) as sum, human_id as humanId from payment group by human_id),
    private String queryForCountAndSum(Map.Entry<String, Map<EntityColumnMapValuesKeyEnum, String>> mapEntry, boolean COUNT) {
        Map<EntityColumnMapValuesKeyEnum, String> value = mapEntry.getValue();
        StringBuilder sql = new StringBuilder()
                .append(mapEntry.getKey())
                .append(COUNT ? "Count " : "Sum ")
                .append(" AS ( SELECT ")
                .append(COUNT ? " COUNT " : " SUM ")
                .append("(id) AS ")
                .append(COUNT ? " count" : " sum")
                .append(",")
                .append(QueryService.makeSnakeCase(value.get(DIRECTIONAL_OURS_COLUMN)))
                .append(" AS ")
                .append(value.get(DIRECTIONAL_OURS_COLUMN))
                .append(" FROM ")
                .append(QueryService.makeSnakeCase(value.get(DIRECTIONAL_FOREIGN_TABLE)))
                .append(" GROUP BY ")
                .append(QueryService.makeSnakeCase(value.get(DIRECTIONAL_OURS_COLUMN)))
                .append("),");

        return sql.toString();
    }


}
