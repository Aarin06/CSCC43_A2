import java.sql.*;
import java.util.ArrayList;

public class Assignment2 {

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
		} catch (ClassNotFoundException e) {
		  e.printStackTrace();
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
				return true;
			}else {
				return false;
			}
		}catch (Exception e){
			return false;
		}
	}

	public boolean disconnectDB() {
		try {
      connection.close();
      return true;
    } catch (Exception e) {
      return false;
    }

	}
  //what to do if student already exists check piazza
	public boolean insertStudent(int sid, String lastName, String firstName,
			String sex, int age, String dcode, int yearOfStudy) {
		try {
			Statement statement = connection.createStatement();
			 
			String sqlQuery = "SELECT dcode FROM A2.department";
			
			ResultSet resultSet = statement.executeQuery(sqlQuery);
			
			boolean validDcode = false;
			
			while (resultSet.next()) {
        String dcodeVal = resultSet.getString("dcode").trim();
        if (dcode.equals(dcodeVal)) {
          validDcode = true;
        }
      }
			statement.close();
			resultSet.close();
			
			if (validDcode && (sex.equals("M") || sex.equals("F")) && yearOfStudy > 0 && yearOfStudy < 5) {
				String sqlUpdate = "INSERT INTO A2.student (sid, slastname, sfirstname, sex, age, dcode, yearofstudy) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sqlUpdate);
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
			}
      return false;
		} catch (Exception e) {
        return false;
		}
	}

	public int getStudentsCount(int year) {
		try {
			if (year <= 0 || year >= 5) {
				return -1;
			}
			String sqlQuery = "SELECT count(*) AS studentCount FROM A2.student WHERE yearofstudy = ?";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, year);
			
			ResultSet resultSet = ps.executeQuery();
			int studentCount = 0;
			if (resultSet.next()) {
				studentCount = resultSet.getInt("studentCount");
			}
			resultSet.close();
			ps.close();
			return studentCount;
		} catch (Exception e) {
			return -1;
		}
		
	}

  //what to do if student has no classes hence no avg bc they are not returned
	public String getStudentInfo(int sid) {
		
    try {
			String sqlQuery = "SELECT s.sid, s.slastname, s.sfirstname, s.sex, s.age, d.dname, s.yearofstudy, AVG(sc.grade) AS average "
					+ "FROM A2.student s JOIN A2.studentCourse sc ON s.sid = sc.sid "
					+ "JOIN A2.department d ON d.dcode = s.dcode "
					+ "WHERE s.sid = ? "
          + "GROUP BY s.sid, s.slastname, s.sfirstname, s.sex, s.age, d.dname, s.yearofstudy";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, sid);
						
			ResultSet resultSet = ps.executeQuery();
			
			String output = "";
			if (resultSet.next()) {
				String lname = resultSet.getString("slastname").trim();
				String fname = resultSet.getString("sfirstname").trim();
				String sex = resultSet.getString("sex").trim();
				int age = resultSet.getInt("age");
				String dname = resultSet.getString("dname").trim();
				int yos = resultSet.getInt("yearofstudy");
				int average = resultSet.getInt("average");
				output = fname + ":" + lname + ":" + sex + ":" + String.valueOf(age) + ":" + String.valueOf(yos) + ":" + dname + ":" + String.valueOf(average);			
			}
			resultSet.close();
			ps.close();
			
			return output;
		} catch (Exception e) {
			return "";
		}
	}

	public boolean switchDepartment(int sid, String oldDcode, String newDcode) {
		try {
			String sqlQuery = "SELECT dcode FROM A2.student s WHERE s.sid = ?";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, sid);
			
			ResultSet resultSet = ps.executeQuery();
			
			if (resultSet.next()) {
				String curDcode = resultSet.getString("dcode").trim();
				if (!curDcode.equals(oldDcode)) {
					return false;
				}
			}
			
			boolean newDcodeExists = false;
			sqlQuery = "SELECT dcode FROM A2.department";
			ps = connection.prepareStatement(sqlQuery);
			
			resultSet = ps.executeQuery();
			
			while (resultSet.next()) {
				String curDcode = resultSet.getString("dcode").trim();
				if (curDcode.equals(newDcode)) {
					newDcodeExists = true;
				}
			}
			
			if (!newDcodeExists) {
				return false;
			}

			String sqlUpdate = "UPDATE A2.student SET dcode = ? WHERE sid = ?";
			ps = connection.prepareStatement(sqlUpdate);
			ps.setString(1, newDcode);
			ps.setInt(2, sid);
			
			int rowsAffected = ps.executeUpdate();
			
      resultSet.close();
			ps.close();
			return rowsAffected == 1;
			
		} catch (Exception e) {
			return false;
		}
		
	}

	public boolean deleteDept(String dcode) {
		try {
			boolean dcodeExists = false;
			String sqlQuery = "SELECT dcode FROM A2.department";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			
			ResultSet resultSet = ps.executeQuery();
			
			while (resultSet.next()) {
				String curDcode = resultSet.getString("dcode").trim();
				if (curDcode.equals(dcode)) {
					dcodeExists = true;
				}
			}
			
			if (!dcodeExists) {
				return false;
			}
			
			boolean dcodeReferenced = false;
			sqlQuery = "(SELECT dcode FROM A2.student)"
					+ "UNION"
					+ "(SELECT dcode FROM A2.instructor)"
					+ "UNION"
					+ "(SELECT dcode FROM A2.course)";
			ps = connection.prepareStatement(sqlQuery);
			
			resultSet = ps.executeQuery();
			
			while (resultSet.next()) {
				String curDcode = resultSet.getString("dcode").trim();
				if (curDcode.equals(dcode)) {
					dcodeReferenced = true;
				}
			}
			
			if (dcodeReferenced) {
				return false;
			}
			
			String sqlDelete = "DELETE FROM A2.department WHERE dcode = ?";
			ps = connection.prepareStatement(sqlDelete);
			ps.setString(1, dcode);
			
			int rowsAffected = ps.executeUpdate();
	
			ps.close();
			resultSet.close();
			return rowsAffected == 1;
			
		} catch (Exception e) {
			  return false;
		}
	}

	public String listCourses(int sid) {
		try {
			String sqlQuery = "SELECT c.cname, d.dname, cs.semester, cs.year, sc.grade FROM A2.courseSection cs "
					+ "JOIN A2.studentCourse sc ON cs.csid = sc.csid "
					+ "JOIN A2.course c ON c.cid = cs.cid "
					+ "JOIN A2.department d ON d.dcode = cs.dcode "
					+ "WHERE sc.sid = ? ORDER BY sc.grade;";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, sid);
						
			ResultSet resultSet = ps.executeQuery();
			
			String output = "";
			while (resultSet.next()) { 
				String cname = resultSet.getString("cname").trim();
				String dname = resultSet.getString("dname").trim();
				int semester = resultSet.getInt("semester");
				int year = resultSet.getInt("year");
				int grade = resultSet.getInt("grade");
				output += cname + ":" + dname + ":" + String.valueOf(semester) + ":" + String.valueOf(year) + ":" + String.valueOf(grade) + "\n";		
			}
			resultSet.close();
			ps.close();
			
			return output;
		} catch (Exception e) {
			return "";
		}
	}
  // check this again
	public ArrayList<Integer> addPrereq(int cid, String dcode, int pcid, String pdcode) {
		try {
			boolean courseValid = false;
			boolean prereqValid = false;
			String sqlQuery = "SELECT cid, dcode FROM A2.course";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			
			ResultSet resultSet = ps.executeQuery();
			
			while (resultSet.next()) {
				int curCid = resultSet.getInt("cid");
				String curDcode = resultSet.getString("dcode").trim();
				if (curDcode.equals(dcode) && curCid == cid) {
					courseValid = true;
				}
				if (curDcode.equals(pdcode) && curCid == pcid) {
					prereqValid = true;
				}
			}
			
			if (!courseValid || !prereqValid) {
				return new ArrayList<Integer>();
			}
			
			String sqlInsert = "INSERT INTO A2.prerequisites (cid, dcode, pcid, pdcode) VALUES (?, ?, ?, ?)";
			ps = connection.prepareStatement(sqlInsert);
			ps.setInt(1, cid);
			ps.setString(2, dcode);
			ps.setInt(3, pcid);
			ps.setString(4, pdcode);
			
			int rowsAffected = ps.executeUpdate();
			
			if ( rowsAffected != 1) {
				return new ArrayList<Integer>();
			}
			
			sqlQuery = "(SELECT sc.sid"
					+ "FROM studentCourse sc "
					+ "JOIN courseSection cs "
					+ "ON sc.csid = cs.csid "
					+ "WHERE cs.cid = ? AND cs.dcode = ?) "
					+ "EXCEPT"
					+ "(SELECT sc.sid"
					+ "FROM studentCourse sc "
					+ "JOIN courseSection cs "
					+ "ON sc.csid = cs.csid "
					+ "WHERE cs.cid = ? AND cs.dcode = ?)";
			
			ps = connection.prepareStatement(sqlQuery);
			ps.setInt(1, cid);
			ps.setString(2, dcode);
			ps.setInt(3, pcid);
			ps.setString(4, pdcode);
			
			resultSet = ps.executeQuery();
			
			ArrayList<Integer> res = new ArrayList<Integer>();
			while (resultSet.next()) {
				int sid = resultSet.getInt("sid");
				res.add(sid);
			}
			resultSet.close();
			ps.close();
			return res;
		} catch (Exception e) {
			return new ArrayList<Integer>();
		}
	}
//has a problem
	public boolean updateDB() {
		try {
			String createMaleStudentsInCS = "CREATE TABLE IF NOT EXISTS A2.maleStudentsInCS("
					+ "sid INTEGER,"
					+ "fname CHAR(20),"
					+ "lname CHAR(20),"
					+ ")";
			PreparedStatement ps = connection.prepareStatement(createMaleStudentsInCS);
			
			boolean createdTable = ps.execute();
			if (!createdTable){
				return false;
			}
			
			String sqlInsert = "INSERT INTO A2.maleStudentsInCS (sid, fname, lname) "
					+ "SELECT s.sid, s.sfirstname AS fname, s.slastname AS lname "
					+ "FROM A2.student s "
					+ "JOIN A2.department d "
					+ "ON s.dcode = d.dcode "
					+ "WHERE d.dname = 'Computer Science' AND s.yearofstudy = 2 AND s.sex = 'M'";
			
			ps = connection.prepareStatement(sqlInsert);
			int rowsAffected = ps.executeUpdate();
			ps.close();
			
			return rowsAffected >= 0; 
			
		} catch (Exception e) {
			return false;
		}
	}
}