--images will need to be changed; junk values added to avoid foreign key errors
INSERT INTO images(uuid, image, forTip)
VALUES
    ('image1', NULL, TRUE),
    ('image2', NULL, FALSE)
;

INSERT INTO university (universityName)
VALUES
    ('Bloomsburg University of Pennsylvania')
;

INSERT INTO campus (campusName, universityId)
VALUES
    ('Lower Campus', 1),
    ('Upper Campus', 1)
;

INSERT INTO buildings (buildingName, picture, occupancy,
    squareFootage, visible, campusId)
VALUES    
    ('Ben Franklin Hall', 2, 100, 10864, FALSE, 1), --square footage and occupancy junk values for BF
    ('Columbia Hall', 2, 425, 83319, TRUE, 1),
    ('Elwell Hall', 2, 689, 155964, TRUE, 1),
    ('Hartline Science Center', 2, 425, 124990, TRUE, 1),
    ('Nelson Field House', 2, 200, 74442, TRUE, 2),
    ('Student Recreation Center', 2, 150, 107626, TRUE, 1)
;

INSERT INTO greenTips (picture, tipText)
VALUES
    (1, 'Print on both sides of the paper.'),
    (1, 'Shower with a friend.'),
    (1, 'Turn the lights off when you leave your room.'),
    (1, 'Open the blinds to get heat from the sun.'),
    (1, 'Set your computer to go to sleep after 5 minutes.'),
    (1, 'Hug a tree.')
;

INSERT INTO meter (meterName, meterDescription, buildingId)
VALUES
    ('CL.ELEC.MTR', 'Columbia Hall', 2),
    ('EL.ELEC.MTR', 'Elwell Hall', 3),
    ('HC.METER.MTR', 'Hartline Main', 4),
    ('HC.WEST.ELEC.MTR', 'Hartline West', 4),
    ('NF.ELEC.MTR', 'Nelson Field House', 5)
;

/*
INSERT INTO readings(readingTime, meterId, energyValue, deltaEnergy, deltaTime)
VALUES
;
*/

INSERT INTO userData (loginName, userPassword,firstName,lastName,emailAddress,
userRole,lastLogin, loginCount)
VALUES 
   ('admin','admin','System','Administrator','cjones@bloomu.edu','SystemAdmin','2015-02-13T20:25:25.596',0),
   ('ngreene','abc123','Nathaniel','Greene','ngreene@bloomu,edu','Administrator','2015-02-13T20:25:25.596',0)
  ;
