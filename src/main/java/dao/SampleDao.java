package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SampleDao {
	public static void main(String[] args) {
		String msg = "";
		try {
			// ドライバロード
			Class.forName("com.mysql.jdbc.Driver");

			// MySQL に接続
			Connection con = DriverManager.getConnection("jdbc:mysql://18.222.110.255:3306/teama", "user", "user");

			// ステートメント生成
			Statement stmt = con.createStatement();

			// SQL を実行
			String sqlStr = "SELECT * FROM product";
			ResultSet rs = stmt.executeQuery(sqlStr);

			// 結果行をループ
			while(rs.next()){
				// レコードの値
				int id = rs.getInt("id");
				String name = rs.getString("name");

				//表示
				System.out.println(id + ":" + name);
			}

			// 接続を閉じる
			rs.close();
			stmt.close();
			con.close();
		}catch (ClassNotFoundException e){
			msg = "ドライバのロードに失敗しました";
			System.out.println(msg);
		}catch (Exception e){
			msg = "ドライバのロードに失敗しました";
			System.out.println(msg);
		}
	}
}
