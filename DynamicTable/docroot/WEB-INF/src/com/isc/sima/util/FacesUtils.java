package com.isc.sima.util;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: Z_Babakhani
 * Date: 10/7/14
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */

public class FacesUtils
{
    private static String[] projectRealClassPath = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("org.apache.catalina.jsp_classpath").toString().split(";")[0].split("/");
    private static Date logDate = new Date(1289000000000l);

    public static String getConfigProjectFilePath()
    {
        String path = "";
        for (String str : projectRealClassPath)
        {
            path += str + File.separator;
            if (str.equals("WEB-INF"))
            {
                break;
            }
        }
        return path + "config-project.xml";
    }

    public static String getProjectPath()
    {
        String path = "";

        for (String str : projectRealClassPath)
        {
            if (str.equals("WEB-INF"))
            {
                break;
            }
            path += str + File.separator;
        }
        if (path.charAt(0) == '\\')
        {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * Get servlet context.
     *
     * @return the servlet context
     */
    public static ServletContext getServletContext()
    {
        return (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
    }

    public static ExternalContext getExternalContext()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null)
        {
            System.out.println(" >>>>>>> FacesContext.getCurrentInstance() == NULL !!!!");
        }
        return fc.getExternalContext();
    }

    public static HttpSession getHttpSession(boolean create)
    {
        return (HttpSession) FacesContext.getCurrentInstance().
                getExternalContext().getSession(create);
    }

    /**
     * Get managed bean based on the bean name.
     *
     * @param beanName the bean name
     * @return the managed bean associated with the bean name
     */
    public static Object getManagedBean(String beanName)
    {

        return getValueBinding(getJsfEl(beanName)).getValue(FacesContext.getCurrentInstance());
    }

    /**
     * Remove the managed bean based on the bean name.
     *
     * @param beanName the bean name of the managed bean to be removed
     */
    public static void resetManagedBean(String beanName)
    {
        getValueBinding(getJsfEl(beanName)).setValue(FacesContext.getCurrentInstance(), null);
    }

    /**
     * Store the managed bean inside the session scope.
     *
     * @param beanName    the name of the managed bean to be stored
     * @param managedBean the managed bean to be stored
     */
    public static void setManagedBeanInSession(String beanName, Object managedBean)
    {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(beanName, managedBean);
    }

    /**
     * Get parameter value from request scope.
     *
     * @param name the name of the parameter
     * @return the parameter value
     */
    public static String getRequestParameter(String name)
    {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }

    /**
     * Add information message.
     *
     * @param msg the information message
     */
    public static void addInfoMessage(String msg)
    {
        addInfoMessage(null, msg);
    }

    /**
     * Add information message to a specific client.
     *
     * @param clientId the client id
     * @param msg      the information message
     */
    public static void addInfoMessage(String clientId, String msg)
    {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
    }

    /**
     * Add error message.
     *
     * @param msg the error message
     */
    public static void addErrorMessage(String msg)
    {
        addErrorMessage(null, msg);
    }

    /**
     * Add error message to a specific client.
     *
     * @param clientId the client id
     * @param msg      the error message
     */
    public static void addErrorMessage(String clientId, String msg)
    {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }

    private static Application getApplication()
    {
        ApplicationFactory appFactory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        return appFactory.getApplication();
    }

    private static ValueBinding getValueBinding(String el)
    {
        return getApplication().createValueBinding(el);
    }

    private static HttpServletRequest getServletRequest()
    {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    private static Object getElValue(String el)
    {
        return getValueBinding(el).getValue(FacesContext.getCurrentInstance());
    }

    private static String getJsfEl(String value)
    {
        return "#{" + value + "}";
    }

    public static String getRemoteIPAddress()
    {
        return getServletRequest().getRemoteAddr();
    }


    public static String readXML(String firstTagName, String secondTagName)
    {
        String value = "";
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(FacesUtils.getConfigProjectFilePath()));
            doc.getDocumentElement().normalize();
            value = ((Element) doc.getElementsByTagName(firstTagName).item(0)).
                    getElementsByTagName(secondTagName).item(0).
                    getChildNodes().item(0).getNodeValue();

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return value;
    }

    public static void logWay()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern("dd-M-yyyy");
        if (!dateFormat.format(logDate).equals(dateFormat.format(new Date())))
        {
            try
            {
                String path = "";
                for (String str : projectRealClassPath)
                {
                    path += str + File.separator;
                    if (str.equals("webapps"))
                        break;
                }
                path += "logs" + File.separator;
                if (path.charAt(0) == '\\')
                    path = path.substring(1);
                logDate = new Date();
//                System.setOut(new PrintStream(new FileOutputStream(path + "log(" + dateFormat.format(logDate) + ").txt", true)));
//                System.setErr(new PrintStream(new FileOutputStream(path + "log(" + dateFormat.format(logDate) + ").txt", true)));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //    public static HSSFWorkbook createWorkbook(LinkedList header, LinkedList rows ) {
//    public static void createWorkbook(LinkedList header, LinkedList rows, String fileName, String fileExtention)
//    {
//
//        HSSFWorkbook wb = new HSSFWorkbook();
//        HSSFSheet worksheet = wb.createSheet("All");
//
//        HSSFRow row = worksheet.createRow(0);
//
//        ListIterator itr = header.listIterator();
//        int i = 0;
//        while (itr.hasNext())
//        {
//            HSSFRichTextString value = new HSSFRichTextString(itr.next().toString());
//            row.createCell(i).setCellValue(value);
//            i++;
//        }
//
//        ListIterator rowsit = rows.listIterator();
//        int j = 1;
//
//        while (rowsit.hasNext())
//        {
//
//            HSSFRow row2 = worksheet.createRow(j);
//            String rowvalue = rowsit.next().toString();
//            String values[] = rowvalue.split("\\|");    //TODO must be changed
//            int cellCnt = 0;
//            for (String value : values)
//            {
//
//                HSSFRichTextString cellVal = new HSSFRichTextString(value);
//                row2.createCell(cellCnt).setCellValue(cellVal);
//                cellCnt++;
//
//            }
//            j++;
//        }
////        return wb;
//
//        FileOutputStream fileOut = null;
//        try
//        {
//            String fileWay = getProjectPath() + "\\WEB-INF\\Resources\\" + fileExtention + "Files\\" + fileName + "." + fileExtention;
//            fileOut = new FileOutputStream(fileWay);
//            wb.write(fileOut);
//            fileOut.close();
//            Desktop.getDesktop().open(new File(fileWay));
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }


    public static String getResourcePath(String resourceType)
    {
        return getProjectPath() + "WEB-INF\\Resources\\" + resourceType + "Files\\";
    }

//    public static void writeExcelFile(LinkedList<String> header, LinkedList<LinkedList<String>> rows, String fileName)
//    {
//        FileOutputStream fileOutputStream = null;
//        HSSFWorkbook sampleWorkbook = null;
//        HSSFSheet sampleDataSheet = null;
//        try
//        {
//            sampleWorkbook = new HSSFWorkbook();
//            sampleDataSheet = sampleWorkbook.createSheet("ReportSheet");
//            HSSFRow headerRow = sampleDataSheet.createRow(0);
//            HSSFCellStyle cellStyle = setHeaderStyle(sampleWorkbook);
//            for (int i = 0; i < header.size(); i++)
//            {
//                HSSFCell headerCell = headerRow.createCell(i);
//                headerCell.setCellStyle(cellStyle);
//                headerCell.setCellValue(new HSSFRichTextString(header.get(i)));
//
//            }
//
//
////
////
////
////            HSSFCell firstHeaderCell = headerRow.createCell(0);
////            firstHeaderCell.setCellStyle(cellStyle);
////            firstHeaderCell.setCellValue(new HSSFRichTextString("Employer Name"));
////
////            HSSFCell secondHeaderCell = headerRow.createCell(1);
////            secondHeaderCell.setCellStyle(cellStyle);
////            secondHeaderCell.setCellValue(new HSSFRichTextString("Designation"));
////
////            HSSFCell thirdHeaderCell = headerRow.createCell(2);
////            thirdHeaderCell.setCellStyle(cellStyle);
////            thirdHeaderCell.setCellValue(new HSSFRichTextString("Country"));
//
//            for (int i = 0; i < rows.size(); i++)
//            {
//                HSSFRow dataRow = sampleDataSheet.createRow(i + 1);
//                for (int j = 0; j < rows.get(i).size(); j++)
//                    dataRow.createCell(j).setCellValue(rows.get(i).get(j));
//            }
//
////            dataRow1.createCell(0).setCellValue(new HSSFRichTextString("Adam Albert"));
////            dataRow1.createCell(1).setCellValue("Hardware Engineer");
////            dataRow1.createCell(2).setCellValue("Havaee");
//            fileOutputStream = new FileOutputStream(getResourcePath("xls") + fileName + ".xls");
//            sampleWorkbook.write(fileOutputStream);
//
////            HttpServletResponse response = (HttpServletResponse) getExternalContext().getResponse();
////            response.setHeader("Content-disposition", "attachment; filename=" + getResourcePath(fileExtention) + fileName + "." + fileExtention);
////            response.setContentType("application/vnd.ms-excel");
//
////            FileInputStream stream = null;
////            try
////            {
////                // Get the directory and iterate them to get file by file...
////                File file = new File(getResourcePath(fileExtention) + fileName + "." + fileExtention);
////
////                response.setContentType("APPLICATION/DOWNLOAD");
////                response.setHeader("Content-Disposition", "attachment" +
////                        "filename=" + file.getPath());
////                stream = new FileInputStream(file);
////                response.setContentLength(stream.available());
////                OutputStream os = response.getOutputStream();
////                os.close();
////                response.flushBuffer();
////
////            } catch (IOException e)
////            {
////                e.printStackTrace();
////            }  finally
////            {
////                if (stream != null)
////                {
////                    try
////                    {
////                        stream.close();
////                    } catch (IOException e)
////                    {
////                        e.printStackTrace();
////                    }
////                }
////            }
//
//
//        } catch (
//                Exception ex
//                )
//
//        {
//            ex.printStackTrace();
//        } finally
//
//        {
//            try
//            {
//                if (fileOutputStream != null)
//                {
//                    fileOutputStream.close();
//                }
//            } catch (IOException ex)
//            {
//                ex.printStackTrace();
//            }
//        }
//
//    }


    /**
     * This method is used to set the styles for all the headers
     * of the excel sheet.
     *
     * @param sampleWorkBook - Name of the workbook.
     * @return cellStyle - Styles for the Header data of Excel sheet.
     */
//    private static HSSFCellStyle setHeaderStyle(HSSFWorkbook sampleWorkBook)
//    {
//        HSSFFont font = sampleWorkBook.createFont();
//        font.setFontName(HSSFFont.FONT_ARIAL);
//        font.setColor(IndexedColors.PLUM.getIndex());
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        HSSFCellStyle cellStyle = sampleWorkBook.createCellStyle();
//        cellStyle.setFont(font);
//        return cellStyle;
//    }

}
