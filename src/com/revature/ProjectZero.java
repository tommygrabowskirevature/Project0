package com.revature;

import java.sql.Connection;
import java.io.Console;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ProjectZero {

	private static Connection connection = null;
	private static Statement statement = null;
	private static PreparedStatement preparedStmt = null;
	private static ResultSet result = null;

	public static void connect() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/projectZero", "root", "s0M8RbV!*0hAnrl4");
	}

	public static int createUser() {
		int insertStatus = 0;

		Console console = System.console();

		Scanner userInput = new Scanner(System.in);
		String createUserQuery = "INSERT INTO users (`userFirstName`, `userLastName`, `userEmail`, `userPassword`) VALUES (?,?,?,?);";

		System.out.println("Enter your first name: ");
		String userFirstName = userInput.nextLine();

		System.out.println("Enter your last name: ");
		String userLastName = userInput.nextLine();

		System.out.println("Enter your email address: ");
		String userEmail = userInput.nextLine();

		char[] passwordArray = console.readPassword("Enter your secret password: ");
		String userPassword = String.valueOf(passwordArray);

		String createDBUserQuery = "CREATE USER `" + userEmail + "` @`localhost` IDENTIFIED BY '" + userPassword + "';";
		String permissionsQuery = "GRANT SELECT userFirstname ON users TO `" + userEmail + "`@`localhost`;";

		PreparedStatement pstmt;
		PreparedStatement pstmt2;
		PreparedStatement pstmt3;

		try {
			pstmt = connection.prepareStatement(createUserQuery);
			pstmt.setString(1, userFirstName);
			pstmt.setString(2, userLastName);
			pstmt.setString(3, userEmail);
			pstmt.setString(4, userPassword);

			pstmt2 = connection.prepareStatement(createDBUserQuery);
			pstmt3 = connection.prepareStatement(permissionsQuery);

			insertStatus = pstmt.executeUpdate() + pstmt2.executeUpdate();
			pstmt3.executeQuery();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		userInput.close();
		return insertStatus;
	}

	public static int userSignIn() {
		int something = 0;
		int input = 0;
		Scanner userInput = new Scanner(System.in);
		PreparedStatement pstmt;

		while (input >= 0) {
			System.out.println("Enter your email: ");
			String userEmail = userInput.nextLine();

			String seachForEmail = "SELECT userEmail FROM users WHERE users.userEmail = `" + userEmail + "`;";
			String checkPassword = "SELECT userPassword FROM users WHERE users.userEmail = `" + userEmail + "`;";
			try {
				pstmt = connection.prepareStatement(seachForEmail);
				pstmt.executeQuery();

				ResultSet rs = pstmt.executeQuery(checkPassword);

				if (seachForEmail == "") {
					System.out.println("Please enter a valid email: ");

				} else {
					System.out.println("Please enter your password: ");
					String userPassword = userInput.nextLine();

					if (userPassword != rs.getString(checkPassword)) {
						System.out.println("Invalid Password");
					} else {
						System.out.println("You're signed in!");
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		userInput.close();
		return something;
	}

	public static void checkBalance() {
		Scanner userInput = new Scanner(System.in);

		System.out.println("Enter account ID: ");
		int accountID = userInput.nextInt();

		try {
			String query = "select checkingbalance from checkingaccounts where checkingaccountid = " + accountID;

			Statement stmt = connection.createStatement();

			ResultSet rs;

			rs = stmt.executeQuery(query);
			System.out.println("Balance");
			System.out.println(rs.getInt("checkingBalance"));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		userInput.close();

	}

	public static void closeResource() {

		try {
			if (result != null) {
				result.close();
			}
			if (preparedStmt != null) {
				preparedStmt.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {

			System.out.println(e);
		}

	}

	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);
		int choice = 0;

		try {
			ProjectZero.connect();

			while (choice >= 0) {
				System.out.println("Welcome to the Totally Secure Bank!!!");
				System.out.println("1] Create account");
				System.out.println("2] Sign into account");
				System.out.println("3] Check balance");
				System.out.println("4] Exit");

				choice = input.nextInt();

				switch (choice) {
				case 1:
					ProjectZero.createUser();
					break;
				case 2:
					ProjectZero.userSignIn();
					break;

				case 3:
					ProjectZero.checkBalance();
				default:
					System.out.println("Please enter a value between [1-4]");
				}

			}
		} catch (Exception e) {
			System.out.println(e);
		}

		input.close();

	}

}
