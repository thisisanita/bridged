ALTER TABLE chat_message
    ADD COLUMN client_message_id UUID;

ALTER TABLE chat_message
    ADD CONSTRAINT uq_chat_message_sender_client_message
        UNIQUE (chat_id, sender_user_id, client_message_id);