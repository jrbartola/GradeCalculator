package eval;


import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TableColumn;

public class Evaluator {

	protected Shell shlGradeCalculator;
	private Text classNameText;
	private Table assignTable;
	private Text assignName;
	private Text assignWeight;
	private Text assignGrade;
	private Map<String, Course> courses;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Evaluator window = new Evaluator();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlGradeCalculator.open();
		shlGradeCalculator.layout();
		while (!shlGradeCalculator.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public boolean checkAssignments(Course course) {
		int weight = 0;
		for (Assignment a : course.getAssignments()) {
			weight += a.getWeight();
		}
		
		if (weight > 100) {
			return false;
		}
		
		return true;
	}
	
	
	public void generateTab(Course newCourse, TabFolder tabFolder) {
		
		courses.put(newCourse.getName(), newCourse);
			
		final TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText(newCourse.getName());
		
		final Composite classComposite = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(classComposite);
		
		final Label classNameLabel = new Label(classComposite, SWT.CENTER);
		classNameLabel.setFont(SWTResourceManager.getFont("Courier New TUR", 18, SWT.NORMAL));
		classNameLabel.setBounds(55, 10, 149, 35);
		classNameLabel.setText(newCourse.getName());
		
		assignTable = new Table(classComposite, SWT.BORDER | SWT.FULL_SELECTION);
		assignTable.setBounds(55, 51, 342, 179);
		assignTable.setHeaderVisible(true);
		assignTable.setLinesVisible(true);
		
		TableColumn tblclmnAssignment = new TableColumn(assignTable, SWT.NONE);
		tblclmnAssignment.setWidth(179);
		tblclmnAssignment.setText("Assignment");
		
		TableColumn tblclmnWeight = new TableColumn(assignTable, SWT.NONE);
		tblclmnWeight.setWidth(76);
		tblclmnWeight.setText("Weight");
		
		TableColumn tblclmnGrade = new TableColumn(assignTable, SWT.NONE);
		tblclmnGrade.setWidth(83);
		tblclmnGrade.setText("Grade");
		
//		TableItem assignItem = new TableItem(assignTable, SWT.NONE);
//		assignItem.setText(new String[] {"Final Paper", "20%", "86"});
//		assignItem.setText("Final Paper");
		
		// BUTTON THAT ADDS AN ASSIGNMENT TO THE TABLE
		Button addAssignBtn = new Button(classComposite, SWT.NONE);
		addAssignBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Assignment newWork = null;
				
				if (assignName.getText() != "" && assignWeight.getText() != "" &&
						assignGrade.getText() != "") {
					try {
						newWork = new Assignment(assignName.getText(),
								Double.parseDouble(assignGrade.getText()),
								Double.parseDouble(assignWeight.getText()));
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(null,
							    ex.getMessage(),
							    "Invalid Input",
							    JOptionPane.ERROR_MESSAGE);
						return;
					}
					
				} else {
					JOptionPane.showMessageDialog(null,
						    "Please fill out all of the assignment fields.",
						    "Empty Fields",
						    JOptionPane.ERROR_MESSAGE);
					// ERROR CASES FOR ASSIGNMENT VALUES
					return;
				}
				
				Course curCourse = courses.get(classNameLabel.getText());
				curCourse.addAssignment(newWork);
				assignName.setText("");
				assignWeight.setText("");
				assignGrade.setText("");
				
				if (!checkAssignments(curCourse)) {
					curCourse.removeAssignment(newWork);
					JOptionPane.showMessageDialog(null,
						    "The total course weight cannot exceed 100%!",
						    "Weight Overload",
						    JOptionPane.ERROR_MESSAGE);
					// ERROR CASE FOR TOO MUCH WEIGHT
					return;
				}
				
				
				TableItem assignItem = new TableItem(assignTable, SWT.NONE);
				assignItem.setText(new String[] {newWork.getName(), newWork.getWeight() + "", newWork.getVal() + ""});
				
			}
		});
		addAssignBtn.setFont(SWTResourceManager.getFont("Segoe Print", 11, SWT.NORMAL));
		addAssignBtn.setBounds(55, 273, 149, 27);
		addAssignBtn.setText("Add Assignment...");
		
		Button btnCalculateGrade = new Button(classComposite, SWT.NONE);
		
		btnCalculateGrade.setText("Calculate Grade");
		btnCalculateGrade.setFont(SWTResourceManager.getFont("Segoe Print", 11, SWT.NORMAL));
		btnCalculateGrade.setBounds(248, 273, 149, 27);
		
		final Label lblFinal = new Label(classComposite, SWT.CENTER);
		lblFinal.setAlignment(SWT.RIGHT);
		lblFinal.setText("00.00%: F");
		lblFinal.setFont(SWTResourceManager.getFont("Courier Std", 18, SWT.BOLD));
		lblFinal.setBounds(248, 10, 149, 35);
		
		// CALCULATES THE GRADE AVERAGE FOR THE COURSE
		btnCalculateGrade.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (assignTable.getItemCount() == 0) {
					JOptionPane.showMessageDialog(null,
						    "You must provide at least one assignment for\n"
						    + "the course in order to calculate an average.",
						    "No Assignments",
						    JOptionPane.ERROR_MESSAGE);
				} else {
					// CALCULATE COURSE AVERAGE
					Course curCourse = courses.get(classNameLabel.getText());
					String letterGrade = curCourse.getLetterGrade();
					lblFinal.setText(letterGrade);
				}
			}
		});
		
		assignName = new Text(classComposite, SWT.BORDER);
		assignName.setBounds(55, 246, 155, 21);
		
		assignWeight = new Text(classComposite, SWT.BORDER);
		assignWeight.setBounds(244, 246, 48, 21);
		
		Label label = new Label(classComposite, SWT.NONE);
		label.setBounds(298, 249, 17, 15);
		label.setText("%");
		
		assignGrade = new Text(classComposite, SWT.BORDER);
		assignGrade.setBounds(327, 246, 48, 21);
		
		Label label_1 = new Label(classComposite, SWT.NONE);
		label_1.setText("%");
		label_1.setBounds(381, 249, 16, 15);
		
		Label lblAdd = new Label(classComposite, SWT.NONE);
		lblAdd.setBounds(11, 249, 38, 15);
		lblAdd.setText("Add...");
		
		// RESET THE TABLE ITEMS TO BE EMPTY
		Button refreshButton = new Button(classComposite, SWT.NONE);
		refreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Course curCourse = courses.get(classNameLabel.getText());
				curCourse.removeAssignments();
				assignTable.removeAll();
				lblFinal.setText("00.00%: F");
			}
		});
		System.out.println(tabFolder.getItem(0).getText());
		refreshButton.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		refreshButton.setText("Reset");
		refreshButton.setToolTipText("Reset the table");
		refreshButton.setBounds(11, 16, 33, 25);
	}
	
	public Map parseCSV(File file) {
		Map<Double, String> gradeScale = new HashMap<>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {

			    // use comma as separator
				String[] letterVal = line.split(cvsSplitBy);
				gradeScale.put(Double.parseDouble(letterVal[0]), letterVal[1]);
				

			}
		} catch (IOException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null,
				    "You must provide the CSV gradelog in the form\n"
				    + "of \"Grade,Letter\".",
				    "Invalid CSV",
				    JOptionPane.ERROR_MESSAGE);
		} finally {
			if (br != null) {
				try {
					br.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return gradeScale;
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		
		final JFileChooser csvUp = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(null, "csv");
		csvUp.setFileFilter(filter);
		
		courses = new HashMap<String, Course>();
		
		shlGradeCalculator = new Shell();
		shlGradeCalculator.setToolTipText("Calculates a class average based off of user-recieved statistics");
		shlGradeCalculator.setSize(475, 400);
		shlGradeCalculator.setText("Grade Calculator");
		shlGradeCalculator.setLayout(new GridLayout(1, false));
		
		final TabFolder tabFolder = new TabFolder(shlGradeCalculator, SWT.NONE);
		GridData gd_tabFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_tabFolder.widthHint = 457;
		gd_tabFolder.heightHint = 348;
		tabFolder.setLayoutData(gd_tabFolder);
		
		TabItem tbtmAddClass = new TabItem(tabFolder, SWT.NONE);
		tbtmAddClass.setText("Add Class...");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmAddClass.setControl(composite);
		
		Label lblEnterClassesAs = new Label(composite, SWT.CENTER);
		lblEnterClassesAs.setFont(SWTResourceManager.getFont("Minion Pro Med", 14, SWT.BOLD));
		lblEnterClassesAs.setBounds(129, 52, 219, 41);
		lblEnterClassesAs.setText("Enter classes as you wish:");
		
		classNameText = new Text(composite, SWT.BORDER);
		classNameText.setBounds(182, 101, 166, 21);
		
		Label lblClassName = new Label(composite, SWT.NONE);
		lblClassName.setFont(SWTResourceManager.getFont("Adobe Song Std L", 12, SWT.NORMAL));
		lblClassName.setBounds(67, 101, 86, 21);
		lblClassName.setText("Class Name:");
		
		Label lblGradingScalefrom = new Label(composite, SWT.NONE);
		lblGradingScalefrom.setText("Grading Scale:");
		lblGradingScalefrom.setFont(SWTResourceManager.getFont("Adobe Song Std L", 12, SWT.NORMAL));
		lblGradingScalefrom.setBounds(63, 155, 90, 21);
		
		Button btnSubmitCsv = new Button(composite, SWT.NONE);
		
		btnSubmitCsv.setBounds(166, 153, 80, 25);
		btnSubmitCsv.setText("Submit CSV");
		
		final Label fileLabel = new Label(composite, SWT.NONE);
		fileLabel.setBounds(265, 158, 144, 15);
		fileLabel.setText("No File");
		
		Button addTabBtn = new Button(composite, SWT.NONE);
		addTabBtn.setBounds(173, 224, 118, 41);
		addTabBtn.setText("Add Class");
		// ADD CLASS TO LIST OF TABS
		addTabBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (classNameText.getText() == "") {
					JOptionPane.showMessageDialog(null,
						    "You must provide a valid name for your course.",
						    "No Class Name",
						    JOptionPane.ERROR_MESSAGE);
				} else {
					Course newClass;
					try {
						if (fileLabel.getText() != "No File") {
							File csv = csvUp.getSelectedFile();
							newClass = new Course(classNameText.getText(), parseCSV(csv));
						
						} else {
							newClass = new Course(classNameText.getText());
						}
						
						fileLabel.setText("No File");
						classNameText.setText("");
						generateTab(newClass, tabFolder);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
							    "You must provide the CSV gradelog in the form\n"
							    + "of \"Grade,Letter\".",
							    "Invalid CSV",
							    JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
/////////////////////////////////////////////////////////////////////////////////////
//		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
//		tbtmNewItem.setText("Anthropology");
//		
//		Composite classComposite = new Composite(tabFolder, SWT.NONE);
//		tbtmNewItem.setControl(classComposite);
//		classComposite.setLayout(null);
//		
//		Label classNameLabel = new Label(classComposite, SWT.CENTER);
//		classNameLabel.setBounds(55, 10, 149, 35);
//		classNameLabel.setFont(SWTResourceManager.getFont("Courier New TUR", 18, SWT.NORMAL));
//		classNameLabel.setText("Anthropology");
//		
//		assignTable = new Table(classComposite, SWT.BORDER | SWT.FULL_SELECTION);
//		assignTable.setBounds(55, 51, 342, 179);
//		assignTable.setHeaderVisible(true);
//		assignTable.setLinesVisible(true);
//		
//		TableColumn tblclmnAssignment = new TableColumn(assignTable, SWT.NONE);
//		tblclmnAssignment.setWidth(179);
//		tblclmnAssignment.setText("Assignment");
//		
//		TableColumn tblclmnWeight = new TableColumn(assignTable, SWT.NONE);
//		tblclmnWeight.setWidth(76);
//		tblclmnWeight.setText("Weight");
//		
//		TableColumn tblclmnGrade = new TableColumn(assignTable, SWT.NONE);
//		tblclmnGrade.setWidth(83);
//		tblclmnGrade.setText("Grade");
//		
////		TableItem assignItem = new TableItem(assignTable, SWT.NONE);
////		assignItem.setText(new String[] {"Final Paper", "20%", "86"});
////		assignItem.setText("Final Paper");
//		
//		Button addAssignBtn = new Button(classComposite, SWT.NONE);
//		addAssignBtn.setBounds(55, 273, 149, 27);
//		addAssignBtn.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				Assignment newWork = null;
//				if (assignName.getText() != "" && assignWeight.getText() != "" &&
//						assignGrade.getText() != "") {
//					newWork = new Assignment(assignName.getText(),
//							Double.parseDouble(assignWeight.getText()),
//							Double.parseDouble(assignGrade.getText()));
//				} else {
//					// ERROR CASES FOR ASSIGNMENT VALUES
//				}
//				
//				TableItem assignItem = new TableItem(assignTable, SWT.NONE);
//				assignItem.setText(new String[] {newWork.getName(), (newWork.getWeight() * 100) + "", newWork.getVal() + ""});
//				assignItem.setText(newWork.getName());
//			}
//		});
//		addAssignBtn.setFont(SWTResourceManager.getFont("Segoe Print", 11, SWT.NORMAL));
//		addAssignBtn.setText("Add Assignment...");
//		
//		Button btnCalculateGrade = new Button(classComposite, SWT.NONE);
//		btnCalculateGrade.setBounds(248, 273, 149, 27);
//		btnCalculateGrade.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if (assignTable.getItemCount() == 0) {
//					JOptionPane.showMessageDialog(null,
//						    "You must provide at least one assignment for\n"
//						    + "the course in order to calculate an average.",
//						    "No Assignments",
//						    JOptionPane.ERROR_MESSAGE);
//				} else {
//					// CALCULATE COURSE AVERAGE
//				}
//			}
//		});
//		btnCalculateGrade.setText("Calculate Grade");
//		btnCalculateGrade.setFont(SWTResourceManager.getFont("Segoe Print", 11, SWT.NORMAL));
//		
//		Label lblFinal = new Label(classComposite, SWT.CENTER);
//		lblFinal.setBounds(248, 10, 149, 35);
//		lblFinal.setAlignment(SWT.RIGHT);
//		lblFinal.setText("00.00%: F");
//		lblFinal.setFont(SWTResourceManager.getFont("Courier Std", 18, SWT.BOLD));
//		
//		assignName = new Text(classComposite, SWT.BORDER);
//		assignName.setBounds(55, 246, 155, 21);
//		
//		assignWeight = new Text(classComposite, SWT.BORDER);
//		assignWeight.setBounds(244, 246, 48, 21);
//		
//		Label label = new Label(classComposite, SWT.NONE);
//		label.setBounds(298, 249, 17, 15);
//		label.setText("%");
//		
//		assignGrade = new Text(classComposite, SWT.BORDER);
//		assignGrade.setBounds(327, 246, 48, 21);
//		
//		Label label_1 = new Label(classComposite, SWT.NONE);
//		label_1.setBounds(381, 249, 16, 15);
//		label_1.setText("%");
//		
//		Label lblAdd = new Label(classComposite, SWT.NONE);
//		lblAdd.setBounds(11, 249, 38, 15);
//		lblAdd.setText("Add...");
//		
//		Button refreshButton = new Button(classComposite, SWT.NONE);
//		refreshButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				
//				assignTable.removeAll();
//			}
//		});
//		refreshButton.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
//		refreshButton.setText("Reset");
//		refreshButton.setToolTipText("Reset the table");
//		refreshButton.setBounds(11, 16, 33, 25);
		
		
		////////////////////////////////////////////////////////////////////
		
		Menu menu = new Menu(shlGradeCalculator, SWT.BAR);
		shlGradeCalculator.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.setText("Exit");
		
		MenuItem mntmAbout = new MenuItem(menu, SWT.CASCADE);
		mntmAbout.setText("About");
		
		Menu menu_2 = new Menu(mntmAbout);
		mntmAbout.setMenu(menu_2);
		
		MenuItem mntmHelp = new MenuItem(menu_2, SWT.NONE);
		mntmHelp.setText("Help");
		
		btnSubmitCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				int returnVal = csvUp.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selected = csvUp.getSelectedFile();
					fileLabel.setText(selected.getName());
					
				}
			}
		});
	}
}
