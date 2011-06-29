package cz.muni.fi.pv168.clockcard;

import cz.muni.fi.pv168.clockcard.ShiftsForm.ShiftTableModel;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.SwingWorker;

/**
 * Worker's control form.
 *
 * @author David Stein
 * @version 2011.0629
 */
public class WorkerForm extends javax.swing.JFrame {

    private static Worker logedWorker;
    private boolean startedBreak=false;
    /** Creates new form WorkerForm */
    public WorkerForm(Worker worker) {
        WorkerForm.logedWorker=worker;
        ResourceBundle.clearCache();
        ResourceBundle translationResource = ResourceBundle.getBundle("Translation", Locale.getDefault());
        initComponents();
        this.setVisible(true);
            
        logedUserLabel.setText(worker.getName()+" "+worker.getSurname());
        if(logedWorker.getCurrentShift()==null){// nema smenu
            startShiftButton.setText(translationResource.getString("WorkerForm.jMenuItem1.text"));
            startBreakButton.setEnabled(false);
            jMenuItem2.setEnabled(false);
            jMenuItem3.setEnabled(false);
            jMenuItem4.setEnabled(false);
        }else{
            startShiftButton.setText(translationResource.getString("WorkerForm.jMenuItem2.text"));
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
           new showShiftTableWorker().execute();
        }
    }

    class showShiftTableCurrentMonthAction extends AbstractAction{
        public void actionPerformed(ActionEvent e) {
           new showShiftTableWorkerCurrentMonth().execute();
        }
    }

    class showShiftTableLastMonthAction extends AbstractAction{
        public void actionPerformed(ActionEvent e) {
           new showShiftTableWorkerLastMonth().execute();
        }
    }

    class newShiftAction extends AbstractAction{
        public void actionPerformed(ActionEvent e) {
            ResourceBundle translationResource = ResourceBundle.getBundle("Translation", Locale.getDefault());
            if(logedWorker.getCurrentShift()==null){//zacina smenu
                try {
                    logedWorker.startShift();
                } catch (WorkerException ex) {
                    Logger.getLogger(WorkerForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                    logedWorker.getCurrentShift().save();
                    logedWorker.save();
                    startShiftButton.setText(translationResource.getString("WorkerForm.jMenuItem2.text"));
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
                    startShiftButton.setText(translationResource.getString("WorkerForm.jMenuItem1.text"));
                    jMenuItem1.setEnabled(true);
                    jMenuItem2.setEnabled(false);
                    jMenuItem3.setEnabled(false);
                    jMenuItem4.setEnabled(false);
            }
        }
    }

    class startBreakAction extends AbstractAction{
        public void actionPerformed(ActionEvent e) {
            ResourceBundle translationResource = ResourceBundle.getBundle("Translation", Locale.getDefault());
            if(startedBreak){//ma zapocatou prestavku->konci break
                try {
                    logedWorker.endBreak();
                    logedWorker.getCurrentShift().save();
                } catch (WorkerException ex) {
                    Logger.getLogger(WorkerForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                    startedBreak=false;
                    startBreakButton.setText(translationResource.getString("WorkerForm.jMenuItem1.text"));
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
                startBreakButton.setText(translationResource.getString("WorkerForm.jMenuItem4.text"));
                jMenuItem1.setEnabled(false);
                jMenuItem2.setEnabled(false);
                jMenuItem3.setEnabled(false);
                jMenuItem4.setEnabled(true);
                startShiftButton.setEnabled(false);
                startedBreak=true;
            }

        }
    }

    class showShiftTableWorker extends SwingWorker<Integer, Integer>{
        @Override
        protected Integer doInBackground() throws Exception {
            ShiftsForm frm = new ShiftsForm();
            frm.setVisible(true);
            JTable table = frm.getjTable1();
            ShiftTableModel model = (ShiftTableModel)table.getModel();
            List<Shift> shifts = (List<Shift>) ShiftManager.getInstance().findByWorkerID(WorkerForm.getLogedWorker().getID());
            model.addShifts(shifts);
            model.fireTableDataChanged();
            return 0;
        }
    }

    class showShiftTableWorkerCurrentMonth extends SwingWorker<Integer, Integer>{
        @Override
        protected Integer doInBackground() throws Exception {
            ShiftsForm frm = new ShiftsForm();
            frm.setVisible(true);
            JTable table = frm.getjTable1();
            ShiftTableModel model = (ShiftTableModel)table.getModel();
            GregorianCalendar now = new GregorianCalendar();
            int lastDay = now.getActualMaximum(GregorianCalendar.DATE);
            int firstDay = now.getActualMinimum(GregorianCalendar.DATE);
            int month = now.get(GregorianCalendar.MONTH);
            int year = now.get(GregorianCalendar.YEAR);
            Timestamp startTime = new Timestamp(new GregorianCalendar(year, month, firstDay).getTimeInMillis());
            Timestamp endTime = new Timestamp(new GregorianCalendar(year, month, lastDay).getTimeInMillis());
            List<Shift> shifts = (List<Shift>) ShiftManager.getInstance().findStartBetween(startTime,endTime,WorkerForm.getLogedWorker().getID());
            model.addShifts(shifts);
            model.fireTableDataChanged();
            return 0;
        }
    }
    class showShiftTableWorkerLastMonth extends SwingWorker<Integer, Integer>{
        @Override
        protected Integer doInBackground() throws Exception {
            ShiftsForm frm = new ShiftsForm();
            frm.setVisible(true);
            JTable table = frm.getjTable1();
            ShiftTableModel model = (ShiftTableModel)table.getModel();
            GregorianCalendar now = new GregorianCalendar();
            GregorianCalendar lastMonth = new GregorianCalendar(now.get(GregorianCalendar.YEAR), now.get(GregorianCalendar.MONTH)-1, now.get(GregorianCalendar.DAY_OF_MONTH));
            int lastDay = lastMonth.getActualMaximum(GregorianCalendar.DATE);
            int firstDay = lastMonth.getActualMinimum(GregorianCalendar.DATE);
            int month = lastMonth.get(GregorianCalendar.MONTH);
            int year = lastMonth.get(GregorianCalendar.YEAR);
            Timestamp startTime = new Timestamp(new GregorianCalendar(year, month, firstDay).getTimeInMillis());
            Timestamp endTime = new Timestamp(new GregorianCalendar(year, month, lastDay).getTimeInMillis());
            List<Shift> shifts = (List<Shift>) ShiftManager.getInstance().findStartBetween(startTime,endTime,WorkerForm.getLogedWorker().getID());
            model.addShifts(shifts);
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
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Translation"); // NOI18N
        setTitle(bundle.getString("WorkerForm.title")); // NOI18N

        info1Label.setText(bundle.getString("WorkerForm.info1Label.text")); // NOI18N

        logedUserLabel.setText(bundle.getString("WorkerForm.logedUserLabel.text")); // NOI18N

        endShiftButton.setAction(new logoutAction());
        endShiftButton.setText(bundle.getString("WorkerForm.endShiftButton.text")); // NOI18N

        startShiftButton.setAction(new newShiftAction());
        startShiftButton.setText(bundle.getString("WorkerForm.startShiftButton.text")); // NOI18N

        startBreakButton.setAction(new startBreakAction());
        startBreakButton.setText(bundle.getString("WorkerForm.startBreakButton.text")); // NOI18N
        startBreakButton.setEnabled(false);

        jMenu1.setMnemonic('S');
        jMenu1.setText(bundle.getString("WorkerForm.jMenu1.text")); // NOI18N

        jMenuItem1.setAction(new newShiftAction());
        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setMnemonic('N');
        jMenuItem1.setText(bundle.getString("WorkerForm.jMenuItem1.text")); // NOI18N
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAction(new newShiftAction());
        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setMnemonic('S');
        jMenuItem2.setText(bundle.getString("WorkerForm.jMenuItem2.text")); // NOI18N
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAction(new startBreakAction());
        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setMnemonic('B');
        jMenuItem3.setText(bundle.getString("WorkerForm.jMenuItem3.text")); // NOI18N
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAction(new startBreakAction());
        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setMnemonic('E');
        jMenuItem4.setText(bundle.getString("WorkerForm.jMenuItem4.text")); // NOI18N
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setMnemonic('V');
        jMenu2.setText(bundle.getString("WorkerForm.jMenu2.text")); // NOI18N

        jMenuItem5.setAction(new showShiftTableAction());
        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem5.setMnemonic('A');
        jMenuItem5.setText(bundle.getString("WorkerForm.jMenuItem5.text")); // NOI18N
        jMenuItem5.setActionCommand(bundle.getString("WorkerForm.jMenuItem5.actionCommand")); // NOI18N
        jMenu2.add(jMenuItem5);

        jMenuItem6.setAction(new showShiftTableCurrentMonthAction());
        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem6.setMnemonic('M');
        jMenuItem6.setText(bundle.getString("WorkerForm.jMenuItem6.text")); // NOI18N
        jMenu2.add(jMenuItem6);

        jMenuItem7.setAction(new showShiftTableLastMonthAction());
        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem7.setMnemonic('L');
        jMenuItem7.setText(bundle.getString("WorkerForm.jMenuItem7.text")); // NOI18N
        jMenu2.add(jMenuItem7);

        jMenuBar1.add(jMenu2);

        jMenu3.setMnemonic('L');

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem8.setMnemonic('L');
        jMenuItem8.setText(bundle.getString("WorkerForm.jMenuItem8.text")); // NOI18N
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
