CREATE USER testeng WITH password 'qwerty';

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


CREATE TABLE questions(
  id SERIAL PRIMARY KEY,
  test_id		integer NOT NULL,
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
  question_id	integer NOT NULL,
  user_id		integer NOT NULL,
  answer		text
);


/*user-sent answers to one and multioption questions*/
CREATE TABLE answers(
  id SERIAL PRIMARY KEY,
  question_id integer NOT NULL,
  user_id     integer NOT NULL,
  option_id   integer NOT NULL
);

CREATE TABLE matching_answers(
  id SERIAL PRIMARY KEY,
  question_id integer NOT NULL,
  user_id     integer NOT NULL,
  key_id      integer NOT NULL,
  pair_id     integer NOT NULL
);


CREATE TABLE users (
  id       SERIAL PRIMARY KEY,
  name     text,
  password TEXT,
  access roles
);