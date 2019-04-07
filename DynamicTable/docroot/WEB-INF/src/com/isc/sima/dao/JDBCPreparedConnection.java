package com.isc.sima.dao;


import com.isc.sima.dio.Setting;
import sepam.Encryptor;

import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.Date;
import org.apache.log4j.Logger;


public class JDBCPreparedConnection {
    private static Logger log = Logger.getLogger(JDBCPreparedConnection.class);
    String driver, url, user, password, query;
    Date date;
    Connection dbConnection = null;
    PreparedStatement preparedStatement = null;
    Setting setting = new Setting();
    static String[] projectClassPath = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("org.apache.catalina.jsp_classpath").toString().split(";")[0].split("/");
    static String imageUrl;
    String image;
    public JDBCPreparedConnection(){
        if(setting.isUseConnectionPoolChecked()){
      try {
        Context initialContext = new InitialContext();
        Context environmentContext = (Context) initialContext.lookup("java:comp/env");
        String dataResourceName = "jdbc/"+setting.getPropertiesPath().toUpperCase();  //SEPAMDB2
        DataSource dataSource = null;
            dataSource = (DataSource) environmentContext.lookup(dataResourceName);
            dbConnection = dataSource.getConnection();
            log.info("***************Using Connection Pool**************" + dataResourceName);

        } catch (Exception e) {
            log.info("Using Connection Pool ERRRRRRRROOOORRR!!!");
            e.printStackTrace();
            dbConnection = getConnection(setting.getPropertiesPath());
        }
        }else{
            dbConnection = getConnection(setting.getPropertiesPath());
        }


    };
    public JDBCPreparedConnection(String db){
        String dbName= setting.getPropertiesPath();
        if (db.equalsIgnoreCase("back")) {
            dbName = setting.getPropertiesPathBack();
        }


        if(setting.isUseConnectionPoolChecked()) {
            try {
                Context initialContext = new InitialContext();
                Context environmentContext = (Context) initialContext.lookup("java:comp/env");
                String dataResourceName = "jdbc/" + dbName.toUpperCase();  //SEPAMDB2
                DataSource dataSource = null;
                dataSource = (DataSource) environmentContext.lookup(dataResourceName);
                dbConnection = dataSource.getConnection();
                log.info(dbName + ":Using Connection Pool" + dataResourceName);

            } catch (Exception e) {
                log.info(dbName + ":Using Connection Pool ERRRRRRRROOOORRR!!!");
                e.printStackTrace();
                dbConnection = getConnection(dbName);
            }
        }else{
            dbConnection = getConnection(dbName);
        }

    };

    public ResultSet getResultSet(String query) {
        ResultSet rs = null;
        if (imageUrl == null)
            setImage(" ");
        else
            setImage(imageUrl);
        try {
            long t1 = System.currentTimeMillis();
            preparedStatement = dbConnection.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            long t2 = System.currentTimeMillis();
            long t = t2-t1;
            log.info(query+"\t"+t1+"\t"+t2+"\t"+t);

        } catch (SQLException e) {
            log.info("query= "+query);
            log.info(e.getMessage());

        }
        return rs;
    }

    public void close() {
        log.info("close connection" );
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }
        } catch (SQLException e) {

            log.info(e.getMessage());

        }
    }
//   ؟step1 load the driver class
//            Class.forName("oracle.jdbc.driver.OracleDriver");
//
//    //step2 create  the connection object
//    Connection con=DriverManager.getConnection(
//            "jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
//
//    //step3 create the statement object
//    Statement stmt=con.createStatement();

    public Connection getConnection(String serverName) {
        log.info("getConnection using propertiesFile ....");
        try {
            File f = new File(findPath(serverName));
            if (!f.exists() && f.length() < 0)
                log.info("The specified DB Connection file is not exist");
            else {
                FileInputStream finp = new FileInputStream(f);
                DataInputStream in = new DataInputStream(finp);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String DBUrl = br.readLine().replaceAll("DBURL=", "");
                String DBUserName = br.readLine().replaceAll("DBUSERNAME=", "");
                String DBPassword = br.readLine().replaceAll("DBPASSWORD=", "");

                DBPassword=new Encryptor().decrypt(DBPassword);
                imageUrl = br.readLine().replaceAll("IMAGEURL=", "");
                String DBDRIVER = br.readLine().replaceAll("DRIVER=", "");
                in.close();
                Class.forName(DBDRIVER);
                return DriverManager.getConnection(DBUrl, DBUserName, DBPassword);
            }

        } catch (Exception e) {
//            FacesUtils.addErrorMessage("اشكال در ارتباط با پايگاه داده");
            log.info(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  ");
            log.info(" >>>>>>>       connecting to database is failed !!!");
            log.info(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  ");


            e.printStackTrace();
        }

        return null;
    }

    private static String findPath(String fileName) {
        String path = "";
        for (String str : projectClassPath) {
            if (str.equals("WEB-INF")) {
                String lastPath="";
                if (!fileName.contains("resource")){
                    path +=str +File.separator+"resource"+ File.separator;
                    break;
                }
            } else {
                path +=  str+File.separator;
            }
        }
        String fileNameExt="";
        if (!fileName.endsWith(".properties"))
            fileNameExt=   ".properties";
        path += fileName + fileNameExt;

        //log.info(path);
        return path;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public Connection getDbConnection() {
        return dbConnection;
    }

    public void setDbConnection(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }
}

