package GUI.OptionsGUI;

import Controller.OptionController;
import Domain.Candidate;
import Domain.Section;
import GUI.OptionsGUI.Reports.AverageSectionsController;
import GUI.OptionsGUI.Reports.TopSectionsController;
import GUI.UsersController.UserType;
import Utils.Exceptions.MyException;
import Utils.ObserverFramework.IObserver;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * Created by andrei on 2017-01-06.
 */
public class OptionGUIController implements IObserver {

    @FXML
    private TableView<Candidate> candidatesTable;

    @FXML
    private TableColumn<Candidate, Integer> candidateIDColumn;

    @FXML
    private TableColumn<Candidate, String> candidateNameColumn;

    @FXML
    private TableColumn<Candidate, Double> candidateGradeColumn;

    @FXML
    private Button previousPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private TextField pageNumberTextField;

    @FXML
    private TableView<Section> sectionsTable;

    @FXML
    private TableColumn<Section, Integer> sectionIDColumn;

    @FXML
    private TableColumn<Section, String> sectionNameColumn;

    @FXML
    private TableColumn<Section, Integer> sectionSlotsColumn;

    @FXML
    private TextField candidateIDTextField;

    @FXML
    private TextField sectionIDTextField;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button warningButton;

    @FXML
    private Slider slider;

    @FXML
    private TextField topValueTextField;

    @FXML
    private Button solicitedSections;

    @FXML
    private Button averageSectionsButton;

    @FXML
    private ComboBox<String> saveOptionComboBox;

    private FXMLLoader topSectionLoader;
    private Stage topSectionStage;
    private Parent topSectionScene;
    private TopSectionsController topSectionsController;

    private FXMLLoader averageSectionLoader;
    private Stage averageSectionStage;
    private Parent averageSectionScene;
    private AverageSectionsController averageSectionsController;

    private OptionController optionController;
    private Integer pageSize;
    private Integer currentPage;
    private ObservableList<Candidate> candidatesModel;
    private ObservableList<Section> sectionsModel;

    public OptionGUIController() {
    }

    public void initComponents(OptionController optionController, Integer pageSize) throws Exception {
        this.optionController = optionController;
        this.pageSize = pageSize;
        this.currentPage = 0;
        this.sectionsModel = new SimpleListProperty<>();
        this.candidatesModel = new SimpleListProperty<>();

        candidateIDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        candidateNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        candidateGradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        sectionIDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        sectionNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sectionSlotsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSlots"));

        slider.valueProperty().addListener(o->sliderAction());

        updateModel();
    }

    @Override
    public void update() {
        updateModel();
    }

    public void candidateChanged() {
        Candidate candidate = candidatesTable.getSelectionModel().getSelectedItem();
        if (candidate == null) return;

        candidateIDTextField.setText(candidate.getID().toString());

        try {
            sectionsModel = new SimpleListProperty<>(FXCollections.observableArrayList(optionController.getSectionsForCandidate(candidate.getID().toString())));
        } catch (MyException e) {
            sectionsModel.clear();
        }
        sectionsTable.setItems(sectionsModel);
    }

    public void sectionChanged() {
        Section section = sectionsTable.getSelectionModel().getSelectedItem();
        if (section == null) return;

        sectionIDTextField.setText(section.getID().toString());
    }

    public void registerCandidate() {
        try {
            optionController.Add(getIDs());
        } catch (MyException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    public void deregisterCandidate() {
        try {
            optionController.Remove(getIDs());
        } catch (MyException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    public void sliderAction(){
        String newValue = String.format("%.0f",slider.getValue());
        if (newValue != topValueTextField.getText()) topValueTextField.setText(newValue);
    }

    public void pageChangedHandler() {
        Integer newPage;
        try {
            newPage = Integer.parseInt(pageNumberTextField.getText());
            if (newPage < 0) pageNumberTextField.setText(currentPage.toString());
            else {
                currentPage = newPage;
                updateModel();
            }
        } catch (NumberFormatException e) {
            pageNumberTextField.setText(currentPage.toString());
        }
    }

    public void previousPageButtonHandler() {
        if (currentPage == 0) return;
        pageNumberTextField.setText((--currentPage).toString());
        updateModel();
    }

    public void nextPageButtonHandler() {
        pageNumberTextField.setText((++currentPage).toString());
        updateModel();
    }

    public void getAverageSections(){

        try{
            averageSectionStage = new Stage();
            averageSectionStage.initModality(Modality.APPLICATION_MODAL);
            averageSectionStage.setResizable(false);
            averageSectionStage.setTitle("Export average sections");
            averageSectionLoader = new FXMLLoader(getClass().getResource("/GUI/OptionsGUI/averageSectionsReport.fxml"));
            averageSectionScene = averageSectionLoader.load();
            averageSectionsController = averageSectionLoader.getController();
            averageSectionStage.setScene(new Scene(averageSectionScene, 1100, 700));
            averageSectionsController.initialiseComponents(this.optionController, averageSectionStage);

            if(saveOptionComboBox.getValue().equals("PieChart"))
                averageSectionsController.generatePieChart(topValueTextField.getText());
            if (saveOptionComboBox.getValue().equals("BarChart"))
                averageSectionsController.generateBarChart(topValueTextField.getText());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    public void getTopSections(){

        try{
            topSectionStage = new Stage();
            topSectionStage.initModality(Modality.APPLICATION_MODAL);
            topSectionStage.setResizable(false);
            topSectionStage.setTitle("Export top sections");
            topSectionLoader = new FXMLLoader(getClass().getResource("/GUI/OptionsGUI/topSectionsReport.fxml"));
            topSectionScene = topSectionLoader.load();
            topSectionsController = topSectionLoader.getController();
            topSectionStage.setScene(new Scene(topSectionScene, 1100, 700));
            topSectionsController.initialiseComponents(this.optionController, topSectionStage);
            if(saveOptionComboBox.getValue().equals("PieChart"))
                topSectionsController.generatePieChart(topValueTextField.getText());
            else if (saveOptionComboBox.getValue().equals("BarChart"))
                topSectionsController.generateBarChart(topValueTextField.getText());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateModel() {
        try {
            candidatesModel = new SimpleListProperty<>(FXCollections.observableArrayList(optionController.getPageCandidates(pageSize, currentPage)));
            candidatesTable.setItems(candidatesModel);
            candidateChanged();
        } catch (MyException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }
    public void setRestrictions(UserType userType)
    {
        addButton.setDisable(userType == UserType.NORMAL_USER);
        deleteButton.setDisable(userType == UserType.NORMAL_USER);
        warningButton.setVisible(userType == UserType.NORMAL_USER);
    }


    public void warningButtonHandler()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Some features might not be available!\nPlease log in as super user for full access!");
        alert.showAndWait();
    }


    private String[] getIDs() {
        String[] id = new String[2];
        id[0] = candidateIDTextField.getText();
        id[1] = sectionIDTextField.getText();
        return id;
    }

}