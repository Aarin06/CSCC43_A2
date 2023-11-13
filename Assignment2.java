import java.sql.*;
import java.util.*;

public class Assignment2 {

	// A connection to the database.
	// This variable is kept public.
	public Connection connection;

	/*
	 * Constructor for Assignment2. Identifies the PostgreSQL driver using
	 * Class.forName() method.
	 */
	public Assignment2() {
//		try {
//			Class.forName("org.postgresql.Driver");
//		}
//		catch(Exception e) {
//			 System.out.println("Failed to connect to the database.");
//		}
	}

	/*
	 * Using the String input parameters which are the URL, username, and
	 * password, establish the connection to be used for this object instance.
	 * If a connection already exists, it will be closed. Return true if a new
	 * connection instance was successfully established.
	 */
	public boolean connectDB(String URL, String username, String password) {
		
		try {
			connection = DriverManager.getConnection(URL, username, password);
			if (connection != null) {
				 System.out.println("Connected to the database.");
				return true;
			}
			else {
				System.out.println("Failed to connect to the database.");
				return false;
			}
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("Failed to connect to the database.");
			return false;
		}
	
	}

	public boolean disconnectDB() {
		 try {
			connection.close();
			System.out.println("Disconnected to the database.");
			return true;
		} catch (SQLException e) {
			System.out.println("Failed to disconnect from the database.");
			return false;
		}
	}

	public boolean insertStudent(int sid, String lastName, String firstName,
			String sex, int age, String dcode, int yearOfStudy) {
		
		Boolean deptExists = false;
		
		try {
			Statement statement = connection.createStatement();
           
			String sqlQuery = "SELECT dcode FROM A2.department";
           
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                String tempDCode = resultSet.getString("dcode");
                if (tempDCode.equals(dcode)){
                	deptExists = true;
                }
            }
			
			if (!deptExists) {
				return false; 
			}
			
			if (!sex.equals("M") && sex.equals("F")) {
				return false;
			}
			
			if (yearOfStudy < 0 || yearOfStudy > 5) {
				return false;
			}
			

			String insertStudentSQL = "INSERT INTO A2.student(sid, slastname, sfirstname, sex, age, dcode, yearofstudy) VALUES(?,?,?,?,?,?,?)";
			PreparedStatement ps = connection.prepareStatement(insertStudentSQL);
			ps.setInt(1, sid);
			ps.setString(2, lastName);
			ps.setString(3, firstName);
			ps.setString(4, sex);
			ps.setInt(5, age);
			ps.setString(6, dcode);
			ps.setInt(7, yearOfStudy);
			ps.executeUpdate();
			
			return true;
			
		} catch (SQLException e) {
			return false;
		
		}
	}

	public int getStudentsCount(int year) {
		try {
	
			if (year < 0 || year > 5) {
				return -1;
			}
			
			String sqlQuery = "SELECT * FROM A2.student WHERE student.yearofstudy = ?";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, year);
            ResultSet resultSet = ps.executeQuery(sqlQuery);
            
            int count = 0;
			
            while (resultSet.next()) {
            	count++;
            }
            
            return count;

		
		} catch (SQLException e) {
			return -1;

		}
	}

	public String getStudentInfo(int sid) {
		
		String finalS = "";
		
		try {
		
            String sqlQuery = "SELECT s.sid, avg(sc.grade) as averageGrade FROM A2.student AS s JOIN A2.studentCourse AS sc ON s.sid = sc.sid JOIN A2.department ON s.dcode = d.dcode WHERE student.sid = ? GROUP BY s.sid";
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, sid);
			ResultSet resultSet = ps.executeQuery(sqlQuery);
     
          	
            while (resultSet.next()) {
            	String currsid = resultSet.getString("sid");
            	String currfirstname = resultSet.getString("sfirstname");
            	String currlastname = resultSet.getString("slastname");
            	String currsex = resultSet.getString("sex");
            	String currage = resultSet.getString("age");
            	String curryearofstudy = resultSet.getString("yearofstudy");
            	String currdepartment = resultSet.getString("dname");
            	String curraveragegrade = resultSet.getString("averageGrade");
            	if (currsid.equals(sid)){
            		finalS = currfirstname+":"+currlastname+":"+currsex+":"+currage+":"+curryearofstudy+":"+currdepartment+":"+curraveragegrade;
            	}
            }
            
            return finalS;
            
		} catch (SQLException e) {
			return "";

		}
	}

	public boolean switchDepartment(int sid, String oldDcode, String newDcode) {
		
		Boolean deptExists = false;

		try {
			Statement statement = connection.createStatement();
			
			String sqlQuery = "SELECT dcode FROM A2.department";
		       
	        ResultSet resultSet = statement.executeQuery(sqlQuery);

	        while (resultSet.next()) {
	            String tempDCode = resultSet.getString("dcode");
	            if (tempDCode.equals(newDcode)){
	            	deptExists = true;
	            }
	        }
	        
	        if (!deptExists) {
				return false; 
			}
	        
			String insertStduentSQL = "UPDATE A2.student SET student.dcode = ? WHERE student.sid = ?";
			PreparedStatement ps = connection.prepareStatement(insertStduentSQL);
			ps.setString(1, newDcode);
			ps.setInt(2, sid);
			ps.executeUpdate();
			return true;
			
	        
		} catch (SQLException e) {
			return false;
		}
    
	}

	public boolean deleteDept(String dcode) {
		Boolean deptExists = false;

		try {
		
			String sqlQuery = "SELECT dcode FROM A2.department WHERE department.dcode = ?";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setString(1, dcode);
			ResultSet resultSet = ps.executeQuery(sqlQuery);
	     
	        while (resultSet.next()) {
	            String tempDCode = resultSet.getString("dcode");
	            if (tempDCode.equals(dcode)){
	            	deptExists = true;
	            }
	        }
	        
	        if (!deptExists) {
				return false; 
			}
	        
	        deptExists = false;
	        
	        sqlQuery = "SELECT dcode FROM A2.student  WHERE student.dcode = ?";
	        ps = connection.prepareStatement(sqlQuery);
			ps.setString(1, dcode);
			resultSet = ps.executeQuery(sqlQuery);

	        while (resultSet.next()) {
	            String tempDCode = resultSet.getString("dcode");
	            if (tempDCode.equals(dcode)){
	            	deptExists = true;
	            }
	        }
	        
	        if (deptExists) {
				return false; 
			}
	        
	        deptExists = false;
	        
	        sqlQuery = "SELECT dcode FROM A2.instructor WHERE instructor.dcode = ?";
	        ps = connection.prepareStatement(sqlQuery);
			ps.setString(1, dcode);
			resultSet = ps.executeQuery(sqlQuery);

	        while (resultSet.next()) {
	            String tempDCode = resultSet.getString("dcode");
	            if (tempDCode.equals(dcode)){
	            	deptExists = true;
	            }
	        }
	       
	        if (deptExists) {
				return false; 
			}
	        
	        deptExists = false;
	        
	        sqlQuery = "SELECT dcode FROM A2.course WHERE course.dcode = ?";
	        ps = connection.prepareStatement(sqlQuery);
			ps.setString(1, dcode);
			resultSet = ps.executeQuery(sqlQuery);

	        while (resultSet.next()) {
	            String tempDCode = resultSet.getString("dcode");
	            if (tempDCode.equals(dcode)){
	            	deptExists = true;
	            }
	        }
	        
	        
	        if (deptExists) {
				return false; 
			}

			String deleteDeptSQL = "DELETE FROM A2.department d WHERE d.dcode = ?";
			ps = connection.prepareStatement(deleteDeptSQL);
			ps.setString(1, dcode);
			ps.executeUpdate();
			return true;
			
		} catch (SQLException e) {
			return false;
		}
	}

	public String listCourses(int sid) {
        
		String finalS = "";

		try {
			
            String sqlQuery = "SELECT c.cname, d.dname, cs.semester, cs.year, sc.grade FROM A2.studentCourse AS sc ON s.sid = sc.sid JOIN A2.courseSection cs ON cs.csid = sc.csid JOIN A2.course c ON c.cid = sc.cid JOIN A2.department d ON d.dcode = c.dcode WHERE sc.sid = ?";
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, sid);
			ResultSet resultSet = ps.executeQuery(sqlQuery);
     
            String addRow = "";
			
            while (resultSet.next()) {
            	String currcname = resultSet.getString("cname");
            	String currdname = resultSet.getString("dname");
            	String currsemseter = resultSet.getString("semester");
            	String curryear = resultSet.getString("year");
            	String currgrade = resultSet.getString("grade");
           
            	addRow = currcname+":"+currdname+":"+currsemseter+":"+curryear+":"+currgrade+"#\n";
            	finalS+=addRow;
            }
            
            return finalS;
            
		} catch (SQLException e) {
			return "";

		}
	}

	public ArrayList<Integer> addPrereq(int cid, String dcode, int pcid, String pdcode) {
		
		ArrayList<Integer> sids = new ArrayList<Integer>();
		
		try {
			// check if exists before inserts
			
			String insertStudentSQL = "INSERT INTO A2.prerequisites(cid, dcode, pcid, pdcode) VALUES(?,?,?,?)";
			PreparedStatement ps = connection.prepareStatement(insertStudentSQL);
			ps.setInt(1, cid);
			ps.setString(2, dcode);
			ps.setInt(3, pcid);
			ps.setString(4, pdcode);
			ps.executeUpdate();
			
			String sqlQuery = "SELECT st.sid \n"
					+ "FROM student st \n"
					+ "JOIN studentCourse s1 ON st.sid = s1.sid\n"
					+ "JOIN courseSection cs1 ON s1.csid = cs1.csid\n"
					+ "JOIN prerequisites p ON p.cid = cs1.cid\n"
					+ "WHERE p.cid = ? AND p.dcode = ? AND p.pcid IN (SELECT cs2.cid\n"
					+ "  FROM studentCourse s2\n"
					+ "  JOIN courseSection cs2 ON s.csid = cs2.csid\n"
					+ "  JOIN prerequisites p ON p.cid = cs2.cid\n"
					+ "  WHERE s1.sid = s2.sid \n"
					+ "  AND cs1.year <= cs2.year\n"
					+ "  AND cs1.semester < cs2.semester)";
			
            ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, cid);
			ps.setString(2, dcode);
			ResultSet resultSet = ps.executeQuery(sqlQuery);
			
			 while (resultSet.next()) {
            	int sid = Integer.parseInt(resultSet.getString("sid"));
            	sids.add(sid);
            }
			
			 return sids;
			
			
		} catch (SQLException e) {
			return sids;
		}
		
	}

	public boolean updateDB() {
		 try {
			 Statement statement = connection.createStatement();
			 String createTableSQL = "CREATE TABLE IF NOT EXISTS maleStudentsInCS ("
			 		+ "sid INTEGER,"
			 		+ "fname CHAR(20),"
			 		+ "lname CHAR(20),"
			 		+ "PRIMARY KEY (sid)"
			 		+ ")";
	         statement.execute(createTableSQL);
	         
	         String insertStudentSQL = "INSERT INTO maleStudentsInCS (sid, fname, lname)\n"
	         		+ "SELECT s.sid, s.sfirstname AS fname, s.slastname AS lname"
	         		+ "FROM student s "
	         		+ "JOIN department d ON s.dcode = d.dcode"
	         		+ "WHERE d.dname = 'Computer Science'"
	         		+ "AND s.sex = 'M'"
	         		+ "AND s.yearofstudy = 2";
			PreparedStatement ps = connection.prepareStatement(insertStudentSQL);
			ps.executeUpdate();
			return true;
	         
		} catch (SQLException e) {
			return false;
		}
	}
}
