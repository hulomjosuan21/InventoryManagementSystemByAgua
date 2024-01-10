package myclass;

import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Database {
    private final static String url = "jdbc:mysql://localhost:3306/imsdb?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private final static String username = "root";
    private final static String password = "";    
    
    public static Connection connection;
    public static Statement statement;
    public static PreparedStatement prepare;   
    public static ResultSet result;
    public static ResultSetMetaData metaData;
    
    public final static String inventoryTable = "inventorytable";
    public final static String[] inventoryColumns = {"productID","Category","ProductName",
        "Description","Quantity","RetailPrice","DateOfPurchase"};
    
    private final static String categoryTable = "categorytable";
    private final static String[] categoryColumns = {"categoryID","categoryName","dateCreated"};
    
    private final static String appTable = "apptable";
    private final static String[] appColumns = {"appID","currentUser"};
    
    private final static String usersTable = "userstable";
    private final static String[] usersColumns = {"userId","firstname","lastname","username","password",
        "birthdate","gender","profileImgPath","userType"};
    
    private final static String recordsTable = "recordstable";
    private final static String[] recordsColumns = {"recordDate","sold"};
    
    private final static String purchasedTable = "purchasedtable";
    private final static String[] purchasedColumns = {"invoiceNumber","product","discountPercent","quantity","subtotal","total","purchasedDate"}; 
   
    private final Component component;
    
    public Database(Component component){
        this.component = component;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
            this.statement = connection.createStatement();
        }catch(Exception e){
          
        }              
    }
    
    public boolean isDatabaseConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
     
    //Inventory Section
    public DefaultTableModel DisplayInventoryData(){
        String query = "SELECT * FROM " + inventoryTable;

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Product ID","Category","Product Name","Description","Quantity","Retail Price","Date of Purchase"});
        try {
            result = statement.executeQuery(query);
            metaData = result.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            while (result.next()) {
                Object[] rowData = new Object[numberOfColumns];
                for (int i = 1; i <= numberOfColumns; i++) {
                    rowData[i - 1] = result.getObject(i);
                }
                model.addRow(rowData);
            }

            result.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), e.getErrorCode()+"", JOptionPane.ERROR_MESSAGE);
        }
        return model;
    }   
    
    public void addInventoryValue(Object[] values){
        String query = "INSERT INTO "+inventoryTable+
                " ("+inventoryColumns[1]+","+inventoryColumns[2]+","+inventoryColumns[3]+
                ","+inventoryColumns[4]+","+inventoryColumns[5]+","+inventoryColumns[6]+") VALUES (?,?,?,?,?,?)";
        
        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, values[0]);
            prepare.setObject(2, values[1]);
            prepare.setObject(3, values[2]); 
            prepare.setObject(4, values[3]); 
            prepare.setObject(5, values[4]); 
            prepare.setObject(6, values[5]);

            prepare.executeUpdate();
            prepare.close();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), e.getErrorCode()+"", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void EditInventoryValue(Object minuVal, int colIdx, Object id){
        String query = "UPDATE " + inventoryTable + " SET " + inventoryColumns[colIdx] + " = ? WHERE " + inventoryColumns[0] + " = ?";

        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, minuVal);
            prepare.setObject(2, id);

            prepare.executeUpdate();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void DeleteInventoryRecord(Object ID) {
        String query = "DELETE FROM "+inventoryTable+" WHERE "+inventoryColumns[0]+" = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, ID);
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void loadInventoryData(JTable table, String search){
        String query = "SELECT * FROM "+inventoryTable+" WHERE "
                    + String.join(" LIKE ? OR ", inventoryColumns) + " LIKE ?";    
        try{
            prepare = connection.prepareStatement(query);
            for (int i = 1; i <= inventoryColumns.length; i++) {
                prepare.setString(i, "%" + search + "%");
            }            
            
            result = prepare.executeQuery();
            DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
            model.setRowCount(0);            
            
            while (result.next()) {
                Object[] row = new Object[inventoryColumns.length];
                for (int i = 0; i < inventoryColumns.length; i++) {
                    row[i] = result.getObject(inventoryColumns[i]);
                }
                model.addRow(row);
            }
            
            result.close();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), e.getSQLState(), JOptionPane.ERROR_MESSAGE);
        }        
    }
    
    public void reduceProductQuantity(Object minusVal, Object p_n){
        String query = "UPDATE "+inventoryTable+" SET "+inventoryColumns[4]+
                " = "+inventoryColumns[4]+" - ? WHERE "+inventoryColumns[2]+" = ?";

        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, minusVal);
            prepare.setObject(2, p_n);

            prepare.executeUpdate();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Category
    public String[] AddElementToComboBox(){
        String query = "SELECT " + categoryColumns[1] + " FROM " + categoryTable;
        
        List<String> getVal = new ArrayList<>();
        
        try {
            result = statement.executeQuery(query);
            while (result.next()) {
                getVal.add(result.getString(categoryColumns[1]));
            }
            result.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
        return getVal.toArray(new String[0]);
    }
    
    public DefaultTableModel DisplayCategoryData(){
        String query = "SELECT * FROM " + categoryTable;

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Category ID","Category Name","Date Added"});
        try {
            result = statement.executeQuery(query);
            metaData = result.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            while (result.next()) {
                Object[] rowData = new Object[numberOfColumns];
                for (int i = 1; i <= numberOfColumns; i++) {
                    rowData[i - 1] = result.getObject(i);
                }
                model.addRow(rowData);
            }

            result.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
        return model;
    }
    
    public void addCategoryValue(String[] values){
        String query = "INSERT INTO "+categoryTable+" ("+categoryColumns[1]+","+categoryColumns[2]+") VALUES (?,?)";
       
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, values[0]);
            prepare.setObject(2, values[1]);
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void EditCategoryValue(Object newVal, int colIdx, Object ID){
        String query = "UPDATE "+categoryTable+" SET "+categoryColumns[colIdx]+" = ? WHERE ("+categoryColumns[0]+" = ?)";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, newVal);
            prepare.setObject(2, ID);
            
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void DeleteCategoryRecord(Object ID){
        String query = "DELETE FROM "+categoryTable+" WHERE "+categoryColumns[0]+" = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, ID);
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void loadCategoryData(JTable table, String search){
        String query = "SELECT * FROM "+categoryTable+" WHERE "
                    + String.join(" LIKE ? OR ", categoryColumns) + " LIKE ?";    
        try{
            prepare = connection.prepareStatement(query);
            for (int i = 1; i <= categoryColumns.length; i++) {
                prepare.setString(i, "%" + search + "%");
            }            
            
            result = prepare.executeQuery();
            DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
            model.setRowCount(0);            
            
            while (result.next()) {
                Object[] row = new Object[categoryColumns.length];
                for (int i = 0; i < categoryColumns.length; i++) {
                    row[i] = result.getObject(categoryColumns[i]);
                }
                model.addRow(row);
            }
            
            result.close();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }        
    }
    
    public void DisplayUserData(JTable table) {
        String[] columnsToDisplay = usersColumns;

        String query = "SELECT " + String.join(", ", columnsToDisplay) + " FROM " + usersTable;
        try {
            prepare = connection.prepareStatement(query);

            result = prepare.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            model.setRowCount(0);

            while (result.next()) {
                Object[] row = new Object[columnsToDisplay.length];
                for (int i = 0; i < columnsToDisplay.length; i++) {
                    row[i] = result.getObject(columnsToDisplay[i]);
                }
                model.addRow(row);
            }

            result.close();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void EditUserData(Object newVal, int colIdx, Object p_n){
        String query = "UPDATE " + usersTable + " SET " + usersColumns[colIdx] + " = ? WHERE " + usersColumns[0] + " = ?";

        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, newVal);
            prepare.setObject(2, p_n);

            prepare.executeUpdate();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public String getUserId(String u_name,String u_pass){
        String query = "SELECT "+usersColumns[0]+" FROM "+usersTable+" WHERE "
                + usersColumns[3] + " = ? AND "+usersColumns[4]+" = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setString(1, u_name);
            prepare.setString(2, u_pass);
            
            result = prepare.executeQuery();
            
            if(result.next()){
                return result.getString(usersColumns[0]);
            }
            
            prepare.close();
            result.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return null;        
    }    
    
    public String getFName(String _id){
        String query = "SELECT "+usersColumns[1]+" FROM "+usersTable+" WHERE "
                + usersColumns[0] + " = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setString(1, _id);          
            result = prepare.executeQuery();
            
            if(result.next()){
                return result.getString(usersColumns[1]);
            }
            
            prepare.close();
            result.close();
        }catch(SQLException e){
           JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return null;        
    }        
        
    public String getLName(String _id){
        String query = "SELECT "+usersColumns[2]+" FROM "+usersTable+" WHERE "
                + usersColumns[0] + " = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setString(1, _id);
            
            result = prepare.executeQuery();
            
            if(result.next()){
                return result.getString(usersColumns[2]);
            }
            
            prepare.close();
            result.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return null;        
    }    
    
    public void DeleteUser(Object ID) {
        String query = "DELETE FROM "+usersTable+" WHERE "+usersColumns[0]+" = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, ID);
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }   
    
    public String getUName(String _id){
        String query = "SELECT "+usersColumns[3]+" FROM "+usersTable+" WHERE "
                + usersColumns[0] + " = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setString(1, _id);
            result = prepare.executeQuery();
            
            if(result.next()){
                return result.getString(usersColumns[3]);
            }
            
            prepare.close();
            result.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return null;        
    }    
    
    public String getBday(int _id){
        String query = "SELECT "+usersColumns[5]+" FROM "+usersTable+" WHERE "
                + usersColumns[0] + " = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setInt(1, _id);
            
            result = prepare.executeQuery();
            
            if(result.next()){
                return result.getString(usersColumns[5]);
            }
            
            prepare.close();
            result.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return null;        
    }
    
    public String getGender(String _id){
        String query = "SELECT "+usersColumns[6]+" FROM "+usersTable+" WHERE "
                + usersColumns[0] + " = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setString(1, _id);
            
            result = prepare.executeQuery();
            
            if(result.next()){
                return result.getString(usersColumns[6]);
            }
            
            prepare.close();
            result.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return null;        
    }
    
    public String getImagePath(String _id){
        String query = "SELECT "+usersColumns[7]+" FROM "+usersTable+" WHERE "
                + usersColumns[0] + " = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setString(1, _id);
            result = prepare.executeQuery();
            
            if(result.next()){
                return result.getString(usersColumns[7]);
            }
            
            prepare.close();
            result.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return null;        
    }
    
    public short getUserType(String id){
        String query = "SELECT "+usersColumns[8]+" FROM "+usersTable+
                " WHERE "+usersColumns[0]+" = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setString(1, id);
            
            result = prepare.executeQuery();
            
            if(result.next()){
                return result.getShort(usersColumns[8]);
            }
            
            prepare.close();
            result.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return 0;        
    } 
    
    public static String getCurrentUser(Component p_c){
        String query = "SELECT "+appColumns[1]+" FROM "+appTable+" WHERE "
                + appColumns[0] + " = 1";
        
        try{
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            
            if(result.next()){
                return result.getString(appColumns[1]);
            }
            
            prepare.close();
            result.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(p_c, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return null;           
    }    
    
    public short checkUserCredentials(String u_n, String u_p) {
        String query = "SELECT * FROM " + usersTable;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();

            while (result.next()) {
                String username = result.getString(usersColumns[3]);
                String password = result.getString(usersColumns[4]);

                if (u_n.equals(username) && u_p.equals(password)) {
                    prepare.close();
                    result.close();
                    return 1;
                } else if (u_n.equals(username)) {
                    prepare.close();
                    result.close();
                    return 2; 
                } else if (u_p.equals(password)) {
                    prepare.close();
                    result.close();
                    return 3; 
                }
            }

            prepare.close();
            result.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        } catch(ArrayIndexOutOfBoundsException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0; 
    }
    
    public void setCurrentUser(String _id,Component p_c){
        String query = "UPDATE "+appTable+" SET "+appColumns[1]+" = ? WHERE "+appColumns[0]+" = 1";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setString(1, _id);
            
            prepare.executeUpdate();
            
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(p_c, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean checkCurrentUser(String value) {
        String query = "SELECT " + usersColumns[0] + " FROM " + usersTable +
                       " WHERE " + usersColumns[0] + " = ?";

        try {
            prepare = connection.prepareStatement(query);
            prepare.setString(1, value);
            result = prepare.executeQuery();

            if (result.next()) {
                prepare.close();
                return true;
            }

            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }
    
    public int getHowManyUsers() {
        String query = "SELECT COUNT(*) FROM " +usersTable;
        int userCount = 0;

        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();

            if (result.next()) {
                userCount = result.getInt(1);
            }

            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return userCount;
    }
    
    public boolean addUser(Object[] values){
        boolean isSuccessful = true;
        String query = "INSERT INTO "+usersTable+" ("+usersColumns[0]+
                ", "+usersColumns[1]+
                ", "+usersColumns[2]+
                ", "+usersColumns[3]+
                ", "+usersColumns[4]+
                ", "+usersColumns[5]+
                ", "+usersColumns[6]+
                ", "+usersColumns[7]+
                ", "+usersColumns[8]+") " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            prepare = connection.prepareStatement(query);
            for (int i = 0; i < values.length; i++) {
                prepare.setObject(i + 1, values[i]);
            }
            prepare.executeUpdate();
            prepare.close();
        } catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
            isSuccessful = false;
        }        
        return isSuccessful;
    }
    
    public void loadInventoryDataSales(JTable table, String search) {
        String[] columnsToDisplay = {"Category", "ProductName", "Description", "Quantity","RetailPrice"};

        String query = "SELECT " + String.join(", ", columnsToDisplay) + " FROM " + inventoryTable
                + " WHERE " + String.join(" LIKE ? OR ", columnsToDisplay) + " LIKE ?";
        try {
            prepare = connection.prepareStatement(query);
            for (int i = 1; i <= columnsToDisplay.length; i++) {
                prepare.setString(i, "%" + search + "%");
            }

            result = prepare.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            model.setRowCount(0);

            while (result.next()) {
                Object[] row = new Object[columnsToDisplay.length];
                for (int i = 0; i < columnsToDisplay.length; i++) {
                    row[i] = result.getObject(columnsToDisplay[i]);
                }
                model.addRow(row);
            }

            result.close();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void recordPurchase(Object invoiceNum, Object item, Object discount, Object quantity, Object subtotal, Object total, Object date){
        String query = "INSERT INTO "+purchasedTable+" ("+purchasedColumns[0]+
                ", "+purchasedColumns[1]+", "+purchasedColumns[2]+
                ", "+purchasedColumns[3]+", "+purchasedColumns[4]+
                ", "+purchasedColumns[5]+", "+purchasedColumns[6]+") VALUES (?,?,?,?,?,?,?)";
        
        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, invoiceNum);
            prepare.setObject(2, item);
            prepare.setObject(3, discount);
            prepare.setObject(4, quantity);
            prepare.setObject(5, subtotal);
            prepare.setObject(6, total);
            prepare.setObject(7, date);
            prepare.executeUpdate();
            prepare.close();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }      
    }
   
    public int countProducts(){
        String query = "SELECT COUNT(*) FROM " + inventoryTable;
        int count = 0;
        try {
            result = statement.executeQuery(query);
            while (result.next()) {
               count = result.getInt(1);
            }
            result.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return count;
    }
    
    public int getProductSold() {
        String query = "SELECT SUM("+purchasedColumns[3]+") AS totalquantity FROM "+purchasedTable+" WHERE DATE("+purchasedColumns[6]+") != CURDATE()";
        int totalProduct = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            
            if (result.next()) {
                totalProduct = result.getInt("totalquantity");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return totalProduct;
    }
    
    public int getProductSoldToday() {
        String query = "SELECT SUM("+purchasedColumns[3]+") AS totalquantity FROM "+purchasedTable+" WHERE DATE("+purchasedColumns[6]+") = CURDATE()";
        int totalProduct = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            
            if (result.next()) {
                totalProduct = result.getInt("totalquantity");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return totalProduct;
    }
    
    public int getOutOfStocks(){
        String query = "SELECT COUNT(*) FROM "+inventoryTable+" WHERE "+inventoryColumns[4]+" = 0";
        int outOfStocks = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();

            if (result.next()) {
                outOfStocks = result.getInt(1); 
            }

            result.close();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return outOfStocks;          
    }
    
    public double getTotalSalesToday() {
        String query = "SELECT SUM("+purchasedColumns[5]+") AS totalSales FROM "+purchasedTable+" WHERE DATE("+purchasedColumns[6]+") = CURDATE()";
        double salesVal = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            
            if (result.next()) {
                salesVal = result.getDouble("totalSales");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return salesVal;
    }
    
    public double getTotalSales() {
        String query = "SELECT SUM("+purchasedColumns[5]+") AS totalSales FROM "+purchasedTable;
        double salesVal = 0;
        try {
            prepare = connection.prepareStatement(query);
            result = prepare.executeQuery();
            
            if (result.next()) {
                salesVal = result.getDouble("totalSales");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), "Error Code: " + e.getErrorCode(), JOptionPane.ERROR_MESSAGE);
        }
        return salesVal;
    }
   
}
