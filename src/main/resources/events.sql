CREATE EVENT IF NOT EXISTS delete_expired_diaries
    ON SCHEDULE EVERY 1 DAY
        STARTS CURRENT_DATE
    DO
    DELETE FROM diary WHERE expires_At < CURRENT_DATE;