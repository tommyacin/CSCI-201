DROP DATABASE IF EXISTS hangman;
CREATE DATABASE hangman;
USE hangman;


create table Users (
	username varchar(50) primary key not null,
    pass varchar(50) not null,
    wins int(11) not null,
    losses int(11) not null
);

create table Games (
	gameName varchar(50) primary key not null,
    numUsers int(11) not null
	#secretWord varchar(50) not null
);