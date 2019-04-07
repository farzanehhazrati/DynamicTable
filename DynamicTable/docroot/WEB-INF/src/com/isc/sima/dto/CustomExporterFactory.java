package com.isc.sima.dto;

        import org.primefaces.component.export.*;

        import javax.faces.FacesException;

public class CustomExporterFactory {

    public static Exporter getExporterForType(String type) {
        Exporter exporter = null;

        try {
            ExporterType exporterType = ExporterType.valueOf(type.toUpperCase());

            switch(exporterType) {
                case XLS:
                    exporter = new ExcelExporter();
                    break;

                case PDF:
                    exporter = new PDFCustomExporter();
                    break;

                case CSV:
                    exporter = new CSVExporter();
                    break;

                case XML:
                    exporter = new XMLExporter();
                    break;

                case XLSX:
                    exporter = new ExcelXExporter();
                    break;
            }
        }
        catch(IllegalArgumentException e) {
            throw new FacesException(e);
        }

        return exporter;
    }
}