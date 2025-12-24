-- Compatibility migration: payload columns may have been created as jsonb or oid (LOB) in older attempts.
DO $$
BEGIN
    -- inbox.payload
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='inbox' AND column_name='payload'
          AND (udt_name IN ('jsonb','json') OR data_type='USER-DEFINED')
    ) THEN
        ALTER TABLE inbox ALTER COLUMN payload TYPE text USING payload::text;
    ELSIF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='inbox' AND column_name='payload'
          AND data_type='oid'
    ) THEN
        ALTER TABLE inbox ALTER COLUMN payload TYPE text USING lo_get(payload);
    END IF;

    -- outbox.payload
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='outbox' AND column_name='payload'
          AND (udt_name IN ('jsonb','json') OR data_type='USER-DEFINED')
    ) THEN
        ALTER TABLE outbox ALTER COLUMN payload TYPE text USING payload::text;
    ELSIF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='outbox' AND column_name='payload'
          AND data_type='oid'
    ) THEN
        ALTER TABLE outbox ALTER COLUMN payload TYPE text USING lo_get(payload);
    END IF;
END $$;
