SET search_path TO A2;

--If you define any views for a question (you are encouraged to), you must drop them
--after you have populated the answer table for that question.
--Good Luck!

--Query 1
CREATE VIEW noPhD AS
	SELECT *
	FROM instructor
	WHERE idegree <> 'PhD';

CREATE VIEW minInstructorCount AS
	SELECT MIN(count_iid) AS min_count_iid
	FROM (
		SELECT COUNT(n.iid) AS count_iid
		FROM department d
		JOIN noPhD n 
			ON d.dcode = n.dcode
		GROUP BY d.dcode
	) AS counts;
	
INSERT INTO query1 (dname)
	SELECT d.dname
	FROM department d
	JOIN noPhD n 
		ON d.dcode = n.dcode
	GROUP BY d.dcode
	HAVING count(n.iid) = minInstructorCount;
	
DROP VIEW NoPhD;
DROP VIEW minInstructorCount;
	
--Query 2
CREATE VIEW csDcode AS
	SELECT dcode
	FROM department
	WHERE dname = 'Computer Science';

DROP VIEW csDcode;
INSERT INTO query2 (num)
	SELECT count(sid) as num
	FROM student
	WHERE yearofstudy = 2 and dcode in csDcode;
	
--Query 3
CREATE VIEW csDcode AS
	SELECT dcode
	FROM department
	WHERE dname = 'Computer Science';
	
CREATE VIEW yearlyEnrollment AS
	SELECT cs.year as year, count(sc.sid) as enrollmentCount
	FROM courseSection cs
	JOIN studentCourse sc
		ON sc.csid = cs.csid
	WHERE cs.dcode in csDcode AND cs.year BETWEEN 2016 AND 2020
	GROUP BY cs.year;

INSERT INTO query3 (year, enrollment)
SELECT year, enrollmentCount as enrollment
FROM yearlyEnrollment
WHERE enrollmentCount = (
	SELECT max(enrollmentCount)
	FROM yearlyEnrollment;
)

DROP VIEW csDcode;
DROP VIEW yearlyEnrollment;

--Query 4
INSERT INTO query4 (bname, rnum) 
SELECT bname, count(distinct rnum) as rnum
FROM classroom
WHERE dcode = 'CSC'
GROUP BY bname
ORDER BY count(distinct rnum) desc;

--Query 5


INSERT INTO query5

--Query 6
CREATE VIEW studentPrereq AS
	SELECT sc.sid, sc.csid, cs.year, cs.semester, p.pcid, p.pdcode
	FROM studentCourse sc
	JOIN courseSection cs
		ON sc.csid = cs.csid
	JOIN prerequisites p
		ON cs.cid = p.cid AND cs.dcode = p.dcode;

CREATE VIEW satisfiedCourses AS
	SELECT sc.sid, sp.csid, sp.pcid, sp.pdcode
	FROM studentCourse sc
	JOIN courseSection cs
		ON sc.csid = cs.csid
	JOIN studentPrereq sp
		ON cs.cid = sp.pcid AND cs.dcode = sp.pdcode
	WHERE sp.sid = sc.sid and sp.year > ;
	
INSERT INTO query6 (fname, lname, cname, year, semester)
SELECT s.sfirstname as fname, s.slastname as lname, c.cname, cs.year, cs.semester
FROM (
	studentPrereq
	except
	satisfiedCourses) AS unsatisfiedCourse uc
JOIN student s
	ON uc.sid = s.sid
JOIN courseSection cs
	ON uc.csid = cs.csid
JOIN course c
	ON cs.cid = c.cid AND cs.dcode = c.dcode;

DROP VIEW studentPrereq;
DROP VIEW satisfiedCourses;

--Query 7
CREATE VIEW csDcode AS
	SELECT dcode
	FROM department
	WHERE dname = 'Computer Science';

CREATE VIEW enrollment3CSAvg AS
	SELECT cs.cid, cs.dcode, cs.semester, cs.year, avg(sc.grade) as avgGrade
	FROM courseSection cs
	JOIN studentCourse sc
		ON cs.csid = sc.csid
	WHERE dcode in csDcode
	GROUP BY cs.cid, cs.dcode, cs.semester, cs.year
	HAVING count(sc.sid) >= 3;

INSERT INTO query7 (cname, semester, year, avgmark)
SELECT c.cname, e1.semester, e1.year, e1.avgGrade as avgmark
FROM enrollment3CSAvg e1
JOIN course c
	on e1.cid = c.cid and e1.dcode = c.dcode
WHERE e1.avgGrade = (
	SELECT max(avgGrade)
	FROM enrollment3CSAvg e2
	WHERE e1.cid = e2.cid AND e1.decode = e2.dcode
	GROUP BY e2.cid, e2.dcode
	
) or e1.avgGrade = (
	SELECT min(avgGrade)
	FROM enrollment3CSAvg e2
	WHERE e1.cid = e2.cid AND e1.decode = e2.dcode;
	GROUP BY e2.cid, e2.dcode
	
)

DROP VIEW csDcode;
DROP VIEW enrollment3CSAvg;
