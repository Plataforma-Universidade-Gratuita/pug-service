CREATE
OR REPLACE FUNCTION uuid_generate_v7()
RETURNS uuid
AS $$
DECLARE
unix_ts_ms bytea;
  uuid_bytes
bytea;
BEGIN
  unix_ts_ms
= substring(int8send(floor(extract(epoch from clock_timestamp()) * 1000)::bigint) from 3);
  uuid_bytes
= unix_ts_ms || gen_random_bytes(10);
  uuid_bytes
= set_byte(uuid_bytes, 6, (get_byte(uuid_bytes, 6) & x'0f'::int) | x'70'::int);
  uuid_bytes
= set_byte(uuid_bytes, 8, (get_byte(uuid_bytes, 8) & x'3f'::int) | x'80'::int);

RETURN encode(uuid_bytes, 'hex')::uuid;
END;
$$
LANGUAGE plpgsql;