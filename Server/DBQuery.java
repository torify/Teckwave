import java.io.PrintStream;import java.sql.*;import java.util.Properties;
		    
       sDbDrv = "com.mysql.jdbc.Driver";	   
        		System.out.println("Kann keine Verbindung zum DB-Server aufbauen");	        }    
	System.out.println(query);        try        {	            st = cn.createStatement();            if(query.substring(0,6).equals("SELECT"))                rs = st.executeQuery(query);            else                st.executeUpdate(query);	    cn.clearWarnings();
        }        catch(Exception e)
    private String sDbDrv;    private String sDbUrl;    private String sUsr;    private String sPwd;}