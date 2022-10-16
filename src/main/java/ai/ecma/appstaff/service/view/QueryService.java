package ai.ecma.appstaff.service.view;

import ai.ecma.appstaff.entity.customField.CustomField;
import ai.ecma.appstaff.entity.customField.CustomFieldDropDown;
import ai.ecma.appstaff.entity.customField.CustomFieldValue;

import ai.ecma.appstaff.entity.view.ViewObject;
import ai.ecma.appstaff.enums.*;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.view.*;
import ai.ecma.appstaff.repository.customField.CustomFieldRepository;
import ai.ecma.appstaff.repository.view.ViewObjectRepository;
import ai.ecma.appstaff.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

import static ai.ecma.appstaff.enums.CompareOperatorTypeEnum.*;
import static ai.ecma.appstaff.enums.CustomFieldTypeEnum.*;
import static ai.ecma.appstaff.enums.EntityColumnMapValuesKeyEnum.*;


@Service
@RequiredArgsConstructor
public class QueryService {


    @Value("${view.board.id.list.size}")
    private String idListSizeForBoard;

    @Value("${view.list.id.list.size}")
    private String idListSizeForList;

    @Value("${view.table.id.list.size}")
    private String idListSizeForTable;


    private final ViewObjectRepository viewObjectRepository;
    private final CustomFieldRepository customFieldRepository;

    public String mainQuery(ViewDTO viewDTO, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS, String forGroupByColumnName, int page, List<String> idListByOtherService) {

        // USERNING VIEWDAGI HUQUQI FULL VA EDIT BO'LSA FILTER, SEARCH VA SORTLARNI DB GA SAQLANADI
        //AGAR USERNING VIEWDAGI HUQUQI VIEW_ONLY BO'LSA FILTER SEARCH VA SORTLARNI DB GA SAQLAMAYMIZ
        ViewObject viewObject = viewObjectRepository.findById(viewDTO.getId()).orElseThrow(() -> RestException.restThrow("NOT.FOUND"));

        String idListSize = calculateIdList(viewObject.getType());

        String query = getIdListForGenericViewQuery(viewDTO, viewObject.getTableName(), viewObject.getType(), ENTITY_FIELDS, forGroupByColumnName, page, idListByOtherService);

        if (viewObject.getType().equals(ViewTypeEnum.TABLE)) {
            return queryForTable(query, false);
        } else {
            query = queryForBoardAndList(query, forGroupByColumnName, page, idListSize);
            return query;
        }
    }

    /**
     * with tempId as (select g.id
     * from application as g
     * where (g.deleted = false or g.deleted is null)
     * ORDER BY g.created_at ASC NULLS LAST, g.created_at DESC NULLS LAST)
     * SELECT CAST(JSON_BUILD_OBJECT(
     * 'count', (count(id)),
     * 'idList', array(select ff.id
     * from tempId as ff
     * )) as varchar)
     * from tempId as f
     */
    private String queryForTable(String query, boolean forExcel) {
        if (forExcel)
            return query;

        StringBuilder sql = new StringBuilder()
                .append("with tempId as ( ")
                .append(query)
                .append(") SELECT CAST(JSON_BUILD_OBJECT( 'count', (count(id)), 'idList', array(select ff.id from tempId as ff limit 1000)) as varchar ) from tempId as f ");

        return sql.toString();


    }

    private String calculateIdList(ViewTypeEnum type) {

        switch (type) {
            case TABLE:
                return idListSizeForTable;
            case LIST:
                return idListSizeForList;
            case BOARD:
                return idListSizeForBoard;
            default:
                return "200";
        }
    }

    private String queryForBoardAndList(String query, String forGroupByColumnName, int page, String idListSize) {

        return new StringBuilder("with tempId as ( " + query + ")")
                .append(" SELECT CAST(JSON_BUILD_OBJECT( '")
                .append(forGroupByColumnName)
                .append("', f.")
                .append(makeSnakeCase(forGroupByColumnName))
                .append(" , 'count', (count(id)), 'idList', array(select ff.id from tempId as ff where ff.")
                .append(forGroupByColumnName)
                .append("=f.")
                .append(forGroupByColumnName)
                .append(" limit ")
                .append(idListSize)
                .append(" offset ")
                .append(page * Integer.parseInt(idListSize))
                .append(")) as varchar ) from tempId as f group by f.")
                .append(makeSnakeCase(forGroupByColumnName))
                .append(" ").toString();
    }


    /**
     * VIEW LAR UCHUN UNIVERSAL SQL QUERY YOZIB BERADI
     * BUNDA FILTER, SORTING VA SEARCHING LAR UCHUN QUERY YOZILADI
     */
    private String getIdListForGenericViewQuery(ViewDTO viewDTO, String tableName,
                                                ViewTypeEnum type, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS,
                                                String forGroupByColumnName, int page, List<String> idListByOtherService) {

        //YIG'IB OLINGAN CUSTOM FIELD LARNI DB DAN OLIB KELINDI
        List<CustomField> customFieldList = customFieldRepository.findAllByTableName(tableName);

        Map<String, CustomField> customFieldMap = mapCustomFieldToHashMap(customFieldList);

        //BUTUN METHOD ICHIDA YOZILGAN SQLLARNI JAMLAYDIGAN VA QAYTARADIGAN ASOSIY QUERY MIZ
        StringBuilder query = new StringBuilder();

        ViewFilterDTO viewFilter = viewDTO.getViewFilter();

        //todo filterni ichida value bo'sh bo'lsa chopish uchun
        if (viewFilter != null)
            removeFilterFieldsWhenValueIsNull(viewFilter);

        queryStart(type, query, tableName, forGroupByColumnName);

        //FILTER ICHIDA KELGAN FIELDLARGA SQL QUERY YOZILADI
        toMakeViewFilterQuery(viewFilter, query, ENTITY_FIELDS, customFieldMap, idListByOtherService);

        toMakeViewSorting(viewDTO, query, ENTITY_FIELDS, customFieldMap, idListByOtherService);

        queryEnd(query, page, type);

        return query.toString();
    }

    private void queryEnd(StringBuilder query, int page, ViewTypeEnum type) {

        if (type.equals(ViewTypeEnum.TABLE)) {
            query.append(" limit ")
                    .append(idListSizeForTable)
                    .append(" offset ")
                    .append(page * Integer.parseInt(idListSizeForTable))
                    .append(" ");
        }
    }

    private void queryStart(ViewTypeEnum viewTypeEnum, StringBuilder query, String tableName, String forGroupByColumnName) {

        tableName = CommonUtils.getTableName(tableName);


        if (viewTypeEnum.equals(ViewTypeEnum.LIST) || viewTypeEnum.equals(ViewTypeEnum.BOARD)) {
            query
                    .append(" select g.id, g.")
                    .append(forGroupByColumnName)
                    .append(" from ")
                    .append(tableName)
                    .append(" as g ");
        } else {

            query.append(" select cast(g.id as varchar) from ")
                    .append(tableName)
                    .append(" as g ");
        }
    }

    /**
     * select id
     * from employee as g
     * where deleted = false
     * and g.id::text =any (string_to_array ('b198a4d0-8449-4098-8c13-299c60e48730,6b8aeb48-1b52-4f91-8ff7-567e2a7d58ad,426490db-fa7d-46d9-90c5-468c4ddf0607',','))
     * order by array_position(
     * '{b198a4d0-8449-4098-8c13-299c60e48730, 6b8aeb48-1b52-4f91-8ff7-567e2a7d58ad, 426490db-fa7d-46d9-90c5-468c4ddf0607}',
     * g.id)
     */
    private void toMakeViewSorting(ViewDTO viewDTO, StringBuilder
            query, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS, Map<String, CustomField> customFieldMap, List<String> idListByOtherService) {

        if (!viewDTO.getSorting().isEmpty()) {

            checkColumnsValidForSort(ENTITY_FIELDS, customFieldMap, viewDTO.getSorting());

            List<ViewSortingDTO> sortingList = viewDTO.getSorting();

            query
                    .append(" ORDER BY ");


            int i = 0;

            for (ViewSortingDTO viewSortingDTO : sortingList) {

                String queryForSort = queryForSort(viewSortingDTO, ENTITY_FIELDS, query);

                query.append(queryForSort);

                if (!queryForSort.isBlank() && (i != sortingList.size() - 1))
                    query.append(",");
                i++;
            }

            boolean existByIdList = idListByOtherService != null && !idListByOtherService.isEmpty();

            if (existByIdList)
                query
                        .append(" ,array_position('{")
                        .append(replaceBracketToEmpty(idListByOtherService))
                        .append("}', g.id) ");

            // order by array_position( '{b198a4d0-8449-4098-8c13-299c60e48730, 6b8aeb48-1b52-4f91-8ff7-567e2a7d58ad, 426490db-fa7d-46d9-90c5-468c4ddf0607}', g.id)


        }
    }

    /**
     * select id
     * from employee as g
     * where deleted = false
     * and g.id::text =any (string_to_array ('b198a4d0-8449-4098-8c13-299c60e48730,6b8aeb48-1b52-4f91-8ff7-567e2a7d58ad,426490db-fa7d-46d9-90c5-468c4ddf0607',','))
     * order by array_position(
     * '{b198a4d0-8449-4098-8c13-299c60e48730, 6b8aeb48-1b52-4f91-8ff7-567e2a7d58ad, 426490db-fa7d-46d9-90c5-468c4ddf0607}',
     * g.id)
     */
    //VIEW_FILTER UCHUN QUERY YOZIB BERADI
    private void toMakeViewFilterQuery(ViewFilterDTO viewFilter, StringBuilder
            query, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS, Map<String, CustomField> customFieldMap, List<String> idListByOtherService) {

        boolean existByIdList = idListByOtherService != null && !idListByOtherService.isEmpty();

        if ((viewFilter != null && viewFilter.getFilterFields() != null && !viewFilter.getFilterFields().isEmpty())
                || (viewFilter != null && viewFilter.getSearchingColumns() != null && !viewFilter.getSearchingColumns().isEmpty() && !viewFilter.getSearch().isBlank()
                || existByIdList)) {

            //  g.id::text =any (string_to_array ('b198a4d0-8449-4098-8c13-299c60e48730,6b8aeb48-1b52-4f91-8ff7-567e2a7d58ad,426490db-fa7d-46d9-90c5-468c4ddf0607',','))
            query.append("where deleted=false and ");
            if (existByIdList)
                query.append(" g.id::text=any(string_to_array('")
                        .append(replaceBracketToEmpty(idListByOtherService))
                        .append("',',')) and ( ");

            assert viewFilter != null;
            if (viewFilter.getSearch() != null && !viewFilter.getSearchingColumns().isEmpty() && !viewFilter.getSearch().isBlank())
                query.append("(");

            forEachFilterFields(viewFilter, ENTITY_FIELDS, query);

            //SEARCHING UCHUN QUERY YOZIB BERADI
            if (viewFilter.getSearch() != null && !viewFilter.getSearchingColumns().isEmpty() && !viewFilter.getSearch().isBlank())
                toMakeViewFilterSearchingQuery(viewFilter, query, ENTITY_FIELDS);

            if (existByIdList)
                query.append(" ) ");

        }
    }


    /**
     * select id
     * from employee as g
     * where
     * deleted = false
     * and g.id in (
     * 'e8ade6e3-e236-4a73-a2c1-7927cab330e2',
     * 'b198a4d0-8449-4098-8c13-299c60e48730',
     * '6b8aeb48-1b52-4f91-8ff7-567e2a7d58ad'
     * )
     * and (
     * g.first_name ilike 'ff' or g.user_id = '1936b29c-d487-4d3e-94d2-fc2c2de96fb2'
     * )
     * order by array_position('{
     * b198a4d0-8449-4098-8c13-299c60e48730,
     * 6b8aeb48-1b52-4f91-8ff7-567e2a7d58ad,
     * 426490db-fa7d-46d9-90c5-468c4ddf0607}', g.id)
     */
    private void forEachFilterFields(ViewFilterDTO viewFilter, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS, StringBuilder query) {

        String sql = "";

        // FILTERLARNI HAR BIRINI AYLANIB HAR BIR FIELDIGA SQL YOZIB ASOSIY QUERYGA QO'SHIB QO'YYAPDI
        for (int i = 0; i < viewFilter.getFilterFields().size(); i++) {

            //FILTER ICHIDAGI FILD UCHUN SQL query YOZILMOQDA
            FilterFieldDTO filterField = viewFilter.getFilterFields().get(i);
//                sql = "";

            //FIELD TURI CHECKBOX LIK HOLATDA
            Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap = ENTITY_FIELDS.get(filterField.getField());

            if (filterField.getFieldType().equals(CHECKBOX)) {

                if (filterField.getCompareOperatorType().equals(EQ) ||
                        filterField.getCompareOperatorType().equals(NOT) ||
                        filterField.getCompareOperatorType().equals(IS_SET) ||
                        filterField.getCompareOperatorType().equals(IS_NOT_SET)
                ) {

                    if (filterField.isCustomField()) {

                        sql = queryCustomFieldForCheckBox(filterField.getCompareOperatorType());
                    } else {

                        sql = queryForCheckBox(filterField.getField(), filterField.getCompareOperatorType());
                    }
                }

                //BIZ KUTADIGAN SOLISHTIRISH OPERATORLARIDAN BOSHQANI BERGANDA
                else throwIfCompareOperatorTypeError(CHECKBOX, EQ, NOT);
            }

            //FIELD TURI DROPDOWNLIK HOLATDA
            else if (filterField.getFieldType().equals(DROPDOWN) || filterField.getFieldType().equals(ENUM_DROPDOWN)) {

                if (filterField.getCompareOperatorType().equals(EQ)
                        || filterField.getCompareOperatorType().equals(NOT)) {

                    if (filterField.isCustomField()) {


                        sql = queryCustomFieldEquals(Arrays.toString(filterField.getValue().getOptionsSelected()), filterField.getCompareOperatorType());

                    } else {

                        if (valuesKeyEnumStringMap.get(RELATIONAL_TABLE) != null) {

                            sql = queryForDropDownRelationTable(filterField, valuesKeyEnumStringMap);

                        } else if (valuesKeyEnumStringMap.get(DIRECTIONAL_FOREIGN_TABLE) != null) {

                            sql = queryForDropDownDirectionForeignTable(filterField, valuesKeyEnumStringMap);

                        } else {

                            sql = queryJoinedTableForDropdown(filterField.getField(), filterField.getValue().getOptionsSelected(), filterField.getCompareOperatorType());
                        }
                    }

                    // QUERY YOZIB BOLIB (AND YOKI OR ) NI QO'SHIB QO'YADI.
                } else if (filterField.getCompareOperatorType().equals(IS_SET) ||
                        filterField.getCompareOperatorType().equals(IS_NOT_SET)) {
                    if (filterField.isCustomField()) {


                        sql = queryCustomFieldIsSetOrIsNotSet(filterField.getCompareOperatorType());
                    } else {

                        if (valuesKeyEnumStringMap.get(RELATIONAL_TABLE) != null) {
                            sql = queryForDropDownRelationTableIsSetOrIsNotSet(filterField, valuesKeyEnumStringMap);
                        } else {

                            sql = queryIsSetOrIsNotSet(filterField.getField(), filterField.getCompareOperatorType(), valuesKeyEnumStringMap);
                        }
                    }
                }

                //BIZ KUTADIGAN SOLISHTIRISH OPERATORLARIDAN BOSHQANI BERGANDA
                else throwIfCompareOperatorTypeError(DROPDOWN, EQ, NOT, IS_SET, IS_NOT_SET);

            }

            //FIELD TURI LABEL LIK HOLATDA
            else if (filterField.getFieldType().equals(LABELS)) {
                if (filterField.getCompareOperatorType().equals(ANY) ||
                        filterField.getCompareOperatorType().equals(ALL) ||
                        filterField.getCompareOperatorType().equals(NOT_ALL) ||
                        filterField.getCompareOperatorType().equals(NOT_ANY)) {
                    if (filterField.isCustomField()) {


                        sql = queryCustomFieldEqualsForLabels(filterField.getValue().getOptionsSelected(), filterField.getCompareOperatorType());

                    } else {

                        sql = queryEqualsForLabels(filterField.getField(), filterField.getValue().getOptionsSelected(), filterField.getCompareOperatorType(), ENTITY_FIELDS);

                    }
                } else if (filterField.getCompareOperatorType().equals(IS_SET) ||
                        filterField.getCompareOperatorType().equals(IS_NOT_SET)) {
                    if (filterField.isCustomField()) {


                        sql = queryCustomFieldIsSetOrIsNotSet(filterField.getCompareOperatorType());
                    } else {


                        sql = queryIsSetOrIsNotSet(filterField.getField(), filterField.getCompareOperatorType(), valuesKeyEnumStringMap);
                    }
                }

                //BIZ KUTADIGAN SOLISHTIRISH OPERATORLARIDAN BOSHQANI BERGANDA
                else throwIfCompareOperatorTypeError(LABELS, ANY, ALL, NOT_ANY, NOT_ALL, IS_SET, IS_NOT_SET);
            }


            //FILE TYPE DAGI NARSA BO'LSA
            else if (filterField.getFieldType().equals(FILES)) {
                if ((filterField.getCompareOperatorType().equals(IS_SET) || filterField.getCompareOperatorType().equals(IS_NOT_SET))) {
                    if (filterField.isCustomField()) {


                        sql = queryCustomFieldIsSetOrIsNotSet(filterField.getCompareOperatorType());
                    } else {


                        sql = queryIsSetOrIsNotSet(filterField.getField(), filterField.getCompareOperatorType(), valuesKeyEnumStringMap);
                    }
                }

                //BIZ KUTADIGAN SOLISHTIRISH OPERATORLARIDAN BOSHQANI BERGANDA
                else throwIfCompareOperatorTypeError(FILES, IS_SET, IS_NOT_SET);
            }

            //STRING BILAN ISHLAYDIGAN HAR QANDAY TYPE UCHUN
            else if (filterField.getFieldType().equals(LONG_TEXT) ||
                    filterField.getFieldType().equals(PHONE) ||
                    filterField.getFieldType().equals(EMAIL) ||
                    filterField.getFieldType().equals(PRIORITY) ||
                    filterField.getFieldType().equals(SHORT_TEXT)) {

                if (filterField.getCompareOperatorType().equals(EQ) ||
                        filterField.getCompareOperatorType().equals(NOT)) {

                    //QIDIRILAYTGAN TEXT CUSTOM FIELD COLUMN DA BO'LSA
                    if (filterField.isCustomField()) {

                        sql = queryCustomFieldLikeOrNotEquals(filterField.getValue().getSearchingValue(), filterField.getCompareOperatorType());
                    }
                    // QIDIRILYOTGAN TEXT HUMAN  TABLEDA BO'LSA
                    else {
                        if (Objects.equals(filterField.getField(), ColumnKey.USER_ID)) {

                            sql = queryForMentorView(filterField);

                        } else if (valuesKeyEnumStringMap.get(RELATIONAL_TABLE) == null && valuesKeyEnumStringMap.get(OTHER_TABLE) == null) {

                            sql = queryLikeOrNotEquals(filterField.getField(), filterField.getValue().getSearchingValue(), filterField.getCompareOperatorType());

                        } else if (valuesKeyEnumStringMap.get(RELATIONAL_TABLE) != null) {

                            sql = queryLikeOrNotEqualsForRelationalTable(filterField.getValue().getSearchingValue(), filterField.getCompareOperatorType(), valuesKeyEnumStringMap);

                        }
                    }

                    // QUERY YOZIB BOLIB (AND YOKI OR ) NI QO'SHIB QO'YADI.
                } else if (filterField.getCompareOperatorType().equals(IS_SET) ||
                        filterField.getCompareOperatorType().equals(IS_NOT_SET)) {

                    if (filterField.isCustomField()) {

                        sql = queryCustomFieldIsSetOrIsNotSet(filterField.getCompareOperatorType());

                    } else {

                        if (valuesKeyEnumStringMap.get(RELATIONAL_TABLE) == null && valuesKeyEnumStringMap.get(OTHER_TABLE) == null) {

                            sql = queryIsSetOrIsNotSet(filterField.getField(), filterField.getCompareOperatorType(), valuesKeyEnumStringMap);

                        } else if (valuesKeyEnumStringMap.get(RELATIONAL_TABLE) != null) {

                            sql = queryLikeOrNotEqualsForRelationalTableIsSetOrIsNotSet(filterField, valuesKeyEnumStringMap, filterField.getValue().getSearchingValue(), filterField.getCompareOperatorType());
                        }
                    }
                }
                //BIZ KUTADIGAN SOLISHTIRISH OPERATORLARIDAN BOSHQANI BERGANDA
                else throwIfCompareOperatorTypeError(SHORT_TEXT, EQ, NOT, IS_SET, IS_NOT_SET);
            }

            //HAR QANDAY RAQAMLIK FIELDLAR UCHUN
            else if (filterField.getFieldType().equals(MONEY) ||
                    filterField.getFieldType().equals(NUMBER) ||
                    filterField.getFieldType().equals(RATING)) {
                if (filterField.getCompareOperatorType().equals(IS_SET) ||
                        filterField.getCompareOperatorType().equals(IS_NOT_SET)) {

                    if (filterField.isCustomField()) {

                        sql = queryCustomFieldIsSetOrIsNotSet(filterField.getCompareOperatorType());
                    } else {

                        sql = queryIsSetOrIsNotSet(filterField.getField(), filterField.getCompareOperatorType(), valuesKeyEnumStringMap);
                    }

                } else if (filterField.getCompareOperatorType().equals(EQ) ||
                        filterField.getCompareOperatorType().equals(NOT) ||
                        filterField.getCompareOperatorType().equals(GT) ||
                        filterField.getCompareOperatorType().equals(LT) ||
                        filterField.getCompareOperatorType().equals(GTE) ||
                        filterField.getCompareOperatorType().equals(LTE) ||
                        filterField.getCompareOperatorType().equals(RA)) {

                    if (filterField.isCustomField()) {

                        sql = queryCustomFieldForNumericTypes(filterField.getValue().getMinValue(), filterField.getValue().getMaxValue(), filterField.getCompareOperatorType());

                    } else {
                        String directionalForeignTable = valuesKeyEnumStringMap.get(DIRECTIONAL_FOREIGN_TABLE);

                        if (directionalForeignTable == null) {

                            sql = queryForNumericTypes(filterField);
                        } else {
//                                // TODO HALI YOZILMAGAN ENDI YOZISH KK 10 JAN 2022
                            String summ = valuesKeyEnumStringMap.get(SUM);
                            String count = valuesKeyEnumStringMap.get(COUNT);
                            if (summ.isEmpty()) {
                                sql = queryForMoneyOtherTableSumm(filterField, valuesKeyEnumStringMap);
                            } else if (count.isEmpty()) {
                                sql = queryForMoneyOtherTableCount(filterField, valuesKeyEnumStringMap);
                            }
                        }
                    }
                }

                //BIZ KUTADIGAN SOLISHTIRISH OPERATORLARIDAN BOSHQANI BERGANDA
                else throwIfCompareOperatorTypeError(NUMBER, EQ, NOT, GT, LT, GTE, LTE, RA, IS_SET, IS_NOT_SET);
            }

            //DATE TURIDAGI FIELDLAR UCHUN
            else if (filterField.getFieldType().equals(DATE)) {
                checkingFilterFieldValueForDate(filterField);

                if (filterField.getCompareOperatorType().equals(IS_SET) ||
                        filterField.getCompareOperatorType().equals(IS_NOT_SET)) {
                    if (filterField.isCustomField()) {

                        sql = queryCustomFieldIsSetOrIsNotSet(filterField.getCompareOperatorType());

                    } else {

                        sql = queryIsSetOrIsNotSet(filterField.getField(), filterField.getCompareOperatorType(), valuesKeyEnumStringMap);

                    }
                } else if (filterField.getCompareOperatorType().equals(EQ) ||
                        filterField.getCompareOperatorType().equals(NOT)) {

                    sql = queryForDateType(filterField, valuesKeyEnumStringMap);
                }

                //BIZ KUTADIGAN SOLISHTIRISH OPERATORLARIDAN BOSHQANI BERGANDA
                else throwIfCompareOperatorTypeError(DATE, EQ, NOT, IS_SET, IS_NOT_SET);
            }

            //TREE TURIDAGI FIELDLAR UCHUN
            else if (filterField.getFieldType().equals(TREE)) {

                if (filterField.getCompareOperatorType().equals(EQ) ||
                        filterField.getCompareOperatorType().equals(NOT)) {
                    if (filterField.isCustomField()) {
                        sql = queryForCustomFieldForTree(filterField);
                    } else {
                        sql = queryForTree(filterField);
                    }
                } else if (filterField.getCompareOperatorType().equals(IS_NOT_SET) ||
                        filterField.getCompareOperatorType().equals(IS_SET)) {
                    if (filterField.isCustomField()) {
                        sql = queryForCustomFieldForTree(filterField);
                    } else {
                        sql = queryIsSetOrIsNotSet(filterField.getField(), filterField.getCompareOperatorType(), valuesKeyEnumStringMap);
                    }
                } else
                    throwIfCompareOperatorTypeError(TREE, EQ, NOT, IS_SET, IS_NOT_SET);
            }

            //BIZ KUTADIGAN SOLISHTIRISH OPERATORLARIDAN BOSHQANI BERGANDA
            else throw RestException.restThrow("Mavjud bo'lmgan type berildi", HttpStatus.BAD_REQUEST);

            query
                    .append(sql);
            if (filterField.isCustomField()) {
                query
                        .append(" and cfv.custom_field_id='")
                        .append(filterField.getField())
                        .append("')");
            }
            if (i != viewFilter.getFilterFields().size() - 1)
                query.append(viewFilter.getFilterOperator());
        }


    }
//g.id::text =any (string_to_array ('b198a4d0-8449-4098-8c13-299c60e48730,6b8aeb48-1b52-4f91-8ff7-567e2a7d58ad,426490db-fa7d-46d9-90c5-468c4ddf0607',','))
    private String queryForMentorView(FilterFieldDTO filterField) {

        return new StringBuilder()
                .append(" g.")
                .append(filterField.getField())
                .append("::text = any ( string_to_array('")
                .append(replaceBracketToEmpty(filterField.getIdListForFilter()))
                .append("', ',' )) ").toString();

    }

    private void checkingFilterFieldValueForDate(FilterFieldDTO filterField) {
        FilterFieldValueDTO value = filterField.getValue();
        if (filterField.getCompareOperatorType().equals(IS_NOT_SET) || filterField.getCompareOperatorType().equals(IS_SET)) {
            return;
        } else if (filterField.getCompareOperatorType().equals(EQ) || filterField.getCompareOperatorType().equals(NOT)) {
            if (value.getDateCompareOperatorType() == null) {
                throw RestException.restThrow("DATE_COMPARE_OPERATOR_TYPE BO'SH BO'LMASIN", HttpStatus.BAD_REQUEST);
            }
            if (value.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.EQ) ||
                    value.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.GT) ||
                    value.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.LT)) {
                if (value.getStarDate() == null) {
                    throw RestException.restThrow("START_DATE BO'SH BO'LMASIN", HttpStatus.BAD_REQUEST);
                }
                return;
            } else if (value.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.RA)) {
                if (value.getStarDate() == null) {
                    throw RestException.restThrow("START_DATE BO'SH BO'LMASIN", HttpStatus.BAD_REQUEST);
                }
                if (value.getEndDate() == null) {
                    throw RestException.restThrow("END_DATE BO'SH BO'LMASIN", HttpStatus.BAD_REQUEST);
                }
                return;
            } else if (value.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.THIS)) {
                if (value.getDateFilterType() == null) {
                    throw RestException.restThrow("DATE_FILTER_TYPE BO'SH BO'LMASIN", HttpStatus.BAD_REQUEST);
                }
                return;
            } else if (value.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.NEXT) ||
                    value.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.LAST)) {
                if (value.getDateXValue() == null || value.getDateXValue().equals("")) {
                    value.setDateXValue(0);
                }
                if (value.getDateFilterType() == null) {
                    throw RestException.restThrow("DATE_FILTER_TYPE BO'SH BO'LMASIN", HttpStatus.BAD_REQUEST);
                }
                return;
            } else if (
                    value.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.TOMORROW) ||
                            value.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.YESTERDAY) ||
                            value.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.TODAY)) {

                if (value.getDateCompareOperatorType() == null) {
                    throw RestException.restThrow("DATE_COMPARE_OPERATOR_TYPE BO'SH BO'LMASIN", HttpStatus.BAD_REQUEST);
                }
                return;
            }
        }
    }


    //SEARCHING UCHUN QUERY YOZIB BERADI
    private void toMakeViewFilterSearchingQuery(ViewFilterDTO viewFilter, StringBuilder
            query, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS) {

        if (!viewFilter.getSearchingColumns().isEmpty()) {
            //FILTER BOSH KELSA  BASE_OPERATOR NI QOYMASLIK KERAK.
            // AGARDA QUERYNI LENGTH I 60 DAN KICHIK BOLSA DEMAK FILTER BOSH KELGAN BOLADI
            if (query.toString().length() > 70) {
                query.append(viewFilter.getFilterOperator());
            }
            query.append(" ( ");
        }

        //CLIENT SEARCH QILGANDA
        String searchForQuery = searchForQuery(viewFilter, ENTITY_FIELDS);

        query.append(searchForQuery);

        if (!viewFilter.getSearchingColumns().isEmpty())
            query.append(") ");

    }

    private String queryLikeOrNotEqualsForRelationalTableIsSetOrIsNotSet(FilterFieldDTO
                                                                                 filter, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap, String
                                                                                 searchingValue, CompareOperatorTypeEnum compareOperatorType) {
        return new StringBuilder()
                .append(" g.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_ID)))
                .append(" in ( select ei.id from ")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE)))
                .append(" as ei where ei.deleted=false and ei.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_FOREIGN_TABLE_ID)))
                .append(" in (select e.id from ")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_FOREIGN_TABLE)))
                .append(" as e where e.deleted=false and e.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_FOREIGN_TABLE_COLUMN)))
                .append(filter.getCompareOperatorType().equals(IS_NOT_SET) ? " is null " : " is not null ")
                .append("ilike '%")
                .append(filter.getValue().getSearchingValue())
                .append("%')) ").toString();

    }

    // g.employment_info_id in (select ei.id from employment_info as ei where ei.employee_id in (select e.id from employee as e where e.first_name ilike '%%'))
    private String queryLikeOrNotEqualsForRelationalTable(String searchingValue, CompareOperatorTypeEnum
            compareOperatorTypeEnum, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap) {


        return new StringBuilder()
                .append(" g.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_ID)))
                .append(" in ( select ei.id from ")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE)))
                .append(" as ei where ei.deleted=false and ei.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_FOREIGN_TABLE_ID)))
                .append(" in (select e.id from ")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_FOREIGN_TABLE)))
                .append(" as e where e.deleted=false and cast(e.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_FOREIGN_TABLE_COLUMN)))
                .append(" as varchar)")
                .append(compareOperatorTypeEnum.equals(NOT) ? " not " : " ")
                .append("ilike '%")
                .append(searchingValue)
                .append("%')) ").toString();

    }


    //g.employment_info_id in (select ff.id from employment_info as ff where ff.department_id is  not null )
    private String queryForDropDownRelationTableIsSetOrIsNotSet(FilterFieldDTO
                                                                        filterField, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap) {

        return new StringBuilder()
                .append(" g.")
                .append(makeSnakeCase(filterField.getField()))
                .append(" in (select ff.id from ")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE)))
                .append(" as ff where ff.deleted=false and ff.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_FOREIGN_TABLE_ID)))
                .append(identify(filterField.getCompareOperatorType())).toString();
    }

    /**
     * (g.id = (select ff.invoice_id from invoice_time_table as ff where ff.invoice_id = g.id and ff.deleted = false and ff.time_table_id='b06692e4-bc56-4f16-80a0-d58b11ffc2f9'))
     */
    private String queryForDropDownDirectionForeignTable(FilterFieldDTO filterField, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap) {

        return new StringBuilder()
                .append(" (g.id = (select ff.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(DIRECTIONAL_OURS_COLUMN)))
                .append(" from ")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(DIRECTIONAL_FOREIGN_TABLE)))
                .append(" as ff where ff.deleted=false and ff.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(DIRECTIONAL_OURS_COLUMN)))
                .append("=g.id and ff.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(DIRECTIONAL_COLUMN)))
                .append("='")
                .append(replaceBracketToEmpty(filterField.getValue().getOptionsSelected()))
                .append("' ))").toString();

    }

    //g.employment_info_id in (select ff.id from employment_info as ff where ff.department_id = :depertmentId)
    private String queryForDropDownRelationTable(FilterFieldDTO
                                                         filterField, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap) {
        return new StringBuilder()
                .append(" g.")
                .append(makeSnakeCase(filterField.getField()))
                .append(" in (select ff.id from ")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE)))
                .append(" as ff where ff.deleted=false and ff.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_FOREIGN_TABLE_ID)))
                .append("=")
                .append(replaceBracketToEmpty(filterField.getValue().getOptionsSelected()))
                .append(") ").toString();


    }

    private String searchForQuery(ViewFilterDTO viewFilter, Map<String,
            Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS) {
        StringBuilder query = new StringBuilder();

        // search qilinyotgan sozni ikki tarafidagi bo'sh joylarni olib tashlaydi
        viewFilter.setSearch(viewFilter.getSearch().trim());

        if (viewFilter.getSearch() != null) {

            String sql = "";
            String search = viewFilter.getSearch();
            String columnName;
            CustomFieldTypeEnum columnType;

            for (ViewFilterSearchingColumnDTO viewFilterSearchingColumnDTO : viewFilter.getSearchingColumns()) {
                columnName = viewFilterSearchingColumnDTO.getColumnName();
                columnType = viewFilterSearchingColumnDTO.getColumnType();

                if (viewFilterSearchingColumnDTO.isCustomField()) {
                    if (columnType.equals(EMAIL) ||
                            columnType.equals(SHORT_TEXT) ||
                            columnType.equals(LONG_TEXT) ||
                            columnType.equals(PHONE)) {
                        Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap = ENTITY_FIELDS.get(viewFilterSearchingColumnDTO.getColumnName());

                        //CUSTOM FIELD TURI (EMAIL, SHORT_TEXT, LONG_TEXT, PHONE) BO'LGANLAR UCHUN SEARCHGA QUERY YOZIBERADI
                        sql = searchCustomFieldForTextPhoneEmail(viewFilterSearchingColumnDTO, search);

                    } else if (columnType.equals(LABELS)) {

                        //FIELD TURI LABELS BO'LGANLAR UCHUN SEARCHGA QUERY YOZIBERADI
                        sql = searchCustomFieldForLabels(columnName, search);
                    } else if (columnType.equals(DROPDOWN)) {

                        //CUSTOM FIELD TURI DROPDOWN BO'LGANLAR UCHUN SEARCHGA QUERY YOZIBERADI
                        sql = searchCustomFieldForDropDown(columnName, search);
                    } else if (columnType.equals(TREE)) {

                    }
                } else {

                    Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnum = ENTITY_FIELDS.get(columnName);
                    if (Objects.equals(RestConstants.YES, valuesKeyEnum.get(NOT_FROM_DB)))
                        continue;

                    // FIELD (TABLDAGI O'ZINING columnName) TURI (EMAIL, SHORT_TEXT, LONG_TEXT, PHONE) BO'LGANLAR UCHUN SEARCHGA QUERY YOZIBERADI
                    if (columnType.equals(EMAIL) ||
                            columnType.equals(LONG_TEXT) ||
                            columnType.equals(PHONE) ||
                            columnType.equals(ENUM_DROPDOWN) ||
                            columnType.equals(PRIORITY) ||
                            columnType.equals(SHORT_TEXT)) {
                        if (valuesKeyEnum.get(RELATIONAL_TABLE) != null) {

                            sql = queryLikeOrNotEqualsForRelationalTable(search, EQ, valuesKeyEnum);

                        } else {
                            // todo success
                            sql = searchForTextPhoneEmail(columnName, search);
                        }
                    }

                    // FIELD TURI LABELS BO'LGANLAR UCHUN SEARCHGA QUERY YOZIBERADI
                    else if (columnType.equals(LABELS)) {

                        //BIZ TURGAN ENTITY VA QIDIRILAYOTGAN QIYMAT UCHUN ALOHIDA 3-TABLE OCHILGAN BO'LSA
                        String otherTableName = valuesKeyEnum.get(OTHER_TABLE);

                        //O'ZIMIZNING DB DAGI TABLEGA BOG'LANGANLIGI
                        String foreignTable = valuesKeyEnum.get(FOREIGN_TABLE);

                        //SEARCH DA IZLANAYOTGAN STRINGNI BIZNING TABLEDAN BOSHQA TABLEDA SAQLANSAYU,
                        // LEKIN BIZNING TABLE BILAN 3-TABLESIZ BOG'LANGAN BO'LSA
                        if (otherTableName == null && foreignTable != null) {

                            sql = searchForLabelsSecondTable(columnName, search, valuesKeyEnum);

                        } else if (foreignTable == null) {
                            if (otherTableName != null) {
                                sql = searchForOtherServiceAndWithOtherTable(
                                        valuesKeyEnum.get(IN_OTHER_TABLE_SEARCHING_COLUMN_NAME),
                                        valuesKeyEnum.get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME),
                                        valuesKeyEnum.get(OTHER_TABLE),
                                        viewFilterSearchingColumnDTO.getSearchedList());
                            } else {
                                sql = searchForOtherServiceAndWithoutOtherTable(columnName, viewFilterSearchingColumnDTO.getSearchedList());
                            }
                        } else {
                            //SEARCH DA IZLANAYOTGAN STRINGNI BIZNING TABLEDAN BOSHQA TABLEDA SAQLANSAYU,
                            // LEKIN BIZNING TABLE BILAN ORADA 3-TABLE ORQALI BOG'LANGAN BO'LSA

                            sql = searchForLabels(columnName, search, ENTITY_FIELDS);
                        }

                    } else if (columnType.equals(DROPDOWN)) {

                        //BIZ TURGAN ENTITY VA QIDIRILAYOTGAN QIYMAT UCHUN ALOHIDA 3-TABLE OCHILGAN BO'LSA
                        String otherService = valuesKeyEnum.get(OTHER_SERVICE);

                        if (otherService == null) {
                            sql = searchForDropDownInThisService(columnName, search, ENTITY_FIELDS);
                        } else {
                            if (valuesKeyEnum.get(OTHER_TABLE) != null) {
                                sql = searchForDropDownInOtherServiceAndOtherTable(viewFilterSearchingColumnDTO.getSearchedList(), valuesKeyEnum);
                            } else {
                                sql = searchForDropDownInOtherService(columnName, viewFilterSearchingColumnDTO.getSearchedList());
                            }
                        }
                    } else if (columnType.equals(TREE)) {

                        sql = searchForTreeOwn(columnName, viewFilterSearchingColumnDTO.getSearchedList());

                    }
                }
                if (!sql.equals("")) {
                    query
                            .append(sql)
                            .append(" or ");
                }
            }

            String queryString = query.toString();

            if (!queryString.equals("")) {
                queryString = queryString.substring(0, queryString.length() - 3);

                queryString += (")");
                System.out.println(queryString);
                return queryString;
            }
        }
        return "";
    }

    //  (g.employment_info_id in (select ff.id from employment_info as ff where ff.branch_id = any ('{1,2,3,4}')))
    private String searchForDropDownInOtherServiceAndOtherTable
    (List<String> searchedList, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnum) {
        return new StringBuilder()
                .append(" (g.")
                .append(makeSnakeCase(valuesKeyEnum.get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME)))
                .append(" in (select ff.id from ")
                .append(makeSnakeCase(valuesKeyEnum.get(OTHER_TABLE)))
                .append(" as ff where ff.deleted=false and ff.")
                .append(makeSnakeCase(valuesKeyEnum.get(IN_OTHER_TABLE_SEARCHING_COLUMN_NAME)))
                .append("=any ('{")
                .append(replaceBracketToEmpty(searchedList))
                .append("}'))) ").toString();
    }

    //   cast(g.roles as varchar) ilike '{1,2%'
    private String searchForTreeOwn(String columnName, List<String> idList) {
        return new StringBuilder()
                .append(" cast(g.")
                .append(columnName)
                .append(" as varchar) ilike '{")
                .append(replaceBracketToEmpty(idList))
                .append("%' ").toString();

    }

    //  replace(cast(g.tree_column as varchar),' ','') ilike replace('{9%  ',' ','')
    private String queryForTree(FilterFieldDTO filterField) {

        StringBuilder sql = new StringBuilder()
                .append(" (replace(cast(g.")
                .append(makeSnakeCase(filterField.getField()))
                .append(" as varchar),' ','') ilike replace('{")
                .append(replaceBracketToEmpty(Arrays.toString(filterField.getValue().getOptionsSelected())))
                .append("%',' ','')");
        if (filterField.getCompareOperatorType().equals(NOT))
            sql.append("=false");

        sql.append(")");
        return sql.toString();
    }

    //  ((select sum(amount) from payment as p where p.id = g.id) between 10 and 15)
    private String queryForMoneyOtherTableCount(FilterFieldDTO
                                                        filterField, Map<EntityColumnMapValuesKeyEnum, String> mapValuesKeyEnumStringMap) {
        CompareOperatorTypeEnum compareOperatorType = filterField.getCompareOperatorType();
        String minValue = filterField.getValue().getMinValue();
        String maxValue = filterField.getValue().getMaxValue();

        String operator = identifyComparison(compareOperatorType);

        StringBuilder query = new StringBuilder()
                .append("(( select sum(")
                .append(makeSnakeCase(mapValuesKeyEnumStringMap.get(DIRECTIONAL_COLUMN)))
                .append(") from ")
                .append(makeSnakeCase(mapValuesKeyEnumStringMap.get(DIRECTIONAL_FOREIGN_TABLE)))
                .append(" as p where p.deleted=false and p.")
                .append(makeSnakeCase(mapValuesKeyEnumStringMap.get(DIRECTIONAL_OURS_COLUMN)))
                .append("=g.id ) ")
                .append(operator)
                .append(" '");
        if (compareOperatorType.equals(RA)) {
            query
                    .append(minValue)
                    .append("' and '")
                    .append(maxValue);
        } else {
            query
                    .append(minValue);
        }
        query.append("')");
        return query.toString();

    }

    private String queryForMoneyOtherTableSumm(FilterFieldDTO
                                                       filterField, Map<EntityColumnMapValuesKeyEnum, String> mapValuesKeyEnumStringMap) {

        CompareOperatorTypeEnum compareOperatorType = filterField.getCompareOperatorType();
        String minValue = filterField.getValue().getMinValue();
        String maxValue = filterField.getValue().getMaxValue();

        String operator = identifyComparison(compareOperatorType);

        StringBuilder query = new StringBuilder()
                .append("(( select count (")
                .append(makeSnakeCase(mapValuesKeyEnumStringMap.get(DIRECTIONAL_COLUMN)))
                .append(") from ")
                .append(makeSnakeCase(mapValuesKeyEnumStringMap.get(DIRECTIONAL_FOREIGN_TABLE)))
                .append(" as p where p.deleted=false and p.")
                .append(makeSnakeCase(mapValuesKeyEnumStringMap.get(DIRECTIONAL_OURS_COLUMN)))
                .append("=g.id ) ")
                .append(operator)
                .append(" '");
        if (compareOperatorType.equals(RA)) {
            query
                    .append(minValue)
                    .append("' and '")
                    .append(maxValue);
        } else {
            query
                    .append(minValue);
        }
        query.append("')");
        return query.toString();

    }


    private String searchForOtherServiceAndWithoutOtherTable(String columnName, List<String> searchIdList) {

        StringBuilder query = new StringBuilder()
                .append(" cast(g.")
                .append(makeSnakeCase(columnName))
                .append(" as varchar) in ('")
                .append(replaceBracketToEmpty(searchIdList))
                .append("')");
        return query.toString();
    }

    private String searchForOtherServiceAndWithOtherTable(String searchingColumnName,
                                                          String thisIdInOtherTableColumnName,
                                                          String otherTableName, List<String> searchIdList) {

        StringBuilder query = new StringBuilder()
                .append(" g.id in(select ")
                .append(makeSnakeCase(thisIdInOtherTableColumnName))
                .append(" from ")
                .append(otherTableName)
                .append(" where cast( ")
                .append(searchingColumnName)
                .append(" as varchar) in ('")
                .append(replaceBracketToEmpty(searchIdList))
                .append("'))");
        return query.toString();
    }

    /**
     * SEARCH DA IZLANAYOTGAN STRINGNI BIZNING TABLEDAN BOSHQA TABLEDA SAQLANSAYU,
     * LEKIN BIZNING TABLE BILAN 3-TABLESIZ BOG'LANGAN BO'LSA
     * SEARCH IZLANAYOTGAN COLUMN TURI LABELS
     */
    private String searchForLabelsSecondTable(String columnName,
                                              String search,
                                              Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnum) {
        return new StringBuilder()
                .append(" (select id from ")
                .append(makeSnakeCase(valuesKeyEnum.get(FOREIGN_TABLE)))
                .append(" where deleted=false and ")
                .append(makeSnakeCase(valuesKeyEnum.get(SEARCHING_COLUMN)))
                .append(" ilike '%")
                .append(search)
                .append("%') = ANY (")
                .append(makeSnakeCase(columnName))
                .append(") ")
                .toString();
    }

    // TODO SUCCESS
    //CUSTOM FIELD TURI DROPDOWN BO'LGANLAR UCHUN SEARCHGA QUERY YOZIBERADI
    private String searchCustomFieldForDropDown(String columnName, String search) {

        return new StringBuilder()
                .append("(select cfdd.")
                .append(CustomFieldDropDown.GET_NAME())
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_DROP_DOWN)
                .append(" as cfdd where cfdd.deleted=false and cast(cfdd.")
                .append(CustomFieldDropDown.GET_ID())
                .append(" as varchar)=(select cfv.")
                .append(CustomFieldValue.GET_VALUE())
                .append(" from ").append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv where cfv.deleted=false and cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_OWNER_ID()))
                .append(" = cast(g.id as varchar) and cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_CUSTOM_FIELD_ID()))
                .append("='")
                .append(columnName)
                .append("')) ilike '%")
                .append(search)
                .append("%'")
                .toString();
    }

    //        TURI LABELS BO'LGANLAR UCHUN SEARCHGA QUERY YOZIBERADI
    private String searchCustomFieldForLabels(String columnName, String search) {

        return new StringBuilder()
                .append("(select string_agg(cfl.label,',') from ")
                .append(TableNameConstant.CUSTOM_FIELD_LABEL)
                .append(" as cfl where cfl.deleted=false and (select string_to_array(cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_VALUE()))
                .append(",',') from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv where cfv.deleted=false and cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_OWNER_ID()))
                .append(" = cast(g.id as varchar) and cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_CUSTOM_FIELD_ID()))
                .append("='")
                .append(columnName)
                .append("') @> (select string_to_array(cast(cfl.id as varchar),','))) ilike '%")
                .append(search)
                .append("%'")
                .toString();
    }

    // TODO SUCCESS
    //FIELD TURI DROPDOWN BO'LGANLAR UCHUN SEARCHGA QUERY YOZIBERADI
    private String searchForDropDownInThisService(String columnName, String
            search, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS) {

        Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnum = ENTITY_FIELDS.get(columnName);
        String otherTableName = valuesKeyEnum.get(FOREIGN_TABLE);

        return new StringBuilder()
                .append("(select r.")
                .append(valuesKeyEnum.get(SEARCHING_COLUMN))
                .append(" from ")
                .append(otherTableName)
                .append(" as r where r.deleted=false and g.")
                .append(makeSnakeCase(columnName))
                .append("=r.id) ilike '%")
                .append(search)
                .append("%'")
                .toString();
    }


    //FIELD TURI DROPDOWN BO'LGANLAR UCHUN SEARCHGA QUERY YOZIBERADI
    private String searchForDropDownInOtherService(String columnName,
                                                   List<String> searchingIdList) {

        return new StringBuilder()
                .append(" g.")
                .append(makeSnakeCase(columnName))
                .append(" =any('{")
                .append(searchingIdList)
                .append("}') ")
                .toString();
    }

    // TODO SUCCESS
    //CUSTOM FIELD TURI (EMAIL, SHORT_TEXT, LONG_TEXT, PHONE) BO'LGANLAR UCHUN SEARCHGA QUERY YOZIBERADI
    private String searchCustomFieldForTextPhoneEmail(ViewFilterSearchingColumnDTO
                                                              viewFilterSearchingColumnDTO, String search) {
        return new StringBuilder()
                .append("cast(g.id as varchar) in (select cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv where cfv.deleted=false and ")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" = cast(g.id as varchar) and cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_CUSTOM_FIELD_ID()))
                .append("='")
                .append(viewFilterSearchingColumnDTO.getColumnName())
                .append("' and cfv.")
                .append(CustomFieldValue.GET_VALUE())
                .append(" ilike '%")
                .append(search)
                .append("%')")
                .toString();
    }

    //  cast(g.id as varchar) in (select cfv.owner_id from custom_field_value as cfv where owner_id = cast(g.id as varchar)
    //  and cfv.custom_field_id = '0fdda9a4-e410-412e-bbc8-f31797d9ef41' and cfv.value not ilike '0fdda9a4-e410-412e-bbc8-f31797d9ef41,0fdda9a4-e410-412e-bbc8-f31797d9ef42%')
    private String queryForCustomFieldForTree(FilterFieldDTO viewFilter) {
        String value = Arrays.toString(viewFilter.getValue().getOptionsSelected());

        StringBuilder sql = new StringBuilder()
                .append(" cast(g.id as varchar) in (select cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv where cfv.deleted=false and ")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" = cast(g.id as varchar) and cfv.")
                .append(CustomFieldValue.GET_VALUE())
                .append(operatorForTree(viewFilter.getCompareOperatorType()));
        if (viewFilter.getCompareOperatorType().equals(EQ) || (viewFilter.getCompareOperatorType().equals(NOT))) {
            sql
                    .append(replaceBracketToEmpty(value).replaceAll(" ", ""))
                    .append("%' ");
        }


        return sql.toString();
    }

    private String operatorForTree(CompareOperatorTypeEnum oper) {
        String operator = "";
        switch (oper) {
            case EQ:
                operator = " ilike '";
                break;
            case NOT:
                operator = " not ilike '";
                break;
            case IS_SET:
                operator = " is not null ";
                break;
            case IS_NOT_SET:
                operator = " is null ";
                break;
            default:
                throwIfCompareOperatorTypeError(TREE, EQ, NOT, IS_SET, IS_NOT_SET);
        }
        return operator;
    }

    // todo success
    // FIELD(TABLDAGI O'ZINING columnName) TURI (EMAIL, SHORT_TEXT, LONG_TEXT, PHONE) BO'LGANLAR UCHUN SEARCHGA QUERY YOZIBERADI
    private String searchForTextPhoneEmail(String columnName, String search) {
        return new StringBuilder()
                .append(" cast(g.")
                .append(makeSnakeCase(columnName))
                .append(" as varchar)")
                .append(" ilike '%")
                .append(search)
                .append("%' ")
                .toString();
    }

    // TODO SUCCESS

    /**
     * SEARCH DA IZLANAYOTGAN STRINGNI BIZNING TABLEDAN BOSHQA TABLEDA SAQLANSAYU,<p>
     * LEKIN BIZNING TABLE BILAN ORADA 3-TABLE ORQALI BOG'LANGAN BO'LSA<p>
     * SEARCH IZLANAYOTGAN COLUMN TURI LABELS
     */
    private String searchForLabels(String columnName, String search,
                                   Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS) {
        Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap = ENTITY_FIELDS.get(columnName);
        String searching_column = valuesKeyEnumStringMap.get(SEARCHING_COLUMN);
        String human_id = valuesKeyEnumStringMap.get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME);
        String course_id = valuesKeyEnumStringMap.get(IN_OTHER_TABLE_SEARCHING_COLUMN_NAME);
        String courseTableName = valuesKeyEnumStringMap.get(FOREIGN_TABLE);
        String course_human = valuesKeyEnumStringMap.get(OTHER_TABLE);


        return new StringBuilder()
                .append("(select string_agg(t2.")
                .append(searching_column)
                .append(", ',')from (select c.")
                .append(searching_column)
                .append(" from ")
                .append(courseTableName)
                .append(" as c where c.deleted=false and c.id in (select ")
                .append(course_id)
                .append(" from ")
                .append(course_human)
                .append(" where ")
                .append(human_id)
                .append("=g.id) order by c.")
                .append(searching_column)
                .append(") t2) ilike '%")
                .append(search)
                .append("'")
                .toString();
    }

    /**
     * HAR BIR SORT UCHUN QUERY YOZIBERADI
     */
    private String queryForSort(ViewSortingDTO sortingDTO, Map<String,
            Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS, StringBuilder query) {

        if (!sortingDTO.isCustomField()) {
            Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap = ENTITY_FIELDS.get(sortingDTO.getField());
        }
        if (!sortingDTO.isCustomField() && ENTITY_FIELDS.get(sortingDTO.getField()) == null)
            throw RestException.restThrow(ResponseMessage.ERROR_DOES_NOT_SORT_THIS_COLUMN + " => " + sortingDTO.getField(), HttpStatus.BAD_REQUEST);

        String sql = "";
        String startSql = "";

        //FIELD TURI CHECKBOX BO'LGANDA QUERY YOZILADI
        if (sortingDTO.getFieldType().equals(CHECKBOX)) {

            if (sortingDTO.isCustomField()) {
                sql = sortQueryCustomFieldForCheckBox(sortingDTO);
            } else {
                sql = sortQueryForCheckbox(sortingDTO);
            }
        }

        //FIELD TURI DROPDOWN BO'LGANDA QUERY YOZILADI
        else if (sortingDTO.getFieldType().equals(DROPDOWN)) {

            if (sortingDTO.isCustomField()) {

                sql = sortCustomFieldForDropDown(sortingDTO);

            } else if (ENTITY_FIELDS.get(sortingDTO.getField()).get(OTHER_SERVICE) == null) {

                sql = sortForDropDown(sortingDTO, ENTITY_FIELDS);

            } else if (ENTITY_FIELDS.get(sortingDTO.getField()).get(OTHER_SERVICE) != null) {

                // TODO  BOSHQA SERVICEGA BORIB KELGANDA SORT UCHUN
                sql = sortForOtherService(sortingDTO.getField(), sortingDTO.getSortingIdList());

            }
        }

        //FIELD TURI LABEL BO'LGANDA QUERY YOZILADI
        else if (sortingDTO.getFieldType().equals(LABELS)) {
            if (sortingDTO.isCustomField()) {

                sql = sortCustomFieldForLabels(sortingDTO);

            } else if (ENTITY_FIELDS.get(sortingDTO.getField()).get(OTHER_SERVICE) == null) {

                sql = sortForLabelsThisService(sortingDTO, ENTITY_FIELDS);

            } else if (ENTITY_FIELDS.get(sortingDTO.getField()).get(OTHER_SERVICE) != null) {

                if (ENTITY_FIELDS.get(sortingDTO.getField()).get(OTHER_TABLE) != null) {

                    //TODO ZAXIRA UCHUN PASTDAGI IKKITA QUERY XATO BERAVERSA BU QUERYNI ISHLATAMZ
                    //  sql = sortForOtherServiceLabelThreeTable(sortingDTO, ENTITY_FIELDS);

                    String arrs = UUID.randomUUID().toString().substring(0, 5);  //arrs
                    //with arrs as (select human_id from human_course order by array_position('{1,4,2,3}', course_id))

                    startSql = sortForOtherServiceLabelThreeTableStarts(sortingDTO, ENTITY_FIELDS, arrs);
                    String queryyy = query.toString();
                    query = new StringBuilder((startSql + queryyy));

                    // array_position((select array(select arrs.human_id from arrs)), id)
                    sql = sortForOtherServiceLabelThreeTableEnd(sortingDTO, ENTITY_FIELDS, arrs);

                } else {

                    //TODO OTHER SERVICE LN ID LIST OWN TABLEDA BOLGAN PAYTIDA tempTableF
                    sql = sortForOtherServiceLabelOwnTable(sortingDTO);

                }
            }
        }

        //FIELD TURI FILES BO'LGANDA QUERY YOZILADI
        else if (sortingDTO.getFieldType().equals(FILES)) {
            if (sortingDTO.isCustomField()) {
                sql = sortQueryCustomFieldIsSetOrIsNotSet(sortingDTO);
            } else {
                sql = sortQueryForFile(sortingDTO, ENTITY_FIELDS);
            }
        } else if (sortingDTO.getFieldType().equals(TREE)) {
            if (sortingDTO.isCustomField()) {
                sql = sortQueryCustomFieldTree(sortingDTO);
            } else {
                sql = sortQueryForTree(sortingDTO, ENTITY_FIELDS);
            }
        } else if (sortingDTO.getFieldType().equals(MONEY) ||
                sortingDTO.getFieldType().equals(NUMBER) ||
                sortingDTO.getFieldType().equals(RATING)) {

            if (sortingDTO.isCustomField()) {
                sql = sortCustomFieldForNumeric(sortingDTO);
            } else {

                Map<EntityColumnMapValuesKeyEnum, String> mapValuesKeyEnumStringMap = ENTITY_FIELDS.get(sortingDTO.getField());
                String directionalForeignTable = mapValuesKeyEnumStringMap.get(DIRECTIONAL_FOREIGN_TABLE);

                if (directionalForeignTable == null) {

                    sql = sortForNumericAndDateAndText(sortingDTO);

                } else {
                    String summ = mapValuesKeyEnumStringMap.get(SUM);
                    String count = mapValuesKeyEnumStringMap.get(COUNT);
                    if (!summ.isEmpty()) {
                        sql = querySORTForMoneyOtherTableSumm(false, sortingDTO, mapValuesKeyEnumStringMap);
                    } else if (!count.isEmpty()) {
                        sql = querySORTForMoneyOtherTableSumm(true, sortingDTO, mapValuesKeyEnumStringMap);
                    }
                }
            }

        } else if (sortingDTO.getFieldType().equals(DATE) ||
                sortingDTO.getFieldType().equals(TIME)) {
            if (sortingDTO.isCustomField()) {

                sql = sortCustomFieldForDate(sortingDTO);
            } else {


                sql = sortForNumericAndDateAndText(sortingDTO);
            }
        } else if (sortingDTO.getFieldType().equals(LONG_TEXT) ||
                sortingDTO.getFieldType().equals(PHONE) ||
                sortingDTO.getFieldType().equals(EMAIL) ||
                sortingDTO.getFieldType().equals(ENUM_DROPDOWN) ||
                sortingDTO.getFieldType().equals(PRIORITY) ||
                sortingDTO.getFieldType().equals(SHORT_TEXT)
        ) {

            if (sortingDTO.isCustomField()) {
                sql = sortCustomFieldForText(sortingDTO);
            } else {
                Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap = ENTITY_FIELDS.get(sortingDTO.getField());

                if (valuesKeyEnumStringMap.get(RELATIONAL_TABLE) == null) {
                    sql = sortForNumericAndDateAndText(sortingDTO);
                } else {
                    sql = sortForRelationTable(sortingDTO, valuesKeyEnumStringMap);
                }
            }
        } else if (sortingDTO.getFieldType().equals(SPECIAL_LABEL)) {

            if (sortingDTO.isCustomField()) {
                sql = sortCustomFieldForSuperLabel(sortingDTO);
            } else {
                sql = sortForSuperLabel(sortingDTO, ENTITY_FIELDS);
            }
        }

        return sql;
    }

    private String sortQueryCustomFieldTree(ViewSortingDTO sortingDTO) {

        return "";
    }

    //array_position('{2,3,4,45,1,2,1}',(select substring(cast(g.id as varchar),2,length(cast(g.id as varchar))-2)));
    private String sortQueryForTree(ViewSortingDTO
                                            sortingDTO, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entity_fields) {
        if (sortingDTO.getSortingIdList() == null || sortingDTO.getSortingIdList().isEmpty())
            return "";
        return new StringBuilder()
                .append(" array_position('{")
                .append(replaceBracketToEmpty(sortingDTO.getSortingIdList()))
                .append("}',(select substring(cast(g.")
                .append(makeSnakeCase(sortingDTO.getField()))
                .append(" as varchar),2,length(cast(g.")
                .append(makeSnakeCase(sortingDTO.getField()))
                .append(" as varchar))-2))) ").toString();

    }

    //array_position((select array(select ei.id from employment_info as ei order by array_position(
    // (select array(select ff.id from employee as ff order by ff.first_name asc)), ei.employee_id))), g.employment_info_id)
    private String sortForRelationTable(ViewSortingDTO
                                                sortingDTO, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap) {
        return new StringBuilder()
                .append("array_position ((select array(select dd.id from ")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE)))
                .append(" as dd order by array_position((select array(select ff.id from ")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_FOREIGN_TABLE)))
                .append(" as ff order by ff.")
                .append(makeSnakeCase(sortingDTO.getField()))
                .append(getDescOrAsc(sortingDTO.getDirection()))
                .append(")), dd.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_FOREIGN_TABLE_ID)))
                .append("))), g.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(RELATIONAL_TABLE_ID)))
                .append(") ").toString();


    }

    //(select count(*) from human_additional_number ss where ss.human_id=g.id ) asc nulls last
    private String sortForSuperLabel(ViewSortingDTO
                                             sortingDTO, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS) {
        Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap = ENTITY_FIELDS.get(sortingDTO.getField());

        return new StringBuilder()
                .append(" select count(*) from ")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(DIRECTIONAL_FOREIGN_TABLE)))
                .append(" ff where ff.deleted=false and ff.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(DIRECTIONAL_OURS_COLUMN)))
                .append("=g.id) asc nulls last").toString();
    }

    private String sortCustomFieldForSuperLabel(ViewSortingDTO sortingDTO) {
        throw RestException.restThrow("CUSTOM FIELDNI SPECIAL_LABEL TYPE  UCHUN SORT YOZILMAGAN ", HttpStatus.BAD_REQUEST);
    }

    // array_position((select array(select arrs.human_id from arrs)), id)
    private String sortForOtherServiceLabelThreeTableEnd(ViewSortingDTO
                                                                 sortingDTO, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS, String arrs) {
        StringBuilder sql = new StringBuilder()
                .append(" array_position((select array(select ")
                .append(arrs)
                .append(".")
                .append(ENTITY_FIELDS.get(sortingDTO.getField()).get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME))
                .append(" from ")
                .append(arrs)
                .append(")), id");

        return sql.toString();
    }

    // with arrs as (select human_id from human_course order by array_position('{1,4,2,3}', course_id))
    private String sortForOtherServiceLabelThreeTableStarts(ViewSortingDTO
                                                                    sortingDTO, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS, String arrs) {
        StringBuilder sql = new StringBuilder()
                .append(" with ")
                .append(arrs)
                .append(" as (select ")
                .append(ENTITY_FIELDS.get(sortingDTO.getField()).get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME))
                .append(" from ")
                .append("human_course")
                .append(" order by array_position('{")
                .append(replaceBracketToEmpty(sortingDTO.getSortingIdList()))
                .append("}', ")
                .append(ENTITY_FIELDS.get(sortingDTO.getField()).get(IN_OTHER_TABLE_SEARCHING_COLUMN_NAME))
                .append(")) ");

        return sql.toString();
    }

    // (select string_agg(gg::varchar, ',')
    //          from (select array_position('{4,2,1,3}', unnest(array [hobbies_id])) gg order by gg) f);
    private String sortForOtherServiceLabelOwnTable(ViewSortingDTO sortingDTO) {

        return new StringBuilder()
                .append(" (select string_agg(gg::varchar, ',') from (select array_position('{")
                .append(replaceBracketToEmpty(sortingDTO.getSortingIdList()))
                .append("}', unnest(array [")
                .append(sortingDTO.getField())
                .append("])) gg order by gg) f)").toString();
    }

    //sortForOtherServiceLabelOwnTable  method ni eski versiyasi
//    private String sortForOtherServiceLabelOwnTable(ViewSortingDTO sortingDTO) {
//
//        return new StringBuilder()
//                .append("(select min(gg) from (select array_position('{")
//                .append(replaceBracketToEmpty(sortingDTO.getSortingIdList()))
//                .append("}', unnest(array [")
//                .append(sortingDTO.getField())
//                .append("])) gg) f) ").toString();
//
//    }

    //array_position((select array(select human_id from human_course order by array_position('{3,4,2,1}', course_id))), id)
    private String sortForOtherServiceLabelThreeTable(ViewSortingDTO
                                                              sortingDTO, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS) {

        Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap = ENTITY_FIELDS.get(sortingDTO.getField());

        StringBuilder sql = new StringBuilder()
                .append(" array_position((select array(select ")
                .append(valuesKeyEnumStringMap.get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME))
                .append(" from ")
                .append(valuesKeyEnumStringMap.get(OTHER_TABLE))
                .append(" order by array_position('{")
                .append(replaceBracketToEmpty(sortingDTO.getSortingIdList()))
                .append("}', ")
                .append(valuesKeyEnumStringMap.get(IN_OTHER_TABLE_SEARCHING_COLUMN_NAME))
                .append(" ))), id ) ");
        return sql.toString();

    }


    //(select sum(amount) from payment as p where p.id = g.id) asc ,
    private String querySORTForMoneyOtherTableSumm(boolean count, ViewSortingDTO
            sortingDTO, Map<EntityColumnMapValuesKeyEnum, String> mapValuesKeyEnumStringMap) {
        StringBuilder query = new StringBuilder()
                .append("(select ");
        if (count) {
            query
                    .append("count(");
        } else {
            query
                    .append(" sum(");
        }
        query
                .append(mapValuesKeyEnumStringMap.get(DIRECTIONAL_COLUMN))
                .append(") from ")
                .append(mapValuesKeyEnumStringMap.get(DIRECTIONAL_FOREIGN_TABLE))
                .append(" as p where p.deleted=false and p.")
                .append(mapValuesKeyEnumStringMap.get(DIRECTIONAL_OURS_COLUMN))
                .append("=g.id) ")
                .append(getDescOrAsc(sortingDTO.getDirection()));

        return query.toString();
    }

    private String sortForOtherService(String columnName, List<String> uuidList) {
        return new StringBuilder()
                .append("( array_position('{")
                .append(replaceBracketToEmpty(uuidList))
                .append("}', ','), cast(g.")
                .append(makeSnakeCase(columnName))
                .append(" as text))")
                .toString();

    }

    private String sortForLabelsThisService(ViewSortingDTO
                                                    sortingDTO, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS) {
        StringBuilder sql = new StringBuilder();

        Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnum = ENTITY_FIELDS.get(sortingDTO.getField());
        String otherTableName = valuesKeyEnum.get(OTHER_TABLE);


        if (valuesKeyEnum.get(FOREIGN_TABLE) != null) {
            sql.append("(select string_agg(ll.")
                    .append(makeSnakeCase(valuesKeyEnum.get(ORDERING_COLUMN)))
                    .append(", ',') from (select ")
                    .append(makeSnakeCase(valuesKeyEnum.get(ORDERING_COLUMN)))
                    .append(" from ")
                    .append(valuesKeyEnum.get(FOREIGN_TABLE))
                    .append(" where deleted=false and id = ANY (g.")
                    .append(makeSnakeCase(sortingDTO.getField()))
                    .append(") order by ")
                    .append(makeSnakeCase(valuesKeyEnum.get(ORDERING_COLUMN)))
                    .append(getDescOrAsc(sortingDTO.getDirection()))
                    .append(") ll)")
                    .append(getDescOrAsc(sortingDTO.getDirection()));
        } else if (valuesKeyEnum.get(OTHER_TABLE) != null) {
            sql
                    .append("(select string_agg(t2.")
                    .append(valuesKeyEnum.get(IN_OTHER_TABLE_SEARCHING_COLUMN_NAME))
                    .append(", ',') from (select c.")
                    .append(valuesKeyEnum.get(ORDERING_COLUMN))
                    .append(" from ")
                    .append(valuesKeyEnum.get(FOREIGN_TABLE))
                    .append(" as c where c.deleted=false and c.id in (select ")
                    .append(valuesKeyEnum.get(IN_OTHER_TABLE_SEARCHING_COLUMN_NAME))
                    .append(" from ")
                    .append(valuesKeyEnum.get(OTHER_TABLE))
                    .append(" where ")
                    .append(valuesKeyEnum.get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME))
                    .append("=g.id) order by c.")
                    .append(valuesKeyEnum.get(ORDERING_COLUMN))
                    .append(" ")
                    .append(getDescOrAsc(sortingDTO.getDirection()))
                    .append(") t2 )")
                    .append(getDescOrAsc(sortingDTO.getDirection()));
        } else {

        }

        return sql.toString();
    }

    private String sortCustomFieldForText(ViewSortingDTO sortingDTO) {
        return new StringBuilder()
                .append("(select ")
                .append(CustomFieldValue.GET_VALUE())
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(getCustomFieldLastWhere(sortingDTO.getField()))
                .append(getDescOrAsc(sortingDTO.getDirection()))
                .toString();
    }

    private String sortCustomFieldForLabels(ViewSortingDTO sortingDTO) {


        return new StringBuilder()

                .append("(select string_agg(t1.labele, ',') from (select cfl.label as labele from ")
                .append(TableNameConstant.CUSTOM_FIELD_LABEL)
                .append(" cfl where cfl.deleted=false and (select string_to_array(cast(cfl.id as text), '')) <@(select string_to_array(cfv.value, ',')from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv ")
                .append(getCustomFieldLastWhere(sortingDTO.getField()))
                .append(" order by cfl.label ")
                .append(getDescOrAsc(sortingDTO.getDirection()))
                .append(")t1)")
                .append(getDescOrAsc(sortingDTO.getDirection()))
                .toString();
    }

    private String sortForDropDown(ViewSortingDTO
                                           sortingDTO, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS) {

        Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnum = ENTITY_FIELDS.get(sortingDTO.getField());

        String foreignTableName = valuesKeyEnum.get(FOREIGN_TABLE);

        String orderingColumnName = valuesKeyEnum.get(ORDERING_COLUMN);

        return new StringBuilder()
                .append("(select t.")
                .append(orderingColumnName)
                .append(" from ")
                .append(foreignTableName)
                .append(" as t where t.deleted=false and ")
                .append("t.id=g.")
                .append(makeSnakeCase(sortingDTO.getField()))
                .append(")")
                .append(getDescOrAsc(sortingDTO.getDirection()))
                .toString();
    }

    private String sortCustomFieldForDropDown(ViewSortingDTO sorting) {
        return new StringBuilder().append(" (select ")
                .append(CustomFieldDropDown.GET_NAME())
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_DROP_DOWN)
                .append(" where deleted=false and cast( ")
                .append(CustomFieldDropDown.GET_ID())
                .append(" as varchar)=(select ")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE())))
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" ")
                .append(getCustomFieldLastWhere(sorting.getField()))
                .append(")")
                .append(getDescOrAsc(sorting.getDirection())).toString();

    }

    private String sortForNumericAndDateAndText(ViewSortingDTO sorting) {
        return new StringBuilder()
                .append(" g.")
                .append(makeSnakeCase(sorting.getField()))
                .append(getDescOrAsc(sorting.getDirection()))
                .toString();
    }

    private String sortCustomFieldForNumeric(ViewSortingDTO sorting) {
        return String.valueOf(new StringBuilder().append(" (select cast(cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE())))
                .append(" as double precision) from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" cfv ")
                .append(getCustomFieldLastWhere(sorting.getField()))
                .append(getDescOrAsc(sorting.getDirection())));
    }

    private String sortCustomFieldForDate(ViewSortingDTO sorting) {
        return String.valueOf(new StringBuilder().append(" (select cast(cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE())))
                .append(" as timestamp)")
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" cfv ")
                .append(getCustomFieldLastWhere(sorting.getField()))
                .append(getDescOrAsc(sorting.getDirection())));
    }

    private String sortQueryForFile(ViewSortingDTO
                                            sorting, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS) {
        Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnum = ENTITY_FIELDS.get(sorting.getField());
        String otherTableName = valuesKeyEnum.get(OTHER_TABLE);

        if (otherTableName != null) {
            return new StringBuilder()
                    .append("(select count(*) from ")
                    .append(valuesKeyEnum.get(OTHER_TABLE))
                    .append(" tt2.where tt2.id=g.id)")
                    .append(getDescOrAsc(sorting.getDirection()))
                    .toString();
        } else {
            return new StringBuilder()
                    .append("(select array_length(g.")
                    .append(makeSnakeCase(sorting.getField()))
                    .append(",1))")
                    .append(getDescOrAsc(sorting.getDirection()))
                    .toString();
        }
    }

    private String sortQueryCustomFieldIsSetOrIsNotSet(ViewSortingDTO sorting) {
        return new StringBuilder()
                .append("(select array_length(string_to_array(cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE())))
                .append(", ','), 1) from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" cfv ")
                .append(getCustomFieldLastWhere(sorting.getField()))
                .append(getDescOrAsc(sorting.getDirection()))
                .toString();
    }

    private String sortQueryCustomFieldForCheckBox(ViewSortingDTO sortingDTO) {
        return String.valueOf(new StringBuilder()
                .append("(select ")
                .append(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE()))
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(getCustomFieldLastWhere(sortingDTO.getField()))
                .append(getDescOrAsc(sortingDTO.getDirection())));
    }


    private String getCustomFieldLastWhere(String customFieldId) {
        return new StringBuilder()
                .append(" where deleted=false and ")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_CUSTOM_FIELD())))
                .append("='")
                .append(customFieldId)
                .append("' and ")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" = cast(g.id as varchar))")
                .toString();
    }

    private String sortQueryForCheckbox(ViewSortingDTO sortingDTO) {
        StringBuilder sql = new StringBuilder();
        sql.append("g.")
                .append(makeSnakeCase(sortingDTO.getField()))
                .append(getDescOrAsc(sortingDTO.getDirection()));
        return sql.toString();
    }

    private String getDescOrAsc(Integer direction) {
        return direction == 1 ? "ASC NULLS LAST" : "DESC NULLS LAST";

    }

    private String queryForNumericTypes(FilterFieldDTO filterField) {
        //  >, >=, <, <=, =, !=,   belgilarini ajrarib beradi
        CompareOperatorTypeEnum compareOperatorType = filterField.getCompareOperatorType();
        FilterFieldValueDTO value = filterField.getValue();
        String operator = identifyComparison(compareOperatorType);

        if (!compareOperatorType.equals(RA)) {
            return new StringBuilder()
                    .append("(g.")
                    .append(makeSnakeCase(filterField.getField()))
                    .append(operator)
                    .append(" '")
                    .append(value.getMinValue())
                    .append("')").toString();
        } else {
            // AGAR RA TANLANGAN BO'LSA
            StringBuilder query = new StringBuilder();
            query
                    .append("(g.")
                    .append(makeSnakeCase(filterField.getField()));

            if (value.getMinValue() != null && value.getMaxValue() != null) {
                query
                        .append(operator)
                        .append(" '")
                        .append(value.getMinValue())
                        .append("' and '")
                        .append(value.getMaxValue())
                        .append("')");
            } else if (value.getMinValue() != null && value.getMaxValue() == null) {
                query
                        .append(">= '")
                        .append(value.getMinValue())
                        .append("')");
            } else if (value.getMinValue() == null && value.getMaxValue() != null) {
                query
                        .append("<= '")
                        .append(value.getMaxValue())
                        .append("')");
            } else if (value.getMinValue() == null && value.getMaxValue() == null) {
                query
                        .append(" is not null )");
            }
            return query.toString();
        }
    }


    private String queryCustomFieldForNumericTypes(String minValue, String maxValue, CompareOperatorTypeEnum
            compareOperatorType) {
        if (minValue == null)
            throw RestException.restThrow("Min value bo'lmasin", HttpStatus.BAD_REQUEST);

        if (compareOperatorType.equals(CompareOperatorTypeEnum.RA) && maxValue == null)
            throw RestException.restThrow("Oraliq qiymat olishda max value bo'sh bo'lmasin", HttpStatus.BAD_REQUEST);

        //  >, >=, <, <=, =, !=,   belgilarini ajrarib beradi
        String operator = identifyComparison(compareOperatorType);

        StringBuilder query = new StringBuilder()
                .append("cast(g.id as varchar) in (select cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv where cfv.deleted=false and cfv.")
                .append(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE()))
                .append(" ")
                .append(operator)
                .append(" '");
        if (compareOperatorType.equals(RA)) {
            query
                    .append(minValue)
                    .append("' and '")
                    .append(maxValue);
        } else {
            query.append(minValue);
        }
        query.append("' ");

        return query.toString();
    }

    //BU METHOD HUMAN TABLEDAGI COLUMNDAN SEARCH QILIP BERADI (LIKE %VALUE%)
    private String queryLikeOrNotEquals(String columnName, String searchValue, CompareOperatorTypeEnum
            compareOperatorType) {

        StringBuilder query = new StringBuilder();
        if (compareOperatorType.equals(CompareOperatorTypeEnum.EQ)) {
            query
                    .append("(cast(g.")
                    .append(makeSnakeCase(columnName))
                    .append(" as varchar)")
                    .append("ilike '%")
                    .append(searchValue)
                    .append("%");
        } else {
            query
                    .append(" NOT")
                    .append("(cast(g.")
                    .append(makeSnakeCase(columnName))
                    .append(" as varchar)")
                    .append("ilike '%")
                    .append(searchValue)
                    .append("%");
        }
        query.append("')");
        return query.toString();

    }

    private String queryForCheckBox(String columnName, CompareOperatorTypeEnum compareOperatorType) {
        StringBuilder query = new StringBuilder();

        query
                .append("(g.")
                .append(makeSnakeCase(columnName));
        if (compareOperatorType.equals(EQ) || compareOperatorType.equals(IS_SET))
            query.append("=true ");
        else {
            query.append("=false or g.")
                    .append(makeSnakeCase(columnName))
                    .append(" is null");
        }
        query.append(")");
        return query.toString();
    }

    private String queryIsSetOrIsNotSet(String columnName, CompareOperatorTypeEnum type, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap) {

        String operator = identify(type);

        StringBuilder sql = new StringBuilder();
        if (valuesKeyEnumStringMap.get(DIRECTIONAL_FOREIGN_TABLE) != null) {
            sql = preQueryForDateForDirectionTable(valuesKeyEnumStringMap);
        } else {
            sql.append("(g.");
        }

        sql
                .append(makeSnakeCase(columnName))
                .append(operator)
                .append(")");

        return sql.toString();
    }

    private String queryJoinedTableForDropdown(String field, String[] optionsSelected,
                                               CompareOperatorTypeEnum compareOperatorTypeEnum) {

        String searchValue = replaceBracketToEmpty(Arrays.toString(optionsSelected));

        StringBuilder query = new StringBuilder()
                .append("(g.")
                .append(makeSnakeCase(field));
        if (compareOperatorTypeEnum.equals(EQ)) {
            query.append("=");
        } else {
            query.append("<>");
        }
        query
                .append("'")
                .append(searchValue)
                .append("')");
        return query.toString();
    }

    public String replaceBracketToEmpty(Object object) {
        return object.toString().replaceAll("[\\[\\]\" ]", "");
    }

    // CUSTOM FIELDDAN  BERILGAN SEARCHVALUENI SEARCH QILIP BERADI (value=searchValue)
    private String queryCustomFieldLikeOrNotEquals(String searchValue, CompareOperatorTypeEnum compareOperatorType) {

        StringBuilder query = new StringBuilder();
        query.append("cast(g.id as varchar) in  (select cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append("from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv where cfv.deleted=false and cfv.")
                .append(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE()))
                .append(" ");
        if (compareOperatorType.equals(CompareOperatorTypeEnum.EQ)) {
            query.append("ilike '%");
            query.append(searchValue);
            query.append("%' ");
        } else {
            query.append("<>'");
            query.append(searchValue);
            query.append("'");
        }
        return query.toString();
    }

    // CUSTOM FIELDDAN  BERILGAN SEARCHVALUENI SEARCH QILIP BERADI (value=searchValue)
    private String queryCustomFieldEquals(String searchValue, CompareOperatorTypeEnum compareOperatorType) {
        if (searchValue.startsWith("[")) {
            searchValue = searchValue.replaceAll("[\\[\\]\" ]", "");
        }

        StringBuilder query = new StringBuilder();
        query.append("cast(g.id as varchar) in  (select cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv where cfv.deleted=false and cfv.")
                .append(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE()));

        if (compareOperatorType.equals(EQ)) {
            query.append("='");
        } else {
            query.append("<>'");
        }
        query
                .append(searchValue)
                .append("'");
        return query.toString();
    }

    // CUSTOM FIELDDAN  BERILGAN SEARCHVALUENI SEARCH QILIP BERADI (value=searchValue)
    private String queryCustomFieldEqualsForLabels(String[] optionsSelected, CompareOperatorTypeEnum
            compareOperatorType) {

        String searchValue = replaceBracketToEmpty(Arrays.toString(optionsSelected));

        StringBuilder query = new StringBuilder();
        query.append("cast(g.id as varchar) in  (select cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv where cfv.deleted=false and ((select string_to_array(cfv.value,','))");

        if (compareOperatorType.equals(ANY) || compareOperatorType.equals(NOT_ANY)) {
            query.append(" && ");
        } else if (compareOperatorType.equals(ALL) || compareOperatorType.equals(NOT_ALL)) {
            query.append(" @> ");
        }

        query
                .append("(select string_to_array('")
                .append(searchValue)
                .append("', ','))");

        if (compareOperatorType.equals(NOT_ALL) || compareOperatorType.equals(NOT_ANY)) {
            query.append("=false ");
        }
        query.append(")");
        return query.toString();
    }

    private String queryEqualsForLabels(String field, String[] optionsSelected,
                                        CompareOperatorTypeEnum compareOperatorType,
                                        Map<String, Map<EntityColumnMapValuesKeyEnum, String>> ENTITY_FIELDS) {

        String searchValue = replaceBracketToEmpty(Arrays.toString(optionsSelected));

        Map<EntityColumnMapValuesKeyEnum, String> entityColumnMapValuesKeyEnumStringMap = ENTITY_FIELDS.get(field);

        String otherTable = entityColumnMapValuesKeyEnumStringMap.get(OTHER_TABLE);

        StringBuilder query = new StringBuilder();

        String NOT_OR_ANY_OR_ALL = "";

        if (compareOperatorType.equals(ANY) || compareOperatorType.equals(NOT_ANY)) {
            NOT_OR_ANY_OR_ALL = " && ";
        } else if (compareOperatorType.equals(ALL) || compareOperatorType.equals(NOT_ALL)) {
            NOT_OR_ANY_OR_ALL = " @> ";
        }

        //O'ZIMIZ TURGAN TABLEDAN QIDIRAMIZ ARRAY[]
        if (otherTable == null) {
            query.append("((select cast(g.")
                    .append(makeSnakeCase(field))
                    .append(" as text[]))")
                    .append(NOT_OR_ANY_OR_ALL)
                    .append("(select string_to_array('")
                    .append(searchValue)
                    .append("', ','))");

            if (compareOperatorType.equals(NOT_ALL) || compareOperatorType.equals(NOT_ANY)) {
                query.append("=false");
            }
        }
        //BOSHQA TABLEDAN QIDIRIAMIZ
        else {
            Map<EntityColumnMapValuesKeyEnum, String> columnMapValuesKeyEnumStringMap = ENTITY_FIELDS.get(field);

            query
                    .append("g.id in(select temptt.")
                    .append(columnMapValuesKeyEnumStringMap.get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME))
                    .append(" from (select ")
                    .append(columnMapValuesKeyEnumStringMap.get(OTHER_TABLE))
                    .append(".")
                    .append(columnMapValuesKeyEnumStringMap.get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME))
                    .append(", cast(array_agg(")
                    .append(columnMapValuesKeyEnumStringMap.get(IN_OTHER_TABLE_SEARCHING_COLUMN_NAME))
                    .append(")as text[])as temp_array_agg from ")
                    .append(columnMapValuesKeyEnumStringMap.get(OTHER_TABLE))
                    .append(" group by ")
                    .append(columnMapValuesKeyEnumStringMap.get(IN_OTHER_TABLE_THIS_TABLES_ID_NAME))
                    .append(") temptt where temptt.temp_array_agg ")
                    .append(NOT_OR_ANY_OR_ALL)
                    .append(" (select string_to_array('")
                    .append(searchValue)
                    .append("', ','))");


            if (compareOperatorType.equals(NOT_ALL) || compareOperatorType.equals(NOT_ANY)) {
                query.append("=false ");
            }
        }
        query.append(")");
        return query.toString();
    }

    private String queryCustomFieldForCheckBox(CompareOperatorTypeEnum compareOperatorType) {
        StringBuilder query = new StringBuilder();
        query.append("cast(g.id as varchar) in  (select cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv where cfv.deleted=false and cfv.")
                .append(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE()));
        if (compareOperatorType.equals(EQ)) {
            query.append("='true'");
        } else {
            query.append("='false' or cfv.")
                    .append(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE()))
                    .append(" is null");
        }
        return query.toString();
    }

    private String queryCustomFieldIsSetOrIsNotSet(CompareOperatorTypeEnum operatorTypeEnum) {

        //IS_SET KELGANDA 'IS NOT NULL' IS_NOT_SET KELGANDA 'IS NULL' YOZIBERADI
        String operator = identify(operatorTypeEnum);

        StringBuilder query = new StringBuilder();
        query.append("cast(g.id as varchar) in  (select cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv where cfv.deleted=false and cfv.")
                .append(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE()))
                .append(operator);
        return query.toString();

    }

    private String queryForDateType(FilterFieldDTO
                                            filterField, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap) {

        DateCompareOperatorTypeEnum dateCompareOperatorType = filterField.getValue().getDateCompareOperatorType();

        if (filterField.isCustomField()) {

            if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.TODAY) ||
                    (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.YESTERDAY)) ||
                    (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.TOMORROW))) {

                return queryCustomFieldForDateYesterdayOrTodayOrTomorrow(filterField.getCompareOperatorType(), dateCompareOperatorType);

            } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.NEXT) ||
                    dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.LAST)) {

                return queryCustomFieldForDateLastOrNext(filterField);

            } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.THIS)) {
                return queryCustomFieldForThisDayOrWeekOrMonthOrYear(filterField);

            } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.RA)) {

                return queryCustomFieldForRange(filterField);
            } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.LT) ||
                    dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.GT) ||
                    dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.EQ)) {

                //BEFORE(LT) YOKI AFTER(GT) TANLAGANDA TANLANGANDA
                return queryCustomFieldForBeforeOrAfter(filterField);

            } else {
                throwIfCompareOperatorTypeError(
                        DateCompareOperatorTypeEnum.YESTERDAY,
                        DateCompareOperatorTypeEnum.TOMORROW,
                        DateCompareOperatorTypeEnum.THIS,
                        DateCompareOperatorTypeEnum.NEXT,
                        DateCompareOperatorTypeEnum.LAST,
                        DateCompareOperatorTypeEnum.RA,
                        DateCompareOperatorTypeEnum.LT,
                        DateCompareOperatorTypeEnum.GT,
                        DateCompareOperatorTypeEnum.EQ);
            }
        } else {

            //AGAR DIRECTION COLUMN BO'LSA
            boolean havePreQuery = (valuesKeyEnumStringMap.get(DIRECTIONAL_FOREIGN_TABLE) != null);

            if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.TODAY) ||
                    (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.YESTERDAY)) ||
                    (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.TOMORROW))) {

                return queryForDateYesterdayOrTodayOrTomorrow(filterField, valuesKeyEnumStringMap, havePreQuery);

            } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.NEXT) ||
                    dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.LAST)) {

                return queryForDateLastOrNext(filterField, valuesKeyEnumStringMap, havePreQuery);

            } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.THIS)) {

                return queryForThisDayOrWeekOrMonthOrYear(filterField, valuesKeyEnumStringMap, havePreQuery);

            } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.RA)) {

                return queryForDateRange(filterField, valuesKeyEnumStringMap, havePreQuery);

            } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.LT) ||
                    dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.GT) ||
                    dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.EQ)) {

                //BEFORE(LT) YOKI AFTER(GT) TANLAGANDA TANLANGANDA
                return queryForDateBeforeOrAfter(filterField, valuesKeyEnumStringMap, havePreQuery);

            } else {
                throwIfCompareOperatorTypeError(
                        DateCompareOperatorTypeEnum.YESTERDAY,
                        DateCompareOperatorTypeEnum.TOMORROW,
                        DateCompareOperatorTypeEnum.THIS,
                        DateCompareOperatorTypeEnum.NEXT,
                        DateCompareOperatorTypeEnum.LAST,
                        DateCompareOperatorTypeEnum.RA,
                        DateCompareOperatorTypeEnum.LT,
                        DateCompareOperatorTypeEnum.GT,
                        DateCompareOperatorTypeEnum.EQ);
            }
        }
        return "";
    }

    // g.id in (select ff.employee_id from employment_info as ff where ff.employee_id = g.id and ff.
    // QUERYNI QOLGAN QISMINI IF LARNI ICHIDA YOZADI. BU PRE QUERY
    private StringBuilder preQueryForDateForDirectionTable
    (Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap) {
        return new StringBuilder()
                .append(" g.id in (select ff.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(DIRECTIONAL_OURS_COLUMN)))
                .append(" from ")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(DIRECTIONAL_FOREIGN_TABLE)))
                .append(" as ff where ff.deleted=false and ff.")
                .append(makeSnakeCase(valuesKeyEnumStringMap.get(DIRECTIONAL_OURS_COLUMN)))
                .append("=g.id and ff.");
    }


    private String queryCustomFieldForDateYesterdayOrTodayOrTomorrow(CompareOperatorTypeEnum
                                                                             compareOperatorType, DateCompareOperatorTypeEnum dateCompareOperatorTypeEnum) {
        StringBuilder query = new StringBuilder();
        query.append("cast(g.id as varchar) in  (select cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" ")
                .append("from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv where cfv.deleted=false and (")
                .append("cast(cfv.")
                .append(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE()))
                .append(" as date) ")
                .append("between ");
        if (dateCompareOperatorTypeEnum.equals(DateCompareOperatorTypeEnum.TODAY)) {
            query
                    .append(getCurrentDateAddingOrSubsInterval(0, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY))
                    .append(" and ")
                    .append(getCurrentDateAddingOrSubsInterval(1, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY));
        } else if (dateCompareOperatorTypeEnum.equals(DateCompareOperatorTypeEnum.YESTERDAY)) {
            query.append(getCurrentDateAddingOrSubsInterval(-1, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY))
                    .append(" and ")
                    .append(getCurrentDateAddingOrSubsInterval(0, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY));
        } else if (dateCompareOperatorTypeEnum.equals(DateCompareOperatorTypeEnum.TOMORROW)) {
            query.append(getCurrentDateAddingOrSubsInterval(1, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY))
                    .append(" and ")
                    .append(getCurrentDateAddingOrSubsInterval(2, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY));
        } else {
            throwIfCompareOperatorTypeError(DateCompareOperatorTypeEnum.TOMORROW, DateCompareOperatorTypeEnum.TODAY, DateCompareOperatorTypeEnum.YESTERDAY);
        }
        if (compareOperatorType.equals(NOT)) {
            query.append("=false ");
        }
        query.append(")");
        return query.toString();
    }


    private String queryForDateYesterdayOrTodayOrTomorrow(FilterFieldDTO
                                                                  filterFieldDTO, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap, boolean havePreQuery) {
        StringBuilder query = new StringBuilder();

        DateCompareOperatorTypeEnum dateCompareOperatorType = filterFieldDTO.getValue().getDateCompareOperatorType();

        if (havePreQuery) {
            query = preQueryForDateForDirectionTable(valuesKeyEnumStringMap);
        } else {
            query.append("(g.");
        }
        query
                .append(makeSnakeCase(filterFieldDTO.getField()))
                .append(" between ");
        if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.TODAY)) {
            query
                    .append(getCurrentDateAddingOrSubsInterval(0, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY))
                    .append(" and ")
                    .append(getCurrentDateAddingOrSubsInterval(1, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY));
        } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.YESTERDAY)) {
            query
                    .append(getCurrentDateAddingOrSubsInterval(-1, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY))
                    .append(" and ")
                    .append(getCurrentDateAddingOrSubsInterval(0, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY));
        } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.TOMORROW)) {
            query
                    .append(getCurrentDateAddingOrSubsInterval(1, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY))
                    .append(" and ")
                    .append(getCurrentDateAddingOrSubsInterval(2, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY));
        } else {
            throwIfCompareOperatorTypeError(DateCompareOperatorTypeEnum.TOMORROW, DateCompareOperatorTypeEnum.TODAY, DateCompareOperatorTypeEnum.YESTERDAY);
        }

        if (filterFieldDTO.getCompareOperatorType().equals(NOT)) {
            if (havePreQuery) {
                query.append(" =false or ff.");
            } else {
                query.append("=false or g.");
            }
            query
                    .append(makeSnakeCase(filterFieldDTO.getField()))
                    .append(" is null");
        }
        query.append(" ) ");
        return query.toString();
    }


    private String queryCustomFieldForDateLastOrNext(FilterFieldDTO filterFieldDTO) {
        FilterFieldValueDTO filterFieldValueDTO = filterFieldDTO.getValue();

        StringBuilder query = new StringBuilder();
        query.append("cast(g.id as varchar) in  (select cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                .append(" as cfv where cfv.deleted=false and (")
                .append("cast(cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE())))
                .append(" as date) ")
                .append("between ");
        int dateXValue = filterFieldValueDTO.getDateFilterType().equals(DateFilterTypeEnum.WEEK) ?
                (filterFieldValueDTO.getDateXValue() * 7) : filterFieldValueDTO.getDateXValue();

        if (filterFieldValueDTO.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.LAST)) {
            query
                    .append(getCurrentDateAddingOrSubsInterval(
                            -dateXValue,
                            filterFieldValueDTO.getDateFilterType().equals(DateFilterTypeEnum.WEEK) ?
                                    DateFilterTypeEnum.DAY :
                                    filterFieldValueDTO.getDateFilterType(),
                            filterFieldValueDTO.getDateFilterType()
                    ))
                    .append(" and ")
                    .append(getCurrentDateAddingOrSubsInterval(0, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY));
        } else if (filterFieldValueDTO.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.NEXT)) {
            query
                    .append(getCurrentDateAddingOrSubsInterval(0, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY))
                    .append(" and ")
                    .append(getCurrentDateAddingOrSubsInterval(
                            dateXValue,
                            filterFieldValueDTO.getDateFilterType().equals(DateFilterTypeEnum.WEEK) ?
                                    DateFilterTypeEnum.DAY :
                                    filterFieldValueDTO.getDateFilterType(),
                            filterFieldValueDTO.getDateFilterType()
                    ));
        }
        if (filterFieldDTO.getCompareOperatorType().equals(NOT)) {
            query.append("=false ");
        }
        query.append(")");
        return query.toString();
    }


    private String queryForDateLastOrNext(FilterFieldDTO
                                                  filterFieldDTO, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap, boolean havePreQuery) {
        FilterFieldValueDTO value = filterFieldDTO.getValue();
        Integer dateXValue = value.getDateXValue();
        StringBuilder query = new StringBuilder();

        DateCompareOperatorTypeEnum dateCompareOperatorType = filterFieldDTO.getValue().getDateCompareOperatorType();
        if (havePreQuery) {
            query = preQueryForDateForDirectionTable(valuesKeyEnumStringMap);
        } else {
            query.append("(g.");
        }
        query
                .append(makeSnakeCase(filterFieldDTO.getField()))
                .append(" between ");

        if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.NEXT)) {
            query
                    .append(getCurrentDateAddingOrSubsInterval(0, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY))
                    .append(" and ")
                    .append(getCurrentDateAddingOrSubsInterval(
                            dateXValue,
                            value.getDateFilterType().equals(DateFilterTypeEnum.WEEK) ?
                                    DateFilterTypeEnum.DAY :
                                    value.getDateFilterType(),
                            value.getDateFilterType()
                    ));
        } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.LAST)) {
            query
                    .append(getCurrentDateAddingOrSubsInterval(
                            -dateXValue,
                            value.getDateFilterType().equals(DateFilterTypeEnum.WEEK) ?
                                    DateFilterTypeEnum.DAY :
                                    value.getDateFilterType(),
                            value.getDateFilterType()
                    ))
                    .append(" and ")
                    .append(getCurrentDateAddingOrSubsInterval(0, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY));

        } else {
            throwIfCompareOperatorTypeError(DateCompareOperatorTypeEnum.LAST, DateCompareOperatorTypeEnum.NEXT);
        }

        if (filterFieldDTO.getCompareOperatorType().equals(NOT)) {
            if (havePreQuery) {
                query.append(" =false or ff.");
            } else {
                query.append("=false or g.");
            }
            query
                    .append(makeSnakeCase(filterFieldDTO.getField()))
                    .append(" is null");
        }
        query.append(" ) ");
        return query.toString();
    }


    private String queryCustomFieldForThisDayOrWeekOrMonthOrYear(FilterFieldDTO filterFieldDTO) {
        FilterFieldValueDTO filterFieldValueDTO = filterFieldDTO.getValue();

        StringBuilder query = new StringBuilder();
        query.append("cast(g.id as varchar) in  (select cfv.")
                .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                .append(" from ")
                .append(TableNameConstant.CUSTOM_FIELD_VALUE + " ")
                .append(" as cfv where cfv.deleted=false and (")
                .append("cast(cfv.")
                .append(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE()))
                .append(" as date) ")
                .append("between ");
        int interval = filterFieldValueDTO.getDateFilterType().equals(DateFilterTypeEnum.WEEK) ? 7 : 1;
        DateFilterTypeEnum dateFilterType = filterFieldValueDTO.getDateFilterType();

        if (filterFieldValueDTO.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.THIS)) {

            query
                    .append(getCurrentDateAddingOrSubsInterval(0, dateFilterType, dateFilterType))

                    .append(getCurrentDateAddingOrSubsInterval(
                            interval,
                            dateFilterType,
                            dateFilterType.equals(DateFilterTypeEnum.WEEK) ?
                                    DateFilterTypeEnum.DAY :
                                    dateFilterType
                    ))
                    .append(" and ")
                    .append(getCurrentDateAddingOrSubsInterval(0, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY));
        } else {
            throwIfDateFilterTypeError(DateFilterTypeEnum.DAY, DateFilterTypeEnum.WEEK, DateFilterTypeEnum.MONTH, DateFilterTypeEnum.YEAR);
        }
        if (filterFieldDTO.getCompareOperatorType().equals(NOT)) {
            query.append("=false ");
        }
        query.append(")");
        return query.toString();
    }


    private String queryForThisDayOrWeekOrMonthOrYear(FilterFieldDTO
                                                              filterFieldDTO, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap, boolean havePreQuery) {

        FilterFieldValueDTO value = filterFieldDTO.getValue();
        StringBuilder query = new StringBuilder();

        if (havePreQuery) {
            query = preQueryForDateForDirectionTable(valuesKeyEnumStringMap);
        } else {
            query.append("(g.");
        }
        query
                .append(makeSnakeCase(filterFieldDTO.getField()))
                .append(" between ");

        int interval = value.getDateFilterType().equals(DateFilterTypeEnum.WEEK) ? 7 : 1;
        DateFilterTypeEnum dateFilterType = value.getDateFilterType();

        if (value.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.THIS)) {

            query
//                    .append(getCurrentDateAddingOrSubsInterval(0, dateFilterType, dateFilterType))

                    .append(getCurrentDateAddingOrSubsInterval(
                            interval,
                            dateFilterType,
                            dateFilterType.equals(DateFilterTypeEnum.WEEK) ?
                                    DateFilterTypeEnum.DAY :
                                    dateFilterType
                    ))
                    .append(" and ")
                    .append(getCurrentDateAddingOrSubsInterval(0, DateFilterTypeEnum.DAY, DateFilterTypeEnum.DAY));
        } else {
            throwIfDateFilterTypeError(DateFilterTypeEnum.DAY, DateFilterTypeEnum.WEEK, DateFilterTypeEnum.MONTH, DateFilterTypeEnum.YEAR);
        }
        if (filterFieldDTO.getCompareOperatorType().equals(NOT)) {
            if (havePreQuery) {
                query.append(" =false or ff.");
            } else {
                query.append("=false or g.");
            }
            query
                    .append(makeSnakeCase(filterFieldDTO.getField()))
                    .append(" is null");
        }
        query.append(" ) ");
        return query.toString();
    }


    private String queryCustomFieldForBeforeOrAfter(FilterFieldDTO filterFieldDTO) {
        FilterFieldValueDTO filterFieldValueDTO = filterFieldDTO.getValue();
        DateCompareOperatorTypeEnum dateCompareOperatorType = filterFieldValueDTO.getDateCompareOperatorType();

        if (filterFieldValueDTO.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.GT) ||
                filterFieldValueDTO.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.LT) ||
                filterFieldValueDTO.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.EQ)) {

            Long starDate = filterFieldValueDTO.getStarDate();
            boolean starDateTime = filterFieldValueDTO.isStarDateTime();
            try {
                Timestamp startDateTimestamp = new Timestamp(starDate);


                StringBuilder query = new StringBuilder();
                query.append("cast(g.id as varchar) in  (select cfv.")
                        .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                        .append(" ")
                        .append("from ")
                        .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                        .append(" as cfv where cfv.deleted=false and(")
                        .append("cast(cfv.")
                        .append(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE()))
                        .append(" as date) ");
                if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.LT)) {
                    query.append("<");
                } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.GT)) {
                    query.append(">");
                } else {
                    query.append("=");
                }
                query
                        .append(" cast('")
                        .append(startDateTimestamp)
                        .append("' as ");
                if (starDateTime) {
                    query.append("timestamp)");
                } else {
                    query.append("date)");
                }

                if (filterFieldDTO.getCompareOperatorType().equals(NOT)) {
                    query.append("=false ");
                }
                query.append(")");
                return query.toString();
            } catch (Exception e) {
                return "";
            }
        } else {
            throwIfDateFilterTypeError(DateFilterTypeEnum.DAY, DateFilterTypeEnum.WEEK, DateFilterTypeEnum.MONTH, DateFilterTypeEnum.YEAR);
        }
        return "";
    }


    private String queryForDateBeforeOrAfter(FilterFieldDTO
                                                     filterFieldDTO, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap, boolean havePreQuery) {
        FilterFieldValueDTO filterFieldValueDTO = filterFieldDTO.getValue();
        DateCompareOperatorTypeEnum dateCompareOperatorType = filterFieldValueDTO.getDateCompareOperatorType();

        if (filterFieldValueDTO.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.GT) ||
                filterFieldValueDTO.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.LT) ||
                filterFieldValueDTO.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.EQ)) {

            Long starDate = filterFieldValueDTO.getStarDate();
            boolean starDateTime = filterFieldValueDTO.isStarDateTime();
            try {
                Timestamp startDateTimestamp = new Timestamp(starDate);


                StringBuilder query = new StringBuilder();
                if (havePreQuery) {
                    query = preQueryForDateForDirectionTable(valuesKeyEnumStringMap);
                } else {
                    query.append("(g.");
                }
                query
                        .append(makeSnakeCase(filterFieldDTO.getField()));

                if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.LT)) {
                    query.append("<");
                } else if (dateCompareOperatorType.equals(DateCompareOperatorTypeEnum.GT)) {
                    query.append(">");
                } else {
                    query.append("=");
                }

                query
                        .append(" cast('")
                        .append(startDateTimestamp)
                        .append("' as ");
                if (starDateTime) {
                    query.append("timestamp)");
                } else {
                    query.append("date)");
                }
//
                if (filterFieldDTO.getCompareOperatorType().equals(NOT)) {
                    if (havePreQuery) {
                        query.append(" =false or ff.");
                    } else {
                        query.append("=false or g.");
                    }
                    query
                            .append(makeSnakeCase(filterFieldDTO.getField()))
                            .append(" is null");
                }
                query.append(" ) ");
                return query.toString();
//
            } catch (Exception e) {
                return "";
            }
        } else {
            throwIfDateFilterTypeError(DateFilterTypeEnum.DAY, DateFilterTypeEnum.WEEK, DateFilterTypeEnum.MONTH, DateFilterTypeEnum.YEAR);
        }
        return "";
    }


    private String queryCustomFieldForRange(FilterFieldDTO filterFieldDTO) {
        FilterFieldValueDTO filterFieldValueDTO = filterFieldDTO.getValue();

        if (filterFieldValueDTO.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.GT) ||
                filterFieldValueDTO.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.LT) ||
                filterFieldValueDTO.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.EQ)) {

            Long starDate = filterFieldValueDTO.getStarDate();
            boolean starDateTime = filterFieldValueDTO.isStarDateTime();

            Long endDate = filterFieldValueDTO.getEndDate();
            boolean endDateTime = filterFieldValueDTO.isEndDateTime();
            try {
                Timestamp startDateTimestamp = new Timestamp(starDate);
                Timestamp endDateTimestamp = new Timestamp(endDate);


                StringBuilder query = new StringBuilder();
                query.append("cast(g.id as varchar) in  (select cfv.")
                        .append(makeSnakeCase(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_OWNER_ID())))
                        .append(" from ")
                        .append(TableNameConstant.CUSTOM_FIELD_VALUE)
                        .append(" as cfv where cfv.deleted=false and (")
                        .append("cast(cfv.")
                        .append(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE()))
                        .append(" as date) ")
                        .append("> cast('")
                        .append(startDateTimestamp)
                        .append("' as ");
                if (starDateTime) {
                    query.append("timestamp)");
                } else {
                    query.append("date)");
                }
                query
                        .append(" and ")
                        .append("cast(cfv.")
                        .append(CustomFieldValue.GET_ENTITY_FIELDS().get(CustomFieldValue.GET_VALUE()))
                        .append(" as date) ")
                        .append("< cast('")
                        .append(endDateTimestamp)
                        .append("' as ");
                if (endDateTime)
                    query.append("timestamp)");
                else
                    query.append("date)");

                if (filterFieldDTO.getCompareOperatorType().equals(NOT)) {
                    query.append("=false ");
                }
                query.append(")");
                return query.toString();
            } catch (Exception e) {
                return "";
            }
        } else {
            throwIfDateFilterTypeError(DateFilterTypeEnum.DAY, DateFilterTypeEnum.WEEK, DateFilterTypeEnum.MONTH, DateFilterTypeEnum.YEAR);
        }
        return "";
    }


    private String queryForDateRange(FilterFieldDTO
                                             filterFieldDTO, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap, boolean havePreQuery) {

        FilterFieldValueDTO value = filterFieldDTO.getValue();
        StringBuilder query = new StringBuilder();

        if (value.getDateCompareOperatorType().equals(DateCompareOperatorTypeEnum.RA)) {

            Long starDate = value.getStarDate();
            boolean starDateTime = value.isStarDateTime();

            Long endDate = value.getEndDate();
            boolean endDateTime = value.isEndDateTime();
            try {
                Timestamp startDateTimestamp = new Timestamp(starDate);
                Timestamp endDateTimestamp = new Timestamp(endDate);

                if (havePreQuery) {
                    query = preQueryForDateForDirectionTable(valuesKeyEnumStringMap);
                } else {
                    query.append("(g.");
                }
                query
                        .append(makeSnakeCase(filterFieldDTO.getField()))
                        .append("> cast('")
                        .append(startDateTimestamp)
                        .append("' as ");
                if (starDateTime) {
                    query.append("timestamp)");
                } else {
                    query.append("date)");
                }
                query
                        .append(" and g.")
                        .append(makeSnakeCase(filterFieldDTO.getField()));
                if (endDateTime) {
                    query
                            .append("< cast('")
                            .append(endDateTimestamp)
                            .append("' as ");
                    query.append("timestamp)");
                } else {
                    endDateTimestamp = new Timestamp(endDateTimestamp.getTime() + 86_400_000);
                    query
                            .append("< cast('")
                            .append(endDateTimestamp)
                            .append("' as ");
                    query.append("date)");
                }
//
                if (filterFieldDTO.getCompareOperatorType().equals(NOT)) {
                    if (havePreQuery) {
                        query.append(" =false or ff.");
                    } else {
                        query.append("=false or g.");
                    }
                    query
                            .append(makeSnakeCase(filterFieldDTO.getField()))
                            .append(" is null");
                }
                query.append(" ) ");
                return query.toString();
//
            } catch (Exception e) {
                return "";
            }
        } else {
            throwIfDateFilterTypeError(DateFilterTypeEnum.DAY, DateFilterTypeEnum.WEEK, DateFilterTypeEnum.MONTH, DateFilterTypeEnum.YEAR);
        }
        return "";
    }

    //BU METHOD date_trunc('DAY', current_date - interval '1' MONTH) KABILARNI YASAB BERADI
    private String getCurrentDateAddingOrSubsInterval(int interval, DateFilterTypeEnum
            firstDateFilterType, DateFilterTypeEnum secondDateFilterType) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append(" date_trunc('")
                .append(firstDateFilterType.name())
                .append("', current_date");
        if (interval == 0)
            stringBuilder
                    .append(") ");
        else {
            if (interval < 0) {
                stringBuilder
                        .append("- ");
            } else {
                stringBuilder.append("+ ");
            }
            stringBuilder
                    .append("interval '");
            if (secondDateFilterType.equals(DateFilterTypeEnum.WEEK))
                stringBuilder.append((Math.abs(interval) * 7));
            else {
                stringBuilder.append((Math.abs(interval)));
            }
            stringBuilder
                    .append("' ");
            if (secondDateFilterType.equals(DateFilterTypeEnum.WEEK))
                stringBuilder.append(DateFilterTypeEnum.DAY.name());
            else {
                stringBuilder.append(secondDateFilterType.name());
            }
            stringBuilder.append(" )");
        }
        return stringBuilder.toString();
    }

    // >, >=, <, <=, =, !=,   belgilarini ajrarib beradi
    public String identifyComparison(CompareOperatorTypeEnum compareOperatorType) {
        String identify = "";
        switch (compareOperatorType) {
            case GT:
                identify = ">";
                break;
            case GTE:
                identify = ">=";
                break;
            case LT:
                identify = "<";
                break;
            case LTE:
                identify = "<=";
                break;
            case EQ:
                identify = "=";
                break;
            case NOT:
                identify = "<>";
                break;
            case RA:
                identify = "between ";
                break;
        }
        return identify;
    }

    //IS_SET KELGANDA 'IS NOT NULL' IS_NOT_SET KELGANDA 'IS NULL' YOZIBERADI
    public String identify(CompareOperatorTypeEnum operatorTypeEnum) {
        if (operatorTypeEnum.equals(IS_SET))
            return " IS NOT NULL ";
        else if (operatorTypeEnum.equals(IS_NOT_SET))
            return " IS NULL  ";
        return "";
    }

    public static String makeSnakeCase(String str) {

        // Empty String
        StringBuilder result = new StringBuilder();

        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));

        for (int i = 1; i < str.length(); i++) {

            char ch = str.charAt(i);

            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        result.append(" ");
        return result.toString();
    }

    //BU AYNAN DOUBLE NING OXIRIDAGI .0 LARNI OLIB TASHLASH UCHUN. .98 NI EMAS
    public static Object numberValueWithoutLastZero(String value) {
        double parseDouble = Double.parseDouble(value);

        long longValue = (long) parseDouble;
        return longValue == parseDouble ? longValue : parseDouble;
    }

    public void throwIfCompareOperatorTypeError(CustomFieldTypeEnum customFieldTypeEnum, CompareOperatorTypeEnum...
            compareOperatorTypeEnum) {
        throw RestException.restThrow(customFieldTypeEnum.getTitle() + " da faqat quyidagi operatorlardan foydalanish mumkin: " + getCompareTypeErrorMessage(compareOperatorTypeEnum), HttpStatus.BAD_REQUEST);
    }

    private void throwIfCompareOperatorTypeError(DateCompareOperatorTypeEnum... dateCompareOperatorTypeEnums) {
        throw RestException.restThrow(CustomFieldTypeEnum.DATE.getTitle() + " da faqat quyidagi operatorlardan foydalanish mumkin: " + getCompareTypeErrorMessage(dateCompareOperatorTypeEnums), HttpStatus.BAD_REQUEST);
    }

    private void throwIfDateFilterTypeError(DateFilterTypeEnum... dateFilterTypeEnums) {
        throw RestException.restThrow(CustomFieldTypeEnum.DATE.getTitle() + " da faqat quyidagi operatorlardan foydalanish mumkin: " + getCompareTypeErrorMessage(dateFilterTypeEnums), HttpStatus.BAD_REQUEST);
    }

    private String getCompareTypeErrorMessage(CompareOperatorTypeEnum... compareOperatorTypeEnums) {
        return String.join(",", Arrays.toString(compareOperatorTypeEnums));
    }

    private String getCompareTypeErrorMessage(DateCompareOperatorTypeEnum... dateCompareOperatorTypeEnums) {
        return String.join(",", Arrays.toString(dateCompareOperatorTypeEnums));
    }

    private String getCompareTypeErrorMessage(DateFilterTypeEnum... dateFilterTypeEnums) {
        return String.join(",", Arrays.toString(dateFilterTypeEnums));

    }

    //------------------------------------------------------------------------------------------------------------//
    private void checkColumnsValidForFilter
    (Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityFields, Map<String, CustomField> customFieldMap, ViewFilterDTO
            viewFilterDTO) {

        for (FilterFieldDTO filterField : viewFilterDTO.getFilterFields()) {
            if (filterField.isCustomField()) {
                if (customFieldMap.get(filterField.getField()) == null)
                    throw RestException.restThrow("BUNDAY COLUMN YO'Q, FILTER:" + filterField.getField(), HttpStatus.BAD_REQUEST);
            } else {
                Map<EntityColumnMapValuesKeyEnum, String> entityColumnMap = entityFields.get(filterField.getField());
                if (entityColumnMap == null) {
                    throw RestException.restThrow("BUNDAY COLUMN YO'Q, FILTER:" + filterField.getField(), HttpStatus.BAD_REQUEST);
                }
            }
        }
        for (ViewFilterSearchingColumnDTO viewFilterSearchingColumnDTO : viewFilterDTO.getSearchingColumns()) {
            if (viewFilterSearchingColumnDTO.isCustomField()) {
                if (customFieldMap.get(viewFilterSearchingColumnDTO.getColumnName()) == null)
                    throw RestException.restThrow("BUNDAY COLUMN YO'Q, SEARCHING:" + viewFilterSearchingColumnDTO.getColumnName(), HttpStatus.BAD_REQUEST);
            } else {
                Map<EntityColumnMapValuesKeyEnum, String> entityColumnMap = entityFields.get(viewFilterSearchingColumnDTO.getColumnName());
                if (entityColumnMap == null) {
                    throw RestException.restThrow("BUNDAY COLUMN YO'Q, SEARCHING:" + viewFilterSearchingColumnDTO.getColumnName(), HttpStatus.BAD_REQUEST);
                }
            }
        }
    }

    private Map<String, CustomField> mapCustomFieldToHashMap(List<CustomField> customFieldList) {
        Map<String, CustomField> customFieldMap = new HashMap<>();
        for (CustomField customField : customFieldList)
            customFieldMap.put(String.valueOf(customField.getId()), customField);
        return customFieldMap;
    }

    private void checkColumnsValidForSort
            (Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityFields, Map<String, CustomField> customFieldMap, List<ViewSortingDTO> viewSortingDTOList) {

        for (ViewSortingDTO viewSortingDTO : viewSortingDTOList) {
            if (viewSortingDTO.isCustomField()) {
                if (customFieldMap.get(viewSortingDTO.getField()) == null)
                    throw RestException.restThrow("BUNDAY COLUMN YO'Q, SORT:" + viewSortingDTO.getField(), HttpStatus.BAD_REQUEST);
            } else {
                Map<EntityColumnMapValuesKeyEnum, String> entityColumnMap = entityFields.get(viewSortingDTO.getField());
                if (entityColumnMap == null) {
                    throw RestException.restThrow("BUNDAY COLUMN YO'Q, SORT:" + viewSortingDTO.getField(), HttpStatus.BAD_REQUEST);
                }
            }
        }
    }

    private void removeFilterFieldsWhenValueIsNull(ViewFilterDTO viewFilter) {

        int size = viewFilter.getFilterFields().size();
        List<FilterFieldDTO> filterFieldDTOList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            FilterFieldDTO filterFieldDTO = viewFilter.getFilterFields().get(i);
            if (filterFieldDTO.getValue() != null || filterFieldDTO.getCompareOperatorType().equals(IS_SET) || filterFieldDTO.getCompareOperatorType().equals(IS_NOT_SET)) {
                filterFieldDTOList.add(filterFieldDTO);
            }
        }
        viewFilter.setFilterFields(filterFieldDTOList);
    }

    // NOT_FROM_DB LI MAP LARNI CHOPIB BERADI
    public void removeNotFromDb(Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entity_fields) {

        entity_fields.entrySet().removeIf(stringMapEntry -> {
            for (Map.Entry<EntityColumnMapValuesKeyEnum, String> entityColumnMapValuesKeyEnumStringEntry : stringMapEntry.getValue().entrySet()) {
                if (entityColumnMapValuesKeyEnumStringEntry.getKey().equals(NOT_FROM_DB)) {
                    return true;
                }
            }
            return false;
        });
    }
}


