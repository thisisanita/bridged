INSERT INTO skill (skill_name, description, priority_id)
VALUES
(
    'ACCOUNT_ENQUIRY',
    'General enquiries about customer accounts and account services.',
    (SELECT priority_id FROM priority WHERE priority = 'NORMAL')
),
(
    'LOAN_ENQUIRY',
    'Enquiries about loans, applications, repayments, and loan services.',
    (SELECT priority_id FROM priority WHERE priority = 'NORMAL')
),
(
    'FRAUDULENT_TRANSACTION',
    'Reports or enquiries about suspected fraudulent account transactions.',
    (SELECT priority_id FROM priority WHERE priority = 'CRITICAL')
),
(
    'LOST_STOLEN_CARD',
    'Reports of lost or stolen credit or debit cards requiring assistance.',
    (SELECT priority_id FROM priority WHERE priority = 'HIGH')
),
(
    'CARD_FEE_WAIVER',
    'Requests for credit or debit card fee waivers.',
    (SELECT priority_id FROM priority WHERE priority = 'LOW')
),
(
    'PAYMENT_ISSUE',
    'Issues involving failed, declined, or unexpected payments.',
    (SELECT priority_id FROM priority WHERE priority = 'HIGH')
),
(
    'ACCOUNT_ACCESS',
    'Issues accessing an account, including locked accounts or login difficulties.',
    (SELECT priority_id FROM priority WHERE priority = 'HIGH')
);