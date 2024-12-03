CREATE DATABASE IF NOT EXISTS TravelTracker;
USE TravelTracker;

-- Places Table 
CREATE TABLE IF NOT EXISTS places (
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    food VARCHAR(255),
    calculated_rating INT CHECK (calculated_rating BETWEEN 1 AND 5),
    PRIMARY KEY (city, country)
);

-- Activities Table (stores distinct activities)
CREATE TABLE IF NOT EXISTS activities (
    activity_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- Place_Activities Table (junction table to store many-to-many relationships)
CREATE TABLE IF NOT EXISTS place_activities (
    place_id INT,
    activity_id INT,
    PRIMARY KEY (place_id, activity_id),
    FOREIGN KEY (place_id) REFERENCES places(location_id) ON DELETE CASCADE,
    FOREIGN KEY (activity_id) REFERENCES activities(activity_id) ON DELETE CASCADE
);



-- History Table
CREATE TABLE IF NOT EXISTS history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    place_id INT,
    event_name VARCHAR(255) NOT NULL,
    year INT NOT NULL,
    description TEXT,
    FOREIGN KEY (place_id) REFERENCES places(location_id)
);

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    planned_trips TEXT, -- Can be JSON or serialized data depending on design preference
    wishlist TEXT -- Can be JSON or serialized data depending on design preference
);

-- Ratings Table
CREATE TABLE IF NOT EXISTS ratings (
    rating_id INT AUTO_INCREMENT PRIMARY KEY,
    completed_trip_id INT,
    rating_number INT CHECK (rating_number BETWEEN 1 AND 5),
    comment TEXT,
    FOREIGN KEY (completed_trip_id) REFERENCES completed_trips(completed_trip_id)
);

-- Wishlist Table
CREATE TABLE IF NOT EXISTS wishlist (
    wishlist_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    place_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (place_id) REFERENCES places(location_id)
);

-- Planned Trips Table
CREATE TABLE IF NOT EXISTS planned_trips (
    trip_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    place_id INT,
    additional_information TEXT,
    completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (place_id) REFERENCES places(location_id)
);

-- Completed Trips Table
CREATE TABLE IF NOT EXISTS completed_trips (
    completed_trip_id INT AUTO_INCREMENT PRIMARY KEY,
    trip_id INT,
    rating_number INT CHECK (rating_number BETWEEN 1 AND 5),
    FOREIGN KEY (trip_id) REFERENCES planned_trips(trip_id)
);
-- Function to calculate average rating for a place
CREATE FUNCTION calculate_average_rating (place_id INT)
RETURNS FLOAT
DETERMINISTIC
BEGIN
    DECLARE avg_rating FLOAT;
    SELECT AVG(rating_number) INTO avg_rating
    FROM completed_trips
    WHERE trip_id IN (
        SELECT trip_id
        FROM planned_trips
        WHERE place_id = place_id
    );
    RETURN avg_rating;
END;

-- Function to count total planned trips for a user
CREATE FUNCTION total_planned_trips (user_id INT)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE total INT;
    SELECT COUNT(*) INTO total
    FROM planned_trips
    WHERE user_id = user_id;
    RETURN total;
END;

-- Function to check if a place is in a user's wishlist
CREATE FUNCTION is_place_in_wishlist (user_id INT, place_id INT)
RETURNS BOOLEAN
DETERMINISTIC
BEGIN
    DECLARE exists_flag BOOLEAN;
    SELECT COUNT(*) > 0 INTO exists_flag
    FROM wishlist
    WHERE user_id = user_id AND place_id = place_id;
    RETURN exists_flag;
END;

-- Procedure to add a place to a user's wishlist
CREATE PROCEDURE add_to_wishlist (IN user_id INT, IN place_id INT)
BEGIN
    IF NOT is_place_in_wishlist(user_id, place_id) THEN
        INSERT INTO wishlist (user_id, place_id)
        VALUES (user_id, place_id);
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Place already in wishlist.';
    END IF;
END;

-- Procedure to mark a trip as completed and add a rating
CREATE PROCEDURE mark_trip_completed (IN trip_id INT, IN rating INT)
BEGIN
    UPDATE planned_trips
    SET completed = TRUE
    WHERE trip_id = trip_id;

    INSERT INTO completed_trips (trip_id, rating_number)
    VALUES (trip_id, rating);
END;

-- Procedure to add a historical event to a place
CREATE PROCEDURE add_history_event (
    IN place_id INT,
    IN event_name VARCHAR(255),
    IN year INT,
    IN description TEXT
)
BEGIN
    INSERT INTO history (place_id, event_name, year, description)
    VALUES (place_id, event_name, year, description);
END;

-- Procedure to add an activity to a place
CREATE PROCEDURE add_activity_to_place (
    IN place_id INT,
    IN activity_id INT
)
BEGIN
    INSERT INTO place_activities (place_id, activity_id)
    VALUES (place_id, activity_id);
END;

-- Trigger to update calculated rating after a new rating is added
CREATE TRIGGER update_calculated_rating_after_rating
AFTER INSERT ON completed_trips
FOR EACH ROW
BEGIN
    DECLARE new_avg FLOAT;
    SET new_avg = calculate_average_rating((SELECT place_id FROM planned_trips WHERE trip_id = NEW.trip_id));
    UPDATE places
    SET calculated_rating = new_avg
    WHERE location_id = (SELECT place_id FROM planned_trips WHERE trip_id = NEW.trip_id);
END;

-- Trigger to prevent duplicate activity association
CREATE TRIGGER prevent_duplicate_activity_association
BEFORE INSERT ON place_activities
FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM place_activities
        WHERE place_id = NEW.place_id AND activity_id = NEW.activity_id
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Activity already associated with this place.';
    END IF;
END;

-- Event to clean up wishlist periodically
CREATE EVENT cleanup_wishlist
ON SCHEDULE EVERY 1 MONTH
DO
BEGIN
    DELETE FROM wishlist
    WHERE place_id NOT IN (SELECT location_id FROM places);
END;

-- Event to recalculate ratings weekly
CREATE EVENT recalculate_ratings
ON SCHEDULE EVERY 1 WEEK
DO
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE place_id INT;
    DECLARE cur CURSOR FOR SELECT location_id FROM places;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO place_id;
        IF done THEN
            LEAVE read_loop;
        END IF;

        UPDATE places
        SET calculated_rating = calculate_average_rating(place_id)
        WHERE location_id = place_id;
    END LOOP;

    CLOSE cur;
END;
