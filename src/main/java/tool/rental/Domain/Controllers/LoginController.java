package tool.rental.Domain.Controllers;

import tool.rental.App.Settings;
import tool.rental.Domain.Entities.User;
import tool.rental.Domain.UseCases.LoginUseCase;
import tool.rental.Presentation.AppMainFrame;
import tool.rental.Presentation.RegisterUserFrame;
import tool.rental.Utils.Controller;
import tool.rental.Utils.PresentationFrame;
import tool.rental.Utils.ToastError;

public class LoginController extends Controller {
    private final LoginUseCase loginUseCase = new LoginUseCase();

    public LoginController(PresentationFrame frame) {
        super(frame);
    }

    public void login(String username, String password, boolean rememberMe) throws ToastError {
        User user = this.loginUseCase.execute(username, password, rememberMe);
        Settings.setUser(user);

        this.frame.swapFrame(new AppMainFrame());
    }

    public void openRegisterModal() {
        this.frame.swapFrame(new RegisterUserFrame(), true);
    }
}
