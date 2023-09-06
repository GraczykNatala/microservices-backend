create table resetOperations
    (
        id              serial primary key,
        users           integer REFERENCES "users" (id),
        createdate     timestamp DEFAULT current_timestamp,
        ui              varchar
)
