-- single line comment
/*
	multi-line comment
*/

DROP DATABASE IF EXISTS CalendarDatabase;
CREATE DATABASE CalendarDatabase;
USE CalendarDatabase;

CREATE TABLE Users (
	email VARCHAR(50) PRIMARY KEY NOT NULL,
    fname VARCHAR(30) NOT NULL,
    lname VARCHAR(50) NULL,
    image_url VARCHAR(200)
);
            
CREATE TABLE Events (
	eventID INT(11) PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(50) NOT NULL,
    eventTitle VARCHAR(200) NOT NULL,
    startDate VARCHAR(50) NOT NULL,
    endDate VARCHAR(50) NOT NULL,
    startTime VARCHAR(50) NOT NULL,
    endTime VARCHAR(50) NOT NULL,
    FOREIGN KEY fk_email (email) REFERENCES Users(email)
);
            
CREATE TABLE Following (
	linkID INT(11) PRIMARY KEY AUTO_INCREMENT,
    email_userA VARCHAR(11) NOT NULL,
    email_userB VARCHAR(11) NOT NULL,
    FOREIGN KEY fk_emailA (email_userA) REFERENCES Users(email),
    FOREIGN KEY fk_emailB (email_userB) REFERENCES Users(email)
);