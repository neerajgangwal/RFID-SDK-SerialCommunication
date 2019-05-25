
package testrfid;

/**
 *
 * @author NEERAJ
 */
public class RFID_Using_TCPIP extends javax.swing.JDialog {

    int arr[] =new int[3];
    int arr1[]=new int[3];
    
    int portno = 0;
    String ip = null;
    
    
    static{
          System.loadLibrary("UHF_Reader18");       // Load UHF_Reader18.dll from Java Native Path
	}
    UHF.Reader18 tnt = new UHF.Reader18();      /*  Make Object of Reader18 Class in which All the Native Methods of DLL
                                                Declared */
    
    
    /**
     * Creates new form RFID_Using_TCPIP
     * @param parent
     * @param modal
     */
    public RFID_Using_TCPIP(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    
    
    private int RFID_TCPIP_Connect(int address,int port,String ipAddress){
        int tcpIp[];
//  RFID Connection using TCPIP
/*  Returns: Return an array when successfully, non-zero value with first byte when error occurred.
            Second is reader address,and last byte is a handle of reader.       */
        tcpIp = tnt.OpenNetPort(address, port, ipAddress);      
        System.out.println("Open Net Port = " + tcpIp[0]+ " at 1st Index = "+ tcpIp[1]+ " at 2nd Index = "+ tcpIp[2]);
        
        if(tcpIp[0] == 0){
            System.out.println(" TCPIP is Connected ");
        }
        return tcpIp[0];
    }
    
    
    private void RFID_TCPIP_Disconnect(int port){
        int tcpIp;
/*  Returns: Zero value when successfully, non-zero value when error occurred    */
        tcpIp = tnt.CloseNetPort(port);      
        
        if(tcpIp == 0){
            System.out.println(" TCPIP Disconnected ");
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("RFID Using TCPIP");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

//  Dialog Open Code    
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        RFID_TCPIP_Connect(255, 6000, "192.168.1.190");
        
        RFID_TCPIP_Disconnect(6000);
    }//GEN-LAST:event_formWindowOpened

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
            java.util.logging.Logger.getLogger(RFID_Using_TCPIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            RFID_Using_TCPIP dialog = new RFID_Using_TCPIP(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                }});
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
