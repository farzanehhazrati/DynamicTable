// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DBConnection.java

package entity.classes;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class DBConnection
{
    public static String[] projectClassPath = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("org.apache.catalina.jsp_classpath").toString().split(";")[0].split("/");

    public DBConnection()
    {
    }

    public static Connection getConnection(String serverName)
    {
        try
        {
            File f = new File(findPath(serverName));
            if(!f.exists() && f.length() < 0L)
            {
                System.out.println("The specified DB Connection file is not exist");
            } else
            {
                FileInputStream finp = new FileInputStream(f);
                DataInputStream in = new DataInputStream(finp);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String DBUrl = br.readLine().replaceAll("DBURL=", "");
                String DBUserName = br.readLine().replaceAll("DBUSERNAME=", "");
                String DBPassword = br.readLine().replaceAll("DBPASSWORD=", "");
                in.close();
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//                System.out.println("------------" + DBUrl);
                return DriverManager.getConnection(DBUrl, DBUserName, DBPassword);
            }
        }
        catch(Exception e)
        {
            System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  ");
            System.out.println(" >>>>>>>       connecting to database is failed !!!");
            System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  ");
            e.printStackTrace();
        }
        return null;
    }
    public static Connection getConnection(String DBUrl,String DBUserName,String DBPassword)
    {
        try
        {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(DBUrl, DBUserName, DBPassword);


        }
        catch (Exception e)
        {
//            FacesUtils.addErrorMessage("اشكال در ارتباط با پايگاه داده");
            System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  ");
            System.out.println(" >>>>>>>       connecting to database is failed !!!");
            System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  ");
            e.printStackTrace();
        }

        return null;
    }
    private static String findPath(String fileName)
    {
        String path = "";
        String arr$[] = projectClassPath;
        int len$ = arr$.length;
        int i$ = 0;
        do
        {
            if(i$ >= len$)
                break;
            String str = arr$[i$];
            if(str.equals("WEB-INF"))
            {
                path = (new StringBuilder()).append(path).append(str).append(File.separator).append("DBServers").append(File.separator).toString();
                break;
            }
            path = (new StringBuilder()).append(path).append(str).append(File.separator).toString();
            i$++;
        } while(true);
        path = (new StringBuilder()).append(path).append(fileName).append(".properties").toString();
        return path;
    }

}
