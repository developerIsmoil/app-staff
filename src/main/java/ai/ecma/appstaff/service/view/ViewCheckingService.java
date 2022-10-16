package ai.ecma.appstaff.service.view;

import ai.ecma.appstaff.entity.customField.CustomField;
import ai.ecma.appstaff.enums.*;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.view.ViewDTO;
import ai.ecma.appstaff.payload.view.*;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.ResponseMessage;
import ai.ecma.appstaff.utils.TableMapList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ViewCheckingService {
    private final QueryService queryService;


    public void checkingViewFilterAndSearchingAndSorting(ViewDTO viewDTO, Map<String, CustomField> customFieldMap, String tableName) {

        Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityMap = TableMapList.ENTITY_FIELDS.get(tableName);

        if (entityMap == null || entityMap.isEmpty())
            throw RestException.restThrow(ResponseMessage.ERROR_NOT_FOUND_FIELD_MAP);

        checkingViewFilter(entityMap, customFieldMap, viewDTO.getViewFilter());

        checkingViewSorting(entityMap, customFieldMap, viewDTO);

    }

    private void checkingViewSorting(Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityMap, Map<String, CustomField> customFieldMap, ViewDTO viewDTO) {

        List<ViewSortingDTO> sorting = viewDTO.getSorting();
        if (sorting == null || sorting.isEmpty())
            return;

        checkingHiddenColumns(viewDTO);

        for (ViewSortingDTO viewSorting : sorting) {
            checkingViewSortingColumn(viewSorting, entityMap, customFieldMap);
        }


    }

    /**
     * HEDDEN =TRUE BOLGAN COLUMNLARNI SORTING LISTDAN CHOPISH
     */
    private void checkingHiddenColumns(ViewDTO viewDTO) {
        List<ViewColumnDTO> viewColumnList = viewDTO.getColumns();
        List<ViewSortingDTO> sorting = viewDTO.getSorting();

        List<ViewSortingDTO> result = new ArrayList<>();

        for (ViewSortingDTO viewSortingDTO : sorting) {
            for (ViewColumnDTO viewColumnDTO : viewColumnList) {
                if (viewColumnDTO.getId().equals(viewSortingDTO.getField())) {
                    if (!viewColumnDTO.isHidden()) {
                        result.add(viewSortingDTO);
                    }
                }
            }
            viewDTO.setSorting(result);
        }
    }

    private void checkingViewSortingColumn(ViewSortingDTO viewSorting, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityMap, Map<String, CustomField> customFieldMap) {
        if (viewSorting == null)
            return;


        if (viewSorting.isCustomField()) {
            checkingViewSortingCustomFieldColumn(viewSorting, customFieldMap);
        } else {
            checkingViewSortingOwnColumn(viewSorting, entityMap);
        }

    }

    private void checkingViewSortingOwnColumn(ViewSortingDTO viewSorting, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityMap) {
        String field = viewSorting.getField();

        Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap = entityMap.get(field);

        if (valuesKeyEnumStringMap == null || valuesKeyEnumStringMap.isEmpty())
            throw RestException.restThrow(ResponseMessage.ERROR_DOES_NOT_SORT_THIS_COLUMN + viewSorting);

        if ((RestConstants.NO).equals(valuesKeyEnumStringMap.get(EntityColumnMapValuesKeyEnum.SORTABLE)))
            throw RestException.restThrow(ResponseMessage.ERROR_DOES_NOT_SORT_THIS_COLUMN + viewSorting);

    }

    private void checkingViewSortingCustomFieldColumn(@NotNull ViewSortingDTO viewSorting, Map<String, CustomField> customFieldMap) {
        String field = viewSorting.getField();

        CustomField customField = customFieldMap.get(field);

        if (customField == null)
            throw RestException.restThrow(ResponseMessage.ERROR_CUSTOM_FIELD_NOT_FOUND + viewSorting);

        CustomFieldTypeEnum type = customField.getType();

        if (CustomFieldTypeEnum.ENUM_DROPDOWN.equals(type) || CustomFieldTypeEnum.SPECIAL_LABEL.equals(type) ||
                CustomFieldTypeEnum.DROPDOWN.equals(type) || CustomFieldTypeEnum.RATING.equals(type) ||
                CustomFieldTypeEnum.LABELS.equals(type) ||
                CustomFieldTypeEnum.SHORT_TEXT.equals(type) || CustomFieldTypeEnum.LONG_TEXT.equals(type) ||
                CustomFieldTypeEnum.NUMBER.equals(type) || CustomFieldTypeEnum.MONEY.equals(type) ||
                CustomFieldTypeEnum.PHONE.equals(type) || CustomFieldTypeEnum.EMAIL.equals(type) ||
                CustomFieldTypeEnum.CHECKBOX.equals(type) || CustomFieldTypeEnum.FILES.equals(type)) {

            checkingViewSortingDirectionorElseThrow(viewSorting.getDirection());

            return;

        } else {
            throw RestException.restThrow(ResponseMessage.ERROR_DOES_NOT_SORT_THIS_CUSTOM_FIELD_COLUMN + viewSorting);

        }
    }

    private void checkingViewSortingDirectionorElseThrow(Integer direction) {
//        if ( !Objects.equals(1, direction) &&!Objects.equals(-1, direction))
//            throw RestException.restThrow(ResponseMessage.VIEW)
    }

    private void checkingViewFilter(Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityMap, Map<String, CustomField> customFieldMap, ViewFilterDTO viewFilter) {

        checkingSearchingColumns(viewFilter, customFieldMap, entityMap);

        checkingViewFilterColumns(viewFilter, customFieldMap, entityMap);

    }

    private void checkingViewFilterColumns(ViewFilterDTO viewFilter, Map<String, CustomField> customFieldMap, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityMap) {
        List<FilterFieldDTO> filterFieldDTOList = viewFilter.getFilterFields();

        if (filterFieldDTOList == null || filterFieldDTOList.isEmpty())
            return;

        for (FilterFieldDTO filterFieldDTO : filterFieldDTOList) {
            checkingViewFilterColumn(filterFieldDTO, entityMap, customFieldMap);
        }
    }

    private void checkingViewFilterColumn(FilterFieldDTO filterFieldDTO, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityMap, Map<String, CustomField> customFieldMap) {

        if (filterFieldDTO.isCustomField()) {
            checkingViewFilterForCustomFieldColumn(filterFieldDTO, customFieldMap);
        } else {
            checkingViewFilterForOwnColumn(filterFieldDTO, entityMap);
        }
    }

    private void checkingViewFilterForCustomFieldColumn(FilterFieldDTO filterFieldDTO, Map<String, CustomField> customFieldMap) {
        CustomField customField = customFieldMap.get(filterFieldDTO.getField());
        if (customField == null)
            throw RestException.restThrow(ResponseMessage.ERROR_DOES_NOT_FILTER_THIS_COLUMN + filterFieldDTO);

        checkingViewFilterValueByType(filterFieldDTO);
    }

    private void checkingViewFilterValueByType(FilterFieldDTO filterFieldDTO) {
        CustomFieldTypeEnum type = filterFieldDTO.getFieldType();

        switch (type) {
            case SHORT_TEXT:
            case LONG_TEXT:
            case EMAIL:
            case PHONE:
            case CALL_TYPE:
            case CALL_STATUS:
            case PRIORITY:
            case SPECIAL_LABEL:
                checkingViewFilterColumnTypeShortText(filterFieldDTO);
                break;

            case DATE:
                checkingViewFilterColumnTypeDate(filterFieldDTO);
                break;

            case DROPDOWN:
            case ENUM_DROPDOWN:
            case LABELS:
                checkingViewFilterColumnTypeDropDownAndLabels(filterFieldDTO);
                break;
            case NUMBER:
            case MONEY:
            case RATING:
                checkingViewFilterColumnTypeNumberAndMoneyAndRating(filterFieldDTO);
                break;

            case CHECKBOX:
            case FILES:
                checkingViewFilterColumnTypeCheckboxAndFiles(filterFieldDTO);
                break;

            default:
                throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_UNKNOWN_REQUIRED);
        }


    }

    private void checkingViewFilterColumnTypeDropDownAndLabels(FilterFieldDTO filterFieldDTO) {

        CompareOperatorTypeEnum compareOperatorType = filterFieldDTO.getCompareOperatorType();

        if (compareOperatorType.equals(CompareOperatorTypeEnum.IS_SET) ||
                compareOperatorType.equals(CompareOperatorTypeEnum.IS_NOT_SET))
            return;

        else if (compareOperatorType.equals(CompareOperatorTypeEnum.EQ)
                || compareOperatorType.equals(CompareOperatorTypeEnum.NOT)
                || compareOperatorType.equals(CompareOperatorTypeEnum.ANY)
                || compareOperatorType.equals(CompareOperatorTypeEnum.ALL)
                || compareOperatorType.equals(CompareOperatorTypeEnum.NOT_ANY)
                || compareOperatorType.equals(CompareOperatorTypeEnum.NOT_ALL)
        ) {

            FilterFieldValueDTO filterFieldValue = filterFieldDTO.getValue();

            if (filterFieldValue == null)
                throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE + filterFieldDTO);

            if (Objects.isNull(filterFieldValue.getOptionsSelected()) || filterFieldValue.getOptionsSelected().length < 1)
                throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE_OPTION_SELECTED_VALUE_REQUIRED + filterFieldDTO.getValue());

            return;

        } else {
            queryService.throwIfCompareOperatorTypeError(filterFieldDTO.getFieldType(), CompareOperatorTypeEnum.EQ, CompareOperatorTypeEnum.NOT, CompareOperatorTypeEnum.IS_NOT_SET, CompareOperatorTypeEnum.IS_SET);
        }

    }

    private void checkingViewFilterColumnTypeCheckboxAndFiles(FilterFieldDTO filterFieldDTO) {

        CompareOperatorTypeEnum compareOperatorType = filterFieldDTO.getCompareOperatorType();

        if (compareOperatorType.equals(CompareOperatorTypeEnum.IS_SET) || compareOperatorType.equals(CompareOperatorTypeEnum.IS_NOT_SET))
            return;

        queryService.throwIfCompareOperatorTypeError(filterFieldDTO.getFieldType(), CompareOperatorTypeEnum.IS_NOT_SET, CompareOperatorTypeEnum.IS_SET);


    }

    private void checkingViewFilterColumnTypeNumberAndMoneyAndRating(FilterFieldDTO filterFieldDTO) {

        CompareOperatorTypeEnum compareOperatorType = filterFieldDTO.getCompareOperatorType();

        if (compareOperatorType.equals(CompareOperatorTypeEnum.IS_SET) ||
                compareOperatorType.equals(CompareOperatorTypeEnum.IS_NOT_SET))
            return;

        else if (compareOperatorType.equals(CompareOperatorTypeEnum.RA)) {
            FilterFieldValueDTO filterFieldValue = filterFieldDTO.getValue();

            if (filterFieldValue == null)
                throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE + filterFieldDTO);

            if (filterFieldValue.getMinValue() == null || filterFieldValue.getMinValue().isBlank())
                throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE_MIN_VALUE_REQUIRED + filterFieldDTO.getValue());

            if (filterFieldValue.getMaxValue() == null || filterFieldValue.getMaxValue().isBlank())
                throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE_MAX_VALUE_REQUIRED + filterFieldDTO.getValue());

            return;

        } else if (compareOperatorType.equals(CompareOperatorTypeEnum.EQ)
                || compareOperatorType.equals(CompareOperatorTypeEnum.NOT)
                || compareOperatorType.equals(CompareOperatorTypeEnum.GT)
                || compareOperatorType.equals(CompareOperatorTypeEnum.GTE)
                || compareOperatorType.equals(CompareOperatorTypeEnum.LT)
                || compareOperatorType.equals(CompareOperatorTypeEnum.LTE)
        ) {

            FilterFieldValueDTO filterFieldValue = filterFieldDTO.getValue();

            if (filterFieldValue == null)
                throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE + filterFieldDTO);

            if (filterFieldValue.getMinValue() == null || filterFieldValue.getMinValue().isBlank())
                throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE_MIN_VALUE_REQUIRED + filterFieldDTO.getValue());


            return;

        } else {
            queryService.throwIfCompareOperatorTypeError(filterFieldDTO.getFieldType(), CompareOperatorTypeEnum.EQ, CompareOperatorTypeEnum.GT, CompareOperatorTypeEnum.GTE, CompareOperatorTypeEnum.LT, CompareOperatorTypeEnum.LTE, CompareOperatorTypeEnum.RA, CompareOperatorTypeEnum.NOT, CompareOperatorTypeEnum.IS_NOT_SET, CompareOperatorTypeEnum.IS_SET);
        }
    }

    private void checkingViewFilterColumnTypeDate(FilterFieldDTO filterFieldDTO) {

        CompareOperatorTypeEnum compareOperatorType = filterFieldDTO.getCompareOperatorType();

        if (compareOperatorType.equals(CompareOperatorTypeEnum.IS_SET) ||
                compareOperatorType.equals(CompareOperatorTypeEnum.IS_NOT_SET))
            return;

        if (compareOperatorType.equals(CompareOperatorTypeEnum.EQ) || compareOperatorType.equals(CompareOperatorTypeEnum.NOT)) {

            FilterFieldValueDTO filterFieldValue = filterFieldDTO.getValue();

            if (filterFieldValue == null)
                throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE + filterFieldDTO);

            checkingViewFilterValueTypeDate(filterFieldDTO.getCompareOperatorType(), filterFieldValue);

        } else {
            queryService.throwIfCompareOperatorTypeError(filterFieldDTO.getFieldType(), CompareOperatorTypeEnum.EQ, CompareOperatorTypeEnum.NOT, CompareOperatorTypeEnum.IS_NOT_SET, CompareOperatorTypeEnum.IS_SET);
        }
    }

    private void checkingViewFilterValueTypeDate(CompareOperatorTypeEnum compareOperatorType, FilterFieldValueDTO filterFieldValue) {

        DateCompareOperatorTypeEnum dateCompareOperatorType = filterFieldValue.getDateCompareOperatorType();
        if (dateCompareOperatorType == null)
            throw RestException.restThrow(ResponseMessage.ERROR_DATE_COMPARE_OPERATOR_TYPE_ENUM_REQUIRED);

        switch (dateCompareOperatorType) {
            case TODAY:
            case YESTERDAY:
            case TOMORROW:
                return;
            case GT:
            case EQ:
            case LT:
                checkDateCompareOperatorTypeEqualsOrGreaterThanOrLastThan(filterFieldValue);
                break;
            case RA:
                checkDateCompareOperatorTypeRange(filterFieldValue);
                break;
            case LAST:
            case NEXT:
                checkDateCompareOperatorTypeLastOrNext(filterFieldValue);
                break;
            case THIS:
                checkDateCompareOperatorTypeThis(filterFieldValue);
                break;
        }


    }

    private void checkDateCompareOperatorTypeRange(FilterFieldValueDTO filterFieldValue) {

        if (filterFieldValue.getStarDate() == null)
            throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE_START_DATE_REQUIRED);

        if (filterFieldValue.getEndDate() == null)
            throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE_END_DATE_REQUIRED);

    }

    private void checkDateCompareOperatorTypeThis(FilterFieldValueDTO filterFieldValue) {

        DateFilterTypeEnum dateFilterType = filterFieldValue.getDateFilterType();
        if (dateFilterType == null)
            throw RestException.restThrow(ResponseMessage.ERROR_DATE_FILTER_TYPE_ENUM_REQUIRED);

    }

    private void checkDateCompareOperatorTypeLastOrNext(FilterFieldValueDTO filterFieldValue) {

        DateFilterTypeEnum dateFilterType = filterFieldValue.getDateFilterType();
        if (dateFilterType == null)
            throw RestException.restThrow(ResponseMessage.ERROR_DATE_FILTER_TYPE_ENUM_REQUIRED);

        if (filterFieldValue.getDateXValue() == null)
            throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE_DATE_X_VALUE_REQUIRED);

    }

    private void checkDateCompareOperatorTypeEqualsOrGreaterThanOrLastThan(FilterFieldValueDTO filterFieldValue) {
        if (filterFieldValue.getStarDate() == null)
            throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE_START_DATE_REQUIRED);
    }

    private void checkingViewFilterColumnTypeShortText(FilterFieldDTO filterFieldDTO) {
        CompareOperatorTypeEnum compareOperatorType = filterFieldDTO.getCompareOperatorType();

        if (compareOperatorType.equals(CompareOperatorTypeEnum.IS_SET) ||
                compareOperatorType.equals(CompareOperatorTypeEnum.IS_NOT_SET))
            return;

        if (compareOperatorType.equals(CompareOperatorTypeEnum.EQ) || compareOperatorType.equals(CompareOperatorTypeEnum.NOT)) {

            FilterFieldValueDTO filterFieldValue = filterFieldDTO.getValue();

            if (filterFieldValue == null)
                throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE + filterFieldDTO);

            if (filterFieldValue.getSearchingValue() == null || filterFieldValue.getSearchingValue().isBlank())
                throw RestException.restThrow(ResponseMessage.ERROR_VIEW_FILTER_VALUE_REQUIRED + filterFieldDTO.getValue());

            return;

        } else {
            queryService.throwIfCompareOperatorTypeError(filterFieldDTO.getFieldType(), CompareOperatorTypeEnum.EQ, CompareOperatorTypeEnum.NOT, CompareOperatorTypeEnum.IS_NOT_SET, CompareOperatorTypeEnum.IS_SET);
        }
    }


    private void checkingViewFilterForOwnColumn(FilterFieldDTO filterFieldDTO, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityMap) {
        Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap = entityMap.get(filterFieldDTO.getField());

        if (valuesKeyEnumStringMap == null)
            throw RestException.restThrow(ResponseMessage.ERROR_DOES_NOT_FILTER_THIS_COLUMN + filterFieldDTO);

        if (RestConstants.NO.equals(valuesKeyEnumStringMap.get(EntityColumnMapValuesKeyEnum.FILTERABLE)))
            throw RestException.restThrow(ResponseMessage.ERROR_DOES_NOT_FILTER_THIS_COLUMN + filterFieldDTO);

        checkingViewFilterValueByType(filterFieldDTO);
    }

    private void checkingSearchingColumns(ViewFilterDTO viewFilter, Map<String, CustomField> customFieldMap, Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityMap) {

        String search = viewFilter.getSearch();
        if (search == null || search.isBlank())
            viewFilter.setSearchingColumns(new HashSet<>());

        Set<ViewFilterSearchingColumnDTO> searchingColumns = viewFilter.getSearchingColumns();
        if (searchingColumns == null || searchingColumns.isEmpty())
            viewFilter.setSearchingColumns(new HashSet<>());

        for (ViewFilterSearchingColumnDTO searchingColumn : searchingColumns) {
            if (searchingColumn.isCustomField()) {
                checkingSearchingColumnForCustomFields(customFieldMap, searchingColumn);
            } else {
                checkingSearchingColumnForOwnColumns(entityMap, searchingColumn);
            }
        }


    }

    private void checkingSearchingColumnForOwnColumns(Map<String, Map<EntityColumnMapValuesKeyEnum, String>> entityMap, ViewFilterSearchingColumnDTO searchingColumn) {

        String columnName = searchingColumn.getColumnName();
        Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap = entityMap.get(columnName);

        if (valuesKeyEnumStringMap == null || valuesKeyEnumStringMap.isEmpty())
            throw RestException.restThrow(ResponseMessage.ERROR_DOES_NOT_SEARCH_THIS_COLUMN + searchingColumn);

        if (RestConstants.NO.equals(valuesKeyEnumStringMap.get(EntityColumnMapValuesKeyEnum.SEARCHABLE)))
            throw RestException.restThrow(ResponseMessage.ERROR_DOES_NOT_SEARCH_THIS_COLUMN + searchingColumn);

        if (Objects.equals((RestConstants.YES), valuesKeyEnumStringMap.get(EntityColumnMapValuesKeyEnum.NOT_FROM_DB)))
            return;

        checkingSearchingColumnForOwnColumnOrElseThrow(searchingColumn, valuesKeyEnumStringMap);

    }

    private void checkingSearchingColumnForOwnColumnOrElseThrow(ViewFilterSearchingColumnDTO searchingColumn, Map<EntityColumnMapValuesKeyEnum, String> valuesKeyEnumStringMap) {

        String columnType = valuesKeyEnumStringMap.get(EntityColumnMapValuesKeyEnum.TYPE);

        if (CustomFieldTypeEnum.SHORT_TEXT.name().equals(columnType) ||
                CustomFieldTypeEnum.LONG_TEXT.name().equals(columnType) ||
                CustomFieldTypeEnum.PHONE.name().equals(columnType) ||
                CustomFieldTypeEnum.EMAIL.name().equals(columnType) ||
                CustomFieldTypeEnum.TREE.name().equals(columnType) ||
                CustomFieldTypeEnum.LABELS.name().equals(columnType) ||
                CustomFieldTypeEnum.DROPDOWN.name().equals(columnType) ||
                CustomFieldTypeEnum.ENUM_DROPDOWN.name().equals(columnType) ||
                CustomFieldTypeEnum.SPECIAL_LABEL.name().equals(columnType)) {

            return;

        } else {

            throw RestException.restThrow(ResponseMessage.ERROR_DOES_NOT_SEARCH_THIS_COLUMN + searchingColumn);

        }

    }

    private void checkingSearchingColumnForCustomFields(Map<String, CustomField> customFieldMap, ViewFilterSearchingColumnDTO searchingColumn) {

        CustomField customField = customFieldMap.get(searchingColumn.getColumnName());

        if (customField == null)
            throw RestException.restThrow(ResponseMessage.ERROR_DOES_NOT_SEARCH_THIS_COLUMN + searchingColumn);

        CustomFieldTypeEnum type = customField.getType();

        if (CustomFieldTypeEnum.SHORT_TEXT.equals(type) ||
                CustomFieldTypeEnum.LONG_TEXT.equals(type) ||
                CustomFieldTypeEnum.ENUM_DROPDOWN.equals(type) ||
                CustomFieldTypeEnum.DROPDOWN.equals(type) ||
                CustomFieldTypeEnum.LABELS.equals(type) ||
                CustomFieldTypeEnum.SPECIAL_LABEL.equals(type) ||
                CustomFieldTypeEnum.EMAIL.equals(type) ||
                CustomFieldTypeEnum.PHONE.equals(type)) {
            return;
        } else {
            throw RestException.restThrow(ResponseMessage.ERROR_DOES_NOT_SEARCH_THIS_COLUMN + searchingColumn);
        }
    }

}
