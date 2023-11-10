import java.sql.*;

public class Assignment2 {

	// A connection to the database.
	// This variable is kept public.
	public Connection connection;

	/*
	 * Constructor for Assignment2. Identifies the PostgreSQL driver using
	 * Class.forName() method.
	 */
	public Assignment2() {

	}

	/*
	 * Using the String input parameters which are the URL, username, and
	 * password, establish the connection to be used for this object instance.
	 * If a connection already exists, it will be closed. Return true if a new
	 * connection instance was successfully established.
	 */
	public boolean connectDB(String URL, String username, String password) {
		throw new RuntimeException("Function Not Implemented");
	}

	public boolean disconnectDB() {
		throw new RuntimeException("Function Not Implemented");
	}

	public boolean insertStudent(int sid, String lastName, String firstName,
			String sex, int age, String dcode, int yearOfStudy) {
		throw new RuntimeException("Function Not Implemented");
	}

	public int getStudentsCount(int year) {
		throw new RuntimeException("Function Not Implemented");
	}

	public String getStudentInfo(int sid) {
		throw new RuntimeException("Function Not Implemented");
	}

	public boolean switchDepartment(int sid, String oldDcode, String newDcode) {
		throw new RuntimeException("Function Not Implemented");
	}

	public boolean deleteDept(String dcode) {
		throw new RuntimeException("Function Not Implemented");
	}

	public String listCourses(int sid) {
		throw new RuntimeException("Function Not Implemented");
	}

	public ArrayList<Integer> addPrereq(int cid, String dcode, int pcid, String pdcode) {

	}

	public boolean updateDB() {
		throw new RuntimeException("Function Not Implemented");
	}
}
