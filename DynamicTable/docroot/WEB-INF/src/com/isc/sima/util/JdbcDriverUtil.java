package com.isc.sima.util;

import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;


/**
 * @version 2.2
 * @author amirsam bahador
 **/
public class JdbcDriverUtil {
    public Connection connection;
    public Statement statement;
    public CallableStatement callableStatement = null;
    private boolean show_sql;

    /**
     *
     * @throws Exception
     * @return boolean
     * @param show_sql
     * @param pass
     * @param user
     * @param url
     * @param class_driver
     */
    public boolean login(String class_driver, String url, String user,
        String pass, boolean show_sql) throws Exception {
        Class.forName(class_driver);
        connection = DriverManager.getConnection(url, user, pass);
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        this.show_sql = show_sql;

        return true;
    }

    /**
     *
     * @throws Exception
     * @return ResultSet
     * @param sql
     */
    public ResultSet getSQLQueryContent(String sql) throws Exception {
        if (show_sql == true) {
            System.out.println(sql);
        }

        ResultSet resultset = statement.executeQuery(sql);

        return resultset;
    }

    /**
     *
     * @throws Exception
     * @return ResultSet
     * @param stp
     */
    public ResultSet getSTPQueryContent(String stp) throws Exception {
        if (show_sql == true) {
            System.out.println(stp);
        }
        //String getDBUSERByUserIdSql = "{call getDBUSERByUserId(?,?,?,?)}";
//        callableStatement = connection.prepareCall("{call stpMonStatToday('BMI')}");
        callableStatement = connection.prepareCall("{call "+stp+"}");

        callableStatement.execute();
        ResultSet resultset = callableStatement.getResultSet();

        return resultset;
    }
    /**
     *
     * @throws Exception
     * @return ResultSet
     * @param stp
     */
    public ResultSet getSTPQueryContent(String stp, String bankName) throws Exception {
        if (show_sql == true) {
            System.out.println(stp);
        }
        //String getDBUSERByUserIdSql = "{call getDBUSERByUserId(?,?,?,?)}";
//        callableStatement = connection.prepareCall("{call stpMonStatToday('BMI')}");
        callableStatement = connection.prepareCall("{call "+stp+"(?)}");
        callableStatement.setString(1, bankName);
        callableStatement.execute();
        ResultSet resultset = callableStatement.getResultSet();

        return resultset;
    }


    /**
     *
     * @throws Exception
     * @return boolean
     * @param sql
     */
    public boolean executeSQLQuery(String sql) throws Exception {
        if (show_sql == true) {
            System.out.println(sql);
        }

        statement.executeUpdate(sql);

        return true;
    }

    /**
     *
     * @throws Exception
     * @return boolean
     * @param sql
     */
    public boolean executeBatchSQLQuery(ArrayList sql)
        throws Exception {
        connection.setAutoCommit(false);

        int i = 0;

        while (i != sql.size()) {
            statement.addBatch(sql.get(i).toString());

            if (show_sql == true) {
                System.out.println(sql.get(i).toString());
            }

            i++;
        }

        statement.executeBatch();
        connection.commit();

        return true;
    }

    /**
     *
     * @throws Exception
     * @return boolean
     */
    public boolean commit() throws Exception {
        if (statement != null) {
				statement.close();
			}
        if (callableStatement != null) {
				callableStatement.close();
			}
        if (connection != null) {
				connection.close();
			}
        return true;
    }
}
