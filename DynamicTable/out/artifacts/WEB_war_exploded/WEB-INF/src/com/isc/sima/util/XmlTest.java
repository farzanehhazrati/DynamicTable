package com.isc.sima.util;

import com.isc.sima.dio.Xml;

/**
 * Created with IntelliJ IDEA.
 * User: b_raeisifard
 * Date: 12/10/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlTest {
    public static void main(String[] args) {
        Xml xml = new Xml();
        xml.loadXml("resource/setting.xml");
        System.out.println(xml.getChartFieldsDescription()+" "+xml.getChartFieldsName()+" "+xml.getChartSeriesNo()+" "+
            xml.getChartTitle()+" "+xml.getQuery());
        xml.saveXml("E:\\liferay-plugins-sdk-6.1.2\\portlets\\Shaparak\\highstock\\docroot\\WEB-INF\\src",
                "/resource/setting.xml","ChartFieldsDescription","ChartFieldsName","ChartSeriesNo","ChartTitle","Query");
    }
}
