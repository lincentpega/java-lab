CREATE OR REPLACE FUNCTION check_database_exists(
    p_database_name VARCHAR(255)
) RETURNS BOOLEAN AS
$$
DECLARE
    v_database_exists BOOLEAN;
BEGIN
    SELECT EXISTS (SELECT 1
                   FROM pg_database
                   WHERE datname = p_database_name)
    INTO v_database_exists;

    RETURN v_database_exists;
END;
$$
    LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE create_user_table()
    LANGUAGE plpgsql
AS
$$
BEGIN
    CREATE TABLE IF NOT EXISTS user_table
    (
        id   BIGINT PRIMARY KEY,
        name VARCHAR(255),
        age  BIGINT
    );
END;
$$;

CALL create_user_table();

CREATE OR REPLACE FUNCTION insert_user(
    p_id BIGINT,
    p_name VARCHAR(255),
    p_age BIGINT
) RETURNS VOID AS
$$
BEGIN
    INSERT INTO user_table(id, name, age)
    VALUES (p_id, p_name, p_age);
END;
$$
    LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION clear_user_table() RETURNS VOID AS
$$
BEGIN
    DELETE FROM user_table;
END;
$$
    LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_user_by_name(
    p_name VARCHAR(255)
) RETURNS SETOF user_table AS
$$
BEGIN
    RETURN QUERY
        SELECT *
        FROM user_table
        WHERE name = p_name;
END;
$$
    LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE update_user(
    p_id BIGINT,
    p_new_name VARCHAR(255),
    p_new_age BIGINT
)
    LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE user_table
    SET name = p_new_name, age = p_new_age
    WHERE id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION check_user_exists(
    p_id BIGINT
) RETURNS BOOLEAN AS
$$
DECLARE
    v_exists BOOLEAN;
BEGIN
    SELECT EXISTS(
        SELECT 1
        FROM user_table
        WHERE id = p_id
    ) INTO v_exists;

    RETURN v_exists;
END;
$$
    LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_all_users()
    RETURNS SETOF user_table
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT *
        FROM user_table;
END;
$$;

CREATE OR REPLACE FUNCTION delete_user(
    p_id BIGINT
) RETURNS VOID AS
$$
BEGIN
    DELETE FROM user_table
    WHERE id = p_id;
END;
$$
    LANGUAGE plpgsql;
