-- Reset Flyway history for V8
-- This script can be run manually if Flyway encounters checksum issues
DELETE FROM flyway_schema_history
WHERE
    version = 8;