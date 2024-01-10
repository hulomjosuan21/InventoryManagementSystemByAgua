package myclass;

import com.toedter.calendar.JDateChooser;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultFormatter;

public class Helper {
    public static String dateFormat = "yyyy-MM-dd";
    public static char currency = '₱';
    private final static String userIdExtension = "wan";
    public static Object get_AddedDate(JDateChooser g_date){
        SimpleDateFormat dateFormats = new SimpleDateFormat(dateFormat);
        if(g_date.getDate() != null){
            return dateFormats.format(g_date.getDate());
        }
        return java.sql.Date.valueOf(LocalDate.now());
    }
    
    public static String getNewValTable(javax.swing.JTable table){
        int editedRow = table.getEditingRow();
        int editedColumn = table.getEditingColumn();
        
        table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        String editedValue = null;
        
        if (editedRow != -1 && editedColumn != -1) {
            TableCellEditor editor = table.getCellEditor(editedRow, editedColumn);
            if (editor != null) {
                editor.stopCellEditing();
            }
            editedValue = model.getValueAt(editedRow, editedColumn).toString();
        }
        
        return editedValue;
    }
    
    public static Object get_RecordID(javax.swing.JTable tb){
        try{
            return tb.getValueAt(tb.getSelectedRow(), 0);
        }catch(ArrayIndexOutOfBoundsException e){
            return null;
        }
    } 
    
    public static Object[] get_RecordIDs(javax.swing.JTable tb){
        Object[] recordIDs = new Object[tb.getSelectedRowCount()];
        int[] selectedRows = tb.getSelectedRows();

        for (int i = 0; i < selectedRows.length; i++) {
            try {
                recordIDs[i] = tb.getValueAt(selectedRows[i], 0);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                recordIDs[i] = 0; 
            }
        }
        return recordIDs;
    } 
    
    public static boolean has_NoZeroVal(Object[] array){
        for(Object i : array){
            if(i == "0"){
                return false;
            }
        }
        return true;
    }
    
    public static String first_LetterUpperCase(String text){
        if (text.isEmpty()) {
            return null; 
        } else {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
    }  
    
    public static boolean isValueExists(String valueToCheck, String column,String table){
        boolean exist = false;
        String query = "SELECT * FROM "+table+" WHERE "+column+" = LOWER(?)";
        try{
            Database.prepare = Database.connection.prepareStatement(query);
            Database.prepare.setString(1, valueToCheck.toLowerCase());
            
            Database.result = Database.prepare.executeQuery();
            
            if(Database.result.next()){
                exist = true;
            }
            
            Database.prepare.close();
            Database.result.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return exist;
    }
   
    public static String[] getAllValue(JTextField addProduct, JTextField addDescription, JTextField addQuantity, JComboBox addCategory, JTextField addPrice, JDateChooser addDate,Component parentComponent) {
        try {
            String getProduct = first_LetterUpperCase(addProduct.getText().trim());
            String getDescription = first_LetterUpperCase(addDescription.getText().trim());
            String getQuantity = addQuantity.getText();
            String getCategory = addCategory.getSelectedItem().toString();
            String getPrice = addPrice.getText();

            if (getProduct.isEmpty()) {
                JOptionPane.showMessageDialog(parentComponent, "Product name cannot be empty.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            } else if (isValueExists(getProduct, Database.inventoryColumns[2], Database.inventoryTable)) {
                JOptionPane.showMessageDialog(parentComponent, "Product with this name already exists.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (getDescription.isEmpty()) {
                JOptionPane.showMessageDialog(parentComponent, "Description cannot be empty.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (getQuantity.isEmpty() || Integer.parseInt(getQuantity) < 1) {
                JOptionPane.showMessageDialog(parentComponent, "Quantity must be greater than 0.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (getPrice.isEmpty() || Double.parseDouble(getPrice) < 1.0) {
                JOptionPane.showMessageDialog(parentComponent, "Price must be greater than 0.0.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return new String[]{getCategory, getProduct, getDescription, getQuantity, getPrice, get_AddedDate(addDate).toString()};
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentComponent, "Invalid number format. Please enter valid numeric values.","Message", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, "Invalid input. Please check your entries.","Message", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public static String getComboxFromTable(JTable table) {
        int row = table.getSelectedRow();
        TableCellEditor editor = table.getCellEditor(row, 1);    
        if (editor instanceof DefaultCellEditor) {
            Component editorComponent = ((DefaultCellEditor) editor).getComponent();
            if (editorComponent instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) editorComponent;
                Object selectedItem = comboBox.getSelectedItem();
                if (selectedItem != null) {
                    return selectedItem.toString();
                }
            }
        }
        return ""; 
    }
    
    public static String getCurrentDate(JDateChooser g_date) {
        if (g_date == null || g_date.getDate() == null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            return dateFormat.format(new Date());
        } else {
            Date selectedDate = g_date.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            return dateFormat.format(selectedDate);
        }
    }

    public static String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return now.format(formatter);
    } 
   
    public static String generateReceipt(DefaultTableModel model, String getDate) {
        StringBuilder receipt = new StringBuilder();

        receipt.append("                                    Josuan\n");
        receipt.append("----------------------------------------------------------------\n");
        receipt.append("                                    Receipt\n");
        receipt.append("----------------------------------------------------------------\n");

        String currentDate = getDate;
        String currentTime = Helper.getCurrentTime();
        receipt.append("Date: ").append(currentDate).append("\n");
        receipt.append("Time: ").append(currentTime).append("\n\n");

        receipt.append("Item                                 Quantity   Price\n");
        receipt.append("----------------------------------------------------------------\n");

        DecimalFormat df = new DecimalFormat("#.00");
        for (int i = 0; i < model.getRowCount(); i++) {
            String itemName = (String) model.getValueAt(i, 0);
            double price = (double) model.getValueAt(i, 1);
            String formattedDiscount = (String) model.getValueAt(i, 2);
            int quantity = ((Number) model.getValueAt(i, 3)).intValue(); // Quantity at index 3
            double subtotal = ((Number) model.getValueAt(i, 4)).doubleValue(); // Subtotal at index 4
            double total = ((Number) model.getValueAt(i, 5)).doubleValue(); // Total at index 5

            receipt.append(String.format("%d. %-30s x%-9d "+Helper.currency+"%.2f\n", i + 1, itemName, quantity, price));
        }
        receipt.append("----------------------------------------------------------------\n");
        double subtotalSum = 0.0;
        double totalSum = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            subtotalSum += ((Number) model.getValueAt(i, 4)).doubleValue(); // Summing up subtotals
            totalSum += ((Number) model.getValueAt(i, 5)).doubleValue(); // Summing up totals
        }

        receipt.append(String.format("Subtotal:                 "+Helper.currency+"%.2f\n", subtotalSum));
        receipt.append(String.format("Total:                    "+Helper.currency+"%.2f\n", totalSum));
        receipt.append("----------------------------------------------------------------\n\n");
        receipt.append("Thank you for your purchase!");

        return receipt.toString();
    }
    
    public static String generateInvoiceNumber() {
        String uniqueID = UUID.randomUUID().toString();
        String invoiceNumber = uniqueID.substring(0, 8) + ".wan";
        return invoiceNumber;
    }
    
    public static double convertPercentageToNumber(String percentString) {
        if(!percentString.equals("No discount")){
            String numericString = percentString.replaceAll("%", "");
        
            double numericValue = Double.parseDouble(numericString);
            double actualNumber = numericValue / 100.0;

            return actualNumber;
        }else{
            return 0.0;
        }
    }
    
    public static String userIdGenerator(int numberOfUsers) {
        String formattedNumber = String.format("%03d", numberOfUsers);
        return "user" + formattedNumber + "."+userIdExtension;
    }
    
    //Image Methods
    public static String makeImageName(String name) {
        String lowercaseName = name.toLowerCase().replace(" ", "_");
        return java.sql.Date.valueOf(LocalDate.now()) + "_" + lowercaseName + ".jpg";
    }

    
    public static File getImage(Component p_c) {
        JFileChooser fileChooser = new JFileChooser();
        String picturesDir = System.getProperty("user.home");
        fileChooser.setCurrentDirectory(new File(picturesDir)); 
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg"));
        int result = fileChooser.showOpenDialog(p_c);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
    
    public static void addTheImage(File selectedFile, Path destination){
        if(destination != null){
            try{
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e) {

            }
        }
    }
    
    public static AbstractMap.SimpleEntry<String, Path> generateImageName(String name, File g_img) {
        File selectedFile = g_img;

        if (selectedFile != null) {
            String imageName = makeImageName(name);
            Path destination = Paths.get("src/images", imageName);

            try {
                if (Files.exists(destination)) {
                    String baseName = imageName.substring(0, imageName.lastIndexOf('.'));
                    String extension = imageName.substring(imageName.lastIndexOf('.'));
                    int count = 1;

                    while (Files.exists(destination)) {
                        imageName = baseName + "_(" + count + ")" + extension;
                        destination = Paths.get("src/images", imageName);
                        count++;
                    }
                }
                return new AbstractMap.SimpleEntry<>(imageName, destination);
            } catch (Exception e) {
                return new AbstractMap.SimpleEntry<>("nullProfile.jpg", null);
            }
        }
        return new AbstractMap.SimpleEntry<>("nullProfile.jpg", null);
    }
    
    public static void deleteImage(String imageName) {
        String folderPath = "src/images/";
        File imageFile = new File(folderPath + imageName);
        if (imageFile.exists()) {
            imageFile.delete();
        }
    }
    
    // User Methods
    public static Object[] getAllUserInput(String userId,String firstName,String lastName,String userName,
            String password,String confirmPassword,String gender, 
            com.toedter.calendar.JDateChooser birthDate, String imgName,int firstUserPos, Component parentComponent) {

        if (firstName == null || lastName == null || userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || gender == null || birthDate == null) {
            JOptionPane.showMessageDialog(parentComponent, "Please fill in all fields.");
            return null;
        }

        if (!validateUsernameValue(userName)) {
            JOptionPane.showMessageDialog(parentComponent, "Invalid username format.");
            return null;
        }

        if (!validatePassword(password, parentComponent)) {
            return null;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(parentComponent, "Passwords do not match.");
            return null;
        }
        return new Object[]{userId, firstName, lastName, userName, confirmPassword, get_AddedDate(birthDate), gender, imgName, firstUserPos};
    }
    
    public static boolean validateUsernameValue(String _username){
        String pattern = "^[a-zA-Z]+@[0-9]+$";
        
        Pattern regex = Pattern.compile(pattern);
        
        Matcher matcher = regex.matcher(_username);
        
        return matcher.matches();
    }   
    
    public static boolean validatePassword(String _password, Component parentComponent) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(_password);

        boolean isPhoneNumber = _password.matches("\\d{10}"); // Assuming a 10-digit phone number
        
        if (isPhoneNumber) {
            JOptionPane.showMessageDialog(parentComponent, "You've used a phone number as a password. Please choose a stronger password.");
            return false;
        }

        if (!matcher.matches()) {
            if (!_password.matches(".*[0-9].*")) {
                JOptionPane.showMessageDialog(parentComponent, "Password should contain at least one digit.");
                return false;
            }

            if (!_password.matches(".*[a-z].*")) {
                JOptionPane.showMessageDialog(parentComponent, "Password should contain at least one lowercase letter.");
                return false;
            }

            if (!_password.matches(".*[A-Z].*")) {
                JOptionPane.showMessageDialog(parentComponent, "Password should contain at least one uppercase letter.");
                return false;
            }

            if (!_password.matches(".*[@#$%^&+=].*")) {
                JOptionPane.showMessageDialog(parentComponent, "Password should contain at least one special character (@#$%^&+=).");
                return false;
            }

            if (_password.length() < 8) {
                JOptionPane.showMessageDialog(parentComponent, "Password should be at least 8 characters long.");
                return false;
            }

            if (_password.contains(" ")) {
                JOptionPane.showMessageDialog(parentComponent, "Password should not contain spaces.");
                return false;
            }

            return false;
        }

        return true;
    }

    public static String getComboxFromTable(JTable table, int colIdx) {
        int row = table.getSelectedRow();
        TableCellEditor editor = table.getCellEditor(row, colIdx);    
        if (editor instanceof DefaultCellEditor) {
            Component editorComponent = ((DefaultCellEditor) editor).getComponent();
            if (editorComponent instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) editorComponent;
                Object selectedItem = comboBox.getSelectedItem();
                if (selectedItem != null) {
                    return selectedItem.toString();
                }
            }
        }
        return ""; 
    }
    
    public static class IntCustomJSpinner extends DefaultCellEditor {

        private JSpinner input;

        public IntCustomJSpinner() {
            super(new JCheckBox());
            input = new JSpinner();
            SpinnerNumberModel numberModel = (SpinnerNumberModel) input.getModel();
            numberModel.setMinimum(0);
            numberModel.setMaximum(4);
            JSpinner.NumberEditor editor = (JSpinner.NumberEditor) input.getEditor();
            DefaultFormatter formatter = (DefaultFormatter) editor.getTextField().getFormatter();
            formatter.setCommitsOnValidEdit(true);
            editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);

            editor.getTextField().addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                        e.consume();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            super.getTableCellEditorComponent(table, value, isSelected, row, column);
            int qty = Integer.parseInt(value.toString());
            input.setValue(qty);
            return input;
        }

        @Override
        public Object getCellEditorValue() {
            return input.getValue();
        }
    }
}
