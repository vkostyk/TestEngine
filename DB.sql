CREATE USER testeng WITH password 'queantes';

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO testeng;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO testeng;

CREATE TYPE question_type AS ENUM ('ONE_OPTION', 'FEW_OPTIONS', 'MATCHING', 'INPUT', 'ESSAY');
CREATE TYPE roles AS ENUM ('USER', 'ADMIN');


CREATE TABLE tests(
  id 	SERIAL PRIMARY KEY,
  name text,
  description text,
  points	integer
);

CREATE TABLE test_map(
  id SERIAL PRIMARY KEY,
  test_id INTEGER NOT NULL ,
  question_id INTEGER NOT NULL
);

CREATE TABLE questions(
  id SERIAL PRIMARY KEY,
  task		text,
  type		question_type,
  max_points	integer
);
/*options with keys for one-,multioption questions*/
CREATE TABLE options(
  id SERIAL PRIMARY KEY,
  question_id	integer NOT NULL,
  option		text,
  is_correct	boolean
);
/*table of correct paired matches*/
CREATE TABLE matching(
  id SERIAL PRIMARY KEY,
  question_id 	integer NOT NULL,
  option		text,
  pair_id		integer
);

/*USER-SENT*/
/*table of user-sent inputs and essays*/
CREATE TABLE user_inputs(
  id SERIAL PRIMARY KEY,
  test_id INTEGER,
  question_id	INTEGER NOT NULL,
  user_id		INTEGER NOT NULL,
  attempt INTEGER NOT NULL ,
  answer		text
);

/*user-sent answers to one and multioption questions*/
CREATE TABLE answers(
  id SERIAL PRIMARY KEY,
  test_id INTEGER,
  question_id integer NOT NULL,
  user_id     integer NOT NULL,
  attempt INTEGER,
  answer   integer NOT NULL
);

CREATE TABLE attempts(
  id SERIAL PRIMARY KEY,
  test_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  last_attempt INTEGER NOT NULL
);

CREATE TABLE users (
  id       SERIAL PRIMARY KEY,
  name     text,
  password TEXT,
  access roles
);