
package testrfid;

import java.util.Timer;
import java.util.TimerTask;
import javax.swing.DefaultListModel;

/**
 *
 * @author NEERAJ
 */
public class Final_RFID_Read_Code extends javax.swing.JDialog {

    public int port = 0;              // Variable for Port No. (0 is for Index No of CB AutoOpenComPort Method)
    public byte ComAddr=(byte)255;            // Com Address (FF in Hexadecimal Fix)
    public int boudRate = 5;          // Boud Rate (Set 5 for 57600bps actual boud rate)
    public int comPortIndex = 0;      // index (This is COM Port Index in Combo Box)
    public boolean singleRead = false;
    public Boolean isComPortOpen = false;  //  Boolean Variable to check COM Port is Open or Not
    boolean no_more_further_read = false;
    int[] Recv=new int[5000];       // A outpot Recv Array to AutoComPort Method Return
    
    public DefaultListModel listModel = new DefaultListModel();    //  Model Array of JList
    //  Timer Variable
    public Timer timer;
    public int counter = 0;
    public String TagId = null;        // Variable to store Tag Id;
    static{
          System.loadLibrary("UHF_Reader18");       // Load UHF_Reader18.dll from Java Native Path
	}
    
    UHF.Reader18 tnt = new UHF.Reader18();      /*  Make Object of Reader18 Class in which All the Native Methods of DLL
                                                Declared */
    
    
    /**
     * Creates new form RFTest
     * @param parent
     * @param modal
     */
    public Final_RFID_Read_Code(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
//        timerMethod();      // Call Timer Method
    }
    
    
//  Reset All the Variables Method
    public void _Reset_RFID_Variables(){
        port = 0;              // Variable for Port No. (0 is for Index No of CB AutoOpenComPort Method)
        ComAddr=(byte)255;            // Com Address (FF in Hexadecimal Fix)
        boudRate = 5;          // Boud Rate (Set 5 for 57600bps actual boud rate)
        comPortIndex = 0;      // index (This is COM Port Index in Combo Box)
        singleRead = false;    // make Single Read Variable Default false 
        isComPortOpen = false;  //  Boolean Variable to check COM Port is Open or Not
        no_more_further_read = false;
    }
    
    
    
//  Open Com Port Method
    private void AutoOpenComPort(byte l_ComAddr, int l_boudRate){
        int [] SendBuff = new int[5000];    // An input Array to paas in AutoOpenComPort with Port No, Boud Rate, COM Address, and COM Port Index
//      int[] Recv=new int[5000];       // A outpot Recv Array to AutoComPort Method Return
        
        SendBuff[0] = l_ComAddr;        // l_ComAddr = 255 (-1 in byte);
        SendBuff[1] = l_boudRate;       // l_boudRate = 5; (but actual in 57600)
        
//  Check Com Port is Open or Not
        if(isComPortOpen == false){
            Port_Label.setText("Port is Close");
        Recv =    tnt.AutoOpenComPort(SendBuff);        // Method to AutoOpenComPort (Auto COM3) This Method will return Array
        }                                              // of 4 bytes [0, 3, 0, 3]                
//  Check COM Port is Open or Not        
        if(Recv[0] == 0){           // If Recv[0] = 0 then Com Port is Open Otherwise Port is Closed
            isComPortOpen = true;
            Port_Label.setText("Port is Open");
//------------------------- SETWORKMODE() Method -------------------------------            
//  Input Array for SetWorkMode Method
        int work [] = new int[8];       // Array for SetWorkMode() Method
        work[0] = 0;                   // First Element of Work Array is PortNo i.e. 0  
        work[1] = 0;
        work[2] = 0;
        work[3] = 1;
        work[4] = 2;
        work[5] = 1;
        work[6] = 0;
        work[7] = Recv[3];   // Last Element of Work Array is Com Port Index i.e. = 3 means COM3
        
        tnt.SetWorkMode(work);     // This Method set Work Mode in Answer Mode (in which we can query for Tag Read)
    }
    }     
    
 
//  RFID Tag Read Method    
    public String RFID_TagRead(int l_port, byte l_ComAddr, int l_boudRate, int l_comPortIndex, boolean l_readSingle){
        int cardCount = 0;          // No of read Card Count
        int [] SendBuff = new int[5000];    // An input Array to paas in AutoOpenComPort with Port No, Boud Rate, COM Address, and COM Port Index
        int[] Recv=new int[5000];       // A outpot Recv Array to AutoComPort Method Return
        
        SendBuff[0] = l_port;           // l_port = 0;
        SendBuff[1] = l_ComAddr;        // l_ComAddr = 255 (-1 in byte);
        SendBuff[2] = l_boudRate;       // l_boudRate = 5; (but actual in 57600)
        SendBuff[3] = l_comPortIndex;   // l_comportIndex = 0; (Com Port Index)
        
//  Check Com Port is Open or Not
        if(isComPortOpen == false){
            Port_Label.setText("Port is Close");
            System.out.println("Port is Close");
        Recv =    tnt.AutoOpenComPort(SendBuff);        // Method to Open Com Port (Auto COM3) This Method will give Array
        }                                              // of 4 bytes [0, 3, 0, 3]                
        
        l_port = Recv[0];                   //  Recv[0] = 0, Recv[0] = 3, Recv[2] = 0, Recv[3] = 3
        l_ComAddr = (byte)Recv[1];
        l_boudRate = Recv[2];
        l_comPortIndex = Recv[3];
                
        if(Recv[0] == 0){           // If Recv[0] = 0 then Com Port is Open Otherwise Port is Closed
            isComPortOpen = true;
            Port_Label.setText("Port is Open");
            System.out.println("Port is Open");
//------------------------- SETWORKMODE() Method -------------------------------            
//  Input Array for SetWorkMode Method
        int work [] = new int[8];       // Array for SetWorkMode() Method
        work[0] = 0;              // First Element of Work Array is PortNo i.e. 0  
        work[1] = 0;
        work[2] = 0;
        work[3] = 1;
        work[4] = 2;
        work[5] = 1;
        work[6] = 0;
        work[7] = l_comPortIndex;   // Last Element of Work Array is Com Port Index i.e. = 0
        
        tnt.SetWorkMode(work);     // This Method set Work Mode in Answer Mode (in which we can query for Tag Read)
    
//------------------------- TAG Read Method Inventory_G2 -----------------------        
//  Variables for Card Read            
    int inventory_g2 [] = new int[2];
        inventory_g2[0] = 0;           // First byte for Inventory_G2 method if Port No Index from Combo Box = 0 (for Auto)
        inventory_g2[1] = 3;           // Second byte for Inventory_G2 method if Com Port Index which is 3
        
    int [] output_inventory_g2 = new int[5000];      // Inventory_G2 Method returns an Array so output_inventory_g2 is an output array
        
    output_inventory_g2 = tnt.Inventory_G2(inventory_g2);   // Set Output of Inventory_G2 Method in output Array
        
        cardCount = output_inventory_g2[1];     // No. of Cards(Tags) Count Read
        if(cardCount >0 && l_readSingle == true){     // Check Card Count for Single Tag Read and make Card Count 1 
            cardCount = 1;
        }
        int EPC_len = output_inventory_g2[2];   // Third byte of ouput_inventory_g2 will give the length of the EPC Tag
                                                // 1 Tag then EPC Length = 13, 2 Tag Read then EPC Length = 26 and so on...                            
//  Array to Store RFID in bytes
        byte EPC [] = new byte[EPC_len];        // Final EPC Array in Size of length of EPC Tag for storing RFID Tag Id in byte
        
//  Copy Inventory_G2 Output  Array EPC Array
        for(int j =3; j<=EPC_len + 2; j++){
            EPC[j-3] = (byte)output_inventory_g2[j];    // Copy All the TagId Bytes in EPC Array from output_inventory_g2 
        }

//  Variable for Making SubString of EPC Tag Id, Convert into Hexadecimal No etc.         
    int j;              // Variable of for loop
    int EPC_length;     //  Variable for EPC always will be 12
    int startIndex = 0; //  start index (initial point of String) for Making Sub String 
    int endIndex = 0;   //  end index (end point of String) for Making Sub String 
    int m = 0;          //  Local Variable to Take value from EPC Array (EPC[0] = 12, EPC[24] = 12)
    int l = 0;          // Variable for List View Elements

// Function to Convert Byte Array to String
    String EPC_Hex = javax.xml.bind.DatatypeConverter.printHexBinary(EPC);      
//  For loop to execute 
    for(j = 0 ; j<cardCount; j++){        // loop for Read Inventory_G2 Method Output Array
        EPC_length = EPC[m];
        endIndex = (endIndex + EPC_length * 2) + 2;
        TagId = EPC_Hex.substring(startIndex+2, endIndex);     // Remove First two byte from the EPC Tag Id which is Tag Length
        m = m + EPC_length + 1;
        startIndex = endIndex ;

        Boolean exist = false;      // Boolean Variable to check Tag Id is exist or not in List View
//  Set Tag Id in List View Model when not Exist   
        for(l = 0; l<listModel.getSize(); l++){         //  For loop to check exist in loop 
        if(TagId.equals(listModel.getElementAt(l))){    //  Check Tag Id Exist or not in JListMode 
            System.out.println(listModel.getElementAt(l));
        exist = true;                                  // If Exist then set Boolean Variable esite = true                      
        break;                                         // and break the loop 
        }       // end of if
        }       // end of for
        if(!exist){     // If exist = false
            if(l_readSingle == true)
                no_more_further_read = true;
            listModel.addElement(TagId); }      // Now set Tag Id in JListModel
        }
        List_View.setModel(listModel);          // Set list Model in List View
        
        }       // End of Com Port Recv Array 
        
        return TagId;
    }
    
//  Timer Class
    public void timerMethod(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
            if(!no_more_further_read){    
            RFID_TagRead(port, ComAddr, boudRate, comPortIndex, singleRead);
            Timer_Label.setText("Timer Count =" + Integer.toString(counter));
            counter++;
            } else {
            timer.purge();      // To remove all the cancel task from the queue
            timer.cancel();     // Cancel all the task method
            }
        }};
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 3, 100);
        System.out.println("Timer Started");
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        Port_Label = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        List_View = new javax.swing.JList<>();
        startBtn = new javax.swing.JButton();
        endButton = new javax.swing.JButton();
        Timer_Label = new javax.swing.JLabel();
        singleRead_Btn = new javax.swing.JButton();
        multiRead_Btn = new javax.swing.JButton();
        multiClose_Btn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButton1.setText("Open Com");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        Port_Label.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        Port_Label.setText("Port :");

        List_View.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(List_View);

        startBtn.setText("Start");
        startBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBtnActionPerformed(evt);
            }
        });

        endButton.setText("End");
        endButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endButtonActionPerformed(evt);
            }
        });

        Timer_Label.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        Timer_Label.setText("Timer");

        singleRead_Btn.setText("Single Read");
        singleRead_Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singleRead_BtnActionPerformed(evt);
            }
        });

        multiRead_Btn.setText("Multi Read");
        multiRead_Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiRead_BtnActionPerformed(evt);
            }
        });

        multiClose_Btn.setText("Multi Close");
        multiClose_Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiClose_BtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(246, 246, 246)
                        .addComponent(Timer_Label))
                    .addComponent(Port_Label)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(multiRead_Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(multiClose_Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(singleRead_Btn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(endButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(startBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(117, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(startBtn)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(endButton)
                .addGap(18, 18, 18)
                .addComponent(singleRead_Btn)
                .addGap(22, 22, 22)
                .addComponent(Timer_Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(multiRead_Btn)
                    .addComponent(multiClose_Btn))
                .addGap(40, 40, 40)
                .addComponent(Port_Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String tagId = RFID_TagRead(port, ComAddr, boudRate, comPortIndex, true);
        System.out.println("Tag Id 11111111111 = " +tagId);
//        Tag_Label.setText("Tag Id :" + tagId);     
    }//GEN-LAST:event_jButton1ActionPerformed

    private void startBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBtnActionPerformed
        timerMethod();      // Call Timer Method
    }//GEN-LAST:event_startBtnActionPerformed

    private void endButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endButtonActionPerformed
        timer.purge();      // To remove all the cancel task from the queue
        timer.cancel();     // Cancel all the task method
    }//GEN-LAST:event_endButtonActionPerformed

//  Single Read Method    
    private void singleRead_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleRead_BtnActionPerformed
        singleRead = true;      // For Single Tag Read set singleRead variable true
        listModel.clear();      // Before Calling the Timer Method clear the listModel Array
        no_more_further_read = false;   // set no more further read variable false (restrict to read RFID Tag more than one)
        timerMethod();      // Call Timer Method
    }//GEN-LAST:event_singleRead_BtnActionPerformed

//  Multi Read Method    
    private void multiRead_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiRead_BtnActionPerformed
        singleRead = false;     // For Multi Tag Read set singleRead variable true
        no_more_further_read = false;       // set no more further read variable false (restrict to read RFID Tag more than one) 
        timerMethod();      // Call Timer Method
    }//GEN-LAST:event_multiRead_BtnActionPerformed

//  Multi Close Method    
    private void multiClose_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiClose_BtnActionPerformed
        listModel.clear();  // Clear listModel again clear
        timer.purge();      // To remove all the cancel task from the queue
        timer.cancel();     // Cancel all the task method
    }//GEN-LAST:event_multiClose_BtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Final_RFID_Read_Code.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            Final_RFID_Read_Code dialog = new Final_RFID_Read_Code(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> List_View;
    private javax.swing.JLabel Port_Label;
    private javax.swing.JLabel Timer_Label;
    private javax.swing.JButton endButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton multiClose_Btn;
    private javax.swing.JButton multiRead_Btn;
    private javax.swing.JButton singleRead_Btn;
    private javax.swing.JButton startBtn;
    // End of variables declaration//GEN-END:variables
}
