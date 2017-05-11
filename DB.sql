CREATE TYPE question_type AS ENUM ('ONEOPTION', 'FEWOPTIONS', 'MATCHING', 'INPUT', 'ESSAY');
CREATE TYPE roles AS ENUM ('USER', 'ADMIN');


CREATE TABLE tests(
  id 	SERIAL PRIMARY KEY,
  points	integer
);


CREATE TABLE questions(
  id integer PRIMARY KEY NOT NULL,
  test_id		integer NOT NULL,
  task		text,
  type		question_type,
  max_points	integer
);
/*options with keys for one-,multioption questions*/
CREATE TABLE options(
  id integer	PRIMARY KEY NOT NULL,
  question_id	integer NOT NULL,
  option		text,
  is_correct	boolean
);
/*table of correct paired matches*/
CREATE TABLE matching(
  id integer	PRIMARY KEY NOT NULL,
  question_id 	integer NOT NULL,
  option		text,
  pair_id		integer
);

/*USER-SENT*/
/*table of user-sent inputs and essays*/
CREATE TABLE user_inputs(
  id integer	PRIMARY KEY NOT NULL,
  question_id	integer NOT NULL,
  user_id		integer NOT NULL,
  answer		text
);


/*user-sent answers to one and multioption questions*/
CREATE TABLE answers(
  id integer  PRIMARY KEY NOT NULL,
  question_id integer NOT NULL,
  user_id     integer NOT NULL,
  option_id   integer NOT NULL
);

CREATE TABLE matching_answers(
  id integer  PRIMARY KEY NOT NULL,
  question_id integer NOT NULL,
  user_id     integer NOT NULL,
  key_id      integer NOT NULL,
  pair_id     integer NOT NULL
);


CREATE TABLE users (
  id       integer PRIMARY KEY NOT NULL,
  name     text,
  password TEXT,
  access roles
);