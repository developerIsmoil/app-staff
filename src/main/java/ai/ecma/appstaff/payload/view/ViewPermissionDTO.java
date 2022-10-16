package ai.ecma.appstaff.payload.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ViewPermissionDTO {

//>>>>>>>>>>VIEW TASHQARISIDA>>>>>>>>>

    //YANGI VIEW YARATA OLADIMI
    private Boolean canCreateView;

//<<<<<<<<<<VIEW TASHQARISIDA<<<<<<<<<


//>>>>>>>>>>>>BITTA VIEW NI ICHI>>>>>>>>

    //VIEW NI NOMINI O'ZGARTIRA OLADIMI
    private Boolean canEditViewName;

    //VIEW NI O'ZGARTIRA OLADIMI
    private Boolean canDeleteView;

//>>>>>>>>>>>>BITTA VIEW NI ICHI>>>>>>>>


//>>>>>>>>>>>>>VIEW NI 3 TA NUQTA BOSIB SHARE NI BOSGANDA>>>>>>>>>>>

    //VIEW NI PUBLIC GA CHIQARA OLADIMI
    private Boolean canChangePublicly;

    //VIEW DAGI USER LARNI NAZORAT QILA OLADIMI
    private Boolean canManageUser;

    //VIEW NI KO'CHAGA LINK ORQALI SHARE QILISH
    private Boolean canShare;

    //VIEW DAN NUSHA OLISH
    private Boolean canDuplicate;

    //TANLANGANLARGA QO'SHISH
    private Boolean canFavourite;

    //BARCHA SORT FILTER LARNI O'CHIRIB TASHLAYDI
    private Boolean canResetViewDefault;

    //AUTOSAVE NI YOQA OLADIMI
    private Boolean canAutoSave;

    //VIEW NI EXPORT QILA OLADIMI
    private Boolean canExportView;

//>>>>>>>>>>>>>VIEW NI 3 TA NUQTA BOSIB SHARE NI BOSGANDA>>>>>>>>>>>


    //MALUMOT KIRITISH( VIEW ONLY DA FALSE )
    private Boolean canEnterData;


    //BULAR EDIT YOKI FULL HUQUQI BOR LAR UCHUN
    private Boolean canManageCustomField;


    //BULAR HAMMADA BOR
    private Boolean canManageColumn;//
    private Boolean canSearch;
    private Boolean canFilter;
    private Boolean canUpdateView;//SHU VIEW DA FILTER SORCH SORT LAR DB GA SAQLANADIMI?


    private Boolean canShowColumn;

    public ViewPermissionDTO(Boolean canEditViewName, Boolean canDeleteView, Boolean canChangePublicly, Boolean canManageUser, Boolean canShare, Boolean canResetViewDefault, Boolean canAutoSave, Boolean canExportView) {
        this.canEditViewName = canEditViewName;
        this.canDeleteView = canDeleteView;
        this.canChangePublicly = canChangePublicly;
        this.canManageUser = canManageUser;
        this.canShare = canShare;
        this.canResetViewDefault = canResetViewDefault;
        this.canAutoSave = canAutoSave;
        this.canExportView = canExportView;
    }

/**
     * CLICK-UP DAGI PERMISSIONLAR PASTDA,
     * AGAR QOSHIMCHA PERMISIIONLAR KK BOLSA SHULARDAN TANLAB OLGAN YAXSHIROQ
     */

    /**
     *
     * : true
     * add_checklists: true
     * add_dependencies: true
     * add_email_account: true
     * add_followers: true
     * add_self_follower: true
     * add_status: true
     * add_subtasks: true
     * add_tags: true
     * archive: true
     * billing: false
     * can_add_automation: true
     * can_add_team_guests: true
     * can_add_team_members: false
     * can_be_added_to_user_groups: true
     * can_convert_item: true
     * can_create_goals: true
     * can_create_lists: true
     * can_create_milestone: true
     * can_create_portfolios: true
     * can_create_projects: true
     * can_create_relationships: true
     * can_create_spaces: true
     * can_create_tasks: true
     * can_create_workload: true
     * can_delete_checklist_item: true
     * can_delete_comments: false
     * can_delete_no_access: false
     * can_edit_description: false
     * can_edit_integrations: false
     * can_edit_list_statuses: false
     * can_edit_privacy: 1
     * can_edit_project_settings: 1
     * can_edit_space_settings: 1
     * can_edit_team: false
     * can_edit_team_members: false
     * can_edit_team_owner: false
     * can_edit_trial: false
     * can_edit_user_groups: false
     * can_edit_view_protection: true
     * can_enable_sso: false
     * can_export_tasks: true
     * can_gdpr_export: false
     * can_import: true
     * can_make_tasks_public: true
     * can_pin_fields: true
     * can_read: true
     * can_recover_inaccessible_spaces: false
     * can_resolve_checklist_item_if_assigned: true
     * can_see_team_members: true
     * can_see_workload: true
     * can_use_git: true
     * can_view_reporting: true
     * change_assignee: 2
     * change_clickapps: true
     * change_description: true
     * change_due_date: true
     * change_incoming_address: true
     * change_points_estimate: true
     * change_priority: true
     * change_recurring: true
     * change_status: true
     * change_time_estimate: true
     * change_title: true
     * comment: true
     * create_automation: true
     * create_dashboards: true
     * create_view: true
     * custom_roles: false
     * delete: true
     * delete_view: true
     * display_name: "edit"
     * duplicate: true
     * edit_attachments: true
     * edit_checklists: true
     * edit_list_details: true
     * edit_view: true
     * like_comments: true
     * make_views_public: true
     * manage_custom_fields: true
     * manage_custom_items: true
     * manage_statuses: true
     * manage_tags: true
     * merge: true
     * move_task: true
     * name: "member"
     * oauth_apps: false
     * permission_level: 5
     * profile: true
     * public_spaces_visible: true
     * remove_attachments: true
     * remove_dependencies: true
     * remove_followers: true
     * remove_self_follower: true
     * remove_status: true
     * remove_tags: true
     * send_email: true
     * set_custom_field_values: true
     * share: true
     * team_permissions: false
     * team_role: 3
     * template: true
     * track_time: true
     */



}
