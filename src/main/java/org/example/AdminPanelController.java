package org.example;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AdminPanelController {

    @FXML private TextField txtSearchUser;
    @FXML private Button btnSearchUser;
    @FXML private TableView<AdminUser> tableUsers;
    @FXML private TableColumn<AdminUser, Integer> colUserId;
    @FXML private TableColumn<AdminUser, String> colUserRole;
    @FXML private TableColumn<AdminUser, String> colUserEmail;
    @FXML private TableColumn<AdminUser, String> colUserDate;
    @FXML private TableColumn<AdminUser, String> colUserStatus;
    
    @FXML private Button btnBlockUser;
    @FXML private Button btnUnblockUser;
    @FXML private Button btnDeleteUser;

    @FXML private TextField txtSearchOffer;
    @FXML private Button btnSearchOffer;
    @FXML private TableView<AdminOffer> tableOffers;
    @FXML private TableColumn<AdminOffer, Integer> colOfferId;
    @FXML private TableColumn<AdminOffer, String> colOfferTitle;
    @FXML private TableColumn<AdminOffer, String> colOfferCompany;
    @FXML private TableColumn<AdminOffer, String> colOfferDate;
    @FXML private TableColumn<AdminOffer, String> colOfferStatus;
    
    @FXML private Button btnViewOffer;
    @FXML private Button btnDeleteOffer;

    @FXML private Button btnLogout;

    private ObservableList<AdminUser> usersList = FXCollections.observableArrayList();
    private ObservableList<AdminOffer> offersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colUserEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colUserDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colUserStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colOfferId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOfferTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colOfferCompany.setCellValueFactory(new PropertyValueFactory<>("company"));
        colOfferDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colOfferStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadUsersData();
        loadOffersData();
    }



    @FXML
    private void handleSearchUser(ActionEvent event) {
        loadUsersData();
    }

    @FXML
    private void handleBlockUser(ActionEvent event) {
        changeUserStatus(true);
    }

    @FXML
    private void handleUnblockUser(ActionEvent event) {
        changeUserStatus(false);
    }

    @FXML
    private void handleDeleteUser(ActionEvent event) {
        AdminUser selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Uwaga", "Proszę wybrać użytkownika do usunięcia.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Czy na pewno chcesz trwale usunąć konto (ID: " + selectedUser.getId() + ")? Ta akcja kaskadowo usunie wszystkie powiązane dane (oferty, aplikacje).", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.YES) {
            String sql = "DELETE FROM Users WHERE UserID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, selectedUser.getId());
                pstmt.executeUpdate();
                
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Konto zostało trwale usunięte z bazy danych.");
                loadUsersData(); 
                
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Nie udało się usunąć użytkownika.");
            }
        }
    }

    @FXML
    private void handleSearchOffer(ActionEvent event) {
        loadOffersData();
    }

    @FXML
    private void handleDeleteOffer(ActionEvent event) {
        AdminOffer selectedOffer = tableOffers.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            showAlert(Alert.AlertType.WARNING, "Uwaga", "Proszę wybrać ogłoszenie do usunięcia.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Czy na pewno chcesz usunąć ofertę pt. '" + selectedOffer.getTitle() + "'?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.YES) {
            String sql = "DELETE FROM JobOffers WHERE OfferID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, selectedOffer.getId());
                pstmt.executeUpdate();
                
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Oferta została usunięta.");
                loadOffersData(); 
                
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Nie udało się usunąć oferty.");
            }
        }
    }

    @FXML
    private void handleViewOffer(ActionEvent event) {
        AdminOffer selectedOffer = tableOffers.getSelectionModel().getSelectedItem();
        
        if (selectedOffer == null) {
            showAlert(Alert.AlertType.WARNING, "Uwaga", "Proszę najpierw wybrać ogłoszenie z listy.");
            return;
        }

        String sql = "SELECT j.Title, j.Description, j.SalaryMIN, j.SalaryMAX, j.Location, j.CreatedAt, " +
                     "e.CompanyName, c.CategoryName, os.StatusName " +
                     "FROM JobOffers j " +
                     "LEFT JOIN Employers e ON j.EmployerID = e.EmployerID " +
                     "LEFT JOIN Categories c ON j.CategoryID = c.CategoryID " +
                     "LEFT JOIN OfferStatuses os ON j.OfferStatusID = os.OfferStatusID " +
                     "WHERE j.OfferID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, selectedOffer.getId());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("Title");
                    String company = rs.getString("CompanyName") != null ? rs.getString("CompanyName") : "Brak danych";
                    String category = rs.getString("CategoryName") != null ? rs.getString("CategoryName") : "Brak kategorii";
                    String desc = rs.getString("Description");
                    String salaryMin = rs.getString("SalaryMIN");
                    String salaryMax = rs.getString("SalaryMAX");
                    String location = rs.getString("Location") != null ? rs.getString("Location") : "Nie podano";
                    String status = rs.getString("StatusName");
                    String date = rs.getString("CreatedAt");

                    String content = String.format(
                        "Stanowisko: %s\nFirma: %s\nBranża: %s\nLokalizacja: %s\nWynagrodzenie: %s - %s PLN\nStatus: %s\nData dodania: %s\n\nOpis i wymagania:\n%s",
                        title, company, category, location, salaryMin, salaryMax, status, date, desc
                    );

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Podgląd Ogłoszenia");
                    alert.setHeaderText("Szczegóły oferty (ID: " + selectedOffer.getId() + ")");
                    
                    TextArea textArea = new TextArea(content);
                    textArea.setEditable(false);
                    textArea.setWrapText(true);
                    textArea.setPrefWidth(500);
                    textArea.setPrefHeight(300);
                    
                    alert.getDialogPane().setContent(textArea);
                    alert.showAndWait();
                    
                } else {
                    showAlert(Alert.AlertType.ERROR, "Błąd", "Nie znaleziono szczegółów ogłoszenia w bazie danych.");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Nie udało się pobrać szczegółów ogłoszenia.");
        }
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się wylogować z systemu.");
        }
    }


    private void loadUsersData() {
        usersList.clear();
        String searchPhrase = txtSearchUser.getText() != null ? txtSearchUser.getText().trim() : "";
        
        String sql = "SELECT UserID, Role, Email, IsBlocked, CreatedAt FROM Users WHERE Email LIKE ? OR Role LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchPhrase + "%");
            pstmt.setString(2, "%" + searchPhrase + "%");
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("UserID");
                String role = rs.getString("Role");
                String email = rs.getString("Email");
                boolean isBlocked = rs.getBoolean("IsBlocked");
                String status = isBlocked ? "Zablokowany" : "Aktywny";
                
                String date = rs.getString("CreatedAt");
                if (date == null) {
                    date = "Brak daty"; 
                }

                usersList.add(new AdminUser(id, role, email, date, status));
            }
            tableUsers.setItems(usersList);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać listy użytkowników z bazy.");
        }
    }

    private void changeUserStatus(boolean block) {
        AdminUser selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Uwaga", "Proszę wybrać użytkownika z listy.");
            return;
        }
        
        String sql = "UPDATE Users SET IsBlocked = ? WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, block);
            pstmt.setInt(2, selectedUser.getId());
            pstmt.executeUpdate();
            
            String akcja = block ? "zablokowany" : "odblokowany";
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Użytkownik został " + akcja + ".");
            loadUsersData(); 
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zmienić statusu użytkownika.");
        }
    }

    private void loadOffersData() {
        offersList.clear();
        String searchPhrase = txtSearchOffer.getText() != null ? txtSearchOffer.getText().trim() : "";
        
        String sql = "SELECT j.OfferID, j.Title, e.CompanyName, j.CreatedAt, os.StatusName " +
                     "FROM JobOffers j " +
                     "LEFT JOIN Employers e ON j.EmployerID = e.EmployerID " +
                     "LEFT JOIN OfferStatuses os ON j.OfferStatusID = os.OfferStatusID " +
                     "WHERE j.Title LIKE ? OR e.CompanyName LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchPhrase + "%");
            pstmt.setString(2, "%" + searchPhrase + "%");
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("OfferID");
                String title = rs.getString("Title");
                String company = rs.getString("CompanyName") != null ? rs.getString("CompanyName") : "Brak firmy";
                String date = rs.getString("CreatedAt") != null ? rs.getString("CreatedAt") : "";
                String status = os_status_mapping(rs.getString("StatusName"));
                
                offersList.add(new AdminOffer(id, title, company, date, status));
            }
            tableOffers.setItems(offersList);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać ofert z bazy danych.");
        }
    }

    private String os_status_mapping(String rawStatus) {
        return rawStatus != null ? rawStatus : "Nieznany";
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class AdminUser {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty role;
        private final SimpleStringProperty email;
        private final SimpleStringProperty date;
        private final SimpleStringProperty status;

        public AdminUser(int id, String role, String email, String date, String status) {
            this.id = new SimpleIntegerProperty(id);
            this.role = new SimpleStringProperty(role);
            this.email = new SimpleStringProperty(email);
            this.date = new SimpleStringProperty(date);
            this.status = new SimpleStringProperty(status);
        }

        public int getId() { return id.get(); }
        public String getRole() { return role.get(); }
        public String getEmail() { return email.get(); }
        public String getDate() { return date.get(); }
        public String getStatus() { return status.get(); }
    }

    public static class AdminOffer {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty title;
        private final SimpleStringProperty company;
        private final SimpleStringProperty date;
        private final SimpleStringProperty status;

        public AdminOffer(int id, String title, String company, String date, String status) {
            this.id = new SimpleIntegerProperty(id);
            this.title = new SimpleStringProperty(title);
            this.company = new SimpleStringProperty(company);
            this.date = new SimpleStringProperty(date);
            this.status = new SimpleStringProperty(status);
        }

        public int getId() { return id.get(); }
        public String getTitle() { return title.get(); }
        public String getCompany() { return company.get(); }
        public String getDate() { return date.get(); }
        public String getStatus() { return status.get(); }
    }
}