    \c postgres;
    DROP DATABASE IF EXISTS echec_pong;
    CREATE DATABASE echec_pong;
    \c echec_pong;
    -- Table partie
    CREATE TABLE partie (
        id SERIAL PRIMARY KEY,
        date TIMESTAMP NOT NULL,
        nbpieces INTEGER NOT NULL
    );

    -- Table piece
    CREATE TABLE piece (
        id SERIAL PRIMARY KEY,
        nom VARCHAR(50) NOT NULL,
        position INTEGER NOT NULL
    );

    -- Table vie_piece
    CREATE TABLE vie_piece (
        id SERIAL PRIMARY KEY,
        piece_id INTEGER REFERENCES piece(id),
        vie INTEGER NOT NULL,
        partie_id INTEGER REFERENCES partie(id)
    );