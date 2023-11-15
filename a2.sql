SET search_path TO A2;

--If you define any views for a question (you are encouraged to), you must drop them
--after you have populated the answer table for that question.
--Good Luck!

--Query 1
CREATE VIEW InstructorNoPHDCount AS

SELECT d.dcode, d.dname, count(*) as numOfInstructors
FROM department d JOIN instructor i ON d.dcode = i.dcode
WHERE idegree <> 'PhD'
GROUP BY d.dcode, d.dname;


INSERT INTO query1(dname)
(SELECT d.dname
FROM InstructorNoPHDCount d
WHERE numOfInstructors = (
  SELECT MIN(numOfInstructors)
  FROM InstructorNoPHDCount)
);

DROP VIEW InstructorNoPHDCount;
	
--Query 2
CREATE VIEW csDcode AS
	SELECT dcode
	FROM department
	WHERE dname = 'Computer Science';

INSERT INTO query2 (num)
	SELECT count(sid) as num
	FROM student
	WHERE yearofstudy = 2 and dcode in (SELECT * FROM csDcode);



--Query 3	
CREATE VIEW yearlyEnrollment AS
	SELECT cs.year as year, count(sc.sid) as enrollmentCount
	FROM courseSection cs
	JOIN studentCourse sc
		ON sc.csid = cs.csid
	WHERE cs.dcode in (SELECT * FROM csDcode) AND cs.year BETWEEN 2016 AND 2020
	GROUP BY cs.year;

INSERT INTO query3 (year, enrollment)
SELECT year, enrollmentCount as enrollment
FROM yearlyEnrollment
WHERE enrollmentCount = (
	SELECT max(enrollmentCount)
	FROM yearlyEnrollment
);

DROP VIEW yearlyEnrollment;

--Query 4
INSERT INTO query4 (bname, rnum) 
SELECT bname, count(distinct rnum) as rnum
FROM classroom
WHERE dcode = 'CSC'
GROUP BY bname
ORDER BY count(distinct rnum) desc;

--Query 5
CREATE VIEW CurrentSem AS
SELECT year, max(semester) as semester
FROM courseSection cs
WHERE year = (
SELECT max(cs2.year)
FROM courseSection cs2)
GROUP BY year;
    
CREATE VIEW AvgGrades AS 
SELECT sc.sid, AVG(sc.grade) as average
FROM courseSection cs 
JOIN studentCourse sc ON cs.csid = sc.csid
WHERE (cs.year,cs.semester) not in (select * from CurrentSem) 
GROUP BY sc.sid;

CREATE VIEW DepartmentAvg AS
SELECT d.dcode, MAX(ag.average) as deptavg
FROM AvgGrades ag
JOIN student s ON s.sid = ag.sid 
JOIN department d ON d.dcode = s.dcode
GROUP BY d.dcode;

INSERT INTO query5 (dept, sid, sfirstname, slastname, avgGrade)
(SELECT d.dname as dept, s.sid, s.sfirstname, s.slastname, da.deptavg as avgGrade
FROM DepartmentAvg da
JOIN department d ON da.dcode = d.dcode
JOIN AvgGrades ag ON ag.average = da.deptavg
JOIN student s ON s.sid = ag.sid);

DROP VIEW DepartmentAvg;
DROP VIEW AvgGrades;
--Query 6
CREATE VIEW StudentPrereq AS
SELECT st.sfirstname, st.slastname, c.cname, cs1.year, cs1.semester 
FROM student st 
JOIN studentCourse s1 ON st.sid = s1.sid
JOIN courseSection cs1 ON s1.csid = cs1.csid
JOIN course c ON c.cid = cs1.cid
JOIN prerequisites p1 ON p1.cid = cs1.cid AND p1.dcode = cs1.dcode
WHERE (p1.pcid, p1.pdcode) IN (SELECT cs2.cid, cs2.dcode
  FROM studentCourse s2
  JOIN courseSection cs2 ON s2.csid = cs2.csid
  WHERE s1.sid = s2.sid 
  AND ((cs1.year = cs2.year
  AND cs1.semester > cs2.semester) OR (cs1.year > cs2.year)));

--might need this
CREATE VIEW coursesToRemove AS
(SELECT *
FROM StudentPrereq s)
UNION
(SELECT st.sfirstname, st.slastname, c.cname, cs1.year, cs1.semester 
FROM student st 
JOIN studentCourse s1 ON st.sid = s1.sid
JOIN courseSection cs1 ON s1.csid = cs1.csid
JOIN course c ON c.cid = cs1.cid
JOIN prerequisites p1 ON p1.pcid = cs1.cid AND p1.pdcode = cs1.dcode);


INSERT INTO query6 (fname, lname, cname, year, semester)
(SELECT st.sfirstname, st.slastname, c.cname, cs1.year, cs1.semester 
FROM student st 
JOIN studentCourse s1 ON st.sid = s1.sid
JOIN courseSection cs1 ON s1.csid = cs1.csid
JOIN course c ON c.cid = cs1.cid
JOIN prerequisites p1 ON p1.cid = cs1.cid AND p1.dcode = cs1.dcode
EXCEPT 
SELECT * FROM StudentPrereq
);

DROP VIEW coursesToRemove;
DROP VIEW StudentPrereq;


--Query 7
CREATE VIEW enrollment3CSAvg AS
	SELECT cs.cid, cs.dcode, cs.semester, cs.year, avg(sc.grade) as avgmark
	FROM courseSection cs
	JOIN studentCourse sc
		ON cs.csid = sc.csid
	WHERE dcode in (SELECT * FROM csDcode) AND (cs.year, cs.semester) NOT IN (SELECT * FROM CurrentSem)
	GROUP BY cs.cid, cs.dcode, cs.semester, cs.year
	HAVING count(sc.sid) >= 3;

CREATE VIEW MaxCourseAverages AS
SELECT c.cname, ca1.semester, ca1.year, ca1.avgmark
FROM enrollment3CSAvg ca1
JOIN course c ON ca1.cid = c.cid AND ca1.dcode = c.dcode
WHERE ca1.avgmark = 
  (SELECT MAX(avgmark)
  FROM enrollment3CSAvg ca2
  WHERE ca2.cid = ca1.cid AND ca2.dcode = ca1.dcode
  GROUP BY ca2.cid, ca2.dcode);

CREATE VIEW MinCourseAverages AS
SELECT c.cname, ca1.semester, ca1.year, ca1.avgmark
FROM enrollment3CSAvg ca1
JOIN course c ON ca1.cid = c.cid AND ca1.dcode = c.dcode
WHERE ca1.avgmark = 
  (SELECT MIN(avgmark)
  FROM enrollment3CSAvg ca2
  WHERE ca2.cid = ca1.cid AND ca2.dcode = ca1.dcode
  GROUP BY ca2.cid, ca2.dcode);

INSERT INTO query7(cname, semester, year, avgmark)
(SELECT *
FROM MaxCourseAverages
UNION 
SELECT * 
FROM MinCourseAverages);

DROP VIEW MinCourseAverages;
DROP VIEW MaxCourseAverages;
DROP VIEW enrollment3CSAvg;
DROP VIEW csDcode;
DROP VIEW CurrentSem;