DROP TABLE IF EXISTS condReadings;
DROP TABLE IF EXISTS rawReadings;
DROP TABLE IF EXISTS greenTips;
DROP TABLE IF EXISTS meter;
DROP TABLE IF EXISTS buildings;
DROP TABLE IF EXISTS campus;
DROP TABLE IF EXISTS university;
DROP TABLE IF EXISTS images;

CREATE TABLE IF NOT EXISTS images (
    imageId INT NOT NULL UNIQUE AUTO_INCREMENT,
    image MEDIUMBLOB,
    forTip BOOLEAN,
    PRIMARY KEY (imageId)
);

CREATE TABLE IF NOT EXISTS university (
    universityId INT NOT NULL UNIQUE AUTO_INCREMENT,
    universityName VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY (universityId)
);

CREATE TABLE IF NOT EXISTS campus (
    campusId INT NOT NULL UNIQUE AUTO_INCREMENT,
    campusName VARCHAR(50) NOT NULL,
    universityId INT NOT NULL,
    PRIMARY KEY (campusId),
    FOREIGN KEY (universityId) REFERENCES university(universityId)
);

CREATE TABLE IF NOT EXISTS buildings (
   buildingId INT NOT NULL UNIQUE AUTO_INCREMENT,
   buildingName VARCHAR(50) NOT NULL UNIQUE,
   picture INT,
   occupancy INT,
   squareFootage INT,
   visible BOOLEAN,
   campusId INT,
   PRIMARY KEY (buildingId),
   FOREIGN KEY (picture) REFERENCES images(imageId),
   FOREIGN KEY (campusId) REFERENCES campus(campusId)
);

CREATE TABLE IF NOT EXISTS meter (
    meterId INT NOT NULL UNIQUE AUTO_INCREMENT,
    meterName VARCHAR(30) UNIQUE,
    meterDescription VARCHAR(30) DEFAULT '',
    buildingId INT NOT NULL,
    PRIMARY KEY (meterId),
    FOREIGN KEY (buildingId) REFERENCES buildings(buildingId)
);

CREATE TABLE IF NOT EXISTS greenTips (
    tipId INT NOT NULL UNIQUE AUTO_INCREMENT,
    picture INT,
    tipText VARCHAR(160),
    PRIMARY KEY (tipId),
    FOREIGN KEY (picture) REFERENCES images(imageId)
);

CREATE TABLE IF NOT EXISTS rawReadings (
    readingTime VARCHAR(50) NOT NULL,
    meterId INT NOT NULL,
    energyValue DECIMAL(12, 3),
    PRIMARY KEY (readingTime, meterId),
    FOREIGN KEY (meterId) REFERENCES meter(meterId)
);

CREATE TABLE IF NOT EXISTS condReadings (
    readingTime VARCHAR(50) NOT NULL,
    meterId INT NOT NULL,
    energyValue DECIMAL(12, 3),
    deltaEnergy DECIMAL(12, 3),
    deltaTime DECIMAL(20, 6),
    PRIMARY KEY (readingTime, meterId),
    FOREIGN KEY (meterId) REFERENCES meter(meterId)
);

DROP TABLE IF EXISTS userData;
CREATE TABLE IF NOT EXISTS userData (
   userNumber INT NOT NULL UNIQUE AUTO_INCREMENT,
   loginName VARCHAR(50) NOT NULL UNIQUE,
   userPassword VARCHAR(60) NOT NULL,
   firstName VARCHAR (25)DEFAULT '',
   lastName VARCHAR (35)DEFAULT '',
   emailAddress VARCHAR(50)DEFAULT '',
   userRole VARCHAR (30) NOT NULL,
   lastLogin VARCHAR (25),
   loginCount INT DEFAULT 0, 
   PRIMARY KEY (userNumber)
);

CREATE INDEX user_email_index on user_data (emailAddress);
