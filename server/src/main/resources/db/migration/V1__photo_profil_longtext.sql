SET @photo_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'utilisateurs'
      AND column_name = 'photo_profil'
);

SET @photo_column_type = (
    SELECT DATA_TYPE
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'utilisateurs'
      AND column_name = 'photo_profil'
);

SET @migration_sql = IF(
    @photo_column_exists = 0,
    'ALTER TABLE utilisateurs ADD COLUMN photo_profil LONGTEXT NULL',
    IF(
        @photo_column_type <> 'longtext',
        'ALTER TABLE utilisateurs MODIFY COLUMN photo_profil LONGTEXT NULL',
        'SELECT 1'
    )
);

PREPARE migration_statement FROM @migration_sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;
