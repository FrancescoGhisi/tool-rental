package tool.rental.presentation;

import tool.rental.domain.controllers.RegisterToolController;
import tool.rental.utils.PresentationFrame;
import tool.rental.utils.ToastError;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterToolFrame extends PresentationFrame {

    private final RegisterToolController registerToolController = new RegisterToolController(this);

    private JTextField toolNameField;
    private JTextField costField;
    private JTextField brandField;
    private JButton confirmButton;
    private JButton cancelButton;
    private JPanel MainPanel;


    public RegisterToolFrame() {
        this.setMainPanel();
        this.setupPageLayout();
        this.setUpListeners();
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    registerToolController.registerTool(
                            toolNameField.getText(),
                            brandField.getText(),
                            Double.parseDouble(costField.getText())
                    );
                } catch (ToastError ex) {
                    ex.display();
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerToolController.closeFrame();
            }
        });
    }

    private void setUpListeners() {
    }

    private void setupPageLayout() {
        this.setTitle("Registrar ferramenta");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(this.toolScreen.widthFraction(15), this.toolScreen.heightFraction(30));
        this.setLocationRelativeTo(null);
    }

    private void setMainPanel() {
        this.setContentPane(this.MainPanel);
    }
}

