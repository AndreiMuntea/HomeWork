package Controller;

import Controller.ControllerExceptions.ControllerException;
import Domain.Section;
import Helper.Saver.FileSaver.CSVFile.SectionCSVFileSaver;
import Helper.Saver.FileSaver.HTMLFile.SectionHTMLSaver;
import Helper.Saver.FileSaver.TextFile.SectionFileSaver;
import Helper.Saver.ISaver;
import Helper.Saver.PDFFile.SectionPDFSaver;
import Helper.Saver.XMLFile.SectionXMLSaver;
import Repository.IRepository;
import Validator.IValidator;

/**
 * Created by andrei on 2017-01-04.
 */
public class SectionController extends AbstractController<Integer, Section> {
    public SectionController() {
    }

    @Override
    public Section CreateFromFormat(String... format) throws ControllerException {

        if (format.length != 3) throw new ControllerException("Invalid number of parameters!\n");

        try{
            Integer sectionID = Integer.parseInt(format[0]);
            String sectionName = format[1];
            Integer availableSlots = Integer.parseInt(format[2]);

            return new Section(sectionID, sectionName, availableSlots);
        }catch(NumberFormatException e){
            throw new ControllerException("ID and available slots should be positive Integers: \n");
        }
    }

    @Override
    public Integer CreateIDFromFormat(String... format) throws ControllerException {
        if (format.length != 1) {
            throw new ControllerException("Invalid number of parameters!\n");
        }

        try {
            Integer ID = Integer.parseInt(format[0]);
            return ID;
        } catch (NumberFormatException e) {
            throw new ControllerException("ID should be a positive integer \n");
        }
    }

    @Override
    protected void loadExporters() {
        exporters.put("PDF",new SectionPDFSaver());
        exporters.put("HTML",new SectionHTMLSaver());
        exporters.put("CSV",new SectionCSVFileSaver());
        exporters.put("TXT",new SectionFileSaver());
        exporters.put("XML",new SectionXMLSaver());
    }
}
