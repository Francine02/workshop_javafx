package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import util.Constraints;

public class DepartmentFormController implements Initializable{
	
	private Department entity;
	
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
	public void onButtonSaveAction() {
		System.out.println("asd");
	}
	
	@FXML
	public void onButtonCancelAction() {
		System.out.println("d");
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
	}
	
	public void setDepartment(Department entity) {
		this.entity = entity;
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
