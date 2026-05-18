package com.bnpp.assessment.ui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class LoginPanel extends JPanel {
    /* ------------ UI components ------------ */
    private JTextField phoneNumberField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel showPasswordLabel;
    private JLabel signUpLink;

    private JTextField newUsernameField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField panField;
    private JTextField aadharField;
    private JTextField phoneField;
    private JButton registerButton;
    private JLabel loginLink;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    /* ------------ Password visibility helpers ------------ */
    private boolean passwordVisible = false;
    private ImageIcon showIcon;
    private ImageIcon hideIcon;

    private boolean newPasswordVisible = false;      // registration field
    private boolean confirmPasswordVisible = false;  // registration field
    private JLabel newPasswordEyeLabel;              // <-- new field
    private JLabel confirmPasswordEyeLabel;

    /*----------------------------------------------------------------------*/
    /* * Constructor */
    /*----------------------------------------------------------------------*/
    
    public LoginPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    /*----------------------------------------------------------------------*/
    /* * Component creation */
    /*----------------------------------------------------------------------*/
    
    private void initializeComponents() {
        // ----- login fields -----
        phoneNumberField = new JTextField();
        limitEntry(phoneNumberField, 10);
        configureFormattedField(phoneNumberField, 200, 40);

        passwordField = createPasswordField(200, 40);
        loginButton = createButton("Login", new Color(0x2E8B57), Color.WHITE, 200, 40);
        showPasswordLabel = createEyeIconLabel();
        signUpLink = createHyperlink("Sign Up");

        // ----- registration fields -----
        newUsernameField = createTextField(200, 30);
        newPasswordField = createPasswordField(200, 30);
        confirmPasswordField = createPasswordField(200, 30);
        newPasswordEyeLabel = createEyeIconLabel();              // <-- added
        confirmPasswordEyeLabel = createEyeIconLabel();
        emailField = createTextField(200, 30);
        panField = createTextField(200, 30);
        limitEntry(panField, 10);

        // formatted fields for Aadhar & Phone
        aadharField = new JTextField();
        limitEntry(aadharField, 12);
        configureFormattedField(aadharField, 200, 30);

        phoneField = new JTextField();
        configureFormattedField(phoneField, 200, 30);
        limitEntry(phoneField, 10);

        registerButton = createButton("Register", new Color(0x2E8B57), Color.WHITE, 200, 40);
        loginLink = createHyperlink("Login");

        // load eye icons
        loadPasswordIcons();
    }

    private void limitEntry(JTextField field, int digits) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (field.getText().length() >= digits) // limit textfield to 10 characters
                    e.consume();
            }
        });
    }

    /** Simple text field without any placeholder text. */
    private JTextField createTextField(int width, int height) {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(width, height));
        field.setBackground(Color.WHITE);
        field.setBorder(createGreenBorder());
        return field;
    }

    /** Password field - start with masked characters. */
    private JPasswordField createPasswordField(int width, int height) {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(width, height));
        field.setBackground(Color.WHITE);
        field.setBorder(createGreenBorder());
        field.setEchoChar('•');
        return field;
    }

    private void configureFormattedField(JTextField field, int width, int height) {
        field.setPreferredSize(new Dimension(width, height));
        field.setBackground(Color.WHITE);
        field.setBorder(createGreenBorder());
    }

    private JButton createButton(String text, Color bg, Color fg, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(w, h));
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        return btn;
    }

    private JLabel createHyperlink(String text) {
        JLabel link = new JLabel(text);
        link.setForeground(new Color(0x2E8B57));
        link.setFont(new Font("Arial", Font.PLAIN, 14));
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return link;
    }

    private JLabel createEyeIconLabel() {
        JLabel label = new JLabel();
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setOpaque(true);               // <-- make background visible
        label.setBackground(Color.WHITE);    // <-- white background
        return label;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF5F5F5));
        // setMinimumSize(new Dimension(1000, 750));

        add(createHeaderPanel(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(createLoginPanel(), "login");
        cardPanel.add(createRegistrationPanel(), "register");
        add(cardPanel, BorderLayout.CENTER);

        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x2E8B57));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel logo = new JLabel("VIRTUAL Options BROKER", SwingConstants.CENTER);
        logo.setFont(new Font("Arial", Font.BOLD, 28));
        logo.setForeground(Color.WHITE);
        header.add(logo, BorderLayout.CENTER);

        // market summary (static example)
        JPanel market = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        market.setBackground(new Color(0x2E8B57));
        market.add(createMarketLabel("NIFTY: 15,342.50 ▲ 0.23%"));
        market.add(Box.createHorizontalStrut(20));
        market.add(createMarketLabel("BANKNIFTY: 38,450.20 ▼ 0.12%"));
        header.add(market, BorderLayout.SOUTH);
        return header;
    }

    private JLabel createMarketLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.PLAIN, 14));
        l.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return l;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        GridBagConstraints gbc = createDefaultGBC();

        // ---- title ----
        JLabel title = new JLabel("Trader Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0x2E8B57));
        gbc.gridy = 0;
        panel.add(title, gbc);

        // ---- Username ----
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 10, 0);
        panel.add(createFieldPanel(phoneNumberField, "Phone Number:", 0), gbc);

        // ---- Password ----
        gbc.gridy = 2;
        panel.add(createPasswordFieldPanel(passwordField, showPasswordLabel, "Password:", 26), gbc);

        // ---- Login button ----
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 10, 0);
        panel.add(createButtonPanel(loginButton), gbc);

        // ---- SignUp link ----
        gbc.gridy = 4;
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setBackground(Color.WHITE);
        linkPanel.add(new JLabel("New to trading? "));
        linkPanel.add(signUpLink);
        panel.add(linkPanel, gbc);

        return panel;
    }

    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        GridBagConstraints gbc = createDefaultGBC();

        // ---- title ----
        JLabel title = new JLabel("New Trader Registration", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0x2E8B57));
        gbc.gridy = 0;
        panel.add(title, gbc);

        // ---- fields ----
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridy = 1; panel.add(createFieldPanel(newUsernameField, "Username: *", 50), gbc);
        gbc.gridy = 2; panel.add(createFieldPanel(emailField, "Email: *", 78), gbc);
        gbc.gridy = 3; panel.add(createFieldPanel(panField, "PAN Number: *", 38), gbc);
        gbc.gridy = 4; panel.add(createFieldPanel(aadharField, "AADHAR Number: *", 15), gbc);
        gbc.gridy = 5; panel.add(createFieldPanel(phoneField, "Phone Number: *", 26), gbc);
        gbc.gridy = 6;
        panel.add(createPasswordFieldPanel(newPasswordField, newPasswordEyeLabel, "Password: *", 52), gbc);
        
        gbc.gridy = 7;
        panel.add(createPasswordFieldPanel(confirmPasswordField, confirmPasswordEyeLabel, "Confirm Password: *", 4), gbc);

        // ---- Register button ----
        gbc.gridy = 8;
        gbc.insets = new Insets(20, 0, 10, 0);
        panel.add(createButtonPanel(registerButton), gbc);

        // ---- Login link ----
        gbc.gridy = 9;
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setBackground(Color.WHITE);
        linkPanel.add(new JLabel("Already registered? "));
        linkPanel.add(loginLink);
        panel.add(linkPanel, gbc);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(0xF0F0F0));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel copy = new JLabel("© 2026 Virtual Options Broker. All rights reserved.", SwingConstants.CENTER);
        copy.setFont(new Font("Arial", Font.PLAIN, 12));
        copy.setForeground(new Color(0x666666));

        JLabel disclaimer = new JLabel("Risk Warning: Trading in futures and options involves substantial risk of loss.", SwingConstants.CENTER);
        disclaimer.setFont(new Font("Arial", Font.ITALIC, 10));
        disclaimer.setForeground(new Color(0x990000));

        JPanel text = new JPanel(new GridBagLayout()); // Using GridBagLayout based on grid initialization in image
        text.setLayout(new java.awt.GridLayout(2, 1, 0, 5));
        text.setBackground(new Color(0xF0F0F0));
        text.add(copy);
        text.add(disclaimer);
        footer.add(text, BorderLayout.CENTER);
        return footer;
    }

    /*----------------------------------------------------------------------*/
    /* * Helper factories */
    /*----------------------------------------------------------------------*/

    private JPanel createFieldPanel(JComponent field, String labelText, int leftMargin) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(labelText);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, leftMargin));
        panel.add(label);

        panel.add(field);
        return panel;
    }

    /** * **NEW** - uses FlowLayout so the password field keeps its preferred width.
     */
    private JPanel createPasswordFieldPanel(JPasswordField pwd, JLabel eye, String labelText, int leftMargin) {
        // ---- 1 Row label (e.g. "Password:") -------------------------------
        JLabel rowLabel = new JLabel(labelText);
        rowLabel.setBorder(BorderFactory.createEmptyBorder(0, 22, 0, leftMargin));
        rowLabel.setForeground(Color.BLACK); // optional styling

        // ---- 2 Wrapper that holds the password field + eye ---------------
        JPanel pwdWrapper = new JPanel(new GridBagLayout());
        pwdWrapper.setBackground(Color.WHITE);
        GridBagConstraints wc = new GridBagConstraints();

        // password field - takes all horizontal space that the wrapper offers
        wc.gridx = 0;
        wc.weightx = 1.0;
        wc.fill = GridBagConstraints.HORIZONTAL;
        wc.insets = new Insets(0, 0, 0, 0);
        pwdWrapper.add(pwd, wc);

        // eye icon - occupies only its own preferred size
        wc.gridx = 1;
        wc.weightx = 0;
        wc.fill = GridBagConstraints.NONE;
        wc.insets = new Insets(0, 5, 0, 0); // a small gap between field & icon
        pwdWrapper.add(eye, wc);

        // Force the wrapper to be exactly the size of the password field + icon
        int wrapperWidth = pwd.getPreferredSize().width + eye.getPreferredSize().width + 5;
        int wrapperHeight = Math.max(pwd.getPreferredSize().height, eye.getPreferredSize().height);
        eye.getPreferredSize(); // eye.getPreferredSize().height;
        pwdWrapper.setPreferredSize(new Dimension(wrapperWidth, wrapperHeight));

        // ---- 3 Assemble the whole row (label + wrapper) ------------------
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        row.setBackground(Color.WHITE);
        row.add(rowLabel);
        row.add(pwdWrapper); // <-- this wrapper already contains the eye
        return row;
    }

    private JPanel createButtonPanel(JButton btn) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        panel.setBackground(Color.WHITE);
        panel.add(btn);
        return panel;
    }

    private Border createGreenBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x2E8B57), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
    }

    private GridBagConstraints createDefaultGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        return gbc;
    }

    /*----------------------------------------------------------------------*/
    /* * Icons & password visibility handling */
    /*----------------------------------------------------------------------*/

    private void loadPasswordIcons() {
        try {
            URL showUrl = getClass().getResource("/com/bnpp/assessment/resources/password_show_icon.png");
            URL hideUrl = getClass().getResource("/com/bnpp/assessment/resources/password_hide_icon.png");

            if (showUrl != null && hideUrl != null) {
                showIcon = scaleIcon(new ImageIcon(showUrl), 20);
                hideIcon = scaleIcon(new ImageIcon(hideUrl), 20);
                showPasswordLabel.setIcon(hideIcon); // default = hidden
                newPasswordEyeLabel.setIcon(hideIcon); // <-- added
                confirmPasswordEyeLabel.setIcon(hideIcon);
            } else {
                showPasswordLabel.setText("👁");
                newPasswordEyeLabel.setText("👁");
                confirmPasswordEyeLabel.setText("👁");
            }
        } catch (Exception e) {
            showPasswordLabel.setText("👁");
            newPasswordEyeLabel.setText("👁");
            confirmPasswordEyeLabel.setText("👁");
        }
    }

    private ImageIcon scaleIcon(ImageIcon src, int targetHeight) {
        int w = src.getIconWidth();
        int h = src.getIconHeight();
        int targetWidth = (int) ((double) w / h * targetHeight);
        ImageIcon scaled = new ImageIcon(src.getImage().getScaledInstance(targetWidth, targetHeight, java.awt.Image.SCALE_SMOOTH));
        return scaled;
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordField.setEchoChar((char) 0);
            showPasswordLabel.setIcon(showIcon);
        } else {
            passwordField.setEchoChar('•');
            showPasswordLabel.setIcon(hideIcon);
        }
    }

    private void toggleNewPasswordVisibility() {
        newPasswordVisible = !newPasswordVisible;
        if (newPasswordVisible) {
            newPasswordField.setEchoChar((char) 0);
            newPasswordEyeLabel.setIcon(showIcon);
        } else {
            newPasswordField.setEchoChar('•');
            newPasswordEyeLabel.setIcon(hideIcon);
        }
    }

    // ------ registration password #2 (confirm password) ------------------
    private void toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible;
        if (confirmPasswordVisible) {
            confirmPasswordField.setEchoChar((char) 0);
            confirmPasswordEyeLabel.setIcon(showIcon);
        } else {
            confirmPasswordField.setEchoChar('•');
            confirmPasswordEyeLabel.setIcon(hideIcon);
        }
    }

    /*----------------------------------------------------------------------*/
    /* * Event handling */
    /*----------------------------------------------------------------------*/

    private void setupEventHandlers() {
        // password visibility toggle (login screen)
        showPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                togglePasswordVisibility();
            }
        });

        // registration screen eyes - **use their own togglers**
        newPasswordEyeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleNewPasswordVisibility();
            }
        });

        confirmPasswordEyeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleConfirmPasswordVisibility();
            }
        });

        // switch to registration view
        signUpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showRegistrationCard();
                resetLoginFields();
            }
        });

        // switch back to login view
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showLoginCard();
                resetSignUpFields();
            }
        });

        // (actual login / registration actions are expected to be wired by a controller)
    }

    private void resetLoginFields() {
        getPhoneNumberField().setText("");
        getPasswordField().setText("");
    }

    public void resetSignUpFields() {
        getNewUsernameField().setText("");
        getNewPasswordField().setText("");
        getConfirmPasswordField().setText("");
        getTemplatesEmailField().setText("");
        getPanField().setText("");
        getAadharField().setText("");
        getPhoneField().setText("");
    }

    public JTextField getPhoneNumberField() { return phoneNumberField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JTextField getNewUsernameField() { return newUsernameField; }
    public JPasswordField getNewPasswordField() { return newPasswordField; }
    public JPasswordField getConfirmPasswordField() { return confirmPasswordField; }
    public JTextField getTemplatesEmailField() { return emailField; }
    public JTextField getPanField() { return panField; }
    public JTextField getAadharField() { return aadharField; }
    public JTextField getPhoneField() { return phoneField; }
    public JButton getRegisterButton() { return registerButton; }

    public void showLoginCard() { cardLayout.show(cardPanel, "login"); }
    public void showRegistrationCard() { cardLayout.show(cardPanel, "register"); }
}