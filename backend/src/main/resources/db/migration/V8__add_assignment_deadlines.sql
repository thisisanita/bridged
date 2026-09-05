--Rename columns

alter table agent
rename column active_chat_count to open_chat_count;

alter table agent
rename column max_active_chats to max_open_chats;

alter table priority
add column assignment_target_minutes integer;

update priority
set assignment_target_minutes =
    case priority
        when  'CRITICAL' then 5
        when 'HIGH' then 10
        when 'NORMAL' then 20
        when 'LOW' then 30
    end;

 alter table priority
 alter column assignment_target_minutes set not null;

 alter table chat_session
 add column assignment_due_at timestamp;

 update chat_session cs
 set assignment_due_at = cs.triage_completed_at + (p.assignment_target_minutes * INTERVAL '1 minute')
 from priority p
 where cs.priority = p.priority_id
 and cs.triage_completed_at is not null
 and cs.assignment_due_at is null;
