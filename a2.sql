SET search_path TO A2;

--If you define any views for a question (you are encouraged to), you must drop them
--after you have populated the answer table for that question.
--Good Luck!

--Query 1
CREATE VIEW InstructorNoPHDCount AS

SELECT d.dcode, d.dname, count(*) as numOfInstructors
FROM department d JOIN instructor i ON d.dcode = i.code
WHERE idegree != 'PhD'
GROUP BY dcode, dname

INSERT INTO query1
(SELECT d.dname
FROM InstructorNoPHDCount
WHERE numOfInstructors = (
  SELECT MIN(numOfInstructors)
  FROM InstructorNoPHDCount)
)

--Query 2
INSERT INTO query2
(SELECT count(*) as num
FROM student s JOIN department d On s.dcode = d.dcode
WHERE s.yearofstudy = 2 and s.sex = 'M' and d.dname = 'Computer Science')

--Query 3
INSERT INTO query3
(SELECT cs.year, count(DISTINCT s.sid) as enrollment
FROM courseSection cs
JOIN student s on cs.csid = s.csid
JOIN department d on cs.dcode = d.dcode
WHERE d.dname = 'Computer Science' 
AND cs.year BETWEEN 2016 AND 2020
GROUP BY cs.year
ORDER BY enrollment DESC
LIMIT 1)

--Query 4
INSERT INTO query4
(SELECT cl.bname, count(cl.rnum) as totalrooms 
FROM classroom cl 
JOIN course c ON c.cid = cl.cid
WHERE c.dcode = 'CSC'
GROUP BY cl.bname
ORDER BY totalrooms DESC)

CREATE VIEW CurrentSem AS 
SELECT DISTINCT cs.year, cs.semester
FROM courseSection cs
WHERE cs.year = (SELECT MAX(cs.year) FROM courseSection cs)
AND cs.semester = (SELECT MAX(cs.semester) FROM courseSection cs WHERE cs.year = (SELECT MAX(cs.year) FROM courseSection cs))

CREATE VIEW AvgGrades AS 
SELECT sc.sid, AVG(sc.grade) as average
FROM courseSection cs 
JOIN studentCourse sc ON cs.csid = sc.csid
WHERE (cs.year,cs.semester) not in (select * from CurrentSem) 
GROUP BY sc.sid

CREATE VIEW DepartmentAvg AS
SELECT d.dcode, MAX(ag.average) as deptavg
FROM AvgGrades ag
JOIN student s ON s.sid = ag.sid 
JOIN department d ON d.dcode = s.dcode
GROUP BY d.dcode

--Query 5
INSERT INTO query5
(SELECT d.dname, s.sid, s.sfirstname, s.slastname, da.deptavg
FROM DepartmentAvg da
JOIN department d ON da.dcode = d.dcode
JOIN AvgGrades ag ON ag.average = da.deptavg
JOIN student s ON s.sid = ag.sid)

--Query 6
INSERT INTO query6
(SELECT st.sfirstname, st.slastname, cs1.cid, cs1.year, cs1.semester 
FROM student st 
JOIN studentCourse s1 ON st.sid = s1.sid
JOIN courseSection cs1 ON s1.csid = cs1.csid
JOIN prerequisites p ON p.cid = cs1.cid
WHERE p.pcid IN (SELECT cs2.cid
  FROM studentCourse s2
  JOIN courseSection cs2 ON s.csid = cs2.csid
  JOIN prerequisites p ON p.cid = cs2.cid
  WHERE s1.sid = s2.sid 
  AND cs1.year>= cs2.year
  AND cs1.semester < cs2.semester)
)

--Query 7
INSERT INTO query7
()

