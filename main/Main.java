import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main{
	public static void main(String[] args){
		JdbcTest jdbcTest = new JdbcTest();
		String filePath = "./log.txt"; //logFile path
		try(ServerSocket serverSocket = new ServerSocket(10004);){ //portNum
			while (true) {
				System.out.println("socket ServerStart");
				Socket clientSocket = serverSocket.accept();
				System.out.println("client connected");

				//client ipAddress
				InetAddress clientIpAdd = clientSocket.getInetAddress();

				//client connection processing
				BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
				String id = input.readLine();

				//connect with DB
				Connection conn = jdbcTest.getConnection();
				if (conn != null) {
					try {
						//query
						String sql = "SELECT UserName FROM socketTestDb.test_tbl WHERE UserId = ?;";
						PreparedStatement prstmt = conn.prepareStatement(sql);
						prstmt.setString(1, id);
						ResultSet rs = prstmt.executeQuery();

						try (FileWriter writer = new FileWriter(filePath, true)) {
							//create file 
							File logFile = new File(filePath);
							if (logFile.createNewFile()) {
								System.out.println("create log file" + logFile.getName());
							} else {
								System.out.println("logFile already created");
							}

							//timestamp
							LocalDateTime currentTime = LocalDateTime.now();
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
							String nowTime = currentTime.format(formatter);

							if (rs.next()) {
								String userName = rs.getString("UserName");
								output.println(userName);
								writer.write("Time : "+nowTime+", Client Ip Address : " + clientIpAdd + ", request : " + id + ", response : " + userName + "\n");
							} else {
								output.println("no user found");
								writer.write("Time : "+nowTime+", Client Ip Address : " + clientIpAdd + ", request : " + id + ", response : user not found\n");
							}
						} catch (IOException e) {
							System.out.println("create log file err" + e.getMessage());
						}
					} catch (Exception e) {
						System.out.println("dbc err :" + e.getMessage());
						e.printStackTrace();
					}
				} else {
					System.out.println("connection already closed");
				}
			}
		} catch (Exception e) {
			System.out.println("socket Server Exception" + e.getMessage());
		}
	}
}
