package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import appModel.FloatFormatter;
import domainModel.Material;
import domainModel.MaterialsManager;
import domainModel.RGB;
import domainModel.RayTracer;

@SuppressWarnings("serial")
public class MaterialEditDialog extends JDialog implements ActionListener {
	Component parent;
	Material material;
	MaterialsManager model;
	String action;
	final String cancelCom = "Cancel";
	final String addCom = "Add";
	final String editCom = "Edit";
	Color color;
	float[] sliderVals = new float[3]; // Kd,Ks, Ka
	float pVal;
	float rcVal;
	float  kd, ks, ka;
	JSlider[] sliders;
	JFormattedTextField pField;
	JFormattedTextField rcField;

	public MaterialEditDialog(Component parent, MaterialsManager model, int index) {
		setModal(true);
		this.parent = parent;
		this.model = model;
		this.material = (index != -1)? model.getMaterial(index) : null;
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout(0, 20));
		//this.setPreferredSize(new Dimension(400,600));
		this.setResizable(false);

		// determine if we are making a new object or editing an existing one
		if (material==null) {
			action = addCom;
			// set initial values for add
			sliderVals[0] = 0.75f;
			sliderVals[1] = 0.75f;
			sliderVals[2] = 0.75f;
			pVal = 20;
			sliderVals[2] = 0;
			color = new Color(0.3f, 0.3f, 0.3f);
			
		}
		else {
			action = editCom;
			// load previous values
			sliderVals[0] = material.Kd;
			sliderVals[1] = material.Ka;
			sliderVals[2] = material.Ks;
			pVal = material.KreflIndex;
			rcVal = material.Krefl;
			ka = material.Ka;
			kd = material.Kd;
			ks = material.Ks;
			color = new Color(material.rgb.r/255, material.rgb.g/255, material.rgb.b/255);
		}

		
		this.setTitle(action+" Material");

		// add the components
		Dimension hoizFiller = new Dimension(10, 0);
		contentPane.add(createButtons(), BorderLayout.SOUTH);
		contentPane.add(Box.createRigidArea(hoizFiller), BorderLayout.WEST);
		contentPane.add(Box.createRigidArea(hoizFiller), BorderLayout.EAST);

		JComponent entryFields = createMaterialFields();
		contentPane.add(entryFields);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
	public static Material createEditDialog(Component parent, MaterialsManager model, int index) {
		MaterialEditDialog dialog = new MaterialEditDialog(parent, model, index);
		return dialog.material;
	}

	public void actionPerformed(ActionEvent e) {
		// Changes Accepted
		if (action.equals(e.getActionCommand())){
			// Parse the input values
			if (!updateVals()) return;
			// Edit existing
			else {
				model.editMaterial(material, new RGB(color.getRed(), color.getGreen(), color.getBlue()), kd, ka, ks, pVal, rcVal);
			}
		}
		setVisible(false);
		dispose();
	}


	private JComponent createButtons() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		String actionLabel = ("Edit".equals(action)) ? "Save" : action;
		JButton acceptBtn = new JButton(actionLabel);
		getRootPane().setDefaultButton(acceptBtn); // make this the default button
		acceptBtn.setActionCommand(action);
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setActionCommand(cancelCom);
		panel.add(cancelBtn);
		panel.add(acceptBtn);
		acceptBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		return panel;
	}

	private JComponent createMaterialFields() {
		final JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints labelCol = new GridBagConstraints();
		Insets rightPadding = new Insets(0,0,0,20);
		labelCol.weighty = 1.0;
		labelCol.gridx = 0;
		labelCol.gridy = 0;
		labelCol.insets = rightPadding;
		labelCol.anchor = GridBagConstraints.EAST;
		GridBagConstraints fieldCol = new GridBagConstraints();
		fieldCol.weighty = 1.0;
		fieldCol.gridx = 1;
		fieldCol.gridy = 0;
		fieldCol.fill = GridBagConstraints.HORIZONTAL;

		labelCol.gridy++;
		fieldCol.gridy++;

		String[] sliderLabelStrings = { "Diffuse Contribution: ", "Ambient Contribution: ", "Specular coefficients: " };
		JLabel[] sliderLabels = new JLabel[sliderLabelStrings.length];

		// Color editor
		JLabel colorLabel = new JLabel("Ambient Color: ", JLabel.TRAILING);
		JPanel colorSection = new JPanel();
		final JPanel colorPreview = new JPanel();
		colorPreview.setPreferredSize(new Dimension(50, 50));
		colorPreview.setBackground(color);
		colorSection.add(colorPreview);
		JButton changeBtn = new JButton("Change");
		changeBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(panel, "Select New Color", color);
            if (newColor != null) { // OK was pressed
                color = newColor;
                colorPreview.setBackground(color);
                updateVals();
            }
        });
		colorSection.add(changeBtn);
		colorLabel.setLabelFor(colorSection);
		panel.add(colorLabel, labelCol);
		labelCol.gridy++;
		panel.add(colorSection, fieldCol);
		fieldCol.gridy++;

		// Sliders
		ChangeListener sliderListener = e -> {
            JSlider theJSlider = (JSlider) e.getSource();
            if (!theJSlider.getValueIsAdjusting()) {
                updateVals();
            }
        };
		sliders = new JSlider[3];
		for (int i=0; i<sliders.length; i++) {
			int initialVal = (int)(sliderVals[i]*100.0f);
			sliders[i] = new JSlider(JSlider.HORIZONTAL, 0, 100, initialVal);
			sliders[i].addChangeListener(sliderListener);
			sliders[i].setMajorTickSpacing(50);
			sliders[i].setMinorTickSpacing(10);
			sliders[i].setPaintTicks(true);
			sliders[i].setPaintLabels(true);

			sliderLabels[i] = new JLabel(sliderLabelStrings[i]);
			sliderLabels[i].setLabelFor(sliders[i]);
			panel.add(sliderLabels[i], labelCol);
			labelCol.gridy++;
			panel.add(sliders[i], fieldCol);
			fieldCol.gridy++;
		}

		JLabel pLabel = new JLabel("Reflectiveness Index: ", JLabel.TRAILING);
		pField = new JFormattedTextField(new FloatFormatter(0, Float.MAX_VALUE));
		pField.setColumns(4);
		pField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		pField.setValue(pVal);
		labelCol.gridy++;
		fieldCol.gridy++;

		pLabel.setLabelFor(pField);
		panel.add(pLabel, labelCol);
		panel.add(pField, fieldCol);

		JLabel rcLabel = new JLabel("Reflection Coefficient: ", JLabel.TRAILING);
		rcField = new JFormattedTextField(new FloatFormatter(0, Float.MAX_VALUE));
		rcField.setColumns(5);
		rcField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		rcField.setValue(rcVal);

		labelCol.gridy++;
		fieldCol.gridy++;

		rcLabel.setLabelFor(rcField);
		panel.add(rcLabel, labelCol);
		panel.add(rcField, fieldCol);

		return panel;
	}
	
	private boolean updateVals() {
		for (int i = 0; i < 3; i++){
			sliderVals[i] = ((float)sliders[i].getValue())/100.0f;
		}
		kd = sliderVals[0];
		ka = sliderVals[1];
		ks = sliderVals[2];

		try {
			pField.commitEdit();
			rcField.commitEdit();
		}
		catch (ParseException ex) {
			return false;
		}
		pVal = (Float)(pField.getValue());
		rcVal = (Float)(rcField.getValue());
		return true;
	}
}
