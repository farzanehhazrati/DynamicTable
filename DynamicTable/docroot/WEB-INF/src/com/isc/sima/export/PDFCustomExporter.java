package com.isc.sima.export;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.faces.bridge.filter.internal.PortletContextBridgeLiferayImpl;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import org.primefaces.component.datatable.DataTable;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.export.Exporter;
import org.primefaces.util.Constants;

public class PDFCustomExporter extends Exporter {

    private Font cellFont;
    private Font facetFont;
    private Font tahomaFont;
    @Override
    public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter pdfWriter=PdfWriter.getInstance(document, baos);

            if (preProcessor != null) {
                preProcessor.invoke(context.getELContext(), new Object[]{document});
            }

            if (!document.isOpen()) {
                document.open();
            }
           /* ColumnText ct = new ColumnText(pdfWriter.getDirectContent());
            ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            ct.setSimpleColumn(100, 100, 500, 800, 24, Element.ALIGN_RIGHT);

            Chunk chunk = new Chunk("آزمایش", tahomaFont);

            ct.addElement(chunk);
            ct.go();
*/
            document.add(exportPDFTable(context, table, pageOnly, selectionOnly, encodingType));

            if(postProcessor != null) {
                postProcessor.invoke(context.getELContext(), new Object[]{document});
            }

            document.close();

            writePDFToResponse(context.getExternalContext(), baos, filename);

        } catch(DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    protected PdfPTable exportPDFTable(FacesContext context, DataTable table, boolean pageOnly, boolean selectionOnly, String encoding) {
        int columnsCount = getColumnsCount(table);
        PdfPTable pdfTable = new PdfPTable(columnsCount);
      /*Start*/


        PortletContextBridgeLiferayImpl servletContext = (PortletContextBridgeLiferayImpl) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String fontPath=servletContext.getRealPath("") + File.separator + "resources" + File.separator +"tahoma.ttf";

        BaseFont baseFont = null;
        try {
            baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tahomaFont = new Font(baseFont, 10, Font.NORMAL, Color.BLACK);


        pdfTable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        pdfTable.setExtendLastRow(true);

 /*       PdfPCell pdfCell = new PdfPCell(new Phrase("آزمایش", tahomaFont));
        pdfCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

        pdfTable.addCell(pdfCell);*/
        /*END*/


        this.cellFont = FontFactory.getFont(String.valueOf(tahomaFont), encoding);
        this.facetFont = FontFactory.getFont(String.valueOf(tahomaFont), encoding, Font.DEFAULTSIZE, Font.BOLD);

        addColumnFacets(table, pdfTable, ColumnType.HEADER);

        if (pageOnly) {
            exportPageOnly(context, table, pdfTable);
        }
        else if (selectionOnly) {
            exportSelectionOnly(context, table, pdfTable);
        }
        else {
            exportAll(context, table, pdfTable);
        }

        if (table.hasFooterColumn()) {
            addColumnFacets(table, pdfTable, ColumnType.FOOTER);
        }

        table.setRowIndex(-1);

        return pdfTable;
    }

    @Override
    protected void exportCells(DataTable table, Object document) {
        PdfPTable pdfTable = (PdfPTable) document;


        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();

            }

            if (col.isRendered() && col.isExportable()) {

                addColumnValue(pdfTable, col.getChildren(), tahomaFont);
            }
        }
    }

    protected void addColumnFacets(DataTable table, PdfPTable pdfTable, ColumnType columnType) {
        for (UIColumn col : table.getColumns()) {
            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if (col.isRendered() && col.isExportable()) {
                UIComponent facet = col.getFacet(columnType.facet());
                if(facet != null) {
                    addColumnValue(pdfTable, facet, tahomaFont);
                }
                else {
                    String textValue;
                    switch(columnType) {
                        case HEADER:
                            textValue = col.getHeaderText();
                            break;

                        case FOOTER:
                            textValue = col.getFooterText();
                            break;

                        default:
                            textValue = "";
                            break;
                    }

                    if(textValue != null) {
                        /*Start*/
                        PdfPCell pdfCell = new PdfPCell(new Paragraph(textValue, tahomaFont));
                        pdfCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        pdfTable.addCell(pdfCell);
                       /*End*/
                        // pdfTable.addCell(new Paragraph(textValue, this.facetFont));
                    }
                }
            }
        }
    }

    protected void addColumnValue(PdfPTable pdfTable, UIComponent component, Font font) {
        String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);

        pdfTable.addCell(new Paragraph(value, font));
    }

    protected void addColumnValue(PdfPTable pdfTable, List<UIComponent> components, Font font) {
        StringBuilder builder = new StringBuilder();
        FacesContext context = FacesContext.getCurrentInstance();

        for (UIComponent component : components) {
            if(component.isRendered() ) {
                String value = exportValue(context, component);

                if (value != null)
                    builder.append(value);
            }
        }

        pdfTable.addCell(new Paragraph(builder.toString(), font));
    }

    protected void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) throws IOException, DocumentException {
        externalContext.setResponseContentType("application/pdf");
        externalContext.setResponseHeader("Expires", "0");
        externalContext.setResponseHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
        externalContext.setResponseHeader("Pragma", "public");
        externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
        externalContext.setResponseContentLength(baos.size());
        externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());
        OutputStream out = externalContext.getResponseOutputStream();
        baos.writeTo(out);
        externalContext.responseFlushBuffer();
    }

    protected int getColumnsCount(DataTable table) {
        int count = 0;

        for(UIColumn col : table.getColumns()) {
            if(col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyStatelessModel();
            }

            if(!col.isRendered()||!col.isExportable()) {
                continue;
            }

            count++;
        }

        return count;
    }
}