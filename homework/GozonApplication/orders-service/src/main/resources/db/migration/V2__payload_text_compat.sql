-- Compatibility migration: outbox.payload may have been created as jsonb in older attempts.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='outbox' AND column_name='payload'
          AND (udt_name IN ('jsonb','json') OR data_type='USER-DEFINED')
    ) THEN
        ALTER TABLE outbox ALTER COLUMN payload TYPE text USING payload::text;
    END IF;
END $$;
