package a2;
import java.sql.*;
import java.util.*;

public class Assignment2 {

	

	//where to return false and empty string(inside catch or not)
	
	// A connection to the database.
	// This variable is kept public.
	public Connection connection;

	/*
	 * Constructor for Assignment2. Identifies the PostgreSQL driver using
	 * Class.forName() method.
	 */
	public Assignment2() {
		try {
			Class.forName("org.postgresql.Driver");
		}
		catch(Exception e) {
			 System.out.println("Failed to connect to the database.");
		}
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
		
		boolean deptExists = false;
		
		try {
			Statement statement = connection.createStatement();
           
			String sqlQuery = "SELECT dcode FROM A2.department";
           
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                String tempDCode = resultSet.getString("dcode");
                if (tempDCode.equals(dcode)){
                	deptExists = true;
                	break;
                }
            }
            statement.close();
            resultSet.close();
			
			if (!deptExists) {
				return false; 
			}
			
			if (!sex.equals("M") && !sex.equals("F")) {
				return false;
			}
			
			if (yearOfStudy <= 0 || yearOfStudy >= 5) {
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
			int rowsAffected = ps.executeUpdate();
			ps.close();
			return rowsAffected == 1;
			
		} catch (SQLException e) {
			System.out.println(e);
			return false;
		
		}
	}

	public int getStudentsCount(int year) {
		try {
	
			if (year <= 0 || year >= 5) {
				return -1;
			}
			
			String sqlQuery = "SELECT count(*) AS studentCount FROM A2.student WHERE student.yearofstudy = ?";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, year);
            ResultSet resultSet = ps.executeQuery(sqlQuery);
            
            int count = 0;
			
            while (resultSet.next()) {
            	count = resultSet.getInt("studentCount");
            }
            ps.close();
            resultSet.close();
            return count;
		
		} catch (SQLException e) {
			System.out.println(e);
			return -1;
		}
	}
	
	//dcode vs dname
	public String getStudentInfo(int sid) {
		
		String finalS = "";
		
		try {
		
            String sqlQuery = "SELECT s.sid, s.sfirstname, s.slastname, s.sex, s.age, s.yearofstudy, s.dname, avg(sc.grade) as averageGrade "
            		+ "FROM A2.student AS s JOIN A2.studentCourse AS sc ON s.sid = sc.sid JOIN A2.department ON s.dcode = d.dcode "
            		+ "WHERE student.sid = ? "
            		+ "GROUP BY s.sid, s.sfirstname, s.slastname, s.sex, s.age, s.yearofstudy, s.dname";
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, sid);
			ResultSet resultSet = ps.executeQuery(sqlQuery);
     
          	
            while (resultSet.next()) {
            	int currsid = resultSet.getInt("sid");
            	String currfirstname = resultSet.getString("sfirstname").trim();
            	String currlastname = resultSet.getString("slastname").trim();
            	String currsex = resultSet.getString("sex").trim();
            	int currage = resultSet.getInt("age");
            	int curryearofstudy = resultSet.getInt("yearofstudy");
            	String currdepartment = resultSet.getString("dname").trim();
            	int curraveragegrade = resultSet.getInt("averageGrade");
            	if (currsid == sid){
            		finalS = currfirstname+":"+currlastname+":"+currsex+":"+String.valueOf(currage)+":"+String.valueOf(curryearofstudy)+":"+currdepartment+":"+String.valueOf(curraveragegrade);
            	}
            }
            
            ps.close();
            resultSet.close();
            
            return finalS;
            
		} catch (SQLException e) {
			System.out.println(e);
			return finalS;
		}
	}

	public boolean switchDepartment(int sid, String oldDcode, String newDcode) {
		
		boolean deptExists = false;

		try {
			
			//checking if oldDcode is the current dcode of student
			
			String sqlQuery = "SELECT dcode FROM A2.student s WHERE s.sid = ?";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, sid);
			ResultSet resultSet = ps.executeQuery(sqlQuery);

	        while (resultSet.next()) {
	            String tempDCode = resultSet.getString("dcode").trim();
	            if (!tempDCode.equals(oldDcode)){
	            	return false;
	            }
	        }
	        
	        //checking if newDcode exists
			Statement statement = connection.createStatement();	        
			statement = connection.createStatement();
			sqlQuery = "SELECT dcode FROM A2.department";
	        resultSet = statement.executeQuery(sqlQuery);

	        while (resultSet.next()) {
	            String tempDCode = resultSet.getString("dcode");
	            if (tempDCode.equals(newDcode)){
	            	deptExists = true;
	            }
	        }
	        
	        if (!deptExists) {
				return false; 
			}
	        
	        //update student dept
			String insertStduentSQL = "UPDATE A2.student SET student.dcode = ? WHERE student.sid = ?";
			ps = connection.prepareStatement(insertStduentSQL);
			ps.setString(1, newDcode);
			ps.setInt(2, sid);
			int rowsAffected = ps.executeUpdate();
			
			ps.close();
			resultSet.close();
			statement.close();
			
			return rowsAffected == 1;
			
		} catch (SQLException e) {
			System.out.println(e);
			return false;
		}
    
	}

	public boolean deleteDept(String dcode) {
		boolean deptExists = false;

		try {
			
			//check if dcode exists in dept
			String sqlQuery = "SELECT dcode FROM A2.department d WHERE d.dcode = ?";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setString(1, dcode);
			ResultSet resultSet = ps.executeQuery(sqlQuery);
	     
	        while (resultSet.next()) {
	            String tempDCode = resultSet.getString("dcode").trim();
	            if (tempDCode.equals(dcode)){
	            	deptExists = true;
	            }
	        }
	        
	        if (!deptExists) {
				return false; 
			}
	        
	        //check if dcode is in any of the three tables
	        deptExists = false;
	        sqlQuery ="(SELECT dcode FROM A2.student)"
					+ "UNION"
					+ "(SELECT dcode FROM A2.instructor)"
					+ "UNION"
					+ "(SELECT dcode FROM A2.course)";;
	        ps = connection.prepareStatement(sqlQuery);
			resultSet = ps.executeQuery(sqlQuery);

	        while (resultSet.next()) {
	            String tempDCode = resultSet.getString("dcode").trim();
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
			int rowsAffected = ps.executeUpdate();
			
			ps.close();
			resultSet.close();
			
			
			return rowsAffected == 1;
			
		} catch (SQLException e) {
			System.out.println(e);
			return false;
		}
	}

	//dcode vs dname
	public String listCourses(int sid) {
        
		String finalS = "";

		try {
			
            String sqlQuery = "SELECT c.cname, d.dname, cs.semester, cs.year, sc.grade FROM A2.studentCourse AS sc"
            		+ "JOIN A2.courseSection cs ON cs.csid = sc.csid "
            		+ "JOIN A2.course c ON c.cid = sc.cid"
            		+ "JOIN A2.department d ON d.dcode = c.dcode"
            		+ "WHERE sc.sid = ?"
            		+ "ORDER BY sc.grade";
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, sid);
			ResultSet resultSet = ps.executeQuery(sqlQuery);
     
            String addRow = "";
			
            while (resultSet.next()) {
            	String currcname = resultSet.getString("cname").trim();
            	String currdname = resultSet.getString("dname").trim();
            	int currsemseter = resultSet.getInt("semester");
            	int curryear = resultSet.getInt("year");
            	int currgrade = resultSet.getInt("grade");
           
            	addRow = currcname+":"+currdname+":"+String.valueOf(currsemseter)+":"+String.valueOf(curryear)+":"+String.valueOf(currgrade)+"\n";
            	finalS+=addRow;
            }
            
            resultSet.close();
            ps.close();
            
            return finalS;
            
		} catch (SQLException e) {
			System.out.println(e);
			return "";
		}
	}

	public ArrayList<Integer> addPrereq(int cid, String dcode, int pcid, String pdcode) {
		
		ArrayList<Integer> sids = new ArrayList<Integer>();
		boolean courseValid = false;
		boolean prereqValid = false;
		
		try {
			// check if exists before inserts
			String sqlQuery = "SELECT c.dcode, c.cid FROM A2.course c";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ResultSet resultSet = ps.executeQuery(sqlQuery);

	        while (resultSet.next()) {
	            String currDCode = resultSet.getString("dcode").trim();
	            int currcid = resultSet.getInt("cid");
	            if (currDCode.equals(dcode) && currcid == cid){
	            	courseValid = true;
	            }
	            if (currDCode.equals(pdcode) && currcid == pcid){
	            	prereqValid = true;
	            }
	        }
	        
	        if (!courseValid || !prereqValid) {
	        	return new ArrayList<Integer>();
	        }
			
			String insertStudentSQL = "INSERT INTO A2.prerequisites(cid, dcode, pcid, pdcode) VALUES(?,?,?,?)";
			ps = connection.prepareStatement(insertStudentSQL);
			ps.setInt(1, cid);
			ps.setString(2, dcode);
			ps.setInt(3, pcid);
			ps.setString(4, pdcode);
			int rowsAffected = ps.executeUpdate();
			
			if (rowsAffected != 1) {
				return new ArrayList<Integer>();
			}
			
			
			sqlQuery = "SELECT sc.sid"
					+ "FROM studentCourse sc"
					+ "JOIN courseSection cs "
					+ "ON sc.csid = cs.csid"
					+ "WHERE cs.cid = ? AND cs.dcode = ?"
					+ "EXCEPT"
					+ "SELECT sc.sid"
					+ "FROM studentCourse sc"
					+ "JOIN courseSection cs "
					+ "ON sc.csid = cs.csid"
					+ "WHERE cs.cid = ? AND cs.dcode = ?";
			
            ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, cid);
			ps.setString(2, dcode);
			ps.setInt(1, pcid);
			ps.setString(2, pdcode);
			resultSet = ps.executeQuery(sqlQuery);
			while (resultSet.next()) {
				int sid = Integer.parseInt(resultSet.getString("sid"));
            	sids.add(sid);
            }
			
			resultSet.close();
			ps.close();
			
			
			return sids;
			
			
		} catch (SQLException e) {
			return new ArrayList<Integer>();
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
	         boolean created = statement.execute(createTableSQL);
	         
	         if (!created) {
	        	 return false;
	         }
	         
	         String insertStudentSQL = "INSERT INTO A2.maleStudentsInCS (sid, fname, lname)"
	         		+ "SELECT s.sid, s.sfirstname AS fname, s.slastname AS lname"
	         		+ "FROM student s "
	         		+ "JOIN department d ON s.dcode = d.dcode"
	         		+ "WHERE d.dname = 'Computer Science'"
	         		+ "AND s.sex = 'M'"
	         		+ "AND s.yearofstudy = 2";
			PreparedStatement ps = connection.prepareStatement(insertStudentSQL);
			ps.executeUpdate();
			
			ps.close();
			statement.close();
			
			return true;
	         
		} catch (SQLException e) {
			return false;
		}
	}
}
