
package testrfid;

import java.util.Timer;
import java.util.TimerTask;
import javax.swing.DefaultListModel;

/**
 *
 * @author NEERAJ
 */
public class RFIDTest_Code extends javax.swing.JDialog {

    public int port = 0;              // Variable for Port No. (0 is for Index No of CB AutoOpenComPort Method)
    public byte ComAddr=(byte)255;            // Com Address (FF in Hexadecimal Fix)
    public int boudRate = 5;          // Boud Rate (Set 5 for 57600bps actual boud rate)
    public int comPortIndex = 0;      // index (This is COM Port Index in Combo Box)
    public boolean singleRead = false;
    public Boolean isComPortOpen = false;  //  Boolean Variable to check COM Port is Open or Not
    boolean no_more_further_read = false;
    int[] Recv=new int[5000];       // A outpot Recv Array to AutoComPort Method Return
    
    public DefaultListModel listModel = new DefaultListModel();    //  Model Array of JList
    public int listCount = 0;       // Variable to store list element count
    //  Timer Variable
    public Timer timer;
    public int counter = 0;
    public String TagId = null;        // Variable to store Tag Id;
    public static int cardCount = 0;          // No of read Card Count
    public static int actualCardCount = 0;          // No of read Card Count
    
    private int listLastCount = 0, listCurrentCount = -1;
    
    static{
          System.loadLibrary("UHF_Reader18");       // Load UHF_Reader18.dll from Java Native Path
	}
    
    UHF.Reader18 tnt = new UHF.Reader18();      /*  Make Object of Reader18 Class in which All the Native Methods of DLL
                                                Declared */
    
    //  HELLO NEERAJ GANGWAL
    //  HOW ARE YOU ?
    
//    I AM FINE
    
    /**
     * Creates new form RFTest
     * @param parent
     * @param modal
     */
    public RFIDTest_Code(java.awt.Frame parent, boolean modal) {
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
    public void AutoOpenComPort(byte l_ComAddr, int l_boudRate){
        int [] SendBuff = new int[5000];    // An input Array to paas in AutoOpenComPort with Port No, Boud Rate, COM Address, and COM Port Index
        
        SendBuff[0] = l_ComAddr;        // l_ComAddr = 255 (-1 in byte);
        SendBuff[1] = l_boudRate;       // l_boudRate = 5; (but actual in 57600)
        
//  Check Com Port is Open or Not
        if(isComPortOpen == false){
            Port_Label.setText("Port is Close");
        Recv =    tnt.AutoOpenComPort(SendBuff);        // Method to AutoOpenComPort (Auto COM3) This Method will return Array
        }                                              // of 4 bytes [0, 3, 0, 3] first byte is Port                
//  Check COM Port is Open or Not        
        if(Recv[0] == 0){           // If Recv[0] = 0 then Com Port is Open Otherwise Port is Closed
            isComPortOpen = true;
            Port_Label.setText("Port is Open");
//------------------------- SETWORKMODE() Method -------------------------------            
//  Input Array for SetWorkMode Method
        int work [] = new int[8];       // Array for SetWorkMode() Method
        work[0] = 0;                   // First Element of Work Array is PortNo i.e. 0 or Recv[0]  
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
    public String RFID_TagRead(boolean l_readSingle){
        if(Recv[0] == 0){           // If Recv[0] = 0 then Com Port is Open Otherwise Port is Closed
            isComPortOpen = true;
            Port_Label.setText("Port is Open");
    
//------------------------- TAG Read Method Inventory_G2 -----------------------        
//  Variables for Card Read            
    int inventory_g2 [] = new int[2];
        inventory_g2[0] = 0;           // First byte for Inventory_G2 method if Port No Index from Combo Box = 0 (for Auto)
        inventory_g2[1] = 3;           // Second byte for Inventory_G2 method if Com Port Index which is 3
        
    int [] output_inventory_g2 = new int[5000];      // Inventory_G2 Method returns an Array so output_inventory_g2 is an output array
        
    output_inventory_g2 = tnt.Inventory_G2(inventory_g2);   // Set Output of Inventory_G2 Method in output Array
        
//        actualCardCount = output_inventory_g2[1];     // Actual No. of Card Counts to Store in some other variable
        
        cardCount = output_inventory_g2[1];     // No. of Cards(Tags) Count Read
//        CardCount_Label.setText("Card Count: " + cardCount);
        if(cardCount >0 && l_readSingle == true){     // Check Card Count for Single Tag Read and make Card Count 1 
//            cardCount = 1;                        // Make Card Count = 1 to add only one tag in List
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

//  Function to Convert Byte Array to String
    String EPC_Hex = javax.xml.bind.DatatypeConverter.printHexBinary(EPC);      
//  For loop to execute 
    for(j = 0 ; j<cardCount; j++){        // loop for Read Inventory_G2 Method Output Array
        EPC_length = EPC[m];
        endIndex = (endIndex + EPC_length * 2) + 2;
        TagId = EPC_Hex.substring(startIndex+2, endIndex);     // Remove First two byte from the EPC Tag Id which is Tag Length
        m = m + EPC_length + 1;             // m is local variable to find the first byte of EPC array(EPC[0]) i.e. 12 (length of EPC Array)
        startIndex = endIndex ;

        Boolean tagExist_inList = false;      // Boolean Variable to check Tag Id is exist or not in List View
//  Set Tag Id in List View Model when not Exist   
        for(l = 0; l<listModel.getSize(); l++){         //  For loop to check exist in loop 
            listCount = listModel.getSize();            // get List Count ans Store in public Variable
            if(TagId.equals(listModel.getElementAt(l))){    //  Check Tag Id Exist or not in JListMode 
            tagExist_inList = true;                                   // If Exist then set Boolean Variable esite = true                      
            break;                                          // and break the loop 
            }       // end of if
        }       // end of for
        if(!tagExist_inList){     // If exist = false
                if(l_readSingle == true){
                no_more_further_read = true;
                }
            listModel.addElement(TagId); 
            }      // Now set Tag Id in JListModel
        }
        
        List_View.setModel(listModel);          // Set list Model in List View
        
        }       // End of Com Port Recv Array 

        return TagId;           // Return String (TagId)
    }
    
//  Timer Class
    public void RFID_timerMethod(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
            if(listCurrentCount == -1){}
                if(listLastCount != listCurrentCount) {
                   if(listCurrentCount > 0){
                        listLastCount = listCurrentCount;
                    }
                    ActualCardCount_Label.setText("Current = "+ listCurrentCount+ " && Last "+ listLastCount);
                    RFID_TagRead(singleRead);
                        if(listModel.getSize() > 0){
                            listCurrentCount = listModel.getSize();
                        }
                    Timer_Label.setText("Timer Count =" + Integer.toString(counter));
                    counter++;
                }
                else {
                    timer.purge();
                    timer.cancel();
                }
            
        }};
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 5, 1000);
//        System.out.println("Timer Started");
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Port_Label = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        List_View = new javax.swing.JList<>();
        Timer_Label = new javax.swing.JLabel();
        singleRead_Btn = new javax.swing.JButton();
        multiRead_Btn = new javax.swing.JButton();
        multiClose_Btn = new javax.swing.JButton();
        CardCount_Label = new javax.swing.JLabel();
        ActualCardCount_Label = new javax.swing.JLabel();
        ListLastCount_Label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        Port_Label.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        Port_Label.setText("Port :");

        List_View.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(List_View);

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

        CardCount_Label.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        CardCount_Label.setText("Card Count:");

        ActualCardCount_Label.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        ActualCardCount_Label.setText("list Current Count = ");

        ListLastCount_Label.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        ListLastCount_Label.setText("List Last Count =");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(multiRead_Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(multiClose_Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(70, 70, 70)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ListLastCount_Label)
                            .addComponent(ActualCardCount_Label)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(singleRead_Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Port_Label))
                        .addGap(118, 118, 118)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CardCount_Label)
                            .addComponent(Timer_Label))))
                .addContainerGap(141, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(singleRead_Btn)
                    .addComponent(Timer_Label))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 18, Short.MAX_VALUE)
                        .addComponent(Port_Label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CardCount_Label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(multiRead_Btn)
                            .addComponent(ActualCardCount_Label))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(multiClose_Btn)
                            .addComponent(ListLastCount_Label))))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

//  Single Read Method    
    private void singleRead_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleRead_BtnActionPerformed
        listLastCount = 0; listCurrentCount = -1;
        singleRead = false;      // For Single Tag Read set singleRead variable true
        listModel.clear();      // Before Calling the Timer Method clear the listModel Array
        no_more_further_read = false;   // set no more further read variable false (restrict to read RFID Tag more than one)
        RFID_timerMethod();      // Call Timer Method
    }//GEN-LAST:event_singleRead_BtnActionPerformed

//  Multi Read Method    
    private void multiRead_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiRead_BtnActionPerformed
        singleRead = false;     // For Multi Tag Read set singleRead variable true
        listModel.clear();      // Before Calling the Timer Method clear the listModel Array
        no_more_further_read = false;       // set no more further read variable false (restrict to read RFID Tag more than one) 
        RFID_timerMethod();      // Call Timer Method
    }//GEN-LAST:event_multiRead_BtnActionPerformed

//  Multi Close Method    
    private void multiClose_BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiClose_BtnActionPerformed
        timer.purge();      // To remove all the cancel task from the queue
        timer.cancel();     // Cancel all the task method
        System.out.println("Total Count = " + listCount);
    }//GEN-LAST:event_multiClose_BtnActionPerformed

//  Open Com Port on Window Open    
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        AutoOpenComPort(ComAddr, boudRate);     // Auto Open Com Port
    }//GEN-LAST:event_formWindowOpened

//  Close Com Port on Window Closing    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        tnt.CloseComPort();     // Close Com Port
    }//GEN-LAST:event_formWindowClosing

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
            java.util.logging.Logger.getLogger(RFIDTest_Code.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            RFIDTest_Code dialog = new RFIDTest_Code(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ActualCardCount_Label;
    private javax.swing.JLabel CardCount_Label;
    private javax.swing.JLabel ListLastCount_Label;
    public static javax.swing.JList<String> List_View;
    private javax.swing.JLabel Port_Label;
    private javax.swing.JLabel Timer_Label;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton multiClose_Btn;
    private javax.swing.JButton multiRead_Btn;
    private javax.swing.JButton singleRead_Btn;
    // End of variables declaration//GEN-END:variables
}
