package com.isc.sima.txn;

import com.ghasemkiani.util.DateFields;
import com.ghasemkiani.util.SimplePersianCalendar;
import com.isc.sima.dao.JDBCPreparedConnection;
import com.isc.sima.dio.Setting;
import com.liferay.faces.bridge.filter.internal.PortletContextBridgeLiferayImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

import com.isc.sima.theme.Theme;
import com.isc.sima.theme.ThemeService;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;
import com.lowagie.text.*;


import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import jalali.GregorianDate;
import jalali.JalaliDate;
import jalali.JalaliDateUtil;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.primefaces.component.themeswitcher.ThemeSwitcher;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.*;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;


/**
 * Created with IntelliJ IDEA.
 * User: z_babakhani
 * Date: 10/2/13
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class DataTableView implements Serializable {
    private static Logger log = Logger.getLogger(DataTableView.class);
    private List<DataTableRow> dataTableRows;
    private Setting setting = new Setting();
    private String namespace;
    TreeMap<Integer, DataTableColumn> dataTableColumnTreeMap;
    private List<DataTableColumn> dataTableColumnList;
    private List<DataTableColumn> dataTableColumnsInExpansion;
    private List<Boolean> columnShowList;
    private HashMap<String, String> themeMap;
    User user;
    Boolean validUser = false;
    DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    SimpleDateFormat formatterTime = new SimpleDateFormat("yyyy-MM-dd");
    String query = "";
    boolean lazyLoadShow = false;
    boolean normalShow = true;
    private boolean tooltipShow;
    List<String> userAttributes;
    public String theme;
    int userPageSize = 30;
    private List<Theme> themes;
    private LazyDataModel<DataTableRow> lazyModel;
    String db = "online";
    String dateType;
    Date lastDate;
    String queryDate = "";
    String title;
    boolean paused = false;
    boolean pausedEnabled = false;
    boolean playEnabled = true;
    String interval;
    TreeMap<Integer, List<HeaderCell>> headerRowTreeMap = new TreeMap<Integer, List<HeaderCell>>();



    public String getNamespace() {
        if (namespace == null) {
            namespace = FacesContext.getCurrentInstance().getExternalContext().encodeNamespace("");
        }
        return namespace;
    }


    public DataTableView() {
        createrHeaderRowTreeMap();
        interval = setting.getInterval();
        pausedEnabled = setting.isPausedEnabled() && pausedEnabled;
        playEnabled = setting.isPausedEnabled() && playEnabled;
        System.out.println("pausedEnabled : " + pausedEnabled);
        System.out.println("playEnabled : " + playEnabled);
        //query = setting.getQuery().toUpperCase().replace("@ShToday".toUpperCase(), getShToday()).replace("@ShYesterday".toUpperCase(), getShYesterday()).replace("@shFirstDayOfCurrentMonth".toUpperCase(), getShFirstDayOfCurrentMonth()).replace("ShFirstDayOfCurrentYear".toUpperCase(), getShFirstDayOfCurrentYear());
        dateType = setting.getDateType();
        if (dateType.equalsIgnoreCase("today")) {
            lastDate = new Date();
            JalaliDate jDate = JalaliDateUtil.convertToJalali(new GregorianDate(lastDate.getTime()));
            queryDate = formatterTime.format(lastDate);
            persianDate = jDate.getDate().replace('-', '/');
        } else if (dateType.equalsIgnoreCase("YesterDay")) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            queryDate = formatterTime.format(cal.getTime());
            String yesterday = getShYesterday();
            persianDate = yesterday.substring(0, 4) + "/" + yesterday.substring(4, 6) + "/" + yesterday.substring(6, 8);
        } else {
            lastDate = new Date();
            JalaliDate jDate = JalaliDateUtil.convertToJalali(new GregorianDate(lastDate.getTime()));
            persianDate = jDate.getDate().replace('-', '/');
        }

        themeMap = new HashMap<String, String>();
        initTheme();
        theme = themeMap.get(getNamespace());
        tooltipShow = true;
        dataTableColumnTreeMap = new TreeMap<Integer, DataTableColumn>();
        userAttributes = new ArrayList<String>();
        dataTableRows = new ArrayList<DataTableRow>();
        dataTableColumnList = new ArrayList<DataTableColumn>();
        dataTableColumnsInExpansion = new ArrayList<DataTableColumn>();
        getNamespace();
        validUser = false;
        try {
            user = UserLocalServiceUtil.getUsers(0, 1).get(0);
        } catch (SystemException e) {
            e.printStackTrace();
        }
        try {
            if (FacesContext.getCurrentInstance().getExternalContext().getRemoteUser() != null) {
                user = UserLocalServiceUtil.getUserById(Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser()));
                validUser = true;
                log.info("logined User  " + user.getEmailAddress());
            } else
                user = UserLocalServiceUtil.getUsers(0, 1).get(0);
        } catch (PortalException e) {
            e.printStackTrace();
        } catch (SystemException e) {
            e.printStackTrace();
        }
        for (String str : user.getExpandoBridge().getAttributes().keySet()) {
            userAttributes.add(str);

        }
        columnShowList = createBooleanArrayList(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false);

        init();
        updateData();


    }

    private void createrHeaderRowTreeMap() {
        if (setting.getHeaderProperties() != null && !setting.getHeaderProperties().equals("")) {
            String[] headerRowProperties = setting.getHeaderProperties().split("\n");
            System.out.println("headerRowProperties.length = " + headerRowProperties.length);
            for (int headerRowIndex = 0; headerRowIndex < headerRowProperties.length; headerRowIndex++) {
                List<HeaderCell> headerCellList = new ArrayList<HeaderCell>();
                String[] tempCellList = headerRowProperties[headerRowIndex].split(";");
                for (int i = 0; i < tempCellList.length; i++) {
                    String[] tempCell=tempCellList[i].split(":");
                    String headerText= tempCell[0];
                    int colSpan=Integer.parseInt((tempCell[1]));
                    headerCellList.add(new HeaderCell(headerText, colSpan));
                }
                headerRowTreeMap.put(headerRowIndex, headerCellList);
            }
        }
    }

    public TreeMap<Integer, List<HeaderCell>> getHeaderRowTreeMap() {
        return headerRowTreeMap;
    }

    public void setHeaderRowTreeMap(TreeMap<Integer, List<HeaderCell>> headerRowTreeMap) {
        this.headerRowTreeMap = headerRowTreeMap;
    }



    public String getTitle() {
        return title;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void updateData() {
        if (!paused) {
            createData();
        }
    }

    public void pausedListener() {
        System.out.println("pausedListener....");
        paused = false;
        playEnabled = true;
        pausedEnabled = false;
        setInterval(setting.getInterval());
    }

    public void playListener() {
        System.out.println("playListener......");
        paused = true;
        pausedEnabled = true;
        playEnabled = false;
        setInterval("10000000");

    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPausedEnabled() {
        return (pausedEnabled && setting.isPausedEnabled());
    }


    public void setPausedEnabled(boolean pausedEnabled) {
        this.pausedEnabled = pausedEnabled;
    }

    public boolean isPlayEnabled() {
        return (playEnabled && setting.isPausedEnabled());
    }

    public void setPlayEnabled(boolean playEnabled) {
        this.playEnabled = playEnabled;
    }


    public void createData() {
        query=getCurentQuery(setting.getQuery()) ;
        db=getCurrentDB();
        title = setting.getTitle1();
        String titleQuery = getCurentQuery(setting.getTitle2());
        title +=  "  " + setting.getValue(db,titleQuery);

        if (setting.getDataModel().toUpperCase().equals("NORMAL")) {
            getData(query);
            lazyLoadShow = false;
            normalShow = true;
        } else {
            try {
                lazyModel = new LazyDataModel<DataTableRow>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public List<DataTableRow> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                        List<DataTableRow> result = getResultList(first, pageSize, sortField, sortOrder, filters);
                        lazyModel.setRowCount(count(sortField, sortOrder, filters));
                        return result;
                    }
                };
            } catch (Exception e) {
                e.getStackTrace();

            }
            lazyLoadShow = true;
            normalShow = false;
        }

    }


    private void init() {
        dataTableTotalColumns();
        if (validUser)
            if (setting.isShowConfigPanel())
                restoreConfig(false);

        dataTableColumnsInExpansion();
    }

    public LazyDataModel<DataTableRow> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<DataTableRow> lazyModel) {
        this.lazyModel = lazyModel;
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

    public String getShTodayForWrite() {
        SimplePersianCalendar pCal = new SimplePersianCalendar();
        pCal.add(SimplePersianCalendar.SECOND, 12600);
        pCal.add(SimplePersianCalendar.DAY_OF_MONTH, 0);
        DateFields shDate = pCal.getDateFields();
        return String.format("%d/%02d/%02d", shDate.getYear(), shDate.getMonth() + 1, shDate.getDay());
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

    public void getData(String query) {

        System.out.println("getdata dateTime1 = " + df.format(new Date()));
        // System.out.println("zQ = " + zQ);

        List<DataTableRow> dataTableRowList = new ArrayList<DataTableRow>();
        try {
            JDBCPreparedConnection jdbcPreparedConnection = new JDBCPreparedConnection(db);
            ResultSet rs1 = jdbcPreparedConnection.getResultSet(query);

            if (rs1 != null) {
                ResultSetMetaData resultSetMetaData = rs1.getMetaData();


                //while (rs1.next() && rs1.getRow()<10) {
                while (rs1.next()) {
                    DataTableRow dataTableRow = new DataTableRow();
                    HashMap hs = new HashMap();
                    for (int cc = 1; cc < resultSetMetaData.getColumnCount() + 1; cc++) {
                        //System.out.println("resultSetMetaData.getColumnTypeName("+cc+")=" + resultSetMetaData.getColumnTypeName(cc));
                        //System.out.println("rs1.getArray(cc) = " + rs1.getString(cc));
                        if (resultSetMetaData.getColumnTypeName(cc).equals("DECIMAL") || resultSetMetaData.getColumnTypeName(cc).equals("INTEGER") || resultSetMetaData.getColumnTypeName(cc).toUpperCase().contains("INT"))
                            hs.put(resultSetMetaData.getColumnLabel(cc), rs1.getLong(cc));
                        else
                            hs.put(resultSetMetaData.getColumnLabel(cc), rs1.getString(cc));
                    }
                    dataTableRow.setHs(hs);
                    dataTableRowList.add(dataTableRow);
                }
            }
            jdbcPreparedConnection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.dataTableRows = dataTableRowList;
        System.out.println("getdata dateTime2 = " + df.format(new Date()));


    }

    public int getDataSize(String query) {
        int rowCount = 0;

        //System.out.println("getDataSize dateTime1 = " + df.format(new Date()));
        // System.out.println("zQ = " + zQ);


        try {
            JDBCPreparedConnection jdbcPreparedConnection = new JDBCPreparedConnection(db);
            ResultSet rs1 = jdbcPreparedConnection.getResultSet(query);

            if (rs1 != null) {
                while (rs1.next()) {
                    rowCount = rs1.getInt("CNT");
                }
            }
            jdbcPreparedConnection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //System.out.println("getDataSize dateTime2 = " + df.format(new Date()));

        return rowCount;
    }


    public void dataTableTotalColumns() {
        if (setting.getColumnsProperties() != null && !setting.getColumnsProperties().equals("")) {
            String[] columnsProperties = setting.getColumnsProperties().split("\n");

            for (int row = 0; row < columnsProperties.length; row++) {
                //System.out.println("columnsProperties[row] = " + columnsProperties[row]);
                if (new DataTableColumn(columnsProperties[row]).isValid())

                    this.dataTableColumnTreeMap.put(row, new DataTableColumn(columnsProperties[row]));
            }

        }
        //columnShowList.set(0, true);
        for (Integer key : this.dataTableColumnTreeMap.keySet()) {

            columnShowList.set(key, false);
            if (this.dataTableColumnTreeMap.get(key).isShowInTable()) {
                columnShowList.set(key, true);
            }
        }
    }


    public void dataTableColumns() {
        List<DataTableColumn> dataTableColumnList = new ArrayList<DataTableColumn>();
        TreeMap<String, DataTableColumn> x = new TreeMap<String, DataTableColumn>();
        int i = 0;
        for (Integer str : this.dataTableColumnTreeMap.keySet()) {
            if (this.dataTableColumnTreeMap.get(str).isShowInTable()) {
                x.put(String.valueOf(i), this.dataTableColumnTreeMap.get(str));
                i++;
            }
        }
        dataTableColumnList.addAll(x.values());
        dataTableColumnList = dataTableColumnList;

    }

    public void dataTableColumnsInExpansion() {
        List<DataTableColumn> dataTableExpansionColumnList = new ArrayList<DataTableColumn>();

        for (Integer key : this.dataTableColumnTreeMap.keySet()) {
            dataTableExpansionColumnList.add(this.dataTableColumnTreeMap.get(key));
        }
        dataTableColumnsInExpansion = dataTableExpansionColumnList;
    }


    public void dataTableOnToggle(ToggleEvent e) {
        if (tempFirstColumn) {
            columnShowList.set(0, false);
            tempFirstColumn = false;
        }
        columnShowList.set((Integer) e.getData() - 1, e.getVisibility() == Visibility.VISIBLE);
    }


    public List<DataTableRow> getDataTableRows() {
        return dataTableRows;
    }

    public void setDataTableRows(List<DataTableRow> dataTableRows) {
        this.dataTableRows = dataTableRows;
    }

    public List<DataTableColumn> getDataTableColumnList() {
        return dataTableColumnList;
    }

    public void setDataTableColumnList(List<DataTableColumn> dataTableColumnList) {
        this.dataTableColumnList = dataTableColumnList;
    }

    public List<Boolean> getColumnShowList() {
        return columnShowList;
    }


    public void setColumnShowList(List<Boolean> columnShowList) {
        this.columnShowList = columnShowList;
    }

    public void handleToggle(ToggleEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Toggled", "Visibility:" + event.getVisibility());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }


    public String dateFormat(String date) {
        if (date.trim().equals(""))
            return date;
        else
            return date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
    }

    public String timeFormat(String time) {
        if (time.trim().equals(""))
            return time;
        else
            return time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
    }

    StreamedContent x;


    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
        tooltipShow = false;
        Document pdf = (Document) document;
        PortletContextBridgeLiferayImpl servletContext = (PortletContextBridgeLiferayImpl) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String fontPath = servletContext.getRealPath("") + File.separator + "resources" + File.separator + "tahoma.ttf";
        BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font tahomaFont = new Font(baseFont, 10, Font.NORMAL, Color.BLACK);
        Phrase phraseBefore = new Paragraph(setting.getTitle1(), tahomaFont);
        Phrase phraseAfter = new Paragraph("SEPAM Portal   " + getShTodayForWrite(), tahomaFont);
        HeaderFooter header = new HeaderFooter(phraseBefore, true);
        HeaderFooter footer = new HeaderFooter(phraseAfter, true);
        // pdf.setHeader(header);
        pdf.setFooter(footer);
        pdf.setPageSize(PageSize.A4.rotate());


        String logo = servletContext.getRealPath("") + File.separator + "resources" + File.separator + "images" + File.separator + "ISC.jpg";
        mergeImageAndText(logo, setting.getTitle1(), new Point(50, 30), "so2.jpg");
        Image logo1 = Image.getInstance("so2.jpg");
        logo1.setAlignment(Image.MIDDLE);
        logo1.scaleAbsoluteHeight(20);
        logo1.scaleAbsoluteWidth(20);
        logo1.scalePercent(100);
        Chunk chunk = new Chunk(logo1, 0, -20);
        HeaderFooter header1 = new HeaderFooter(new Phrase(chunk), false);
        header1.setAlignment(Element.ALIGN_CENTER);
        header1.setBorder(Rectangle.NO_BORDER);
        pdf.setHeader(header1);
        int columnCount = -1;
        for (boolean b : columnShowList)
            if (b)
                columnCount++;
        if (columnCount == -1) {
            columnShowList.set(0, true);
            tempFirstColumn = true;
        }
        pdf.open();

    }

    boolean tempFirstColumn = false;

    public void preProcessXLS(Object document) throws IOException, BadElementException, DocumentException {
        tooltipShow = false;
        int columnCount = -1;
        for (boolean b : columnShowList)
            if (b)
                columnCount++;
        if (columnCount == -1) {
            columnShowList.set(0, true);
            tempFirstColumn = true;
        }
    }

    public boolean isTooltipShow() {
        return tooltipShow;
    }

    public void setTooltipShow(boolean tooltipShow) {
        this.tooltipShow = tooltipShow;
    }


    public List<DataTableColumn> getDataTableColumnsInExpansion() {
        return dataTableColumnsInExpansion;
    }

    public void setDataTableColumnsInExpansion(List<DataTableColumn> dataTableColumnsInExpansion) {
        this.dataTableColumnsInExpansion = dataTableColumnsInExpansion;
    }

     public void postProcessXLS(Object document) {

         int columnCount = -1;
         for (boolean b : columnShowList)
             if (b)
                 columnCount++;

         HSSFWorkbook wb = (HSSFWorkbook) document;
         CellStyle style = wb.createCellStyle();
         style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
         style.setBorderBottom(CellStyle.BORDER_THIN);
         style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
         style.setBorderLeft(CellStyle.BORDER_THIN);
         style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
         style.setBorderRight(CellStyle.BORDER_THIN);
         style.setRightBorderColor(IndexedColors.BLACK.getIndex());
         style.setBorderTop(CellStyle.BORDER_THIN);
         style.setTopBorderColor(IndexedColors.BLACK.getIndex());

         CellStyle headerStyle = wb.createCellStyle();
         //  style1.setFillForegroundColor(HSSFColor.BLUE.index);
         headerStyle.setFillForegroundColor(HSSFColor.LIME.index);
         headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
         headerStyle.setBottomBorderColor(IndexedColors.DARK_RED.getIndex());
         headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

         headerStyle.setBorderBottom(CellStyle.BORDER_THIN);
         headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
         headerStyle.setBorderLeft(CellStyle.BORDER_THIN);
         headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
         headerStyle.setBorderRight(CellStyle.BORDER_THIN);
         headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
         headerStyle.setBorderTop(CellStyle.BORDER_THIN);
         headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

         HSSFSheet sheet = wb.getSheetAt(0);
         sheet.shiftRows(0, sheet.getLastRowNum(), headerRowTreeMap.size() + 1);
         int ColumnIndex = 0;

             HSSFRow tempRow=sheet.createRow(0);
                 Cell cell=tempRow.createCell(0);
                 cell.setCellValue(setting.getTitle1());
                 cell.setCellStyle(headerStyle);
                 for (int i = 1 ; i <  columnCount+1 ; i++) {
                     cell = tempRow.createCell(i);
                     cell.setCellStyle(headerStyle);
                 }
                 sheet.addMergedRegion(new CellRangeAddress(0,0, 0, columnCount ));



         for (Integer headerRow : this.headerRowTreeMap.keySet()) {
             ColumnIndex = 0;
             List<HeaderCell> headerCells=headerRowTreeMap.get(headerRow);
             tempRow=sheet.createRow(headerRow + 1);
             for (HeaderCell headerCell : headerCells){
                 cell=tempRow.createCell(ColumnIndex);
                 cell.setCellValue(headerCell.getHeaderText());
                 cell.setCellStyle(headerStyle);
                 for (int i = ColumnIndex+1 ; i < ColumnIndex + headerCell.getColSpan(); i++) {
                     cell = tempRow.createCell(i);
                     cell.setCellStyle(headerStyle);
                 }
                 sheet.addMergedRegion(new CellRangeAddress(headerRow + 1, headerRow + 1, ColumnIndex, ColumnIndex + headerCell.getColSpan()-1));
                 ColumnIndex += headerCell.getColSpan();
             }
         }
         for (Cell celll : sheet.getRow(0)) {
                celll.setCellStyle(headerStyle);
            }
         for (Cell celll : sheet.getRow(headerRowTreeMap.size()+1)) {
                celll.setCellStyle(headerStyle);
            }


         for(int i=headerRowTreeMap.size()+1+1;i<sheet.getLastRowNum()+1;i++){
             for (Cell celll : sheet.getRow(i)) {
                 celll.setCellStyle(style);
             }

         }
         for(int ii=0;ii<columnCount+1;ii++){
             sheet.autoSizeColumn(ii);
         }


     }

    public void initTheme() {

        themeMap.put(getNamespace(), "cupertino");
        ThemeService themeService = new ThemeService();
        themes = themeService.getThemes();

    }


    public void saveTheme(AjaxBehaviorEvent ajax) {
        setTheme((String) ((ThemeSwitcher) ajax.getSource()).getValue());

    }

    public void changeTheme() {
        if (themeMap.containsKey(getNamespace()))
            themeMap.remove(getNamespace());
        themeMap.put(getNamespace(), theme);

        // System.out.println("theme = " + theme);
      /*  Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if(params.containsKey("globaltheme")) {
            theme = params.get("globaltheme");
        }*/
    }

    public List<Theme> getThemes() {
        return themes;
    }


    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserCustomFieldValue(User user, String field, String value) throws
            SystemException, PortalException {
        ExpandoTable table = ExpandoTableLocalServiceUtil.getDefaultTable(user.getCompanyId(), User.class.getName());
        ExpandoColumn column = ExpandoColumnLocalServiceUtil.getColumn(table.getTableId(), field);
        ExpandoValueLocalServiceUtil.addValue(user.getCompanyId(), User.class.getName(), table.getName(), column.getName(),
                user.getUserId(), value);
    }

    public List<String> getUserAttributes() {
        return userAttributes;
    }

    public void setUserAttributes(List<String> userAttributes) {
        this.userAttributes = userAttributes;
    }

    private String message1 = "تنظیمات کاربر ذخیره شد";
    private String message2 = "تنظیمات کاربر بازیابی شد";

    public void saveConfig() {

        String shareSetting = "";
        if (user.getExpandoBridge().getAttributes().get("shareSetting") != null) {
            if (user.getExpandoBridge().getAttributes().get("shareSetting").toString().contains("|"))
                shareSetting = "";
            else
                shareSetting = user.getExpandoBridge().getAttributes().get("shareSetting").toString().trim();
        }

        if (themeMap.containsKey(getNamespace()))
            theme = themeMap.get(getNamespace());
        String shareSettingAdd = "<" + setting.getUniqueID() + "_" + "theme=" + theme + ">";
        System.out.println("shareSettingAdd = " + shareSettingAdd);


        //theme
        if (!shareSetting.contains("<" + setting.getUniqueID() + "_" + "theme=")) {
            shareSetting += shareSettingAdd;
        } else {
            int sSOffBegin = shareSetting.indexOf("<" + setting.getUniqueID() + "_" + "theme=");
            int sSOffEnd = shareSetting.indexOf(">", sSOffBegin);
            shareSetting = shareSetting.replace(shareSetting.substring(sSOffBegin, sSOffEnd + 1), shareSettingAdd);
        }

        ////userPageSize
        shareSettingAdd = "<" + setting.getUniqueID() + "_" + "userPageSize=" + userPageSize + ">";
        if (!shareSetting.contains("<" + setting.getUniqueID() + "_" + "userPageSize=")) {
            shareSetting += shareSettingAdd;
        } else {
            int sSOffBegin = shareSetting.indexOf("<" + setting.getUniqueID() + "_" + "userPageSize=");
            int sSOffEnd = shareSetting.indexOf(">", sSOffBegin);
            shareSetting = shareSetting.replace(shareSetting.substring(sSOffBegin, sSOffEnd + 1), shareSettingAdd);
        }
        ////columnShowList
        shareSettingAdd = "<" + setting.getUniqueID() + "_" + "columnShowList=" + columnShowList + ">";
        if (!shareSetting.contains("<" + setting.getUniqueID() + "_" + "columnShowList=")) {
            shareSetting += shareSettingAdd;
        } else {
            int sSOffBegin = shareSetting.indexOf("<" + setting.getUniqueID() + "_" + "columnShowList=");
            int sSOffEnd = shareSetting.indexOf(">", sSOffBegin);
            shareSetting = shareSetting.replace(shareSetting.substring(sSOffBegin, sSOffEnd + 1), shareSettingAdd);
        }
        try {
            setUserCustomFieldValue(user, "shareSetting", shareSetting);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("***", message1));
        } catch (SystemException e) {
            e.printStackTrace();
        } catch (PortalException e) {
            e.printStackTrace();
        }
    }

    public void restoreConfig(boolean showMessage) {
        String shareSetting = "";
        if (user.getExpandoBridge().getAttributes().get("shareSetting") != null) {
            if (user.getExpandoBridge().getAttributes().get("shareSetting").toString().contains("|"))
                shareSetting = "";
            else
                shareSetting = user.getExpandoBridge().getAttributes().get("shareSetting").toString().trim();
        }

        if (shareSetting.contains("<" + setting.getUniqueID() + "_" + "theme=")) {
            int sSOffBegin = shareSetting.indexOf("<" + setting.getUniqueID() + "_" + "theme=");
            int sSOffEnd = shareSetting.indexOf(">", sSOffBegin);
            theme = shareSetting.substring(sSOffBegin, sSOffEnd).replace("<" + setting.getUniqueID() + "_" + "theme=", "");
            themeMap.put(getNamespace(), theme);
        }

        if (shareSetting.contains("<" + setting.getUniqueID() + "_" + "userPageSize=")) {
            int sSOffBegin = shareSetting.indexOf("<" + setting.getUniqueID() + "_" + "userPageSize=");
            int sSOffEnd = shareSetting.indexOf(">", sSOffBegin);
            userPageSize = Integer.parseInt(shareSetting.substring(sSOffBegin, sSOffEnd).replace("<" + setting.getUniqueID() + "_" + "userPageSize=", ""));

        }
        if (shareSetting.contains("<" + setting.getUniqueID() + "_" + "columnShowList=")) {
            int sSOffBegin = shareSetting.indexOf("<" + setting.getUniqueID() + "_" + "columnShowList=");
            int sSOffEnd = shareSetting.indexOf(">", sSOffBegin);
            String columnShowListStr = shareSetting.substring(sSOffBegin, sSOffEnd).replace("<" + setting.getUniqueID() + "_" + "columnShowList=", "").replace("[", "").replace("]", "");
            String[] columnShowList1 = columnShowListStr.split(",");

            columnShowList = createBooleanArrayList(columnShowList1);

        }

        if (showMessage == true) {
            FacesContext context2 = FacesContext.getCurrentInstance();
            context2.addMessage(null, new FacesMessage("***", message2));
        }
    }


    public static ArrayList<String> createArrayList(String... elements) {
        ArrayList<String> list = new ArrayList<String>();
        for (String element : elements) {
            list.add(element);
        }
        return list;
    }

    public static ArrayList<Boolean> createBooleanArrayList(Boolean... elements) {
        ArrayList<Boolean> list = new ArrayList<Boolean>();
        for (Boolean element : elements) {
            list.add(element);
        }
        return list;
    }

    public static ArrayList<Boolean> createBooleanArrayList(String[] listStr) {
        ArrayList<Boolean> list = new ArrayList<Boolean>();
        for (String element : listStr) {
            if (element.toUpperCase().trim().equals("TRUE"))
                list.add(true);
            else
                list.add(false);
        }
        return list;
    }


    public void setThemeMap(HashMap<String, String> themeMap) {
        this.themeMap = themeMap;
    }

    public HashMap<String, String> getThemeMap() {
        return themeMap;
    }


    public List<DataTableRow> getResultList(int first, int pageSize, String sortField, SortOrder
            sortOrder, Map<String, Object> filters) {
        List<DataTableRow> all = new ArrayList<DataTableRow>();
        getData(getQuery(first, pageSize, sortField, sortOrder, filters));
        all.addAll(this.dataTableRows);
        return all;
    }

    public int count(String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        //System.out.println("count:"  );
        return getDataSize(getSizeQuery(filters));
    }

    public String getQuery(int first, int pageSize, String sortField, SortOrder
            sortOrder, Map<String, Object> filters) {
        String newQuery = query;


        //filter
        String filterStr = "";
        if (filters != null && filters.size() > 0) {
            int whereIndex = newQuery.indexOf("WHERE");
            int groupByIndex = newQuery.indexOf("GROUP");
            int orderByIndex = newQuery.indexOf("ORDER");

            for (Iterator<String> it = filters.keySet().iterator(); it.hasNext(); ) {
                String filterProperty = it.next();
                Object filterValue = filters.get(filterProperty);
                if (filterProperty.toUpperCase().contains("."))
                    filterProperty = "[" + filterProperty + "]";

                filterStr += filterProperty + " LIKE " + "'%" + filterValue + "%'  AND ";
            }
            filterStr += "END";

            filterStr = filterStr.replace("AND END", "");
            filterStr = filterStr.replace("END", "");

            if (whereIndex == -1 && groupByIndex == -1 && orderByIndex == -1 && !filterStr.trim().equals("")) {
                newQuery = newQuery + " WHERE  " + filterStr;
            } else if (whereIndex > 0) {
                newQuery = newQuery.substring(0, whereIndex + 6) + filterStr + " AND " + newQuery.substring(whereIndex + 6);
            } else if (whereIndex == -1 && (groupByIndex > 0)) {
                newQuery = newQuery.substring(0, groupByIndex) + " WHERE " + filterStr + newQuery.substring(groupByIndex);
            } else if (whereIndex == -1 && (orderByIndex > 0)) {
                newQuery = newQuery.substring(0, orderByIndex) + " WHERE " + filterStr + newQuery.substring(orderByIndex);
            }
        }
        //sort
        String orderbyStr = "";

        if (sortField != null) {
            if (sortOrder == null) {
                //just don't sort
            } else if (sortOrder.equals(SortOrder.ASCENDING) || sortOrder.equals(SortOrder.DESCENDING)) {
                if (sortField.toUpperCase().contains("_FDESC"))
                    sortField = sortField.replace("_FDESC", "");
                if (sortField.toUpperCase().contains("."))
                    sortField = "[" + sortField + "]";
                if (sortField.toUpperCase().startsWith("_"))
                    sortField = sortField.replace("_", "");
                if (sortField.toUpperCase().contains("CITYNAME"))
                    sortField = "CITY";
                if (sortField.toUpperCase().contains("LATENCY"))
                    sortField = "CONCAT(ISSUSP,ISTERM ) ";
                if (sortOrder.equals(SortOrder.ASCENDING))
                    orderbyStr = " ORDER BY " + sortField + " ASC ";
                else if (sortOrder.equals(SortOrder.DESCENDING))
                    orderbyStr = " ORDER BY " + sortField + " DESC ";
            } else if (sortOrder.equals(SortOrder.UNSORTED)) {
                //just don't sort
            } else {
                //just don't sort
            }
        }

        //pagination
        String paginationStr = "";
        if (setting.getDataBaseType().toUpperCase().equals("SQL")) {
            if (!orderbyStr.equals("")) {
                int orderByIndex = newQuery.indexOf("ORDER");
                if  (orderByIndex>0)
                newQuery = newQuery.substring(0, orderByIndex);
            } else {
                if (!newQuery.contains("ORDER"))
                    orderbyStr = " ORDER BY " + dataTableColumnsInExpansion.get(0).getProperty() + " ";
            }

            newQuery += orderbyStr;
            if (first >= 0) {
                paginationStr = " OFFSET " + first + " ROWS ";
            }
            if (pageSize >= 0) {
                paginationStr += " FETCH NEXT " + pageSize + " ROWS ONLY";
            }

            newQuery += paginationStr;
        } else if (setting.getDataBaseType().toUpperCase().equals("DB2")){   // db2 up 9.5 LIMIT 3 OFFSET 2;
           /* if (pageSize >= 0) {
                paginationStr = " LIMIT " + pageSize;
            }
            if (first >= 0) {
                paginationStr += " OFFSET " + first;
            }*/
            //
            int fromIndex = newQuery.indexOf("FROM");
            int selectIndex = newQuery.indexOf("SELECT");
            int endTablenameIndex = newQuery.indexOf("WHERE");
            if (endTablenameIndex == -1)
                endTablenameIndex = newQuery.indexOf("GROUP");
            if (endTablenameIndex == -1)
                endTablenameIndex = newQuery.length();

            String tableName = newQuery.substring(fromIndex + 4, endTablenameIndex).trim();
            String feildsStr = newQuery.substring(selectIndex + 6, fromIndex).replace("*", tableName + ".*");

            if (!orderbyStr.equals("")) {
                int orderByIndex = newQuery.indexOf("ORDER");
                if  (orderByIndex>0)
                    newQuery = newQuery.substring(0, orderByIndex);
            } else {
                if (!newQuery.contains("ORDER"))
                    orderbyStr = " ORDER BY " + dataTableColumnsInExpansion.get(0).getProperty() + " ";
            }
            newQuery = "  Select * from (SELECT ROW_NUMBER() OVER(" + orderbyStr + ") AS RID," + feildsStr + newQuery.substring(fromIndex) + orderbyStr
                    + " FETCH FIRST " + (first + pageSize) + " ROWS ONLY)  q where RID >" + first + " AND  RID<=" + (first + pageSize);

        }
        //System.out.println("newQuery = " + newQuery);
        return newQuery;
    }

    public String getSizeQuery(Map<String, Object> filters) {
        String newQuery = query;
        int fromIndex = newQuery.indexOf("FROM");
        newQuery = " SELECT COUNT(*) CNT " + newQuery.substring(fromIndex);
        //filter
        String filterStr = "";
        int whereIndex = newQuery.indexOf("WHERE");
        int groupByIndex = newQuery.indexOf("GROUP");
        int orderByIndex = newQuery.indexOf("ORDER");

        if (filters != null && filters.size() > 0) {
            for (Iterator<String> it = filters.keySet().iterator(); it.hasNext(); ) {
                String filterProperty = it.next();
                Object filterValue = filters.get(filterProperty);
                if (filterProperty.toUpperCase().contains("."))
                    filterProperty = "[" + filterProperty + "]";
                filterStr += filterProperty + " LIKE " + "'%" + filterValue + "%'  AND ";
            }
            filterStr += "END";

            filterStr = filterStr.replace("AND END", "");
            filterStr = filterStr.replace("END", "");

            if (whereIndex == -1 && groupByIndex == -1 && orderByIndex == -1 && !filterStr.trim().equals("")) {
                newQuery = newQuery + " WHERE  " + filterStr;
            } else if (whereIndex > 0) {
                newQuery = newQuery.substring(0, whereIndex + 6) + filterStr + " AND " + newQuery.substring(whereIndex + 6);
            } else if (whereIndex == -1 && (groupByIndex > 0)) {
                newQuery = newQuery.substring(0, groupByIndex) + " WHERE " + filterStr + newQuery.substring(groupByIndex);
            } else if (whereIndex == -1 && (orderByIndex > 0)) {
                newQuery = newQuery.substring(0, orderByIndex) + " WHERE " + filterStr + newQuery.substring(orderByIndex);
            }
        }
        orderByIndex = newQuery.indexOf("ORDER");
        if (orderByIndex>0)
            newQuery = newQuery.substring(0, orderByIndex);

        System.out.println("newQuery = " + newQuery);
        return newQuery;
    }

    public boolean isLazyLoadShow() {
        return lazyLoadShow;
    }

    public void setLazyLoadShow(boolean lazyLoadShow) {
        this.lazyLoadShow = lazyLoadShow;
    }

    public boolean isNormalShow() {
        return normalShow;
    }

    public void setNormalShow(boolean normalShow) {
        this.normalShow = normalShow;
    }

    public static File mergeImageAndText(String imageFilePath,
                                         String text, Point textPosition, String outFilePath) throws IOException {
        BufferedImage im = ImageIO.read(new File(imageFilePath));
        Graphics2D g2 = im.createGraphics();
        g2.setFont(g2.getFont().deriveFont(20f));
        g2.setColor(Color.decode("#4e8fff"));
        g2.drawString(text, textPosition.x, textPosition.y);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(im, "png", baos);
        FileOutputStream fos = new FileOutputStream(outFilePath);
        fos.write(baos.toByteArray());
        fos.close();
        return new File(outFilePath);
    }

    public int getUserPageSize() {
        return userPageSize;
    }

    public void setUserPageSize(int userPageSize) {
        this.userPageSize = userPageSize;
    }

    String persianDate;

    public String getPersianDate() {
        return persianDate;
    }

    public void setPersianDate(String persianDate) {
        this.persianDate = persianDate;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getCurentQuery(String preQuery) {
        String tempQuery = preQuery.toUpperCase().replace("@ShToday".toUpperCase(), getShToday()).replace("@ShYesterday".toUpperCase(), getShYesterday()).replace("@shFirstDayOfCurrentMonth".toUpperCase(), getShFirstDayOfCurrentMonth()).replace("@ShFirstDayOfCurrentYear".toUpperCase(), getShFirstDayOfCurrentYear());
        if (tempQuery.contains("###")) {
            GregorianDate gDate = JalaliDateUtil.convertToGregorian(new JalaliDate(persianDate.replace('/', '-')));
            if (setting.isDateConvertChecked()) {
                queryDate = gDate.getDate();
            } else
                queryDate = persianDate;

            if (tempQuery != null && !"".equals(queryDate)) {
                tempQuery = tempQuery.replaceAll("###", queryDate);
            }
        }
        return tempQuery;
    }
    public String getCurrentDB(){
        String tempDB="online";
        GregorianDate gDate = JalaliDateUtil.convertToGregorian(new JalaliDate(persianDate.replace('/', '-')));
        if (!gDate.toString().equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString())) {
            tempDB = "back";
            } else
            tempDB = "online";
            return tempDB;
    }

}