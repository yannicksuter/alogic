package ch.archilogic.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import ch.archilogic.solver.config.Config;
import ch.archilogic.solver.config.ConfigType;

import static javax.swing.GroupLayout.Alignment.*;

public class SelectConfigDlg extends JDialog implements ActionListener {
	private static final long serialVersionUID = -3887441711904582173L;
	
	private JComboBox box = null;
	private JFormattedTextField lenTextField = new JFormattedTextField(new Float(1.2));
	private JLabel lenLabel = new JLabel("Side len [0.4 - 4]:");
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

        lenLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        lenLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(LEADING)
                .addComponent(box)
                .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                		.addComponent(lenLabel)
                		.addComponent(lenTextField))
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
    				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    					    .addComponent(lenLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
    					    .addComponent(lenTextField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))          		
                    .addComponent(cancelButton))
            .addGroup(layout.createParallelGroup(LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(desc))))
        );
        
        this.box.setSelectedItem(ConfigType.MODEL_DACH_F3);        

        setTitle("Select a configuration..");
        setResizable(false);
        setPreferredSize(new Dimension(350, 165));
        
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
		desc.setText(String.format("Description:\n- object: %s\n- model: %s\n" , conf.getRefObjPathShort().replace("file:", ""), conf.getUseThinkModel().getDesc()));
    }
    
    public boolean isCanceled() {
		return canceled;
	}
	public Config getConfig() {
		Float value = (Float)lenTextField.getValue();
		if (value > 0 && value < 10) {
			config.setUseEdgeLen(Double.valueOf(value.toString()));
		}
		return config;
	}
	
	public static SelectConfigDlg create() {
		SelectConfigDlg dlg = new SelectConfigDlg();
		return dlg;
	}
}
