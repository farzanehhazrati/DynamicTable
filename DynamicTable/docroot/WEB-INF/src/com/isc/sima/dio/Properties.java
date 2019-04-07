package com.isc.sima.dio;

import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import java.io.FileOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: b_raeisifard
 * Date: 12/10/13
 * Time: 1:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class Properties {
    String driver = null;
    String url = null;
    String username = null;
    String password = null;
    java.util.Properties properties = new java.util.Properties();

    public void loadProperties(String propertiesPath) {
        try {
            //load a properties file from class path, inside static method
            //properties.load(Connection.class.getClassLoader().getResourceAsStream(connPath));
            //For non-static method, use this :
            this.properties.load(getClass().getClassLoader().getResourceAsStream(propertiesPath));
            //The following also works but i don't like it for some reason.
            //properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(connPath));
            this.driver = properties.getProperty("driver");
            System.out.println("loadProperties : driver = " + driver);
            this.url = properties.getProperty("url");
            this.username = properties.getProperty("username");
            this.password = properties.getProperty("password");
        } catch (Exception e) {
            System.out.println("loadProperties"+e.getMessage());
        }
    }

    public void saveProperties(String fullPath, String propertiesPath,
                               String driver, String url, String username, String password) {
        try {
            FileOutputStream out = new FileOutputStream(fullPath + propertiesPath);
            this.properties.setProperty("driver", driver);
            System.out.println("saveProperties : driver = " + driver);
            this.properties.setProperty("url", url);
            this.properties.setProperty("username", username);
            this.properties.setProperty("password", password);
            this.properties.store(out, null);
            out.close();
        } catch (Exception e) {
            System.out.println("saveProperties: "+e.toString());
        }
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
