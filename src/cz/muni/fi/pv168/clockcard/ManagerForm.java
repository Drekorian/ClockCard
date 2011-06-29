package cz.muni.fi.pv168.clockcard;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

/**
 * Manager login form
 *
 * @author Marek Osvald
 * @version 2011.0629
 */
public class ManagerForm extends javax.swing.JFrame {

    private static ManagerForm thisWindows;

    /** Creates new form ManagerForm */
    public ManagerForm() {
        initComponents();
        setThisWindows();
    }

    public static ManagerForm getThisWindows() {
        return thisWindows;
    }

    private void setThisWindows() {
        thisWindows = this;
    }

    class LogoutAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            ManagerForm.getThisWindows().dispose();
            new LoginForm().setVisible(true);
        }
    }

    class ShowWorkerAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            new ShowWorkerManager().execute();
        }
    }


    class ShowAllShiftsAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            new ShowShiftManager("all").execute();
        }
    }

    class ShowLastShiftsAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            new ShowShiftManager("last").execute();
        }
    }

    class ShowCurrentShiftsAction extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            new ShowShiftManager("current").execute();
        }
    }

    class ShowShiftManager extends SwingWorker<Integer, Integer> {

        private String iter = "all";

        public ShowShiftManager(String iter) {
            this.iter = iter;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            List<Shift> data = null;
            if (iter.equals("all")) {
                data = Supervisor.getInstance().getAllShifts();
            } else if (iter.equals("current")) {
                data = Supervisor.getInstance().getCurrentMonthShifts();
            } else if (iter.equals("last")) {
                data = Supervisor.getInstance().getLastMonthShifts();
            }
            if (data.isEmpty()) {
                //this.cancel(true);
                data = new ArrayList<Shift>();
                data.add(null);
            }
                ShiftsForm form = new ShiftsForm();
                ShiftTableModel mod = new ShiftTableModel();
                mod.addShifts(data);
                mod.fireTableStructureChanged();
                mod.fireTableDataChanged();
                form.getjTable1().setModel(mod);
                form.setVisible(true);
            return 0;
        }
    }

    class ShowWorkerManager extends SwingWorker<Integer, Integer> {

        @Override
        protected Integer doInBackground() throws Exception {
            ShiftsForm form = new ShiftsForm();
            WorkerTableModel mod = new WorkerTableModel();
            mod.addShifts(Supervisor.getInstance().getAllWorkers());
            mod.fireTableStructureChanged();
            mod.fireTableDataChanged();
            form.getjTable1().setModel(mod);
            form.setVisible(true);
            return 0;
        }
    }

    class WorkerTableModel extends AbstractTableModel {

        List<Worker> workers = new ArrayList<Worker>();

        public WorkerTableModel() {
            super();
        }

        public void addShifts(List<Worker> Workers) {
            for (Worker sh : Workers) {
                workers.add(sh);
            }
            workers.remove(0);
    }

    public int getRowCount() {
        return workers.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        ResourceBundle translationResource = ResourceBundle.getBundle("Translation", Locale.getDefault());
        switch (columnIndex) {
            case 0:
                return "Id";
            case 1:
                return translationResource.getString("WorkerTable.name");
            case 2:
                return translationResource.getString("WorkerTable.surname");
            case 3:
                return translationResource.getString("WorkerTable.login");
            case 4:
                return translationResource.getString("WorkerTable.password");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            case 4:
                return String.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public int getColumnCount() {
        return 5;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Worker worker = workers.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return worker.getID();
            case 1:
                return worker.getName();
            case 2:
                return worker.getSurname();
            case 3:
                return worker.getLogin();
            case 4:
                return worker.getPassword();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
}

class ShiftTableModel extends AbstractTableModel {

    List<Shift> shifts = new ArrayList<Shift>();

    public ShiftTableModel() {
        super();
    }

    public void addShifts(List<Shift> shift) {
        for (Shift sh : shift) {
            shifts.add(sh);
        }
        shifts.remove(0);
    }

    public int getRowCount() {
        return shifts.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        ResourceBundle translationResource = ResourceBundle.getBundle("Translation", Locale.getDefault());
        switch (columnIndex) {
            case 0:
                return translationResource.getString("ShiftFormTable.worker");
            case 1:
                return translationResource.getString("ShiftFormTable.start");
            case 2:
                return translationResource.getString("ShiftFormTable.end");
            case 3:
                return translationResource.getString("ShiftFormTable.nettoTime");
            case 4:
                return translationResource.getString("ShiftFormTable.totalBreakTime");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            case 4:
                return String.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public int getColumnCount() {
        return 5;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Shift shift = shifts.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return WorkerManager.getInstance().find(shift.getWorkerID()).getName();
            case 1:
                return new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(shift.getStart().getTime());
            case 2:
                return new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(shift.getEnd().getTime());
            case 3:
                return String.valueOf((shift.getEnd().getTimeInMillis() - shift.getStart().getTimeInMillis() - shift.getTotalBreakTime()) / 1000 / 60) + "min";
            case 4:
                return String.valueOf(shift.getTotalBreakTime() / 1000 / 60) + "min";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
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

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Translation"); // NOI18N
        setTitle(bundle.getString("ManagerForm.title")); // NOI18N

        jButton1.setAction(new ShowWorkerAction());
        jButton1.setText(bundle.getString("ManagerForm.jButton1.text_1")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jButton2.setAction(new ShowAllShiftsAction());
        jButton2.setMnemonic('W');
        jButton2.setText(bundle.getString("ManagerForm.jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N

        jButton3.setAction(new LogoutAction());
        jButton3.setMnemonic('W');
        jButton3.setText(bundle.getString("ManagerForm.jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMenu1.setMnemonic('S');
        jMenu1.setText(bundle.getString("ManagerForm.jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem2.setAction(new ShowAllShiftsAction());
        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setMnemonic('A');
        jMenuItem2.setText(bundle.getString("ManagerForm.jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAction(new ShowCurrentShiftsAction());
        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setMnemonic('M');
        jMenuItem3.setText(bundle.getString("ManagerForm.jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAction(new ShowLastShiftsAction());
        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setMnemonic('N');
        jMenuItem4.setText(bundle.getString("ManagerForm.jMenuItem4.text")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu3.setMnemonic('W');
        jMenu3.setText(bundle.getString("ManagerForm.jMenu3.text")); // NOI18N
        jMenu3.setName("jMenu3"); // NOI18N

        jMenuItem5.setAction(new ShowWorkerAction());
        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setMnemonic('A');
        jMenuItem5.setText(bundle.getString("ManagerForm.jMenuItem5.text")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        jMenu3.add(jMenuItem5);

        jMenuBar1.add(jMenu3);

        jMenu2.setMnemonic('L');
        jMenu2.setText(bundle.getString("ManagerForm.jMenu2.text")); // NOI18N
        jMenu2.setName("jMenu2"); // NOI18N

        jMenuItem1.setAction(new LogoutAction());
        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setMnemonic('L');
        jMenuItem1.setText(bundle.getString("ManagerForm.jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(185, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(159, 159, 159))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    // End of variables declaration//GEN-END:variables


}
