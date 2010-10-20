DROP TABLE IF EXISTS two_string_table;
CREATE TABLE two_string_table(
  col1 VARCHAR(32),
  col2 VARCHAR(32)
);

DROP TABLE IF EXISTS single_id_table;
CREATE TABLE single_id_table(
  id INT
);

DROP TABLE IF EXISTS date_table;
CREATE TABLE date_table(
  id INT,
  creation_date TIMESTAMP
);

DROP TABLE IF EXISTS boolean_table;
CREATE TABLE boolean_table(
  id INT NOT NULL PRIMARY KEY,
  is_valid BIT
);