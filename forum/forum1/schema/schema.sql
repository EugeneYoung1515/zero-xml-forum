CREATE TABLE t_board (
    board_id SERIAL,
    board_desc character varying(255),
    board_name character varying(255),
    topic_num integer,
    PRIMARY KEY(board_id)
);

CREATE TABLE t_board_manager (
    user_id integer NOT NULL,
    board_id integer NOT NULL
);

CREATE TABLE t_login_log (
    login_log_id SERIAL,
    ip character varying(255),
    login_datetime timestamp without time zone,
    user_id integer,
    PRIMARY KEY(login_log_id)
);

CREATE TABLE t_post (
    post_type character varying(31) NOT NULL,
    post_id SERIAL,
    board_id integer,
    create_time timestamp without time zone,
    post_text character varying(255),
    post_title character varying(255),
    topic_id integer,
    user_id integer,
    PRIMARY KEY(post_id)
);

CREATE TABLE t_topic (
    topic_id SERIAL,
    board_id integer,
    create_time timestamp without time zone,
    digest integer NOT NULL,
    last_post timestamp without time zone,
    topic_replies integer,
    topic_title character varying(255),
    topic_views integer,
    user_id integer,
    PRIMARY KEY(topic_id)
);

CREATE TABLE t_user (
    user_id SERIAL,
    credit integer NOT NULL,
    last_ip character varying(255),
    last_visit timestamp without time zone,
    locked integer NOT NULL,
    password character varying(255),
    user_name character varying(255),
    user_type integer,
    PRIMARY KEY(user_id)
);
