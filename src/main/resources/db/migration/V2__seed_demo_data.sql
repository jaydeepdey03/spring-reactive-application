INSERT INTO users (
        id,
        email,
        display_name,
        auth_provider,
        provider_user_id,
        created_at,
        updated_at,
        role,
        version
    )
VALUES (
        '11111111-1111-1111-1111-111111111111',
        'demo@example.com',
        'Demo User',
        'GOOGLE',
        'google-demo-user',
        TIMESTAMPTZ '2026-07-01 09:00:00+00',
        TIMESTAMPTZ '2026-07-01 09:00:00+00',
        'USER',
        0
    ) ON CONFLICT DO NOTHING;
INSERT INTO protein_entries (
        id,
        user_id,
        food_name,
        grams_consumed,
        protein_grams,
        entry_date,
        created_at,
        version
    )
VALUES (
        '22222222-2222-2222-2222-222222222221',
        '11111111-1111-1111-1111-111111111111',
        'Greek yogurt bowl',
        250.00,
        18.50,
        DATE '2026-07-01',
        TIMESTAMPTZ '2026-07-01 09:15:00+00',
        0
    ) ON CONFLICT DO NOTHING;
INSERT INTO expense_entries (
        id,
        user_id,
        description,
        category,
        amount,
        currency,
        entry_date,
        created_at,
        version
    )
VALUES (
        '33333333-3333-3333-3333-333333333331',
        '11111111-1111-1111-1111-111111111111',
        'Groceries',
        'Food',
        420.00,
        'INR',
        DATE '2026-07-01',
        TIMESTAMPTZ '2026-07-01 10:00:00+00',
        0
    ) ON CONFLICT DO NOTHING;
INSERT INTO daily_protein_summary (
        id,
        user_id,
        summary_date,
        total_protein_grams,
        entry_count,
        updated_at
    )
VALUES (
        '44444444-4444-4444-4444-444444444441',
        '11111111-1111-1111-1111-111111111111',
        DATE '2026-07-01',
        18.50,
        1,
        TIMESTAMPTZ '2026-07-01 09:15:00+00'
    ) ON CONFLICT DO NOTHING;
INSERT INTO daily_expense_summary (
        id,
        user_id,
        summary_date,
        total_amount,
        entry_count,
        updated_at
    )
VALUES (
        '55555555-5555-5555-5555-555555555551',
        '11111111-1111-1111-1111-111111111111',
        DATE '2026-07-01',
        420.00,
        1,
        TIMESTAMPTZ '2026-07-01 10:00:00+00'
    ) ON CONFLICT DO NOTHING;