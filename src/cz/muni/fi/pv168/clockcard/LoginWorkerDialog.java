package cz.muni.fi.pv168.clockcard;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Worker login dialog
 *
 * @author David Stein
 * @version 2011.0629
 */

public class LoginWorkerDialog extends javax.swing.JDialog {

    /** Creates new form LoginWorkerDialog */
    public LoginWorkerDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        loginLabel = new javax.swing.JLabel();
        loginTextField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        infoLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Translation"); // NOI18N
        setTitle(bundle.getString("LoginWorkerDialog.title")); // NOI18N
        setResizable(false);

        loginLabel.setText(bundle.getString("LoginWorkerDialog.loginLabel.text")); // NOI18N
        loginLabel.setName("loginLabel"); // NOI18N

        loginTextField.setName("loginTextField"); // NOI18N

        passwordLabel.setText(bundle.getString("LoginWorkerDialog.passwordLabel.text")); // NOI18N
        passwordLabel.setName("passwordLabel"); // NOI18N

        passwordField.setName("passwordField"); // NOI18N

        btnOk.setText(bundle.getString("LoginWorkerDialog.btnOk.text")); // NOI18N
        btnOk.setName("btnOk"); // NOI18N
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        btnCancel.setText(bundle.getString("LoginWorkerDialog.btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        infoLabel.setName("infoLabel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(passwordLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(loginLabel)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(loginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {loginTextField, passwordField});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {loginLabel, passwordLabel});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCancel, btnOk});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loginLabel))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        this.getParent().setVisible(true);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        LoginWorker loginWorker = new LoginWorker(this);
        loginWorker.execute();
    }//GEN-LAST:event_btnOkActionPerformed

    class LoginWorker extends SwingWorker<Integer,Integer>{

        private LoginWorkerDialog parentWindow;

        private LoginWorker(LoginWorkerDialog aThis) {
            this.parentWindow=aThis;
        }
        @Override
        protected Integer doInBackground() throws Exception {
            ResourceBundle translationResource = ResourceBundle.getBundle("Translation", Locale.getDefault());
            String login = loginTextField.getText();
            String password = String.valueOf(passwordField.getPassword());
            btnOk.setEnabled(false);
            btnCancel.setEnabled(false);
            infoLabel.setText(translationResource.getString("LoginWorkerDialog.logging"));
            if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, translationResource.getString("LoginWorkerDialog.notFilledFields"), translationResource.getString("LoginWorkerDialog.error"), JOptionPane.ERROR_MESSAGE);
            } else {
                Worker workerToLogin = WorkerManager.getInstance().findByLogin(login);
                if (workerToLogin == null) {
                    JOptionPane.showMessageDialog(null, "User doesn't exist", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (workerToLogin.authenticate(password)) {
                        this.parentWindow.setVisible(false);
                        new WorkerForm(workerToLogin).setVisible(true);
                        return 0;
                    } else {
                        JOptionPane.showMessageDialog(null, "Wrong password", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            return 1;
        }

        @Override
        protected void done(){
           btnOk.setEnabled(true);
            btnCancel.setEnabled(true);
            infoLabel.setText("");
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel loginLabel;
    private javax.swing.JTextField loginTextField;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    // End of variables declaration//GEN-END:variables

}
