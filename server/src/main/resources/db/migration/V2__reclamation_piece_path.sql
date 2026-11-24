SET @piece_chemin_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'reclamations'
      AND column_name = 'piece_chemin'
);

SET @migration_sql = IF(
    @piece_chemin_exists = 0,
    'ALTER TABLE reclamations ADD COLUMN piece_chemin VARCHAR(255) NULL',
    'SELECT 1'
);

PREPARE migration_statement FROM @migration_sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;
