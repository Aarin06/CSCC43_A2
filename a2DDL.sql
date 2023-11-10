DROP SCHEMA IF EXISTS A2 CASCADE;
CREATE SCHEMA A2;
SET search_path TO A2;


-- The department table contains the departments at the university
CREATE TABLE department (
        dcode       CHAR(3) PRIMARY KEY,
        dname       VARCHAR(20) NOT NULL);

-- The student table contains information about the students at the univerity.
-- sid is the student number.
-- sex is either 'M' or 'F'
-- yearofstudy is number of years the student has been studying at the university.
CREATE TABLE student (
        sid         INTEGER PRIMARY KEY,
        slastname   CHAR(20) NOT NULL,
        sfirstname  CHAR(20) NOT NULL,
        sex	    CHAR(1)  NOT NULL,
        age 	    INTEGER  NOT NULL,
	    dcode       CHAR(3)  NOT NULL, 
        yearofstudy INTEGER  NOT NULL,
	FOREIGN KEY (dcode) REFERENCES department(dcode) ON DELETE RESTRICT);

-- The instructor table contains information about the instructors at the university.
-- iid is the instructor employee id.
-- idegree is the highest post-graduate degree held by the instructor (e.g., PhD, MSC)
-- an instructor can only be associated with one department.
CREATE TABLE instructor (
        iid         INTEGER PRIMARY KEY,
        ilastname   CHAR(20) NOT NULL,
        ifirstname  CHAR(20) NOT NULL,
        idegree	    CHAR (5) NOT NULL,
        dcode	    CHAR(3)  NOT NULL,
        FOREIGN KEY (dcode) REFERENCES department(dcode) ON DELETE RESTRICT);	 
	
-- The course table contains the courses offered at the university.
CREATE TABLE course (
        cid         INTEGER,
        dcode	    CHAR(3) REFERENCES DEPARTMENT(dcode) ON DELETE RESTRICT,
        cname	    CHAR(20) NOT NULL,
        PRIMARY KEY (cid, dcode));

-- The courseSection table contains the sections that are offered 
-- for courses at the university for each semester of each year.
-- Semester values are '9' fall, '1' winter, and '5' summer.
CREATE TABLE courseSection (
        csid        INTEGER PRIMARY KEY,
        cid         INTEGER NOT NULL,
        dcode       CHAR(3) NOT NULL,
        year	    INTEGER NOT NULL,
        semester    INTEGER NOT NULL,
        section	    CHAR(5) NOT NULL,
        iid         INTEGER REFERENCES instructor(iid),
        FOREIGN KEY (cid, dcode) REFERENCES course(cid, dcode), 
        UNIQUE (cid, dcode, year, semester, section));

-- The studentCourse table contains the courses a student has enrolled in, and their grade.
-- the grade is maintained as an integer value from 0 to 100.
CREATE TABLE studentCourse (
        sid         INTEGER REFERENCES student(sid),
        csid	    INTEGER REFERENCES courseSection(csid),
        grade	    NUMERIC(5,2) NOT NULL DEFAULT 0.00,
        PRIMARY KEY (sid, csid));
-- The classroom table contains the building name and the classroom number of the course taken with its capacity        
CREATE TABLE classroom(
        bname  VARCHAR(20) NOT NULL,
        rnum  INTEGER NOT NULL,
        capacity INTEGER NOT NULL,
        cid INTEGER NOT NULL,
        dcode  CHAR(3) NOT NULL,
        FOREIGN KEY (cid, dcode) REFERENCES course(cid, dcode),
        PRIMARY KEY (bnum,rnum));
        
-- The prerequisites table contains the prerequisites for each course.  There may be more than
-- one per course.  The course for which the prerequisite applies is identified by (cid, dcode).
-- The prerequisite for that course is identified by (pcid, pcode).
CREATE TABLE prerequisites (
        cid	    INTEGER  NOT NULL,
        dcode	    CHAR (3) NOT NULL,
        pcid	    INTEGER  NOT NULL,
        pdcode      CHAR(3)  NOT NULL,
        FOREIGN KEY (cid, dcode) REFERENCES course(cid, dcode),
        FOREIGN KEY (pcid, pdcode) REFERENCES course(cid, dcode),
        PRIMARY KEY (cid, dcode,pcid, pdcode));
