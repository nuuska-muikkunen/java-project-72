    DROP TABLE IF EXISTS urls CASCADE;
    DROP TABLE IF EXISTS url_checks;

    CREATE TABLE urls
    (
        id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
        name       VARCHAR(255)                            NOT NULL,
        created_at TIMESTAMP                               NOT NULL,
        CONSTRAINT pk_url PRIMARY KEY (id)
    );

    CREATE TABLE url_checks
    (
        id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
        url_id      BIGINT                                  NOT NULL,
        status_code INT,
        title       TEXT,
        h1          VARCHAR(255),
        description TEXT,
        created_at  TIMESTAMP,
        CONSTRAINT pk_url_checks PRIMARY KEY (id)
    );

    ALTER TABLE url_checks
        ADD CONSTRAINT fk_url_checks_url_id FOREIGN KEY (url_id)
        REFERENCES urls (id) ON DELETE RESTRICT ON UPDATE RESTRICT;
