create table smallint_table (smallint_column smallint);
create table integer_table (integer_column integer);
create table bigint_table (bigint_column bigint);
create table decimal_table (decimal_column decimal);
create table real_table (real_column real);
create table double_table (double_column double);
create table char_table (char_column char);
create table char_bit_table (char_bit_column char for bit data);
create table varchar_table (varchar_column varchar(255));
create table varchar_bit_table (varchar_bit_column varchar(255) for bit data);
create table long_varchar_table (long_varchar_column long varchar);
create table long_varchar_bit_table (long_varchar_bit_column long varchar for bit data);
create table clob_table (clob_column clob);
create table blob_table (blob_column blob);
create table date_table (date_column date);
create table time_table (time_column time);
create table timestamp_table (timestamp_column timestamp);

CREATE PROCEDURE PROC_DTO_PARAM(
  IN param1 INTEGER,
  INOUT param2 INTEGER,
  OUT param3 INTEGER)
LANGUAGE SQL
DYNAMIC RESULT SETS 0
BEGIN
  SET param2 = param2 + param1;
  SET param3 = param1;
END
@
