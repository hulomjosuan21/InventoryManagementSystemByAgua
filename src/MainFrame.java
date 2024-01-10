import com.formdev.flatlaf.FlatLightLaf;
import customComponents.ImageAvatar;
import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import myclass.Database;
import myclass.Helper;

//author Josuan
public final class MainFrame extends javax.swing.JFrame {
    private final Database mydb = new Database(this);
    private final static String[] listOfGenders = {"Male","Female"};
    
    public MainFrame() {
        initComponents();
        A_1();
        A_2();
        A_4();
        A_5();
        A_3();
        A_6();
        if (this.getExtendedState() == this.MAXIMIZED_BOTH) {
            this.setExtendedState(this.NORMAL);
        } else {
            this.setExtendedState(this.MAXIMIZED_BOTH);
        }
    }
    
    public static void main(String args[]) {
        FlatLightLaf.setup();
        
        Database checkdb = new Database(new MainFrame());
        
        if(checkdb.isDatabaseConnected()){
            String value = checkdb.getCurrentUser(new MainFrame());
            boolean checkUser = checkdb.checkCurrentUser(value);
            
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    if(!checkUser){
                        new LoginFrame().setVisible(true);
                    }else{
                        new MainFrame().setVisible(true);
                    }
                }
            });           
        }else {
            JOptionPane.showMessageDialog(null, "Error: No database connected!", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

    }
    
    private void A_1(){
        inventoryTable.setModel(mydb.DisplayInventoryData());
        im4.setModel(new DefaultComboBoxModel(mydb.AddElementToComboBox()));
        inventoryTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean isCellEditable(EventObject e) {
                return false;
            }
        });
        inventoryTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean isCellEditable(EventObject e) {
                return false;
            }
        });
        inventoryTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(mydb.AddElementToComboBox())));
    }
    
    private void A_2(){
        categoryTable.setModel(mydb.DisplayCategoryData());
        im4.setModel(new DefaultComboBoxModel(mydb.AddElementToComboBox()));
        categoryTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean isCellEditable(EventObject e) {
                return false;
            }
        });
        categoryTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean isCellEditable(EventObject e) {
                return false;
            }
        });
    }
    
    public void A_3(){
        totalProductsLabel.setText(mydb.countProducts()+"");
        soldOldLabel.setText(mydb.getProductSold()+"");
        soldTotayLabel.setText(mydb.getProductSoldToday()+"");
        outStockLabel.setText(mydb.getOutOfStocks()+"");
        todaysalesLabel.setText(Helper.currency+" "+mydb.getTotalSalesToday());
        totalSalesLabel.setText(Helper.currency+" "+mydb.getTotalSales());      
        inventoryTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox(mydb.AddElementToComboBox())));
    }

    private void A_4(){
        String get_id = mydb.getCurrentUser(this);
        String get_image = mydb.getImagePath(get_id);
        String get_username = mydb.getUName(get_id);
        String get_fname = mydb.getFName(get_id);
        String get_lname = mydb.getLName(get_id);
        String get_fullName = capitalizeEachWord((get_fname+" "+get_lname));
        short get_usertype = mydb.getUserType(get_id);
        String get_gender = mydb.getGender(get_id);
        usernameLabel.setText(get_username);
        fullNameLabel.setText(get_fullName);
        fullNameLabel.repaint();
        setImageToAvatar(imageAvatar1, get_image);
        switch (get_usertype) {
            case 0:
                switchPanel(menuLayeredPane,adminMenu);
                adminSettingsBtn.setEnabled(true);
                break;
            case 1:
                switchPanel(menuLayeredPane,notAdminMenu);
                adminSettingsBtn.setEnabled(false);
                break;
            default:
                switchPanel(menuLayeredPane,notAdminMenu);
                adminSettingsBtn.setEnabled(false);
                break;
        }
        
        try{          
            if(get_gender.equals(listOfGenders[0])){
                genderLabel.setIcon(new ImageIcon("src/icons/male.png"));
            }else if(get_gender.equals(listOfGenders[1])){
                genderLabel.setIcon(new ImageIcon("src/icons/female.png"));
            }else{
                genderLabel.setIcon(new ImageIcon("src/icons/nogender.png"));
            }
        }catch(NullPointerException e){
        }
    }
    
    public void A_5(){
        salesCategoryComboBox.setModel(new DefaultComboBoxModel(mydb.AddElementToComboBox()));
   
        SpinnerNumberModel spinnerModel1 = new SpinnerNumberModel(0, 0, 100, 10);
        discountSpinner.setModel(spinnerModel1);
    }
    
    public void A_6(){
        mydb.DisplayUserData(usersTable);
        usersTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JComboBox(listOfGenders)));
        getGenderComboBox.setModel(new DefaultComboBoxModel(listOfGenders));
        usersTable.getColumnModel().getColumn(8).setCellEditor(new Helper.IntCustomJSpinner());
    }
    
    public static String capitalizeEachWord(String fullName) {
        String[] words = fullName.split("\\s+"); 

        StringBuilder capitalizedFullName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                capitalizedFullName.append(capitalizedWord).append(" ");
            }
        }
        return capitalizedFullName.toString().trim(); 
    }
    
    private void setImageToAvatar(ImageAvatar imgAvatar, String imageName){
        ImageIcon img = new ImageIcon("src/images/"+imageName);
        imgAvatar.setIcon(img);
        imgAvatar.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainLayeredPane = new javax.swing.JLayeredPane();
        mainPanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        closeLabel = new javax.swing.JLabel();
        maximizeLabel = new javax.swing.JLabel();
        minimizeLabel = new javax.swing.JLabel();
        contentLayeredPane = new javax.swing.JLayeredPane();
        dashboardPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        totalProductsLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        soldOldLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        totalSalesLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        outStockLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        soldTotayLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        todaysalesLabel = new javax.swing.JLabel();
        usersPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        fullNameLabel = new javax.swing.JLabel();
        genderLabel = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        usernameLabel = new javax.swing.JLabel();
        imageAvatar1 = new customComponents.ImageAvatar();
        adminSettingsBtn = new javax.swing.JButton();
        inventoryPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        inventoryTable = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        im1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        im2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        im3 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        im4 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        im5 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        im6 = new com.toedter.calendar.JDateChooser();
        inventoryAddBtn = new javax.swing.JButton();
        inventoryEditBtn = new javax.swing.JButton();
        inventoryDeleteBtn = new javax.swing.JButton();
        inventorySearchBar = new javax.swing.JTextField();
        categoryPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        categoryTable = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        addCategory = new javax.swing.JTextField();
        CAdd = new javax.swing.JButton();
        CEdit = new javax.swing.JButton();
        CDelete = new javax.swing.JButton();
        categorySearchBar = new javax.swing.JTextField();
        salesPanel = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        recieptTextArea = new javax.swing.JTextArea();
        payBtn = new javax.swing.JButton();
        printBtn = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        getTotalTextField = new javax.swing.JTextField();
        receivedTextField = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        getBalanceTextField = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        salesCategoryComboBox = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        itemNameTextField = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        descriptionTextField = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        dateCHooser = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        quantitySpinner = new javax.swing.JSpinner();
        totalSpinner = new javax.swing.JSpinner();
        priceSpinner = new javax.swing.JSpinner();
        discountSpinner = new javax.swing.JSpinner();
        jScrollPane4 = new javax.swing.JScrollPane();
        salesTable2 = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        salesTable1 = new javax.swing.JTable();
        addCartBtn = new javax.swing.JButton();
        removeCartBtn = new javax.swing.JButton();
        invoiceTextField = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        reportPanel = new javax.swing.JPanel();
        adminPanel = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        usersTable = new javax.swing.JTable();
        userEditBtn = new javax.swing.JButton();
        deleteUserBtn = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        getFNameTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        getLNameTextField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        getUNameTextField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        getPasswordPasswordField = new javax.swing.JPasswordField();
        jLabel19 = new javax.swing.JLabel();
        getConfirmPasswordPasswordField = new javax.swing.JPasswordField();
        jLabel20 = new javax.swing.JLabel();
        getBirthDateDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel21 = new javax.swing.JLabel();
        getGenderComboBox = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        termServiceCheckBox = new javax.swing.JCheckBox();
        imageAvatar2 = new customComponents.ImageAvatar();
        addUserBtn = new javax.swing.JButton();
        returnItemPanel = new javax.swing.JPanel();
        pricelistPanel = new javax.swing.JPanel();
        menuLayeredPane = new javax.swing.JLayeredPane();
        adminMenu = new javax.swing.JPanel();
        dashboardLabel = new javax.swing.JLabel();
        usersLabel = new javax.swing.JLabel();
        inventoryLabel = new javax.swing.JLabel();
        categoryLabel = new javax.swing.JLabel();
        salesLabel = new javax.swing.JLabel();
        returnItemLabel = new javax.swing.JLabel();
        priceListLabel = new javax.swing.JLabel();
        reportLabel = new javax.swing.JLabel();
        logoutLabel = new javax.swing.JLabel();
        notAdminMenu = new javax.swing.JPanel();
        dashboardLabel1 = new javax.swing.JLabel();
        userLabel1 = new javax.swing.JLabel();
        salesLabel1 = new javax.swing.JLabel();
        returnItemLabel1 = new javax.swing.JLabel();
        pricelistlabel1 = new javax.swing.JLabel();
        logoutLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        mainLayeredPane.setLayout(new java.awt.CardLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("INVENTORY MANAGEMENT SYSTEM");

        closeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        closeLabel.setText("X");
        closeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeLabelMouseClicked(evt);
            }
        });

        maximizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        maximizeLabel.setText("X");
        maximizeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                maximizeLabelMouseClicked(evt);
            }
        });

        minimizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minimizeLabel.setText("X");
        minimizeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                minimizeLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(189, 189, 189)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(84, 84, 84)
                .addComponent(minimizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maximizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(closeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(maximizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(minimizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        contentLayeredPane.setLayout(new java.awt.CardLayout());

        dashboardPanel.setBackground(new java.awt.Color(204, 204, 255));

        jLabel11.setBackground(new java.awt.Color(255, 153, 153));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Total Products");
        jLabel11.setOpaque(true);

        totalProductsLabel.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        totalProductsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalProductsLabel.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(totalProductsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalProductsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel12.setBackground(new java.awt.Color(255, 153, 153));
        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Products Sold (old)");
        jLabel12.setOpaque(true);

        soldOldLabel.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        soldOldLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        soldOldLabel.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(soldOldLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(soldOldLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel13.setBackground(new java.awt.Color(255, 153, 153));
        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Total Sales");
        jLabel13.setOpaque(true);

        totalSalesLabel.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        totalSalesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalSalesLabel.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(totalSalesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalSalesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel14.setBackground(new java.awt.Color(255, 153, 153));
        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Out of Stocks");
        jLabel14.setOpaque(true);

        outStockLabel.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        outStockLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        outStockLabel.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(outStockLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outStockLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel15.setBackground(new java.awt.Color(255, 153, 153));
        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Products Sold (today)");
        jLabel15.setOpaque(true);

        soldTotayLabel.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        soldTotayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        soldTotayLabel.setText("0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(soldTotayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(soldTotayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel16.setBackground(new java.awt.Color(255, 153, 153));
        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Today's Sales");
        jLabel16.setOpaque(true);

        todaysalesLabel.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        todaysalesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        todaysalesLabel.setText("0");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(todaysalesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(todaysalesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout dashboardPanelLayout = new javax.swing.GroupLayout(dashboardPanel);
        dashboardPanel.setLayout(dashboardPanelLayout);
        dashboardPanelLayout.setHorizontalGroup(
            dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardPanelLayout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(57, 57, 57))
        );
        dashboardPanelLayout.setVerticalGroup(
            dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardPanelLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(32, 32, 32))
        );

        contentLayeredPane.add(dashboardPanel, "card2");

        usersPanel.setBackground(new java.awt.Color(204, 204, 255));

        jPanel8.setBackground(new java.awt.Color(185, 185, 255));

        fullNameLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        fullNameLabel.setForeground(new java.awt.Color(255, 255, 255));
        fullNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fullNameLabel.setText("Example Name");

        genderLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        genderLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/female.png"))); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(genderLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(fullNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                .addGap(93, 93, 93))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(genderLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fullNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.setBackground(new java.awt.Color(153, 153, 255));

        usernameLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        usernameLabel.setForeground(new java.awt.Color(255, 255, 255));
        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usernameLabel.setText("Username");

        imageAvatar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/nullProfile.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(usernameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageAvatar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageAvatar1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(293, Short.MAX_VALUE))
        );

        adminSettingsBtn.setText("Admin Settings");
        adminSettingsBtn.setEnabled(false);
        adminSettingsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminSettingsBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout usersPanelLayout = new javax.swing.GroupLayout(usersPanel);
        usersPanel.setLayout(usersPanelLayout);
        usersPanelLayout.setHorizontalGroup(
            usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(usersPanelLayout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(usersPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(adminSettingsBtn)
                        .addContainerGap())))
        );
        usersPanelLayout.setVerticalGroup(
            usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(usersPanelLayout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(adminSettingsBtn)
                .addContainerGap())
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        contentLayeredPane.add(usersPanel, "card3");

        inventoryPanel.setBackground(new java.awt.Color(204, 204, 255));

        inventoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(inventoryTable);

        jPanel9.setBackground(new java.awt.Color(153, 153, 255));

        jLabel5.setText("Product");

        jLabel6.setText("Description");

        jLabel7.setText("Quantity");

        im3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                im3KeyTyped(evt);
            }
        });

        jLabel8.setText("Category");

        im4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel9.setText("Retail Price");

        im5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                im5KeyTyped(evt);
            }
        });

        jLabel10.setText("Date of Purchase");

        inventoryAddBtn.setText("ADD");
        inventoryAddBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryAddBtnActionPerformed(evt);
            }
        });

        inventoryEditBtn.setText("EDIT");
        inventoryEditBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryEditBtnActionPerformed(evt);
            }
        });

        inventoryDeleteBtn.setText("DELETE");
        inventoryDeleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryDeleteBtnActionPerformed(evt);
            }
        });

        inventorySearchBar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inventorySearchBarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(im1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(im2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addComponent(im3, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(im4, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(im6, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(inventorySearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel9)
                            .addComponent(im5, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(inventoryAddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(inventoryEditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(inventoryDeleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(im1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(im3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(im5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(im2)
                        .addComponent(im4)
                        .addComponent(im6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(inventorySearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inventoryAddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inventoryEditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inventoryDeleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout inventoryPanelLayout = new javax.swing.GroupLayout(inventoryPanel);
        inventoryPanel.setLayout(inventoryPanelLayout);
        inventoryPanelLayout.setHorizontalGroup(
            inventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inventoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        inventoryPanelLayout.setVerticalGroup(
            inventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inventoryPanelLayout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentLayeredPane.add(inventoryPanel, "card4");

        categoryPanel.setBackground(new java.awt.Color(204, 204, 255));

        categoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(categoryTable);

        jPanel10.setBackground(new java.awt.Color(153, 153, 255));

        jLabel23.setText("Category");

        CAdd.setText("ADD");
        CAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CAddActionPerformed(evt);
            }
        });

        CEdit.setText("EDIT");
        CEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CEditActionPerformed(evt);
            }
        });

        CDelete.setText("DELETE");
        CDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CDeleteActionPerformed(evt);
            }
        });

        categorySearchBar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                categorySearchBarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(categorySearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(102, 102, 102))
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel10Layout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel23)
                        .addComponent(addCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel10Layout.createSequentialGroup()
                            .addComponent(CAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(CEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(CDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(377, Short.MAX_VALUE)))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addComponent(categorySearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel10Layout.createSequentialGroup()
                    .addGap(5, 5, 5)
                    .addComponent(jLabel23)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(addCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(CAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(5, 5, 5)))
        );

        javax.swing.GroupLayout categoryPanelLayout = new javax.swing.GroupLayout(categoryPanel);
        categoryPanel.setLayout(categoryPanelLayout);
        categoryPanelLayout.setHorizontalGroup(
            categoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(categoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        categoryPanelLayout.setVerticalGroup(
            categoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, categoryPanelLayout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentLayeredPane.add(categoryPanel, "card5");

        salesPanel.setBackground(new java.awt.Color(204, 204, 255));

        jPanel11.setBackground(new java.awt.Color(153, 153, 255));

        recieptTextArea.setColumns(20);
        recieptTextArea.setRows(5);
        jScrollPane3.setViewportView(recieptTextArea);

        payBtn.setText("PAY");
        payBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payBtnActionPerformed(evt);
            }
        });

        printBtn.setText("PRINT");
        printBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBtnActionPerformed(evt);
            }
        });

        jLabel31.setText("Total");

        receivedTextField.setEnabled(false);
        receivedTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                receivedTextFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                receivedTextFieldKeyTyped(evt);
            }
        });

        jLabel32.setText("Received");

        jLabel33.setText("Balance");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(payBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(printBtn)
                .addGap(19, 19, 19))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(getBalanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(receivedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(getTotalTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(43, 43, 43))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addGap(27, 27, 27)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(getTotalTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(receivedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(getBalanceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(payBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(printBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        jPanel7.setBackground(new java.awt.Color(153, 153, 255));

        jLabel4.setText("Category");

        salesCategoryComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        salesCategoryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesCategoryComboBoxActionPerformed(evt);
            }
        });

        jLabel24.setText("Item Name");

        jLabel25.setText("Description");

        jLabel26.setText("Date");

        jLabel27.setText("Price");

        jLabel28.setText("Quantity");

        jLabel29.setText("Discount");

        jLabel30.setText("Total");

        quantitySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                quantitySpinnerStateChanged(evt);
            }
        });

        totalSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                totalSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                        .addGap(52, 52, 52))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(41, 41, 41))
                    .addComponent(salesCategoryComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(itemNameTextField))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                        .addGap(40, 40, 40))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(76, 76, 76))
                    .addComponent(descriptionTextField)
                    .addComponent(dateCHooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE))
                        .addGap(50, 50, 50))
                    .addComponent(quantitySpinner)
                    .addComponent(priceSpinner))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(50, 50, 50))
                    .addComponent(totalSpinner)
                    .addComponent(discountSpinner))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel30)
                        .addGap(28, 28, 28))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(descriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(priceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(discountSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateCHooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(salesCategoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addGap(28, 28, 28)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(itemNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(quantitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(totalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        salesTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Name", "Retail Price", "Discount", "Quantity", "Sub Total", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(salesTable2);

        salesTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Category", "Product Name", "Description", "Quantity", "Retail Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        salesTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                salesTable1MouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(salesTable1);

        addCartBtn.setText("ADD");
        addCartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCartBtnActionPerformed(evt);
            }
        });

        removeCartBtn.setText("REMOVE");
        removeCartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeCartBtnActionPerformed(evt);
            }
        });

        jLabel34.setText("Invoice Number");

        javax.swing.GroupLayout salesPanelLayout = new javax.swing.GroupLayout(salesPanel);
        salesPanel.setLayout(salesPanelLayout);
        salesPanelLayout.setHorizontalGroup(
            salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salesPanelLayout.createSequentialGroup()
                .addGroup(salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salesPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addCartBtn)
                        .addGap(18, 18, 18)
                        .addComponent(removeCartBtn)
                        .addGap(27, 27, 27))
                    .addGroup(salesPanelLayout.createSequentialGroup()
                        .addGroup(salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(salesPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane4)
                                    .addComponent(jScrollPane5)))
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4)))
                .addGroup(salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(salesPanelLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(invoiceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salesPanelLayout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        salesPanelLayout.setVerticalGroup(
            salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salesPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(invoiceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, salesPanelLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(salesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addCartBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeCartBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        contentLayeredPane.add(salesPanel, "card6");

        reportPanel.setBackground(new java.awt.Color(204, 204, 255));

        javax.swing.GroupLayout reportPanelLayout = new javax.swing.GroupLayout(reportPanel);
        reportPanel.setLayout(reportPanelLayout);
        reportPanelLayout.setHorizontalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 722, Short.MAX_VALUE)
        );
        reportPanelLayout.setVerticalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
        );

        contentLayeredPane.add(reportPanel, "card7");

        jScrollPane6.setBorder(null);
        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane6.setToolTipText("");

        jPanel14.setBackground(new java.awt.Color(204, 204, 255));

        usersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "User ID", "Firstname", "Lastname", "Username", "Password", "Birth Date", "Gender", "Image", "User Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(usersTable);

        userEditBtn.setText("EDIT");
        userEditBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userEditBtnActionPerformed(evt);
            }
        });

        deleteUserBtn.setText("DELETE");
        deleteUserBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteUserBtnActionPerformed(evt);
            }
        });

        jPanel15.setBackground(new java.awt.Color(153, 153, 255));

        jLabel2.setText("First Name");

        jLabel3.setText("Last Name");

        jLabel17.setText("Username");

        jLabel18.setText("Password");

        jLabel19.setText("Confirm Password");

        jLabel20.setText("Birthdate");

        jLabel21.setText("Gender");

        getGenderComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setText("Add Image");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        termServiceCheckBox.setText("I agree to the Terms and Services");
        termServiceCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                termServiceCheckBoxActionPerformed(evt);
            }
        });

        imageAvatar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/nullProfile.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel17)
                            .addComponent(getUNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(54, 54, 54)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(getBirthDateDateChooser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(getFNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(getLNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(54, 54, 54)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(getPasswordPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel19)
                                    .addComponent(getConfirmPasswordPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGap(56, 56, 56)
                                .addComponent(termServiceCheckBox)))
                        .addGap(54, 54, 54)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(getGenderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(imageAvatar2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel18)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(getFNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getPasswordPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getGenderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(getConfirmPasswordPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(getLNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel20))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(getUNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(getBirthDateDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(termServiceCheckBox)
                        .addGap(0, 6, Short.MAX_VALUE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(imageAvatar2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );

        addUserBtn.setText("ADD");
        addUserBtn.setEnabled(false);
        addUserBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUserBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jScrollPane7)
                        .addGap(18, 18, 18))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(addUserBtn)
                                .addGap(18, 18, 18)
                                .addComponent(userEditBtn)
                                .addGap(18, 18, 18)
                                .addComponent(deleteUserBtn))
                            .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(240, Short.MAX_VALUE))))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userEditBtn)
                    .addComponent(deleteUserBtn)
                    .addComponent(addUserBtn))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jScrollPane6.setViewportView(jPanel14);

        javax.swing.GroupLayout adminPanelLayout = new javax.swing.GroupLayout(adminPanel);
        adminPanel.setLayout(adminPanelLayout);
        adminPanelLayout.setHorizontalGroup(
            adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6)
        );
        adminPanelLayout.setVerticalGroup(
            adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
        );

        contentLayeredPane.add(adminPanel, "card8");

        javax.swing.GroupLayout returnItemPanelLayout = new javax.swing.GroupLayout(returnItemPanel);
        returnItemPanel.setLayout(returnItemPanelLayout);
        returnItemPanelLayout.setHorizontalGroup(
            returnItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 722, Short.MAX_VALUE)
        );
        returnItemPanelLayout.setVerticalGroup(
            returnItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
        );

        contentLayeredPane.add(returnItemPanel, "card9");

        javax.swing.GroupLayout pricelistPanelLayout = new javax.swing.GroupLayout(pricelistPanel);
        pricelistPanel.setLayout(pricelistPanelLayout);
        pricelistPanelLayout.setHorizontalGroup(
            pricelistPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 722, Short.MAX_VALUE)
        );
        pricelistPanelLayout.setVerticalGroup(
            pricelistPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
        );

        contentLayeredPane.add(pricelistPanel, "card10");

        menuLayeredPane.setLayout(new java.awt.CardLayout());

        adminMenu.setBackground(new java.awt.Color(102, 153, 255));

        dashboardLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        dashboardLabel.setText("DASHBOARD");
        dashboardLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashboardLabelMouseClicked(evt);
            }
        });

        usersLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        usersLabel.setText("USERS");
        usersLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                usersLabelMouseClicked(evt);
            }
        });

        inventoryLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        inventoryLabel.setText("INVENTORY");
        inventoryLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inventoryLabelMouseClicked(evt);
            }
        });

        categoryLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        categoryLabel.setText("CATEGORY");
        categoryLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                categoryLabelMouseClicked(evt);
            }
        });

        salesLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        salesLabel.setText("SALES");
        salesLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                salesLabelMouseClicked(evt);
            }
        });

        returnItemLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        returnItemLabel.setText("RETURN ITEM");
        returnItemLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                returnItemLabelMouseClicked(evt);
            }
        });

        priceListLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        priceListLabel.setText("PRICE LIST");
        priceListLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                priceListLabelMouseClicked(evt);
            }
        });

        reportLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        reportLabel.setText("REPORT");
        reportLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reportLabelMouseClicked(evt);
            }
        });

        logoutLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        logoutLabel.setText("LOG OUT");
        logoutLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout adminMenuLayout = new javax.swing.GroupLayout(adminMenu);
        adminMenu.setLayout(adminMenuLayout);
        adminMenuLayout.setHorizontalGroup(
            adminMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, adminMenuLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(adminMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dashboardLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usersLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reportLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logoutLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inventoryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(categoryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(salesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(returnItemLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(priceListLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        adminMenuLayout.setVerticalGroup(
            adminMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dashboardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usersLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inventoryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(salesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(returnItemLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(priceListLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reportLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logoutLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(217, 217, 217))
        );

        menuLayeredPane.add(adminMenu, "card2");

        notAdminMenu.setBackground(new java.awt.Color(102, 204, 255));

        dashboardLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        dashboardLabel1.setText("DASHBOARD");
        dashboardLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashboardLabel1MouseClicked(evt);
            }
        });

        userLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        userLabel1.setText("USERS");
        userLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userLabel1MouseClicked(evt);
            }
        });

        salesLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        salesLabel1.setText("SALES");
        salesLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                salesLabel1MouseClicked(evt);
            }
        });

        returnItemLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        returnItemLabel1.setText("RETURN ITEM");
        returnItemLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                returnItemLabel1MouseClicked(evt);
            }
        });

        pricelistlabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        pricelistlabel1.setText("PRICE LIST");
        pricelistlabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pricelistlabel1MouseClicked(evt);
            }
        });

        logoutLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        logoutLabel1.setText("LOG OUT");
        logoutLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutLabel1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout notAdminMenuLayout = new javax.swing.GroupLayout(notAdminMenu);
        notAdminMenu.setLayout(notAdminMenuLayout);
        notAdminMenuLayout.setHorizontalGroup(
            notAdminMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notAdminMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(notAdminMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, notAdminMenuLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(dashboardLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(notAdminMenuLayout.createSequentialGroup()
                        .addGroup(notAdminMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(userLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(salesLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(returnItemLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pricelistlabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logoutLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        notAdminMenuLayout.setVerticalGroup(
            notAdminMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(notAdminMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dashboardLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(salesLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(returnItemLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pricelistlabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logoutLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(310, 310, 310))
        );

        menuLayeredPane.add(notAdminMenu, "card3");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(menuLayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(contentLayeredPane))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contentLayeredPane)
                    .addComponent(menuLayeredPane)))
        );

        mainLayeredPane.add(mainPanel, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainLayeredPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainLayeredPane)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void closeLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeLabelMouseClicked
        this.dispose();
    }//GEN-LAST:event_closeLabelMouseClicked

    private void maximizeLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_maximizeLabelMouseClicked
        if (this.getExtendedState() == this.MAXIMIZED_BOTH) {
            this.setExtendedState(this.NORMAL);
        } else {
            this.setExtendedState(this.MAXIMIZED_BOTH);
        }
    }//GEN-LAST:event_maximizeLabelMouseClicked

    private void minimizeLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizeLabelMouseClicked
        this.setExtendedState(this.ICONIFIED);
    }//GEN-LAST:event_minimizeLabelMouseClicked

    private void dashboardLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardLabelMouseClicked
        switchPanel(contentLayeredPane,dashboardPanel);
        A_3();
    }//GEN-LAST:event_dashboardLabelMouseClicked

    private void usersLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersLabelMouseClicked
        switchPanel(contentLayeredPane,usersPanel);
    }//GEN-LAST:event_usersLabelMouseClicked

    private void inventoryLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryLabelMouseClicked
        switchPanel(contentLayeredPane,inventoryPanel);
    }//GEN-LAST:event_inventoryLabelMouseClicked

    private void categoryLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_categoryLabelMouseClicked
        switchPanel(contentLayeredPane,categoryPanel);
    }//GEN-LAST:event_categoryLabelMouseClicked

    private void salesLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salesLabelMouseClicked
        switchPanel(contentLayeredPane,salesPanel);
    }//GEN-LAST:event_salesLabelMouseClicked

    private void returnItemLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_returnItemLabelMouseClicked
        switchPanel(contentLayeredPane,returnItemPanel);
    }//GEN-LAST:event_returnItemLabelMouseClicked

    private void priceListLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_priceListLabelMouseClicked
        switchPanel(contentLayeredPane,pricelistPanel);
    }//GEN-LAST:event_priceListLabelMouseClicked

    private void reportLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reportLabelMouseClicked
        switchPanel(contentLayeredPane,reportPanel);
    }//GEN-LAST:event_reportLabelMouseClicked

    private void logoutLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutLabelMouseClicked
        mydb.setCurrentUser("nullUser", this);
        new LoginFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoutLabelMouseClicked

    private void inventoryAddBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventoryAddBtnActionPerformed
        try{
            Object[] value = new Object[]{im4.getSelectedItem(),im1.getText(),im2.getText(),im3.getText(),im5.getText(),Helper.get_AddedDate(im6)};

            if(value != null){
                mydb.addInventoryValue(value);
                A_1();

                im1.setText("");
                im2.setText("");
                im3.setText("");
                im5.setText("");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Something went wrong!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_inventoryAddBtnActionPerformed

    private void im3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_im3KeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume(); 
        }
    }//GEN-LAST:event_im3KeyTyped

    private void im5KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_im5KeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE) {
            evt.consume();
        }
    }//GEN-LAST:event_im5KeyTyped

    private void inventoryEditBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventoryEditBtnActionPerformed
        int getSelectedRow = inventoryTable.getSelectedRow();
        int getSelectedColumn = inventoryTable.getSelectedColumn();
        Object getId = Helper.get_RecordID(inventoryTable);
        Object getVal = null;
        
        if(getSelectedColumn != 1){
            getVal = Helper.getNewValTable(inventoryTable);
        }else{
            getVal = Helper.getComboxFromTable(inventoryTable);
        }
        
        if(getVal != null){
            mydb.EditInventoryValue(getVal, getSelectedColumn, getId);
            A_1();
        }else{
            JOptionPane.showMessageDialog(this, "Something went wrong!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_inventoryEditBtnActionPerformed

    private void inventoryDeleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventoryDeleteBtnActionPerformed
        if(Helper.has_NoZeroVal(Helper.get_RecordIDs(inventoryTable)) && inventoryTable.getSelectedRow() != -1){
            for(Object i : Helper.get_RecordIDs(inventoryTable)){
                mydb.DeleteInventoryRecord(i);
            }
            A_1();
        }else{
            JOptionPane.showMessageDialog(this, "No cell is being selected.", "Inventory", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_inventoryDeleteBtnActionPerformed

    private void inventorySearchBarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inventorySearchBarKeyReleased
        mydb.loadInventoryData(inventoryTable, inventorySearchBar.getText());
    }//GEN-LAST:event_inventorySearchBarKeyReleased

    private void CAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CAddActionPerformed
        try{
            String getVal = addCategory.getText();
            if(!getVal.isEmpty()){
                mydb.addCategoryValue(new String[]{getVal,Helper.get_AddedDate(im6).toString()});
                A_2();
                addCategory.setText("");
            }else{
                JOptionPane.showMessageDialog(this, "Category connot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Something went wrong!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_CAddActionPerformed

    private void CEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CEditActionPerformed
        try{
            int getSelectedColumn = categoryTable.getSelectedColumn();
        
            if(getSelectedColumn != -1){
                Object getId = Helper.get_RecordID(categoryTable);
                Object getNewVal = Helper.getNewValTable(categoryTable);
                if(getNewVal != null){
                    mydb.EditCategoryValue(getNewVal, getSelectedColumn, getId);
                    A_2();
                }
            }else{
                JOptionPane.showMessageDialog(this, "No cell is being selected.", "Category", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Something went wrong!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_CEditActionPerformed

    private void CDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CDeleteActionPerformed
        if(Helper.has_NoZeroVal(Helper.get_RecordIDs(categoryTable)) && categoryTable.getSelectedRow() != -1){
            for(Object i : Helper.get_RecordIDs(categoryTable)){
                mydb.DeleteCategoryRecord(i);
            }
            A_2();
        }else{
            JOptionPane.showMessageDialog(this, "No cell is being selected.","Category",JOptionPane.ERROR_MESSAGE);
        }  
    }//GEN-LAST:event_CDeleteActionPerformed

    private void categorySearchBarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_categorySearchBarKeyReleased
        mydb.loadCategoryData(categoryTable, categorySearchBar.getText());
    }//GEN-LAST:event_categorySearchBarKeyReleased

    private void dashboardLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardLabel1MouseClicked
        switchPanel(contentLayeredPane,dashboardPanel);
        A_3();
    }//GEN-LAST:event_dashboardLabel1MouseClicked

    private void userLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userLabel1MouseClicked
        switchPanel(contentLayeredPane,usersPanel);
    }//GEN-LAST:event_userLabel1MouseClicked

    private void salesLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salesLabel1MouseClicked
        switchPanel(contentLayeredPane,salesPanel);
    }//GEN-LAST:event_salesLabel1MouseClicked

    private void returnItemLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_returnItemLabel1MouseClicked
        switchPanel(contentLayeredPane,returnItemPanel);
    }//GEN-LAST:event_returnItemLabel1MouseClicked

    private void pricelistlabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pricelistlabel1MouseClicked
        switchPanel(contentLayeredPane,pricelistPanel);
    }//GEN-LAST:event_pricelistlabel1MouseClicked

    private void logoutLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutLabel1MouseClicked
        mydb.setCurrentUser("nullUser", this);
        new LoginFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoutLabel1MouseClicked

    private void salesCategoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salesCategoryComboBoxActionPerformed
        String get_val = (String) salesCategoryComboBox.getSelectedItem(); 
        
        mydb.loadInventoryDataSales(salesTable1, get_val);
    }//GEN-LAST:event_salesCategoryComboBoxActionPerformed

    private void salesTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_salesTable1MouseClicked
        setSelectedItem();
    }//GEN-LAST:event_salesTable1MouseClicked

    private double calculateTotalPrice(int quantity) {
        int getSelectedRow = salesTable1.getSelectedRow();
        double unitPrice = Double.parseDouble(salesTable1.getValueAt(getSelectedRow, 4).toString());
        return unitPrice * quantity;
    }    
    
    private void quantitySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_quantitySpinnerStateChanged
        try{
            int newQuantity = (Integer) quantitySpinner.getValue();
            double totalPrice = calculateTotalPrice(newQuantity);
            totalSpinner.setValue(totalPrice);
        }catch(ArrayIndexOutOfBoundsException e){
            JOptionPane.showMessageDialog(this, "No product is being selected.", "Sales", JOptionPane.ERROR_MESSAGE);
            SpinnerNumberModel spinnerModel1 = new SpinnerNumberModel(0, 0, 0, 1);
            quantitySpinner.setModel(spinnerModel1);
        }
    }//GEN-LAST:event_quantitySpinnerStateChanged

    private void addCartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCartBtnActionPerformed
        try {
            String itemName = itemNameTextField.getText();
            if (itemName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an item name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double retailPrice = Double.parseDouble(priceSpinner.getValue().toString());
            double discount = Double.parseDouble(discountSpinner.getValue().toString());
            int quantity = (int) quantitySpinner.getValue();

            if (retailPrice <= 0 || quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Retail price and quantity must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double subtotalWithoutDiscount = retailPrice * quantity;
            double subtotal = subtotalWithoutDiscount;
            double total = (discount > 0) ? (retailPrice - (retailPrice * (discount / 100))) * quantity : subtotalWithoutDiscount;

            DefaultTableModel model = (DefaultTableModel) salesTable2.getModel();
            boolean itemExists = false;
            int rowIndex = -1;

            for (int i = 0; i < model.getRowCount(); i++) {
                if (itemName.equals(model.getValueAt(i, 0))) {
                    itemExists = true;
                    rowIndex = i;
                    break;
                }
            }

            if (itemExists) {
                int currentQuantity = (int) model.getValueAt(rowIndex, 3);
                double currentSubtotal = (double) model.getValueAt(rowIndex, 4);
                double currentTotal = (double) model.getValueAt(rowIndex, 5);

                double updatedSubtotal = currentSubtotal + subtotalWithoutDiscount;
                double updatedTotal = currentTotal + total;
                int updatedQuantity = currentQuantity + quantity;

                model.setValueAt(updatedQuantity, rowIndex, 3);
                model.setValueAt(updatedSubtotal, rowIndex, 4);
                model.setValueAt(updatedTotal, rowIndex, 5);
            } else {
                String formattedDiscount = (discount > 0) ? String.format("%.0f%%", discount) : "No discount";
                Object[] rowData = {itemName, retailPrice, formattedDiscount, quantity, subtotal, total};
                model.addRow(rowData);
            }
//            recieptTextArea.setText(SMT.generateReceipt(model));
            totalSpinner.setValue(0);
            itemNameTextField.setText("");
            descriptionTextField.setText("");
            priceSpinner.setValue(0);
            quantitySpinner.setValue(0);
            discountSpinner.setValue(0);
            
            double getTotal = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                getTotal += Double.parseDouble((model.getValueAt(i, 5)).toString());
            }
            String formattedTotal = String.format("%.2f", getTotal);
            getTotalTextField.setText(formattedTotal);
            
//            double get_total = Double.parseDouble(getTotalTextField.getText());
            if (Double.parseDouble(getTotalTextField.getText()) < 1) {
                receivedTextField.setEnabled(false);
            } else {
                receivedTextField.setEnabled(true);
            }
            
            try{
                String receivedTextVal = receivedTextField.getText();
            
                double receivedVal = Double.parseDouble(receivedTextVal);
                if (!receivedTextVal.isEmpty()) {
                    if (receivedVal < getTotal) {
                        payBtn.setEnabled(false);
                        printBtn.setEnabled(false);
                    } else {
                        payBtn.setEnabled(true);
                        printBtn.setEnabled(true); 
                    }
                } else {
                    payBtn.setEnabled(false);
                    printBtn.setEnabled(false);
                }
            }catch(NumberFormatException e){}
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input format. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addCartBtnActionPerformed

    private void totalSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_totalSpinnerStateChanged
        try{
            int newQuantity = (Integer) quantitySpinner.getValue();
            double totalPrice = calculateTotalPrice(newQuantity);
            totalSpinner.setValue(totalPrice);
        }catch(ArrayIndexOutOfBoundsException e){
            JOptionPane.showMessageDialog(this, "No product is being selected.", "Sales", JOptionPane.ERROR_MESSAGE);
            SpinnerNumberModel spinnerModel1 = new SpinnerNumberModel(0, 0, 0, 1);
            totalSpinner.setModel(spinnerModel1);
        }
    }//GEN-LAST:event_totalSpinnerStateChanged

    private void removeCartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeCartBtnActionPerformed
        DefaultTableModel model = (DefaultTableModel) salesTable2.getModel();
        int selectedRow = salesTable2.getSelectedRow();
        String oldTotalVal = getTotalTextField.getText();
        String receivedTextVal = receivedTextField.getText();
        if (selectedRow >= 0) {
            double receivedVal = Double.parseDouble(receivedTextVal);
            double curTotalVal = Double.parseDouble(oldTotalVal);
            double minusVal = Double.parseDouble(model.getValueAt(selectedRow, 5).toString());

            model.removeRow(selectedRow);

            curTotalVal -= minusVal;
            String formattedTotal = String.format("%.2f", curTotalVal);

            getTotalTextField.setText(formattedTotal);

            if (model.getRowCount() == 0) {
                recieptTextArea.setText("");
                invoiceTextField.setText("");
                payBtn.setEnabled(false);
                printBtn.setEnabled(false);
            }
            
            if (!receivedTextVal.isEmpty()) {
                if (receivedVal < curTotalVal || curTotalVal == 0) {
                    payBtn.setEnabled(false);
                    printBtn.setEnabled(false);
                } else {
                    payBtn.setEnabled(true);
                    printBtn.setEnabled(true);
                }
            } else {
                payBtn.setEnabled(false);
                printBtn.setEnabled(false);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No cell is being selected.", "Sales", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_removeCartBtnActionPerformed

    private void payBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payBtnActionPerformed
        DefaultTableModel model = (DefaultTableModel) salesTable2.getModel();
        
        String getINV = Helper.generateInvoiceNumber();
        invoiceTextField.setText(getINV); 
        
        String getDate = Helper.getCurrentDate(dateCHooser);
        Object dateRec = Helper.get_AddedDate(dateCHooser);
        recieptTextArea.setText(Helper.generateReceipt(model,getDate));
        
        for (int i = 0; i < model.getRowCount(); i++) {
            
            mydb.reduceProductQuantity(model.getValueAt(i, 3), model.getValueAt(i, 0));
            
            Object item = model.getValueAt(i, 0);
            Object discountPercent = Helper.convertPercentageToNumber(model.getValueAt(i, 2).toString());
            Object quantity = model.getValueAt(i, 3);
            Object subtotal = model.getValueAt(i, 4);
            Object total = model.getValueAt(i, 5);
            
            mydb.recordPurchase(getINV, item, discountPercent,quantity,subtotal,total,dateRec);
            System.out.println();
        }
        
        inventoryTable.setModel(mydb.DisplayInventoryData());
        String get_val = (String) salesCategoryComboBox.getSelectedItem(); 
        
        mydb.loadInventoryDataSales(salesTable1, get_val);
        
        double total = Double.parseDouble(getTotalTextField.getText());
        double received = Double.parseDouble(receivedTextField.getText());
        
        double balance = received - total;

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedBalance = "₱ " + decimalFormat.format(balance);

        getBalanceTextField.setText(formattedBalance);
    }//GEN-LAST:event_payBtnActionPerformed

    private void printBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBtnActionPerformed
        System.out.println(recieptTextArea.getText());
    }//GEN-LAST:event_printBtnActionPerformed

    private void receivedTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_receivedTextFieldKeyReleased
        try{
            double total = Double.parseDouble(getTotalTextField.getText());
            double received = Double.parseDouble(receivedTextField.getText());

            if (total > received) {
                payBtn.setEnabled(false);
            } else {
                payBtn.setEnabled(true);
            }
        }catch(NumberFormatException e){
                
        }
    }//GEN-LAST:event_receivedTextFieldKeyReleased

    private void receivedTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_receivedTextFieldKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != KeyEvent.VK_PERIOD && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE
                && (c == '.' && receivedTextField.getText().contains("."))) {
            evt.consume(); 
        }
    }//GEN-LAST:event_receivedTextFieldKeyTyped

    private void adminSettingsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminSettingsBtnActionPerformed
        switchPanel(contentLayeredPane,adminPanel);
    }//GEN-LAST:event_adminSettingsBtnActionPerformed
    private static File get_imgFile1;
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        get_imgFile1 = Helper.getImage(this);     
        if(get_imgFile1 != null){
//            imageAvatar2.setIcon(new ImageIcon(get_imgFile1.getAbsolutePath()));
//            imageAvatar2.repaint();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void addUserBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUserBtnActionPerformed
        String userId = Helper.userIdGenerator(mydb.getHowManyUsers());
        String firstName = getFNameTextField.getText();
        String lastName = getLNameTextField.getText();
        String userName = getUNameTextField.getText();
        String password = String.valueOf(getPasswordPasswordField.getPassword());
        String confirmPassword = String.valueOf(getConfirmPasswordPasswordField.getPassword());
        String gender = (String) getGenderComboBox.getSelectedItem();
        
        
        AbstractMap.SimpleEntry<String, Path> imageEntry = new AbstractMap.SimpleEntry<>("nullProfile.jpg", null);
        
        if(get_imgFile1 != null){
            imageEntry = Helper.generateImageName(firstName, get_imgFile1);
        }
        
        Object[] allValue = Helper.getAllUserInput(userId, firstName, lastName, userName, password, confirmPassword, gender, getBirthDateDateChooser, imageEntry.getKey(),4,this);
        
        if(allValue != null){
            if(mydb.addUser(allValue)){
                if(imageEntry.getValue() != null){
                    if (get_imgFile1 != null) {
                        Helper.addTheImage(get_imgFile1, imageEntry.getValue());
                    } 
                }
            }
            mydb.DisplayUserData(usersTable);
            getFNameTextField.setText("");
            getLNameTextField.setText("");
            getUNameTextField.setText("");
            getPasswordPasswordField.setText("");
            getConfirmPasswordPasswordField.setText("");
            addUserBtn.setEnabled(false);
            getBirthDateDateChooser.setDate(null);
            termServiceCheckBox.setSelected(false);
        }
    }//GEN-LAST:event_addUserBtnActionPerformed

    private void termServiceCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_termServiceCheckBoxActionPerformed
        if(termServiceCheckBox.isSelected()){
            addUserBtn.setEnabled(true);
        }else{
            addUserBtn.setEnabled(false);
        }
    }//GEN-LAST:event_termServiceCheckBoxActionPerformed

    private void userEditBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userEditBtnActionPerformed
        int getSelectedRow = usersTable.getSelectedRow();
        int getSelectedColumn = usersTable.getSelectedColumn();
        
        if(getSelectedColumn != -1 || getSelectedRow != -1){
            Object getNewVal = null;
            
            if(getSelectedColumn == 6){
                getNewVal = Helper.getComboxFromTable(usersTable, 6);
            }else{
                getNewVal = Helper.getNewValTable(usersTable);
            }

            if(getNewVal != null){
                mydb.EditUserData(getNewVal, getSelectedColumn, Helper.get_RecordID(usersTable));
                mydb.DisplayUserData(usersTable);
            }
        }else{
            JOptionPane.showMessageDialog(this, "No cell is being selected.", "Admin", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_userEditBtnActionPerformed

    private void deleteUserBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteUserBtnActionPerformed
        if(Helper.has_NoZeroVal(Helper.get_RecordIDs(usersTable)) && usersTable.getSelectedRow() != -1){
            
            for(Object i : Helper.get_RecordIDs(usersTable)){
                mydb.DeleteUser(i);
            }
            for(int i : usersTable.getSelectedRows()){
                String j = usersTable.getValueAt(i, 7).toString();
                if(!j.equals("nullProfile.jpg")){
                    Helper.deleteImage(j);
                }
            }
            mydb.DisplayUserData(usersTable);
        }else{
            JOptionPane.showMessageDialog(this, "No cell is being selected.", "Admin", JOptionPane.INFORMATION_MESSAGE);
        }    
    }//GEN-LAST:event_deleteUserBtnActionPerformed

    private void imageAvatar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageAvatar1MouseClicked
        openImage(imageAvatar1.getIcon());
    }//GEN-LAST:event_imageAvatar1MouseClicked

    private void setSelectedItem() {
        try{
            int getSelectedRow = salesTable1.getSelectedRow();
            itemNameTextField.setText(salesTable1.getValueAt(getSelectedRow, 1).toString());
            descriptionTextField.setText(salesTable1.getValueAt(getSelectedRow, 2).toString());

            int quantityMax = Integer.parseInt(salesTable1.getValueAt(getSelectedRow, 3).toString());

            if (quantityMax == 0) {
                JOptionPane.showMessageDialog(this, "Out of stock");
            } else {
                SpinnerNumberModel spinnerModel1 = new SpinnerNumberModel(0, 0, quantityMax, 1);
                quantitySpinner.setModel(spinnerModel1);

                Object priceValue = salesTable1.getValueAt(getSelectedRow, 4);
                priceSpinner.setValue(priceValue);
            }
        }catch(NullPointerException e){
            
        }
    }

    public static void openImage(Icon getImage){
        String imagePath = getImage.toString();
        
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists() && (imageFile.isFile() || imageFile.canRead())) {
                Desktop.getDesktop().open(imageFile);
            } else {
                System.err.println("File does not exist or cannot be read.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }   
    }
    public static void switchPanel(JLayeredPane layered, JPanel panel){
        layered.removeAll();
        layered.add(panel);
        layered.repaint();
        layered.revalidate();         
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CAdd;
    private javax.swing.JButton CDelete;
    private javax.swing.JButton CEdit;
    private javax.swing.JButton addCartBtn;
    private javax.swing.JTextField addCategory;
    private javax.swing.JButton addUserBtn;
    private javax.swing.JPanel adminMenu;
    private javax.swing.JPanel adminPanel;
    private javax.swing.JButton adminSettingsBtn;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JPanel categoryPanel;
    private javax.swing.JTextField categorySearchBar;
    private javax.swing.JTable categoryTable;
    private javax.swing.JLabel closeLabel;
    private javax.swing.JLayeredPane contentLayeredPane;
    private javax.swing.JLabel dashboardLabel;
    private javax.swing.JLabel dashboardLabel1;
    private javax.swing.JPanel dashboardPanel;
    private com.toedter.calendar.JDateChooser dateCHooser;
    private javax.swing.JButton deleteUserBtn;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JSpinner discountSpinner;
    private javax.swing.JLabel fullNameLabel;
    private javax.swing.JLabel genderLabel;
    private javax.swing.JTextField getBalanceTextField;
    private com.toedter.calendar.JDateChooser getBirthDateDateChooser;
    private javax.swing.JPasswordField getConfirmPasswordPasswordField;
    private javax.swing.JTextField getFNameTextField;
    private javax.swing.JComboBox<String> getGenderComboBox;
    private javax.swing.JTextField getLNameTextField;
    private javax.swing.JPasswordField getPasswordPasswordField;
    private javax.swing.JTextField getTotalTextField;
    private javax.swing.JTextField getUNameTextField;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JTextField im1;
    private javax.swing.JTextField im2;
    private javax.swing.JTextField im3;
    private javax.swing.JComboBox<String> im4;
    private javax.swing.JTextField im5;
    private com.toedter.calendar.JDateChooser im6;
    private customComponents.ImageAvatar imageAvatar1;
    private customComponents.ImageAvatar imageAvatar2;
    private javax.swing.JButton inventoryAddBtn;
    private javax.swing.JButton inventoryDeleteBtn;
    private javax.swing.JButton inventoryEditBtn;
    private javax.swing.JLabel inventoryLabel;
    private javax.swing.JPanel inventoryPanel;
    private javax.swing.JTextField inventorySearchBar;
    private javax.swing.JTable inventoryTable;
    private javax.swing.JTextField invoiceTextField;
    private javax.swing.JTextField itemNameTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel logoutLabel;
    private javax.swing.JLabel logoutLabel1;
    private javax.swing.JLayeredPane mainLayeredPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel maximizeLabel;
    private javax.swing.JLayeredPane menuLayeredPane;
    private javax.swing.JLabel minimizeLabel;
    private javax.swing.JPanel notAdminMenu;
    private javax.swing.JLabel outStockLabel;
    private javax.swing.JButton payBtn;
    private javax.swing.JLabel priceListLabel;
    private javax.swing.JSpinner priceSpinner;
    private javax.swing.JPanel pricelistPanel;
    private javax.swing.JLabel pricelistlabel1;
    private javax.swing.JButton printBtn;
    private javax.swing.JSpinner quantitySpinner;
    private javax.swing.JTextField receivedTextField;
    private javax.swing.JTextArea recieptTextArea;
    private javax.swing.JButton removeCartBtn;
    private javax.swing.JLabel reportLabel;
    private javax.swing.JPanel reportPanel;
    private javax.swing.JLabel returnItemLabel;
    private javax.swing.JLabel returnItemLabel1;
    private javax.swing.JPanel returnItemPanel;
    private javax.swing.JComboBox<String> salesCategoryComboBox;
    private javax.swing.JLabel salesLabel;
    private javax.swing.JLabel salesLabel1;
    private javax.swing.JPanel salesPanel;
    private javax.swing.JTable salesTable1;
    private javax.swing.JTable salesTable2;
    private javax.swing.JLabel soldOldLabel;
    private javax.swing.JLabel soldTotayLabel;
    private javax.swing.JCheckBox termServiceCheckBox;
    private javax.swing.JLabel todaysalesLabel;
    private javax.swing.JLabel totalProductsLabel;
    private javax.swing.JLabel totalSalesLabel;
    private javax.swing.JSpinner totalSpinner;
    private javax.swing.JButton userEditBtn;
    private javax.swing.JLabel userLabel1;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JLabel usersLabel;
    private javax.swing.JPanel usersPanel;
    private javax.swing.JTable usersTable;
    // End of variables declaration//GEN-END:variables
}
