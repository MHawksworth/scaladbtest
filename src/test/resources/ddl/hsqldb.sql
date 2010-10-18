---------------------------------------------------------------------------
-- two_string_table
---------------------------------------------------------------------------

DROP TABLE IF EXISTS two_string_table;
CREATE TABLE two_string_table(
  col1 VARCHAR(32),
  col2 VARCHAR(32)
);

---------------------------------------------------------------------------
-- single_id_table
---------------------------------------------------------------------------

DROP TABLE IF EXISTS single_id_table;
CREATE TABLE single_id_table(
  id INT
);

---------------------------------------------------------------------------
-- date_table
---------------------------------------------------------------------------

DROP TABLE IF EXISTS date_table;
CREATE TABLE date_table(
  id INT,
  creation_date TIMESTAMP
);