package tool.rental.presentation;

import tool.rental.domain.controllers.LoginController;
import tool.rental.utils.PresentationFrame;
import tool.rental.utils.ToastError;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends PresentationFrame {
    private final LoginController loginController = new LoginController(this);
    private JPanel MainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JCheckBox rememberMeCheckBox;
    private JButton registerButton;


    public LoginFrame() {
        this.setMainPanel();
        this.setupPageLayout();
        this.setUpListeners();
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginController.openRegisterModal();
            }
        });
    }

    protected void setUpListeners() {
        this.loginButtonActionListeners();
    }

    private void loginButtonActionListeners() {
        this.loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    loginController.login(
                            usernameField.getText(),
                            new String(passwordField.getPassword()),
                            rememberMeCheckBox.isSelected()
                    );
                } catch (ToastError ex) {
                    ex.display();

                }
            }
        });
    }

    private void setupPageLayout() {
        this.setTitle("Faça seu login");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(this.userScreen.widthFraction(30), this.userScreen.heightFraction(40));
        this.setLocationRelativeTo(null);
    }

    private void setMainPanel() {
        this.setContentPane(this.MainPanel);
    }
}
