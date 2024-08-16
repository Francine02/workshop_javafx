package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listener.DataChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;
import util.Alerts;
import util.Constraints;
import util.Utils;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;

	private DepartmentService departmentService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField textId;

	@FXML
	private TextField textName;

	@FXML
	private TextField textEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField textBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorNameLabel;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button buttonSave;

	@FXML
	private Button buttonCancel;

	private ObservableList<Department> obsList;

	@FXML
	public void onButtonSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entidade nula!");
		}
		if (service == null) {
			throw new IllegalStateException("Service nulo!");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);

			notifyDataChangeListeners();

			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar objeto!", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {
		Seller seller = new Seller();

		ValidationException exception = new ValidationException("Erro de validação");

		seller.setId(util.Utils.tryParseToInt(textId.getText()));

		if (textName.getText() == null || textName.getText().trim().equals("")) {
			exception.addError("Nome", "O campo não pode ser vazio!!");
		}
		seller.setName(textName.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return seller;
	}

	@FXML
	public void onButtonCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void updateFromData() {
		if (entity == null) {
			throw new IllegalStateException("Entidade nula!");
		}
		textId.setText(String.valueOf(entity.getId()));
		textName.setText(entity.getName());
		textEmail.setText(entity.getEmail());
		textBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		
		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}
	}

	public void subscribeDataChangeListener(DataChangeListener data) {
		dataChangeListeners.add(data);
	}

	public void loadAssociatedOjects() {
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(textId);
		Constraints.setTextFieldMaxLength(textName, 35);
		Constraints.setTextFieldDouble(textBaseSalary);
		Constraints.setTextFieldMaxLength(textEmail, 70);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	private void setErrorMessages(Map<String, String> error) {
		Set<String> fieldSet = error.keySet();

		if (fieldSet.contains("Nome")) {
			labelErrorNameLabel.setText(error.get("Nome"));
		}
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
}
