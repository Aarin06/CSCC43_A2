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
()

--Query 5
INSERT INTO query5
()

--Query 6
INSERT INTO query6
()

--Query 7
INSERT INTO query7
()

