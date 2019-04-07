package com.isc.sima.util;

import com.isc.sima.dio.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: b_raeisifard
 * Date: 12/10/13
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesTest {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.loadProperties("/resource/setting.properties");
        System.out.println(properties.getDriver()+" "+properties.getUrl()+" "+properties.getUsername()+" "+
            properties.getPassword());
        properties.saveProperties("E:\\liferay-plugins-sdk-6.1.2\\portlets\\Shaparak\\highstock\\docroot\\WEB-INF\\src",
                "/resource/setting.properties","driver01","url01","username01","password01");
        System.out.println(properties.getDriver()+" "+properties.getUrl()+" "+properties.getUsername()+" "+
                properties.getPassword());
    }
}
