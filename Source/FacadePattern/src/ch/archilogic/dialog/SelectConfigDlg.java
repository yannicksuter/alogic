package ch.archilogic.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import ch.archilogic.solver.config.Config;
import ch.archilogic.solver.config.ConfigType;

import static javax.swing.GroupLayout.Alignment.*;

public class SelectConfigDlg extends JDialog implements ActionListener {
	private static final long serialVersionUID = -3887441711904582173L;
	
	private JComboBox box = null;
	private JTextArea desc = new JTextArea("Config...");
	private JButton solveButton = new JButton("Solve");
	private JButton cancelButton = new JButton("Cancel");

	private boolean canceled = true;
	private Config config = null;
	
    public SelectConfigDlg() {
        this.box = new JComboBox(ConfigType.values());
        
        solveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        box.addActionListener(this);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(LEADING)
                .addComponent(box)
                .addComponent(desc))
            .addGroup(layout.createParallelGroup(LEADING)
                .addComponent(solveButton)
                .addComponent(cancelButton))
        );
       
        layout.linkSize(SwingConstants.HORIZONTAL, solveButton, cancelButton);

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(BASELINE)
                .addComponent(box)
                .addComponent(solveButton))
            .addGroup(layout.createParallelGroup(LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(desc)))
                .addComponent(cancelButton))
        );
        
        this.box.setSelectedItem(ConfigType.MODEL_DACH_F3);        

        setTitle("Select a configuration..");
        setResizable(false);
        setPreferredSize(new Dimension(350, 150));
        
        pack();

		setLocationRelativeTo(null);
		setModal(true);
		setVisible(true);
		
		solveButton.enableInputMethods(true);
    }

    @Override
	public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == solveButton) {
    		canceled = false;
    		setVisible(false);
    	} else
    	if (e.getSource() == cancelButton) {
    		setVisible(false);
    	} else 
    	if (e.getSource() == box) {
    		config = ConfigType.getConfig((ConfigType) box.getSelectedItem());
    		setDescText(config);
    	}
	}

    private void setDescText(Config conf) {
    	desc.setBackground(this.getBackground());
		desc.setText(String.format("Description:\n- object: %s\n"+
				"- model: %s\n" +
				"- edge len: %f"
				, conf.getRefObjPath().replace("file:", ""), conf.getUseThinkModel().getDesc(), conf.getUseEdgeLen()));
    }
    
    public boolean isCanceled() {
		return canceled;
	}
	public Config getConfig() {
		return config;
	}
	
	public static SelectConfigDlg create() {
		SelectConfigDlg dlg = new SelectConfigDlg();
		return dlg;
	}
}
