import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Sql {

	private static Statement ste = null;
    private static Connection con = null;
   // private static DefaultTableModel tableModel;
    
	public Sql(){
		String uri = 
        //"jdbc:mysql://localhost:3306/hotel?useSSL=true&characterEncoding=utf-8&serverTimezone=UTC";
        "jdbc:sqlite:snake_score.db";
        try{
            con = DriverManager.getConnection(uri);
            System.out.println("Database connected");
        }
        catch(SQLException e){
            System.out.println(e);
        }
	}

	public static int executeUpdate(String sql) {
//		System.out.println ("Update SQL : " + sql);
		int i = 0 ;
       	if(con == null) 
            return -1;
		try {
            con.setAutoCommit(false);
            ste = con.createStatement();
			i = ste.executeUpdate(sql) ;
            //con.setAutoCommit(false); 
			con.commit();
            con.setAutoCommit(true);
		}catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace() ;
		}//End try
		return i ;
	}
        
       
    public static ResultSet executeQuery(String sql) {
//		System.out.println ("Query SQL : " + sql);  
		ResultSet rs = null ;
		try {
                    ste = con.createStatement();
                    rs = ste.executeQuery(sql) ;
		}catch(Exception e) {
                    JOptionPane.showMessageDialog(null, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace() ;
		}//End try
		return rs ;
	}
    
//    public static int recCount(ResultSet rrs) {
//		int i = 0;
//		try {
//			if(rrs.getRow() != 0)
//				rrs.beforeFirst();
//			//while用于计算rs的记录条数
//			while(rrs.next())
//				i++;
//			rrs.beforeFirst();	
//	    }catch(Exception ex) {
//                JOptionPane.showMessageDialog(null, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
//	    	ex.printStackTrace();
//	    }//End try
//		return i;
//    }
    
//    public static void initDTM (DefaultTableModel fdtm, String sqlCode) {
//    	try {
//            ResultSet rs = executeQuery(sqlCode);	//获得结果集
//            int row = recCount(rs);			//获得结果集中有几行数据
//            ResultSetMetaData rsm =rs.getMetaData();	//获得列集
//            int col = rsm.getColumnCount();		//获得列的个数
//            String colName[] = new String[col];
//            //取结果集中的表头名称, 放在colName数组中
//            for (int i = 0; i < col; i++) {
//            	colName[i] = rsm.getColumnName( i + 1 );
//            }//End for
//            rs.beforeFirst();
//            String data[][] = new String[row][col];
//            //取结果集中的数据, 放在data数组中
//            for (int i = 0; i < row; i++) {
//                rs.next();
//                for (int j = 0; j < col; j++) {
//                	data[i][j] = rs.getString (j + 1);
//                }
//            }//End for
//            fdtm.setDataVector (data, colName);
//    	}
//    	catch (Exception ex) {
//                JOptionPane.showMessageDialog(null, "Table Initialize failed ...", "Error", JOptionPane.ERROR_MESSAGE);
//                System.out.println ("sunsql.initDTM (): false");
//    	}//End try
//    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Sql sql1 = new Sql();
		
		//Create Table
//		String sql = "CREATE TABLE SCORE " +
//                "(SCORE INT  		NOT NULL," +
//                " NAME  CHAR(50)    NOT NULL)"; 
//		sql1.executeUpdate(sql);
		
		//Insert data
		String insert_sql  = "insert into SCORE(NAME,score) values('vax',2020)";
		sql1.executeUpdate(insert_sql);
		
		//del
//		String del_sql = "Delete from SCORE";
//		sql1.executeUpdate(del_sql);
		
		//select
		ResultSet resultSet = null;
		String select_sql;
		//select_sql = "Select count(Name) from SCORE";
		select_sql = "Select name,score from SCORE order by score DESC";
		resultSet  = sql1.executeQuery(select_sql);
		// Iterate through the result and print the student names
	    try {
			while (resultSet.next())
			  System.out.println(resultSet.getString(1) + "\t" +
			    resultSet.getString(2));
			//System.out.println(resultSet.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		}

}
