-- DROP INDEX if exists uk_user_view_user_id_and_view_id; create unique index if not exists uk_user_view_user_id_and_view_id on user_view (user_id, view_id) where (deleted is null or deleted = false);

