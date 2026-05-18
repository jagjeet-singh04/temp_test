package com.bnpp.assessment.controllers;

import java.awt.Window;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.bnpp.assessment.dto.LoginUserDto;
import com.bnpp.assessment.dto.RegisterUserDto;
import com.bnpp.assessment.models.User;
import com.bnpp.assessment.service.UserService;
import com.bnpp.assessment.service.UserServiceImpl;
import com.bnpp.assessment.ui.panels.LoginPanel;
import com.bnpp.assessment.ui.panels.MainAppPanel;
import com.bnpp.assessment.ui.windows.MainWindow;
import com.bnpp.assessment.util.ValidationUtil;

public class LoginController {

    private LoginPanel loginView;
    private MainController mainController;
    private UserService service = new UserServiceImpl();

    //========= LOGIN CONSTRUCTOR ============
    public LoginController(LoginPanel loginView, MainController mainController) {
        this.loginView = loginView;
        this.mainController = mainController;
        attachListeners();
    }

    private void attachListeners() {

        loginView.getLoginButton().addActionListener(e -> {
            LoginUserDto existingUser = new LoginUserDto();
            String phone = loginView.getPhoneNumberField().getText();
            String password = new String(loginView.getPasswordField().getPassword());
            existingUser.setPhone(phone);
            existingUser.setPassword(password);

            Boolean phoneValid = ValidationUtil.isPhoneValid(phone);

            if (!phoneValid) {
                JOptionPane.showMessageDialog(null, "Please Enter valid Phone number");
                return;
            }

            Boolean check = service.checkUserCred(Long.parseLong(existingUser.getPhone()), password);
            

            if (check) {
                JOptionPane.showMessageDialog(null, "Login Successful.");
                Long userId = service.findByPhone(Long.parseLong(phone)).getId();
                User getUser = service.findByPhone(Long.parseLong(phone)) ; 
                mainController.setCurrentUser(userId);

                // Close login window
                Window loginWindow = SwingUtilities.getWindowAncestor(loginView);
                if (loginWindow != null) {
                    loginWindow.dispose();
                }

                // Create and show main window
                SwingUtilities.invokeLater(() -> {
                	MainAppPanel mainAppPanel = new MainAppPanel(mainController , getUser );
                    MainWindow mainWindow = new MainWindow(mainAppPanel);
                    mainWindow.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Credentials.");
            }
        });

        loginView.getRegisterButton().addActionListener(e -> {
            
            RegisterUserDto registerUser = new RegisterUserDto();
            String username = loginView.getNewUsernameField().getText().trim().toLowerCase();
            String pass = new String(loginView.getNewPasswordField().getPassword()).trim().toLowerCase();
            String confirmPass = new String(loginView.getConfirmPasswordField().getPassword()).trim().toLowerCase();
            String email = loginView.getTemplatesEmailField().getText().trim().toLowerCase();
            String pan = loginView.getPanField().getText().trim();
            String aadhar = loginView.getAadharField().getText().trim();
            String phone = loginView.getPhoneField().getText().trim();

            if (username.isBlank() || pass.isBlank() || confirmPass.isBlank() || email.isBlank() ||
                pan.isBlank() || aadhar.isBlank() || phone.isBlank()) {
                JOptionPane.showMessageDialog(null, "Please Enter all the details.");
                return;
            }

            if (!ValidationUtil.isPanValid(pan)) {
                JOptionPane.showMessageDialog(null, "Invalid PAN number.");
                return;
            }

            if (!ValidationUtil.isAadharValid(aadhar)) {
                JOptionPane.showMessageDialog(null, "Invalid Aadhar number.");
                return;
            }

            if (!ValidationUtil.isPhoneValid(phone)) {
                JOptionPane.showMessageDialog(null, "Invalid phone number.");
                return;
            }

            if (!ValidationUtil.isValidEmail(email)) {
                JOptionPane.showMessageDialog(null, "Invalid Email.");
                return;
            }

            if (!ValidationUtil.isUsernameValid(username)) {
                JOptionPane.showMessageDialog(null, "Invalid Username" + "\n" + "Username should contain atleast 2 character in the begining");
                return;
            }

            if (!pass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(null, "Password does not match with confirm password.");
                return;
            }

            registerUser.setUsername(username);
            registerUser.setPassword(pass);
            registerUser.setEmail(email);
            registerUser.setPan(pan);
            registerUser.setAadhar(aadhar);
            registerUser.setPhone(phone);

            String check = checkUserExist(registerUser);
            if (check != null) {
                JOptionPane.showMessageDialog(null, check + " is already registered.");
                return;
            }

            service.register(registerUser);
            JOptionPane.showMessageDialog(null, "User Registered Successfully");
            return;
        });
    }

    private String checkUserExist(RegisterUserDto user) {
        String field = null;
        
        if (service.findByUsername(user.getUsername()) != null) {
            field = "Username";
            return field;
        }
        if (service.findByEmail(user.getEmail()) != null) {
            field = "Email";
            return field;
        }
        if (service.findByPan(user.getPan()) != null) {
            field = "Pan";
            return field;
        }
        if (service.findByPhone(Long.parseLong(user.getPhone())) != null) {
            field = "Phone";
            return field;
        }
        if (service.findByAadhar(Long.parseLong(user.getAadhar())) != null) {
            field = "Aadhar";
            return field;
        }
        return field;
    }
}