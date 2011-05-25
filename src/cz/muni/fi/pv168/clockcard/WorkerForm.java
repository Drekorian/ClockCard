/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * WorkerForm.java
 *
 * Created on 3.5.2011, 12:30:22
 */

package cz.muni.fi.pv168.clockcard;

import cz.muni.fi.pv168.clockcard.ShiftsForm.ShiftTableModel;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingWorker;

/**
 *
 * @author Fires
 */
public class WorkerForm extends javax.swing.JFrame {

    private static Worker logedWorker;
    private boolean startedBreak=false;
    /** Creates new form WorkerForm */
    public WorkerForm(Worker worker) {
        logedWorker = worker;
        initComponents();
        //set gui to actual worker
        logedUserLabel.setText(worker.getName()+" "+worker.getSurname());
        if(logedWorker.getCurrentShift()==null){// nema smenu
            startShiftButton.setText("zacit novou smenu -l");
            startBreakButton.setEnabled(false);
            jMenuItem2.setEnabled(false);
            jMenuItem3.setEnabled(false);
            jMenuItem4.setEnabled(false);
        }else{
            startShiftButton.setText("Skoncit smenu -l");
            startBreakButton.setEnabled(false);
            jMenuItem2.setEnabled(true);
            jMenuItem3.setEnabled(true);
            jMenuItem4.setEnabled(false);
        }
    }

    public static Worker getLogedWorker() {
        return logedWorker;
    }


    class logoutAction extends AbstractAction{
        public void actionPerformed(ActionEvent e) {
           System.exit(0);
        }
    }

    class showShiftTableAction extends AbstractAction{
        public void actionPerformed(ActionEvent e) {
           new showShitTableWorker().execute();
        }
    }

    class newShiftAction extends AbstractAction{
        public void actionPerformed(ActionEvent e) {
            if(logedWorker.getCurrentShift()==null){//zacina smenu
                try {
                    logedWorker.startShift();
                } catch (WorkerException ex) {
                    Logger.getLogger(WorkerForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                    logedWorker.getCurrentShift().save();
                    logedWorker.save();
                    startShiftButton.setText("End shift -l");
                    startBreakButton.setEnabled(true);
                    jMenuItem1.setEnabled(false);
                    jMenuItem2.setEnabled(true);
                    jMenuItem3.setEnabled(true);
                    jMenuItem4.setEnabled(false);
            }else{
                try {
                    logedWorker.endShift();
                } catch (WorkerException ex) {
                    Logger.getLogger(WorkerForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                    logedWorker.save();
                    startBreakButton.setEnabled(false);
                    startShiftButton.setText("Start shift-l");
                    jMenuItem1.setEnabled(true);
                    jMenuItem2.setEnabled(false);
                    jMenuItem3.setEnabled(false);
                    jMenuItem4.setEnabled(false);
            }
        }
    }

    class startBreakAction extends AbstractAction{
        public void actionPerformed(ActionEvent e) {
            if(startedBreak){//ma zapocatou prestavku->konci break
                try {
                    logedWorker.endBreak();
                    logedWorker.getCurrentShift().save();
                } catch (WorkerException ex) {
                    Logger.getLogger(WorkerForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                    startedBreak=false;
                    startBreakButton.setText("start break -l");
                    jMenuItem2.setEnabled(true);
                    jMenuItem3.setEnabled(true);
                    jMenuItem4.setEnabled(false);
                    startShiftButton.setEnabled(true);

            }else{
                try {
                    logedWorker.startBreak();
                } catch (WorkerException ex) {
                    Logger.getLogger(WorkerForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                startBreakButton.setText("end break -l");
                jMenuItem1.setEnabled(false);
                jMenuItem2.setEnabled(false);
                jMenuItem3.setEnabled(false);
                jMenuItem4.setEnabled(true);
                startShiftButton.setEnabled(false);
                startedBreak=true;
            }

        }
    }

    class showShitTableWorker extends SwingWorker<Integer, Integer>{

        @Override
        protected Integer doInBackground() throws Exception {
            ShiftsForm frm = new ShiftsForm();
            frm.setVisible(true);
            JTable table = frm.getjTable1();
            ShiftTableModel model = (ShiftTableModel)table.getModel();
            model.addShifts((List<Shift>) ShiftManager.getInstance().findByWorkerID(WorkerForm.getLogedWorker().getID()));
            model.fireTableDataChanged();
            return 0;
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        info1Label = new javax.swing.JLabel();
        logedUserLabel = new javax.swing.JLabel();
        endShiftButton = new javax.swing.JButton();
        startShiftButton = new javax.swing.JButton();
        startBreakButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Worker");

        info1Label.setText("Currently loggged as:");

        logedUserLabel.setText("Steve Wozniak");

        endShiftButton.setText("Logout");

        startShiftButton.setAction(new newShiftAction());
        startShiftButton.setText("New Shift");

        startBreakButton.setAction(new startBreakAction());
        startBreakButton.setText("Break");
        startBreakButton.setEnabled(false);

        jMenu1.setMnemonic('S');
        jMenu1.setText("Shift");

        jMenuItem1.setAction(new newShiftAction());
        jMenuItem1.setMnemonic('N');
        jMenuItem1.setText("New Shift");
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAction(new newShiftAction());
        jMenuItem2.setMnemonic('S');
        jMenuItem2.setText("End Shift");
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAction(new startBreakAction());
        jMenuItem3.setMnemonic('B');
        jMenuItem3.setText("Start Break");
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAction(new startBreakAction());
        jMenuItem4.setMnemonic('E');
        jMenuItem4.setText("End Break");
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setMnemonic('V');
        jMenu2.setText("View");

        jMenuItem5.setAction(new showShiftTableAction());
        jMenuItem5.setMnemonic('A');
        jMenuItem5.setText("View All My Shifts");
        jMenuItem5.setActionCommand("All My Shifts");
        jMenu2.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setMnemonic('M');
        jMenuItem6.setText("My Shifts (Current Month)");
        jMenu2.add(jMenuItem6);

        jMenuItem7.setMnemonic('L');
        jMenuItem7.setText("My Shifts (Last Month)");
        jMenu2.add(jMenuItem7);

        jMenuBar1.add(jMenu2);

        jMenu3.setMnemonic('L');
        jMenu3.setText("Logout");

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem8.setMnemonic('L');
        jMenuItem8.setText("Logout");
        jMenu3.add(jMenuItem8);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(info1Label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(logedUserLabel))
                    .addComponent(startShiftButton, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startBreakButton, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(endShiftButton, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(info1Label)
                    .addComponent(logedUserLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(startShiftButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startBreakButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endShiftButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton endShiftButton;
    private javax.swing.JLabel info1Label;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JLabel logedUserLabel;
    private javax.swing.JButton startBreakButton;
    private javax.swing.JButton startShiftButton;
    // End of variables declaration//GEN-END:variables

}
