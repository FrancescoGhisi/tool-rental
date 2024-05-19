package tool.rental.domain.controllers;

import tool.rental.domain.use_cases.RegisterToolUseCase;
import tool.rental.utils.Controller;
import tool.rental.utils.PresentationFrame;
import tool.rental.utils.ToastError;

import javax.swing.*;

public class RegisterToolController extends Controller {

    private final RegisterToolUseCase registerToolUseCase = new RegisterToolUseCase();

    public RegisterToolController(PresentationFrame frame) {
        super(frame);
    }

    public void registerTool(String brand, double cost) throws ToastError {
        this.registerToolUseCase.execute(brand, cost);
        JOptionPane.showMessageDialog(null, "Ferramenta cadastrada com sucesso!");

        closeFrame();
    }

    public void closeFrame() {
        frame.setVisible(false);
    }
}
