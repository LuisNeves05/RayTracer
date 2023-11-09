package view;

import appModel.CollectionList;
import domainModel.*;
import domainModel.Object3D;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainGUI implements ActionListener {
	public static final Color errorColor = new Color(1.0f, 0.65f, 0.65f);
	private RayTracer tracer;
	private Container mainFrame;
	private RayTracerCanvas canvas;
	private CollectionList<Object3D> shapesList;
	private CollectionList<Light> lightsList;
	private CollectionList<Material> materialsList;
	private final int maxDepth = 15;
	private final SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, maxDepth, 1);;
	private final JPanel recPanel = new JPanel(new FlowLayout(FlowLayout.LEFT ,10, 0));

	public MainGUI() {
		tracer = new RayTracer(300, 300, 60, 0, 0.5f, 0.3f, 0.4f, new Camera(new Vector3D(0,0,30),30,30));
		createGUI();
	}

	private void createGUI() {
		JFrame frame = new JFrame("Ray Tracer");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame = frame.getContentPane();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel centerPanel = new JPanel();
		canvas = new RayTracerCanvas(frame, tracer);
		centerPanel.add(canvas);

		JPanel bottomPanel = new JPanel(new GridBagLayout());

		// set up layout
		Insets midPadding = new Insets(10,10,0,10);
		Insets noPadding = new Insets(0,0,0,0);
		GridBagConstraints labelRow = new GridBagConstraints();
		labelRow.weightx = 1.0;
		labelRow.gridy = 0;
		labelRow.insets = noPadding;
		labelRow.anchor = GridBagConstraints.PAGE_END;
		GridBagConstraints listRow = new GridBagConstraints();
		listRow.weightx = 1.0;
		listRow.gridy = 1;
		listRow.insets = midPadding;
		listRow.fill = GridBagConstraints.BOTH;
		GridBagConstraints buttonRow = new GridBagConstraints();
		buttonRow.weightx = 1.0;
		buttonRow.gridy = 2;
		buttonRow.insets = noPadding;
		buttonRow.anchor = GridBagConstraints.PAGE_START;
		Dimension colSize = new Dimension(200,100);

		// lights collection
		LightsToolbar lightsToolbar = new LightsToolbar(frame);
		lightsList = new CollectionList<>(tracer.lights, lightsToolbar);
		tracer.lightsManager.addObserver(lightsList.getModel());
		JLabel lightsLabel = new JLabel("Lights");
		lightsLabel.setLabelFor(shapesList);
		labelRow.gridx = listRow.gridx = buttonRow.gridx = 1;
		bottomPanel.add(lightsLabel, labelRow);
		JScrollPane lights = new JScrollPane(lightsList);
		lights.setPreferredSize(colSize);
		bottomPanel.add(lights, listRow);
		bottomPanel.add(lightsToolbar, buttonRow);

		// materials collection
		MaterialsToolbar materialsToolbar = new MaterialsToolbar(frame);
		materialsList = new CollectionList<Material>(tracer.materials, materialsToolbar);
		tracer.materialsManager.addObserver(materialsList.getModel());
		JLabel materialsLabel = new JLabel("Materials");
		materialsLabel.setLabelFor(shapesList);
		labelRow.gridx = listRow.gridx = buttonRow.gridx = 2;
		bottomPanel.add(materialsLabel, labelRow);
		JScrollPane materials = new JScrollPane(materialsList);
		materials.setPreferredSize(colSize);
		bottomPanel.add(materials, listRow);
		bottomPanel.add(materialsToolbar, buttonRow);

		// spinner Recursion depth
		JSpinner spinner = new JSpinner(spinnerModel);
		JLabel recLabel = new JLabel("Recursion depth:");
		recLabel.setLabelFor(shapesList);
		labelRow.gridx = listRow.gridx = buttonRow.gridx = 0;
		recPanel.add(recLabel);
		recPanel.add(spinner);

		mainPanel.add(centerPanel);
		mainPanel.add(new JSeparator());
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(bottomPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(new JSeparator());
		mainPanel.add(recPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(createOptionsPane());
		mainFrame.add(mainPanel);
		frame.pack();
		frame.setVisible(true);
	}

	private JComponent createOptionsPane() {
		JPanel panel = new JPanel();

		JButton bgColorBtn = new JButton("Change Background Color");
		bgColorBtn.setActionCommand("Color");
		bgColorBtn.addActionListener(this);
		panel.add(bgColorBtn);

		JButton sizeBtn = new JButton("Change Size / Field of View");
		sizeBtn.setActionCommand("Size");
		sizeBtn.addActionListener(this);
		panel.add(sizeBtn);

		JButton saveBtn = new JButton("Save");
		saveBtn.setActionCommand("Save");
		saveBtn.addActionListener(this);
		panel.add(saveBtn);

		JButton loadBtn = new JButton("Load");
		loadBtn.setActionCommand("Load");
		loadBtn.addActionListener(this);
		panel.add(loadBtn);

		JButton startBtn = new JButton("Start");
		startBtn.setActionCommand("Start");
		startBtn.addActionListener(this);
		recPanel.add(startBtn);

		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		/*
		 * Change background color
		 */
        switch (command) {
            case "Color":
                Color newColor = JColorChooser.showDialog(mainFrame, "Select Background Color", new Color(tracer.bgColor.r/255, tracer.bgColor.g/255, tracer.bgColor.b/255));
                if (newColor != null) { // OK was pressed
                    float color[] = newColor.getColorComponents(null);
                    tracer.setBackgroundRGB(new RGB(color[0], color[1], color[2]));
					tracer.alterDimensions(tracer.Hres,tracer.Vres, tracer.fov);
                }
                break;
            /*
             * Change size / field-of-view
             */
            case "Size":
                new SizeDialog(mainFrame, tracer);
                break;
			/*
			 * Load Image
			 */
			case "Load":
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (.txt)", "txt"));
				int loadResult = fileChooser.showOpenDialog(mainFrame);
				tracer.clearScene();
				if (loadResult == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					Parser.load(selectedFile, tracer);
				}
				break;
            /*
             * Save Image
             */
            case "Save":
                JFileChooser chooser = new JFileChooser();
                String extension = "png";
                chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG image", extension));
                int result = chooser.showSaveDialog(mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File filepath = chooser.getSelectedFile();
                    String name = filepath.getName();
                    // Validate that the file extension is correct
                    if (!name.toLowerCase().endsWith("." + extension.toLowerCase())) {
                        name += "." + extension;
                        filepath = new File(filepath.getParent(), name);
                    }
                    // Check if a file already exists at this location
                    if (filepath.exists()) {
                        int response = JOptionPane.showConfirmDialog(mainFrame, "A file by that name already exists.\nAre you sure you wish to overwrite the existing file?", "Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (response == JOptionPane.CANCEL_OPTION) return;
                    }
                    // Save the image
                    canvas.saveImage(filepath, extension);
                    JOptionPane.showMessageDialog(mainFrame, "Image saved to " + filepath.getPath() + ".", "Image Saved", JOptionPane.PLAIN_MESSAGE);
                }
			/*
			 * Change size / field-of-view
			 */
			case "Start":
				int newMaxRecursionDepth = (int) spinnerModel.getValue();
				tracer.setMaxRecursionDepth(newMaxRecursionDepth);
				tracer.alterDimensions(tracer.Hres,tracer.Vres, tracer.fov);
                break;
        }
	}

	@SuppressWarnings("serial")
	class MaterialsToolbar extends CollectionToolbar {
		MaterialsToolbar(Component parent) { super(parent, true); }
		void addBtnClicked() {

		}

		@Override
		void removeBtnClicked() {

		}

		void editBtnClicked() {
			int index = materialsList.getSelectedIndex();
			MaterialEditDialog.createEditDialog(parent, tracer.materialsManager, index);
			tracer.alterDimensions(tracer.Hres,tracer.Vres, tracer.fov);
		}
	}

	class LightsToolbar extends CollectionToolbar {
		LightsToolbar(Component parent) { super(parent); }
		void addBtnClicked() {
			Light l = LightEditDialog.createAddDialog(parent, tracer.lightsManager);
			lightsList.setSelectedValue(l, true);
			tracer.alterDimensions(tracer.Hres,tracer.Vres, tracer.fov);
		}
		void editBtnClicked() {
			int index = lightsList.getSelectedIndex();
			LightEditDialog.createEditDialog(parent, tracer.lightsManager, index);
			tracer.alterDimensions(tracer.Hres,tracer.Vres, tracer.fov);
		}
		void removeBtnClicked() {
			int index = lightsList.getSelectedIndex();
			if (index != -1) {
				tracer.lightsManager.removeLight(index);
				tracer.alterDimensions(tracer.Hres,tracer.Vres, tracer.fov);
				lightsList.clearSelection();
			}
		}
	}

	public static void main(String[] args) {
		new MainGUI();
	}
}