import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Student {
    private String name;
    private String course;

    public Student(String name, String course) {
        this.name = name;
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }

    public String toFileString() {
        return name + "," + course;
    }
}

public class StudentInfoApp {

    private JFrame frame;
    private JTextField nameField, searchField;
    private JComboBox<String> courseBox;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private List<Student> studentList = new ArrayList<>();

    private final String FILE_NAME = "students.txt";

    public StudentInfoApp() {

        frame = new JFrame("Student Information System");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        setupMenuBar();

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Student Entry"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(15);
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Course:"), gbc);

        gbc.gridx = 1;
        courseBox = new JComboBox<>(new String[]{
                "Math","Science","History","Computer Science"
        });
        inputPanel.add(courseBox, gbc);

        JPanel btnPanel = new JPanel();

        JButton saveButton = new JButton("Save");
        JButton clearButton = new JButton("Clear");

        btnPanel.add(saveButton);
        btnPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        inputPanel.add(btnPanel, gbc);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search Name");

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        String[] columns = {"Student Name","Course"};

        tableModel = new DefaultTableModel(columns,0);
        studentTable = new JTable(tableModel);

        rowSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(rowSorter);

        loadFromFile();

        saveButton.addActionListener(e -> handleSave());
        clearButton.addActionListener(e -> clearFields());

        searchBtn.addActionListener(e -> {
            String text = searchField.getText();

            if(text.trim().length()==0)
                rowSorter.setRowFilter(null);
            else
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)"+text));
        });

        JPanel topContainer = new JPanel(new BorderLayout());

        topContainer.add(inputPanel, BorderLayout.NORTH);
        topContainer.add(searchPanel, BorderLayout.SOUTH);

        frame.add(topContainer, BorderLayout.NORTH);
        frame.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setupMenuBar() {

        JMenuBar mb = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");

        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        aboutItem.addActionListener(e ->
                JOptionPane.showMessageDialog(frame,
                        "Student Info System v2.0"));

        helpMenu.add(aboutItem);

        mb.add(fileMenu);
        mb.add(helpMenu);

        frame.setJMenuBar(mb);
    }

    private void handleSave() {

        String name = nameField.getText().trim();
        String course = (String) courseBox.getSelectedItem();

        if(name.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Name is required!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student student = new Student(name, course);

        studentList.add(student);

        tableModel.addRow(new Object[]{
                student.getName(),
                student.getCourse()
        });

        saveToFileAsync();

        clearFields();
    }

    private void clearFields() {

        nameField.setText("");
        courseBox.setSelectedIndex(0);
        searchField.setText("");

        rowSorter.setRowFilter(null);
    }

    private void saveToFileAsync() {

        SwingWorker<Void,Void> worker = new SwingWorker<>() {

            protected Void doInBackground() throws Exception {

                try(PrintWriter writer =
                            new PrintWriter(new FileWriter(FILE_NAME))) {

                    for(Student s : studentList)
                        writer.println(s.toFileString());

                } catch(IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        worker.execute();
    }

    private void loadFromFile() {

        File file = new File(FILE_NAME);

        if(!file.exists())
            return;

        try(BufferedReader reader =
                    new BufferedReader(new FileReader(file))) {

            String line;

            while((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                Student student = new Student(data[0], data[1]);

                studentList.add(student);

                tableModel.addRow(new Object[]{
                        student.getName(),
                        student.getCourse()
                });
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new StudentInfoApp());

    }
}