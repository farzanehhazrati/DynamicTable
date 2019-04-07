package com.isc.sima.dio;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.ghasemkiani.util.DateFields;
import com.ghasemkiani.util.SimplePersianCalendar;
import com.isc.sima.dao.JDBCPreparedConnection;
import com.isc.sima.util.FacesMessageUtil;
import jalali.GregorianDate;
import jalali.JalaliDate;
import jalali.JalaliDateUtil;

/**
 * Created by IntelliJ IDEA.
 * User: b_raeisifard
 * Date: 12/28/12
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@RequestScoped
public class Setting implements Serializable {

    String query = null;
    String messageQuery = null;

    private String fullPath = null;
    private String propertiesPath = null;
    private String uniqueID;
    private String title;
    private String dataBaseType;
    private String title1;
    private String title2;
    private boolean showConfigPanel;
    private boolean useConnectionPoolChecked;
    private boolean dateInputChecked;
    private boolean dateConvertChecked;
    private String dataModel;
    private String propertiesPathBack = null;
    String interval;
    boolean hasInterval;
    boolean showExportButton;
    boolean showColumnToggler;
    boolean showColumnGroup;
    boolean showRowToggler;
    String dateType;
    boolean pausedEnabled = true;

    public Setting() {
        PortletRequest portletRequest = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        fullPath = portletRequest.getPortletSession().getPortletContext().getRealPath("") + "/WEB-INF/classes/";

        this.dataBaseType = portletRequest.getPreferences().getValue("dataBaseType", "DB2");
        this.propertiesPath = portletRequest.getPreferences().getValue("propertiesPath", "DB2SepamZ");
        this.query = portletRequest.getPreferences().getValue("query", "SELECT * FROM  WB_BANKINFO");
        this.columnsProperties = portletRequest.getPreferences().getValue("columnsProperties", "");
        this.headerProperties = portletRequest.getPreferences().getValue("headerProperties", "");
        this.uniqueID = portletRequest.getPreferences().getValue("uniqueID", "");
        this.title = portletRequest.getPreferences().getValue("title", "");
        this.dataModel = portletRequest.getPreferences().getValue("dataModel", "NORMAL");
        this.showConfigPanel = Boolean.parseBoolean(portletRequest.getPreferences().getValue("showConfigPanel", "true"));
        this.showColumnToggler = Boolean.parseBoolean(portletRequest.getPreferences().getValue("showColumnToggler", "true"));
        this.showRowToggler = Boolean.parseBoolean(portletRequest.getPreferences().getValue("showRowToggler", "true"));
        this.useConnectionPoolChecked = Boolean.parseBoolean(portletRequest.getPreferences().getValue("useConnectionPoolChecked", "true"));
        this.dateInputChecked = Boolean.parseBoolean(portletRequest.getPreferences().getValue("dateInputChecked", "false"));
        this.dateConvertChecked = Boolean.parseBoolean(portletRequest.getPreferences().getValue("dateConvertChecked", "false"));
        this.propertiesPathBack = portletRequest.getPreferences().getValue("propertiesPathBack", propertiesPath);
        this.interval = portletRequest.getPreferences().getValue("interval", "60");
        this.hasInterval = Boolean.parseBoolean(portletRequest.getPreferences().getValue("hasInterval", "false"));
        this.showExportButton = Boolean.parseBoolean(portletRequest.getPreferences().getValue("showExportButton", "true"));
        this.dateType = portletRequest.getPreferences().getValue("dateType", "ToDay");
        this.pausedEnabled = Boolean.parseBoolean(portletRequest.getPreferences().getValue("pausedEnabled", "true"));
        this.showColumnGroup= !showColumnToggler && !this.headerProperties.equals("");
    }

    public void saveSetting() {
        //  if (!columnsProperties.contains("Error")){
        new PortletPreferencesBackingBean().submit();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        // Get a list of portlet preference names.
        PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
        javax.portlet.PortletPreferences portletPreferences = portletRequest.getPreferences();

        try {
            portletPreferences.setValue("uniqueID", uniqueID);
            portletPreferences.setValue("title", title);
            portletPreferences.setValue("propertiesPath", propertiesPath);
            portletPreferences.setValue("dataBaseType", dataBaseType);
            portletPreferences.setValue("query", query);
            portletPreferences.setValue("columnsProperties", columnsProperties);
            portletPreferences.setValue("headerProperties", headerProperties);
            portletPreferences.setValue("dataModel", dataModel);
            portletPreferences.setValue("showConfigPanel", Boolean.toString(showConfigPanel));
            portletPreferences.setValue("showColumnToggler", Boolean.toString(showColumnToggler));
            portletPreferences.setValue("showRowToggler", Boolean.toString(showRowToggler));
            portletPreferences.setValue("useConnectionPoolChecked", Boolean.toString(useConnectionPoolChecked));
            portletPreferences.setValue("dateInputChecked", Boolean.toString(dateInputChecked));
            portletPreferences.setValue("dateConvertChecked", Boolean.toString(dateConvertChecked));
            portletPreferences.setValue("propertiesPathBack", propertiesPathBack);
            portletPreferences.setValue("interval", interval);
            portletPreferences.setValue("hasInterval", Boolean.toString(hasInterval));
            portletPreferences.setValue("showExportButton", Boolean.toString(showExportButton));
            portletPreferences.setValue("dateType", dateType);
            portletPreferences.setValue("pausedEnabled", Boolean.toString(pausedEnabled));
            portletPreferences.store();

        } catch (Exception e) {
            FacesMessageUtil.addGlobalUnexpectedErrorMessage(facesContext);
        }
        //   }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getMessageQuery() {
        return messageQuery;
    }

    public void setMessageQuery(String messageQuery) {
        this.messageQuery = messageQuery;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getPropertiesPath() {
        return propertiesPath;
    }

    public void setPropertiesPath(String propertiesPath) {
        this.propertiesPath = propertiesPath;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    String columnsProperties = "";
    String headerProperties = "";

    public String getColumnsProperties() {
        return columnsProperties;
    }

    public void setColumnsProperties(String columnsProperties) {
        this.columnsProperties = columnsProperties;
    }

    public void getMetaData() {
        String queryDate = "";
        String persianDate = getShTodayForWrite();
        if (this.getQuery().toUpperCase().contains("###")) {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            if (this.isDateConvertChecked()) {
                GregorianDate gDate = JalaliDateUtil.convertToGregorian(new JalaliDate(persianDate.replace('/', '-')));
                queryDate = gDate.getDate();
            } else
                queryDate = persianDate;
            query = this.getQuery().toUpperCase();
            if (query != null && !"".equals(queryDate)) {
                query = query.replaceAll("###", queryDate);
            }
        }
        String tempQuery = query.toUpperCase().replace("@ShToday".toUpperCase(), getShToday()).replace("@ShYesterday".toUpperCase(), getShYesterday()).replace("@shFirstDayOfCurrentMonth".toUpperCase(), getShFirstDayOfCurrentMonth()).replace("ShFirstDayOfCurrentYear".toUpperCase(), getShFirstDayOfCurrentYear());
        System.out.println("getMetaData = " + tempQuery);
        columnsProperties = "";
        try {
            JDBCPreparedConnection jdbcPreparedConnection = new JDBCPreparedConnection();
            //ResultSet rs1 = jdbcPreparedConnection.getResultSet(tempQuery);
            ResultSet rs1 = null;
            try {
                PreparedStatement preparedStatement = jdbcPreparedConnection.getDbConnection().prepareStatement(tempQuery);
                rs1 = preparedStatement.executeQuery();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                columnsProperties = "SQLException:\n";
                columnsProperties += e.getMessage();
                //e.printStackTrace();
            }
            if (rs1 != null) {
                ResultSetMetaData resultSetMetaData = rs1.getMetaData();
                for (int cc = 1; cc < resultSetMetaData.getColumnCount() + 1; cc++) {
                    //System.out.println("x.getColumnName(cc) = " + resultSetMetaData.getColumnName(cc));
                    columnsProperties += resultSetMetaData.getColumnName(cc) + ":" + resultSetMetaData.getColumnTypeName(cc) + ":" + resultSetMetaData.getColumnName(cc) + ":" + "true" + ":" + "true" + "\n";
                }
            }

            jdbcPreparedConnection.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    public String getShTodayForWrite() {
        SimplePersianCalendar pCal = new SimplePersianCalendar();
        pCal.add(SimplePersianCalendar.SECOND, 12600);
        pCal.add(SimplePersianCalendar.DAY_OF_MONTH, 0);
        DateFields shDate = pCal.getDateFields();
        return String.format("%d/%02d/%02d", shDate.getYear(), shDate.getMonth() + 1, shDate.getDay());
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getValue(String db,String query) {
        String tiltleValue = "";
        try {
            JDBCPreparedConnection jdbcPreparedConnection = new JDBCPreparedConnection(db);
            ResultSet rs1 = jdbcPreparedConnection.getResultSet(query);
            if (rs1 != null) {
                ResultSetMetaData resultSetMetaData = rs1.getMetaData();
                while (rs1.next()) {
                    for (int cc = 1; cc < resultSetMetaData.getColumnCount() + 1; cc++) {
                        // System.out.println((resultSetMetaData.getColumnLabel(cc)+"========"+ rs1.getString(cc)));
                        tiltleValue += " " + rs1.getString(cc);
                    }
                }
            }
            jdbcPreparedConnection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tiltleValue;
    }

    public String getTitle1() {
        String[] titleSplit = title.split("\\|");
        if (titleSplit.length > 1) {
            title1 = titleSplit[0];
            title2 = titleSplit[1];
        } else {
            title1 = title;
            title2 = "";
        }
        return title1;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(String dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public String getDataModel() {
        return dataModel;
    }

    public void setDataModel(String dataModel) {
        this.dataModel = dataModel;
    }

    public String getShYesterday() {
        SimplePersianCalendar pCal = new SimplePersianCalendar();
        pCal.add(SimplePersianCalendar.SECOND, 12600);
        pCal.add(SimplePersianCalendar.DAY_OF_MONTH, -1);
        DateFields shDate = pCal.getDateFields();
        return String.format("%d%02d%02d", shDate.getYear(), shDate.getMonth() + 1, shDate.getDay());
    }

    public String getShToday() {
        SimplePersianCalendar pCal = new SimplePersianCalendar();
        pCal.add(SimplePersianCalendar.SECOND, 12600);
        pCal.add(SimplePersianCalendar.DAY_OF_MONTH, 0);
        DateFields shDate = pCal.getDateFields();
        return String.format("%d%02d%02d", shDate.getYear(), shDate.getMonth() + 1, shDate.getDay());
    }

    public String getShFirstDayOfCurrentMonth() {
        SimplePersianCalendar pCal = new SimplePersianCalendar();
        pCal.add(SimplePersianCalendar.SECOND, 12600);
        pCal.add(SimplePersianCalendar.DAY_OF_MONTH, 0);
        DateFields shDate = pCal.getDateFields();
        return String.format("%d%02d%02d", shDate.getYear(), shDate.getMonth() + 1, 1);
    }

    public String getShFirstDayOfCurrentYear() {
        SimplePersianCalendar pCal = new SimplePersianCalendar();
        pCal.add(SimplePersianCalendar.SECOND, 12600);
        pCal.add(SimplePersianCalendar.DAY_OF_MONTH, 0);
        DateFields shDate = pCal.getDateFields();
        return String.format("%d%02d%02d", shDate.getYear(), 1, 1);
    }

    public boolean isShowConfigPanel() {
        return showConfigPanel;
    }

    public void setShowConfigPanel(boolean showConfigPanel) {
        this.showConfigPanel = showConfigPanel;
    }

    public boolean isShowColumnToggler() {
        return showColumnToggler;
    }

    public boolean isShowRowToggler() {
        return showRowToggler;
    }

    public void setShowRowToggler(boolean showRowToggler) {
        this.showRowToggler = showRowToggler;
    }

    public void setShowColumnToggler(boolean showColumnToggler) {
        this.showColumnToggler = showColumnToggler;
    }

    public boolean isUseConnectionPoolChecked() {
        return useConnectionPoolChecked;
    }

    public void setUseConnectionPoolChecked(boolean useConnectionPoolChecked) {
        this.useConnectionPoolChecked = useConnectionPoolChecked;
    }

    public boolean isDateInputChecked() {
        return dateInputChecked;
    }

    public void setDateInputChecked(boolean dateInputChecked) {
        this.dateInputChecked = dateInputChecked;
    }

    public boolean isDateConvertChecked() {
        return dateConvertChecked;
    }

    public void setDateConvertChecked(boolean dateConvertChecked) {
        this.dateConvertChecked = dateConvertChecked;
    }

    public String getPropertiesPathBack() {
        return propertiesPathBack;
    }

    public void setPropertiesPathBack(String propertiesPathBack) {
        this.propertiesPathBack = propertiesPathBack;
    }

    public boolean isHasInterval() {
        return hasInterval;
    }

    public void setHasInterval(boolean hasInterval) {
        this.hasInterval = hasInterval;
    }

    public boolean isShowExportButton() {
        return showExportButton;
    }

    public void setShowExportButton(boolean showExportButton) {
        this.showExportButton = showExportButton;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public boolean isPausedEnabled() {
        return pausedEnabled;
    }

    public void setPausedEnabled(boolean pausedEnabled) {
        this.pausedEnabled = pausedEnabled;
    }

    public String getHeaderProperties() {
        return headerProperties;
    }

    public void setHeaderProperties(String headerProperties) {
        this.headerProperties = headerProperties;
    }

    public boolean isShowColumnGroup() {
        return this.showColumnGroup= !showColumnToggler && !this.headerProperties.equals("");

    }

    public void setShowColumnGroup(boolean showColumnGroup) {
        this.showColumnGroup = showColumnGroup;
    }
}

