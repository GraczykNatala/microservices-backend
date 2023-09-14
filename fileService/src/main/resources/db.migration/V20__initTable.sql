CREATE TABLE image_data
(
    id              serial      primary key,
    uuid            varchar     not null,
    path            varchar     not null,
    isUsed          boolean     not null DEFAULT FALSE,
    createAt        DATE        not null
);

