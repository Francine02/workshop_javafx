package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;
import util.Alerts;
import util.Constraints;
import util.Utils;

public class DepartmentFormController implements Initializable{
	
	private Department entity;
	
	private DepartmentService service;
	
	@FXML
	private TextField textId;
	
	@FXML
	private TextField textName;

	@FXML
	private Label labelErrorNameLabel;
	
	@FXML
	private Button buttonSave;
	
	@FXML
	private Button buttonCancel;
	
	@FXML
	public void onButtonSaveAction(ActionEvent event) {
		if(entity == null) {
			throw new IllegalStateException("Entidade nula!");
		}
		if (service == null) {
			throw new IllegalStateException("Service nulo!");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			
			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar objeto!", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private Department getFormData() {
		Department department = new Department();
		department.setId(util.Utils.tryParseToInt(textId.getText()));
		department.setName(textName.getText());
		return department;
	}

	@FXML
	public void onButtonCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
	}
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void updateFromData() {
		if(entity == null) {
			throw new IllegalStateException("Entidade nula!");
		}
		textId.setText(String.valueOf(entity.getId()));
		textName.setText(entity.getName());
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(textId);
		Constraints.setTextFieldMaxLength(textName, 35);
	}

}
