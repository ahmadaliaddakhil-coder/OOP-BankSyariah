package ui;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import enums.JenisAkad;
import enums.StatusAkad;
import enums.StatusKeputusan;
import enums.StatusPengajuan;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.AkadPembiayaan;
import model.AnalisisKelayakan;
import model.BankSyariah;
import model.KeputusanPembiayaan;
import model.Nasabah;
import model.PegawaiBank;
import model.Pencairan;
import model.PengajuanPembiayaan;
import model.RekeningNasabah;
import model.RiwayatPengajuan;
import model.Usaha;

/**
 * Controller antarmuka perbankan digital syariah modern.
 * Mengimplementasikan arsitektur navigasi bersih, sistem akun nasabah
 * (onboarding & switch account),
 * tombol logout/ganti akun, metrik 3 kolom tanpa redundansi, pelacak progres
 * (stepper),
 * dan rincian terpadu.
 */
public class BankSyariahController {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final NumberFormat RUPIAH = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    private final BankSyariah bank = new BankSyariah("B-001", "Bank Syariah Berkah");
    private final PegawaiBank pegawai = new PegawaiBank("PG-001", "Ahmad Fauzi", "Senior Financing Analyst", bank);
    private final List<CaseRecord> records = new ArrayList<>();
    private final List<UserProfile> userProfiles = new ArrayList<>();
    private UserProfile activeUser;

    private final ObservableList<CaseRecord> tableRows = FXCollections.observableArrayList();
    private final FilteredList<CaseRecord> employeeRows = new FilteredList<>(tableRows, item -> true);
    private final FilteredList<CaseRecord> customerRows = new FilteredList<>(tableRows, item -> true);
    private final SortedList<CaseRecord> sortedEmployeeRows = new SortedList<>(employeeRows);
    private final SortedList<CaseRecord> sortedCustomerRows = new SortedList<>(customerRows);

    /* --- TOPBAR CONTROLS --- */
    @FXML
    private Button topProfileButton;
    @FXML
    private Label topAvatarInitials;
    @FXML
    private Label topUserName;
    @FXML
    private Label topUserRole;

    /* --- SIDEBAR CONTROLS --- */
    @FXML
    private Button customerNavButton;
    @FXML
    private Button employeeNavButton;

    /* --- VIEW SCROLL PANES --- */
    @FXML
    private ScrollPane customerScrollPane;
    @FXML
    private ScrollPane employeeScrollPane;

    /* --- CUSTOMER DASHBOARD CONTROLS --- */
    @FXML
    private Label customerGreetingTitle;
    @FXML
    private Label customerGreetingSubtitle;
    @FXML
    private Label customerMetric1Value;
    @FXML
    private Label customerMetric1Sub;
    @FXML
    private Label customerMetric2Value;
    @FXML
    private Label customerMetric2Sub;
    @FXML
    private Label customerMetric3Value;
    @FXML
    private Label customerMetric3Sub;

    @FXML
    private TableView<CaseRecord> customerTable;
    @FXML
    private TableColumn<CaseRecord, String> customerIdColumn;
    @FXML
    private TableColumn<CaseRecord, String> customerDateColumn;
    @FXML
    private TableColumn<CaseRecord, String> customerAkadColumn;
    @FXML
    private TableColumn<CaseRecord, String> customerAmountColumn;
    @FXML
    private TableColumn<CaseRecord, CaseRecord> customerStatusColumn;
    @FXML
    private HBox customerStepper;
    @FXML
    private VBox customerRightPanel;

    /* --- EMPLOYEE / ANALYST CONTROLS --- */
    @FXML
    private Label employeeBadge;
    @FXML
    private Label employeeMetric1Value;
    @FXML
    private Label employeeMetric1Sub;
    @FXML
    private Label employeeMetric2Value;
    @FXML
    private Label employeeMetric2Sub;
    @FXML
    private Label employeeMetric3Value;
    @FXML
    private Label employeeMetric3Sub;

    @FXML
    private TextField search;
    @FXML
    private TableView<CaseRecord> employeeTable;
    @FXML
    private TableColumn<CaseRecord, String> employeeIdColumn;
    @FXML
    private TableColumn<CaseRecord, String> employeeDateColumn;
    @FXML
    private TableColumn<CaseRecord, String> employeeCustomerColumn;
    @FXML
    private TableColumn<CaseRecord, String> employeeBusinessColumn;
    @FXML
    private TableColumn<CaseRecord, String> employeeAmountColumn;
    @FXML
    private TableColumn<CaseRecord, CaseRecord> employeeStatusColumn;
    @FXML
    private HBox employeeStepper;
    @FXML
    private VBox employeeRightPanel;

    public BankSyariahController() {
    }

    @FXML
    private void initialize() {
        bank.tambahPegawai(pegawai);
        configureTable();
        loadInitialDemoData();
        showCustomerView();
    }

    /**
     * Memuat 2 data profil nasabah dan kasus pembiayaan nyata:
     * 1. Siti Rahma (Kedai Berkah): Selesai dicairkan penuh Rp 80.000.000
     * (Musyarakah).
     * 2. Hendra Pratama (Berkah Konveksi): Baru diajukan Rp 50.000.000 (Mudharabah
     * - siap diproses).
     */
    private void loadInitialDemoData() {
        try {
            // Profil Nasabah 1: Siti Rahma
            Nasabah nasabah1 = new Nasabah(
                    "N-001", "Siti Rahma", "Jl. Riau No. 12, Bandung",
                    "1995-04-12", "Wiraswasta Kuliner", 5_000_000L);
            Usaha usaha1 = new Usaha(
                    "U-001", "Kedai Berkah", "Kuliner Tradisional",
                    "Jl. Riau No. 12, Bandung", nasabah1);
            RekeningNasabah rekening1 = new RekeningNasabah(
                    "1029384756", nasabah1, 1_000_000L);
            nasabah1.tambahUsaha(usaha1);
            nasabah1.tambahRekening(rekening1);

            PengajuanPembiayaan pengajuan1 = new PengajuanPembiayaan(
                    "P-001", nasabah1, usaha1, 80_000_000L, 24,
                    "Ekspansi cabang dan penambahan peralatan dapur",
                    "2026-09-27", JenisAkad.MUSYARAKAH, 40, 60,
                    20_000_000L, 30_000_000L, 12_000_000L, 8_000_000L, 3_000_000L);
            nasabah1.ajukanPembiayaan(pengajuan1);

            CaseRecord record1 = new CaseRecord(nasabah1, usaha1, rekening1, pengajuan1);
            pengajuan1.mulaiAnalisis(pegawai, "2026-09-27 10:00");
            AnalisisKelayakan analisis1 = new AnalisisKelayakan(
                    "A-001", "2026-09-27", pengajuan1, pegawai,
                    30_000_000L, 12_000_000L, 8_000_000L, 3_000_000L,
                    "Arus kas surplus Rp7.000.000/bln. Rekam jejak penjualan sangat baik.");
            pengajuan1.catatAnalisis(analisis1);

            KeputusanPembiayaan keputusan1 = new KeputusanPembiayaan(
                    "K-001", "2026-09-27 14:00", pengajuan1, pegawai,
                    StatusKeputusan.DISETUJUI, 80_000_000L,
                    "Disetujui komite pembiayaan sesuai usulan nisbah 40:60.");
            pengajuan1.catatKeputusan(keputusan1);
            record1.keputusan = keputusan1;

            pengajuan1.buatAkad("AK-001", keputusan1, "2028-09-27");
            pengajuan1.getAkad().tandatangani("2026-09-28");
            pengajuan1.getAkad().cairkan("PC-001", "2026-09-29", "TRX-20260929-001", rekening1);
            records.add(record1);

            // Profil Nasabah 2: Hendra Pratama
            Nasabah nasabah2 = new Nasabah(
                    "N-002", "Hendra Pratama", "Jl. Soekarno-Hatta No. 88, Bandung",
                    "1990-08-20", "Pengusaha Tekstil & Pakaian", 8_000_000L);
            Usaha usaha2 = new Usaha(
                    "U-002", "Berkah Konveksi Nusantara", "Tekstil & Fashion",
                    "Jl. Soekarno-Hatta No. 88, Bandung", nasabah2);
            RekeningNasabah rekening2 = new RekeningNasabah(
                    "5544332211", nasabah2, 3_500_000L);
            nasabah2.tambahUsaha(usaha2);
            nasabah2.tambahRekening(rekening2);

            PengajuanPembiayaan pengajuan2 = new PengajuanPembiayaan(
                    "P-002", nasabah2, usaha2, 50_000_000L, 12,
                    "Pengadaan bahan baku kain pesanan seragam instansi",
                    "2026-11-05", JenisAkad.MUDHARABAH, 35, 65,
                    0L, 25_000_000L, 10_000_000L, 5_000_000L, 2_000_000L);
            nasabah2.ajukanPembiayaan(pengajuan2);
            CaseRecord record2 = new CaseRecord(nasabah2, usaha2, rekening2, pengajuan2);
            records.add(record2);

            userProfiles.add(new UserProfile(nasabah1, rekening1));
            userProfiles.add(new UserProfile(nasabah2, rekening2));
            activeUser = userProfiles.get(0);

            tableRows.setAll(records);
            if (!records.isEmpty()) {
                customerTable.getSelectionModel().select(0);
                employeeTable.getSelectionModel().select(0);
            }
        } catch (Exception error) {
            System.err.println("Gagal memuat demo data: " + error.getMessage());
        }
    }

    @FXML
    private void showCustomerView() {
        customerScrollPane.setVisible(true);
        customerScrollPane.setManaged(true);
        employeeScrollPane.setVisible(false);
        employeeScrollPane.setManaged(false);

        updateNavigation(customerNavButton, employeeNavButton);
        if (activeUser != null) {
            topAvatarInitials.setText(getInitials(activeUser.getNasabah().getNamaPihak()));
            topUserName.setText(activeUser.getNasabah().getNamaPihak());
            topUserRole.setText("Nasabah Aktif");

            if (customerGreetingTitle != null) {
                customerGreetingTitle.setText("Selamat Datang, " + activeUser.getNasabah().getNamaPihak() + "!");
            }
            if (customerGreetingSubtitle != null) {
                customerGreetingSubtitle.setText("Nomor Rekening: " + activeUser.getRekening().getNomorRekening()
                        + " · Saldo: " + rupiah(activeUser.getRekening().getSaldo()));
            }
            customerRows.setPredicate(item -> item.nasabah == activeUser.getNasabah());
        } else {
            customerRows.setPredicate(item -> true);
        }

        CaseRecord selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null && !customerTable.getItems().isEmpty()) {
            customerTable.getSelectionModel().select(0);
            selected = customerTable.getSelectionModel().getSelectedItem();
        }
        updateCustomerMetrics();
        renderCustomerDetails(selected);
    }

    @FXML
    private void showEmployeeView() {
        customerScrollPane.setVisible(false);
        customerScrollPane.setManaged(false);
        employeeScrollPane.setVisible(true);
        employeeScrollPane.setManaged(true);

        updateNavigation(employeeNavButton, customerNavButton);
        topAvatarInitials.setText("AF");
        topUserName.setText(pegawai.getNama());
        topUserRole.setText(pegawai.getJabatan());

        CaseRecord selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null && !records.isEmpty()) {
            employeeTable.getSelectionModel().select(0);
            selected = employeeTable.getSelectionModel().getSelectedItem();
        }
        updateEmployeeMetrics();
        renderEmployeeDetails(selected);
    }

    private void updateNavigation(Button active, Button inactive) {
        if (!active.getStyleClass().contains("nav-active")) {
            active.getStyleClass().add("nav-active");
        }
        inactive.getStyleClass().remove("nav-active");
    }

    private void configureTable() {
        // Customer Table
        customerIdColumn
                .setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().pengajuan.getIdPengajuan()));
        customerDateColumn
                .setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().pengajuan.getTanggalPengajuan()));
        customerAkadColumn
                .setCellValueFactory(cell -> new SimpleStringProperty(label(cell.getValue().pengajuan.getJenisAkad())));
        customerAmountColumn
                .setCellValueFactory(cell -> new SimpleStringProperty(rupiah(cell.getValue().pengajuan.getNominal())));
        customerStatusColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue()));
        customerStatusColumn.setCellFactory(col -> new StatusPillTableCell());

        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        customerTable.setPlaceholder(new Label("Belum ada pengajuan pembiayaan aktif untuk akun ini."));
        sortedCustomerRows.comparatorProperty().bind(customerTable.comparatorProperty());
        customerTable.setItems(sortedCustomerRows);
        customerTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, selected) -> {
                    updateCustomerMetrics();
                    renderCustomerDetails(selected);
                });

        // Employee Table
        employeeIdColumn
                .setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().pengajuan.getIdPengajuan()));
        employeeDateColumn
                .setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().pengajuan.getTanggalPengajuan()));
        employeeCustomerColumn
                .setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().nasabah.getNamaPihak()));
        employeeBusinessColumn
                .setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().usaha.getNamaUsaha()));
        employeeAmountColumn
                .setCellValueFactory(cell -> new SimpleStringProperty(rupiah(cell.getValue().pengajuan.getNominal())));
        employeeStatusColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue()));
        employeeStatusColumn.setCellFactory(col -> new StatusPillTableCell());

        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        employeeTable.setPlaceholder(new Label("Tidak ada antrean pengajuan masuk."));
        sortedEmployeeRows.comparatorProperty().bind(employeeTable.comparatorProperty());
        employeeTable.setItems(sortedEmployeeRows);
        employeeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, selected) -> {
                    updateEmployeeMetrics();
                    renderEmployeeDetails(selected);
                });

        search.textProperty().addListener((obs, oldVal, newVal) -> employeeRows.setPredicate(this::matchesSearch));
    }

    private boolean matchesSearch(CaseRecord item) {
        String query = search.getText() == null ? "" : search.getText().trim().toLowerCase(Locale.ROOT);
        return query.isEmpty()
                || item.pengajuan.getIdPengajuan().toLowerCase(Locale.ROOT).contains(query)
                || item.nasabah.getNamaPihak().toLowerCase(Locale.ROOT).contains(query)
                || item.usaha.getNamaUsaha().toLowerCase(Locale.ROOT).contains(query);
    }

    /*
     * =========================================================================
     * USER ACCOUNT REGISTRATION & LOGOUT / ACCOUNT SWITCHER
     * =========================================================================
     */

    /**
     * Membuka formulir pendaftaran nasabah baru dan pembukaan rekening bisnis
     * syariah.
     */
    @FXML
    private void registerNewAccount() {
        Form form = new Form("Registrasi Akun Nasabah & Pembukaan Rekening", "/views/register-form.fxml");
        TextField idField = form.text("regCustomerId");
        TextField nameField = form.text("regCustomerName");
        TextField addressField = form.text("regAddress");
        DatePicker birthDateField = form.date("regBirthDate", LocalDate.of(1996, 5, 20));
        TextField occupationField = form.text("regOccupation");
        TextField salaryField = form.money("regMonthlySalary", "8000000");
        TextField accField = form.text("regAccountNumber");
        TextField balanceField = form.money("regStartingBalance", "2500000");
        CheckBox consent = form.control("regConsent", CheckBox.class);

        // Pre-fill ID & Nomor Rekening otomatis
        idField.setText("3273" + String.format(Locale.ROOT, "%012d", System.currentTimeMillis() % 1000000000000L));
        accField.setText("99" + String.format(Locale.ROOT, "%08d", (userProfiles.size() + 1) * 111111));

        if (!form.show(() -> {
            required(idField);
            required(nameField);
            required(addressField);
            if (!dateValue(birthDateField).isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Tanggal lahir harus sebelum hari ini.");
            }
            required(occupationField);
            parseAmount(salaryField);
            required(accField);
            parseAmount(balanceField);
            if (!consent.isSelected()) {
                throw new IllegalArgumentException("Harap setujui pernyataan pembukaan rekening syariah.");
            }
        })) {
            return;
        }

        try {
            Nasabah newNasabah = new Nasabah(
                    required(idField),
                    required(nameField),
                    required(addressField),
                    dateValue(birthDateField).toString(),
                    required(occupationField),
                    parseAmount(salaryField));
            RekeningNasabah newRekening = new RekeningNasabah(
                    required(accField),
                    newNasabah,
                    parseAmount(balanceField));
            newNasabah.tambahRekening(newRekening);

            UserProfile newProfile = new UserProfile(newNasabah, newRekening);
            userProfiles.add(newProfile);
            activeUser = newProfile;

            refreshTables();
            showCustomerView();
            showSuccess("Alhamdulillah! Akun nasabah baru atas nama "
                    + newNasabah.getNamaPihak() + " berhasil didaftarkan.\nRekening Syariah: "
                    + newRekening.getNomorRekening() + " dengan saldo awal "
                    + rupiah(newRekening.getSaldo())
                    + ".\nSilakan klik '+ Ajukan Pembiayaan Baru' untuk mengajukan modal usaha!");
        } catch (IllegalArgumentException error) {
            showError(error.getMessage());
        }
    }

    /**
     * Manajemen Sesi & Akun Pengguna: Memungkinkan nasabah beralih profil rekening,
     * mendaftar nasabah/rekening baru, atau masuk ke portal Analis Bank.
     */
    @FXML
    private void handleLogout() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Profil Pengguna & Manajemen Sesi");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        dialog.getDialogPane().setPrefWidth(540);

        ButtonType btnSwitch = new ButtonType("Beralih ke Akun Terpilih", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnRegister = new ButtonType("+ Registrasi Akun Baru", ButtonBar.ButtonData.OTHER);
        ButtonType btnToggleMode = new ButtonType(
                customerScrollPane.isVisible() ? "Beralih ke Mode Analis" : "Beralih ke Mode Nasabah",
                ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(btnSwitch, btnRegister, btnToggleMode, ButtonType.CANCEL);

        VBox content = new VBox(14);
        content.setPadding(new Insets(16));

        // Active Session Overview Card
        VBox activeCard = new VBox(5);
        activeCard.setStyle(
                "-fx-background-color: #f1f5f9; -fx-padding: 12px 14px; -fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-radius: 8px;");
        Label activeHeader = new Label("SESI AKTIF SAAT INI");
        activeHeader.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748b; -fx-letter-spacing: 0.5px;");

        Label activeTitle = new Label(
                customerScrollPane.isVisible()
                        ? (activeUser != null ? activeUser.getNasabah().getNamaPihak() + " (Nasabah)"
                                : "Belum Ada Sesi")
                        : (pegawai.getNama() + " (" + pegawai.getJabatan() + ")"));
        activeTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0b4635;");

        Label activeSub = new Label(
                customerScrollPane.isVisible() && activeUser != null
                        ? "Nomor Rekening: " + activeUser.getRekening().getNomorRekening() + "  |  Saldo: "
                                + rupiah(activeUser.getRekening().getSaldo())
                        : "NIP: " + pegawai.getNomorPegawai() + "  |  Unit Kerja: " + bank.getNamaBank());
        activeSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #475569;");
        activeCard.getChildren().addAll(activeHeader, activeTitle, activeSub);

        // Account Switcher Choice
        VBox switchSection = new VBox(6);
        Label switchLabel = new Label("Pilih Profil Nasabah untuk Diaktifkan:");
        switchLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        ChoiceBox<UserProfile> userChoice = new ChoiceBox<>(FXCollections.observableArrayList(userProfiles));
        userChoice.setValue(activeUser);
        userChoice.setMaxWidth(Double.MAX_VALUE);
        switchSection.getChildren().addAll(switchLabel, userChoice);

        Label hint = new Label(
                "💡 Anda dapat berpindah profil untuk menguji pengajuan yang berbeda, mendaftar akun nasabah baru, atau berganti peran antara Nasabah dan Analis Bank.");
        hint.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        hint.setWrapText(true);

        content.getChildren().addAll(activeCard, switchSection, hint);
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait().ifPresent(response -> {
            if (response == btnSwitch && userChoice.getValue() != null) {
                activeUser = userChoice.getValue();
                showCustomerView();
                showSuccess("Berhasil beralih ke sesi akun: " + activeUser.getNasabah().getNamaPihak());
            } else if (response == btnRegister) {
                registerNewAccount();
            } else if (response == btnToggleMode) {
                if (customerScrollPane.isVisible()) {
                    showEmployeeView();
                } else {
                    showCustomerView();
                }
            }
        });
    }

    private static String getInitials(String name) {
        if (name == null || name.isBlank())
            return "BS";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1)
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
        return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase(Locale.ROOT);
    }

    /*
     * =========================================================================
     * METRICS CALCULATION (NON-REDUNDANT 3-COLUMN)
     * =========================================================================
     */

    private void updateCustomerMetrics() {
        long totalPlafonAktif = 0;
        int activeCount = 0;
        long saldoRekening = activeUser != null ? activeUser.getRekening().getSaldo() : 0;
        String latestStatus = "Belum Ada Pengajuan";

        if (activeUser != null) {
            customerMetric2Sub.setText("No. Rekening " + activeUser.getRekening().getNomorRekening());
            if (customerGreetingSubtitle != null) {
                customerGreetingSubtitle.setText("Nomor Rekening: " + activeUser.getRekening().getNomorRekening()
                        + " · Saldo: " + rupiah(activeUser.getRekening().getSaldo()));
            }
            for (CaseRecord r : records) {
                if (r.nasabah == activeUser.getNasabah()) {
                    if (r.pengajuan.getAkad() != null && r.pengajuan.getAkad().getStatus() == StatusAkad.DICAIRKAN) {
                        totalPlafonAktif += r.pengajuan.getAkad().getJumlahDisetujui();
                        activeCount++;
                    }
                    latestStatus = r.pengajuan.getIdPengajuan() + ": " + getDisplayStatus(r);
                }
            }
        }

        customerMetric1Value.setText(rupiah(totalPlafonAktif));
        customerMetric1Sub.setText(activeCount + " Fasilitas Pembiayaan Berjalan");
        customerMetric2Value.setText(rupiah(saldoRekening));
        customerMetric3Value.setText(latestStatus);
        customerMetric3Sub.setText("Status berkas permohonan terkini");
    }

    private void updateEmployeeMetrics() {
        int totalMasuk = records.size();
        int pendingReview = 0;
        long totalCair = 0;

        for (CaseRecord r : records) {
            StatusPengajuan st = r.pengajuan.getStatus();
            if (st == StatusPengajuan.DIAJUKAN || st == StatusPengajuan.DIPROSES) {
                pendingReview++;
            }
            if (r.pengajuan.getAkad() != null && r.pengajuan.getAkad().getStatus() == StatusAkad.DICAIRKAN) {
                totalCair += r.pengajuan.getAkad().getPencairan().getJumlah();
            }
        }

        employeeMetric1Value.setText(totalMasuk + " Berkas");
        employeeMetric1Sub.setText("Total pembiayaan terdaftar");
        employeeMetric2Value.setText(pendingReview + " Berkas");
        employeeMetric2Sub.setText("Memerlukan verifikasi / keputusan");
        employeeMetric3Value.setText(rupiah(totalCair));
        employeeMetric3Sub.setText("Realisasi pencairan penuh ke nasabah");
    }

    private String getDisplayStatus(CaseRecord record) {
        if (record.pengajuan.getAkad() != null) {
            if (record.pengajuan.getAkad().getStatus() == StatusAkad.DICAIRKAN) {
                return "Dicairkan";
            } else if (record.pengajuan.getAkad().getStatus() == StatusAkad.MENUNGGU_PENCAIRAN) {
                return "Menunggu Pencairan";
            } else {
                return "Draft Akad";
            }
        }
        return label(record.pengajuan.getStatus());
    }

    /*
     * =========================================================================
     * STEPPER TRACKING (DIRECTLY UNDER TABLE)
     * =========================================================================
     */

    private void updateStepper(HBox stepperContainer, CaseRecord record) {
        stepperContainer.getChildren().clear();
        String[] stepTitles = { "1. Pengajuan", "2. Analisis Usaha", "3. Keputusan Komite", "4. Akad Digital",
                "5. Pencairan Dana" };

        int currentStep = 1;
        StatusPengajuan status = record.pengajuan.getStatus();
        AkadPembiayaan akad = record.pengajuan.getAkad();

        if (status == StatusPengajuan.DIAJUKAN) {
            currentStep = 1;
        } else if (status == StatusPengajuan.DIPROSES) {
            currentStep = 2;
        } else if (status == StatusPengajuan.DITOLAK) {
            currentStep = 3;
        } else if (status == StatusPengajuan.DISETUJUI) {
            if (akad == null) {
                currentStep = 3;
            } else if (akad.getStatus() == StatusAkad.DRAFT) {
                currentStep = 4;
            } else if (akad.getStatus() == StatusAkad.MENUNGGU_PENCAIRAN) {
                currentStep = 4;
            } else if (akad.getStatus() == StatusAkad.DICAIRKAN) {
                currentStep = 5;
            }
        }

        boolean isCompletedAll = akad != null && akad.getStatus() == StatusAkad.DICAIRKAN;
        boolean isRejected = status == StatusPengajuan.DITOLAK;

        for (int i = 0; i < 5; i++) {
            int stepNum = i + 1;
            VBox node = new VBox();
            node.setAlignment(Pos.CENTER);
            node.setSpacing(4);

            HBox circle = new HBox();
            circle.setAlignment(Pos.CENTER);
            circle.getStyleClass().add("step-circle");

            Label numberLabel = new Label();
            Label titleLabel = new Label(stepTitles[i]);
            titleLabel.getStyleClass().add("step-title");

            if (isRejected && stepNum == 3) {
                circle.setStyle("-fx-background-color: #dc2626;");
                numberLabel.setText("✕");
                numberLabel.getStyleClass().add("step-number-done");
                titleLabel.setText("3. Ditolak");
                titleLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
            } else if (stepNum < currentStep || (stepNum == 5 && isCompletedAll)) {
                circle.getStyleClass().add("step-circle-done");
                numberLabel.setText("✓");
                numberLabel.getStyleClass().add("step-number-done");
            } else if (stepNum == currentStep && !isCompletedAll) {
                circle.getStyleClass().add("step-circle-active");
                numberLabel.setText(String.valueOf(stepNum));
                numberLabel.getStyleClass().add("step-number-active");
                titleLabel.getStyleClass().add("step-title-active");
            } else {
                numberLabel.setText(String.valueOf(stepNum));
                numberLabel.getStyleClass().add("step-number");
            }

            circle.getChildren().add(numberLabel);
            node.getChildren().addAll(circle, titleLabel);
            stepperContainer.getChildren().add(node);

            if (i < 4) {
                Region line = new Region();
                line.getStyleClass().add("step-line");
                HBox.setHgrow(line, Priority.ALWAYS);
                if (stepNum < currentStep || isCompletedAll) {
                    line.getStyleClass().add("step-line-done");
                }
                stepperContainer.getChildren().add(line);
            }
        }
    }

    /*
     * =========================================================================
     * CONSOLIDATED RIGHT DETAIL PANEL RENDERING
     * =========================================================================
     */

    private void renderCustomerDetails(CaseRecord record) {
        customerRightPanel.getChildren().clear();
        if (record == null) {
            customerStepper.getChildren().clear();
            Label empty = new Label("Pilih pengajuan pada tabel untuk melihat detail.");
            empty.getStyleClass().add("hint-text");
            customerRightPanel.getChildren().add(empty);
            return;
        }

        updateStepper(customerStepper, record);

        // 1. Account Highlight
        customerRightPanel.getChildren().add(createAccountHighlight(record.rekening, record.nasabah));

        // 2. Ringkasan Akad & Usaha
        customerRightPanel.getChildren().add(createContractSummarySection(record));

        // 3. Ringkasan Finansial Usaha
        customerRightPanel.getChildren().add(createFinancialSummarySection(record.pengajuan));

        // 4. Jejak Riwayat (Linked List)
        customerRightPanel.getChildren().add(createHistorySection(record.pengajuan));
    }

    private void renderEmployeeDetails(CaseRecord record) {
        employeeRightPanel.getChildren().clear();
        if (record == null) {
            employeeStepper.getChildren().clear();
            Label empty = new Label("Pilih berkas pengajuan untuk melihat detail tindakan.");
            empty.getStyleClass().add("hint-text");
            employeeRightPanel.getChildren().add(empty);
            return;
        }

        updateStepper(employeeStepper, record);

        // 1. Action Hub di paling atas
        employeeRightPanel.getChildren().add(createActionHubSection(record));

        // 2. Info Rekening
        employeeRightPanel.getChildren().add(createAccountHighlight(record.rekening, record.nasabah));

        // 3. Hasil Analisis Keuangan & Verifikasi Pegawai
        employeeRightPanel.getChildren().add(createAnalystFinancialSection(record.pengajuan));

        // 4. Skema Akad & Nisbah
        employeeRightPanel.getChildren().add(createContractSummarySection(record));

        // 5. Jejak Riwayat
        employeeRightPanel.getChildren().add(createHistorySection(record.pengajuan));
    }

    private Node createAccountHighlight(RekeningNasabah rekening, Nasabah nasabah) {
        VBox box = new VBox();
        box.getStyleClass().add("account-highlight-card");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label("REKENING PENCAIRAN UTAMA");
        lbl.getStyleClass().add("account-card-label");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label statusTag = new Label("TERVERIFIKASI");
        statusTag.setStyle("-fx-font-size: 10px; -fx-text-fill: #ffd978; -fx-font-weight: bold;");
        top.getChildren().addAll(lbl, sp, statusTag);

        Label bal = new Label(rupiah(rekening.getSaldo()));
        bal.getStyleClass().add("account-card-balance");

        String raw = rekening.getNomorRekening();
        String masked = raw.length() >= 4 ? "Rekening: •••• " + raw.substring(raw.length() - 4) : raw;
        Label acc = new Label(masked + " (" + nasabah.getNamaPihak() + ")");
        acc.getStyleClass().add("account-card-number");

        box.getChildren().addAll(top, bal, acc);
        return box;
    }

    private Node createActionHubSection(CaseRecord record) {
        VBox box = new VBox();
        box.getStyleClass().add("action-box-container");

        Label title = new Label("⚡ Tindakan Tahapan Selanjutnya");
        title.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #166534;");

        StatusPengajuan status = record.pengajuan.getStatus();
        if (status == StatusPengajuan.DIAJUKAN) {
            Label prompt = new Label("Pengajuan baru masuk. Silakan lakukan pemeriksaan dan verifikasi data keuangan.");
            prompt.getStyleClass().add("action-prompt-text");
            Button btn = new Button("🔍  Periksa Data Keuangan");
            btn.getStyleClass().add("action-primary-btn");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> recordAnalysis(record));
            box.getChildren().addAll(title, prompt, btn);
        } else if (status == StatusPengajuan.DIPROSES && record.pengajuan.getAnalisisKelayakan() != null) {
            Label prompt = new Label("Analisis keuangan telah tercatat. Pegawai/Komite dapat menetapkan persetujuan.");
            prompt.getStyleClass().add("action-prompt-text");
            Button btn = new Button("⚖  Tetapkan Keputusan Komite");
            btn.getStyleClass().add("action-primary-btn");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> decide(record));
            box.getChildren().addAll(title, prompt, btn);
        } else if (status == StatusPengajuan.DISETUJUI && record.pengajuan.getAkad() == null) {
            Label prompt = new Label("Pengajuan disetujui komite! Siapkan dokumen draft akad pembiayaan.");
            prompt.getStyleClass().add("action-prompt-text");
            Button btn = new Button("📝  Terbitkan Draft Akad");
            btn.getStyleClass().add("action-primary-btn");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> createContract(record));
            box.getChildren().addAll(title, prompt, btn);
        } else if (record.pengajuan.getAkad() != null && record.pengajuan.getAkad().getStatus() == StatusAkad.DRAFT) {
            Label prompt = new Label("Draft akad telah terbit. Lakukan proses penandatanganan akad digital.");
            prompt.getStyleClass().add("action-prompt-text");
            Button btn = new Button("✍  Tanda Tangani Akad Digital");
            btn.getStyleClass().add("action-primary-btn");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> signContract(record));
            box.getChildren().addAll(title, prompt, btn);
        } else if (record.pengajuan.getAkad() != null && record.pengajuan.getAkad().getPencairan() == null) {
            Label prompt = new Label("Akad telah ditandatangani. Siap mengeksekusi pencairan penuh dana ke rekening.");
            prompt.getStyleClass().add("action-prompt-text");
            Button btn = new Button("💳  Eksekusi Pencairan Dana");
            btn.getStyleClass().add("action-primary-btn");
            btn.setStyle("-fx-background-color: #0b4635;");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> disburse(record));
            box.getChildren().addAll(title, prompt, btn);
        } else if (status == StatusPengajuan.DITOLAK) {
            Label done = new Label("Pengajuan ini telah DITOLAK oleh komite pembiayaan.");
            done.setStyle("-fx-font-weight: bold; -fx-text-fill: #dc2626; -fx-font-size: 11px;");
            box.getChildren().addAll(title, done);
        } else {
            Label done = new Label("✓ Seluruh proses selesai. Dana berhasil dicairkan penuh ke rekening nasabah.");
            done.setStyle("-fx-font-weight: bold; -fx-text-fill: #0b4635; -fx-font-size: 11px;");
            box.getChildren().addAll(title, done);
        }
        return box;
    }

    private Node createContractSummarySection(CaseRecord record) {
        VBox box = new VBox(8);
        box.getStyleClass().add("panel-divider");

        Label title = new Label("Struktur Pembiayaan & Nisbah");
        title.getStyleClass().add("panel-section-title");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("info-grid");

        addInfoRow(grid, 0, "Nominal Plafon", rupiah(record.pengajuan.getNominal()));
        addInfoRow(grid, 1, "Tenor Pembiayaan", record.pengajuan.getTenorBulan() + " Bulan");
        addInfoRow(grid, 2, "Tujuan Penggunaan", record.pengajuan.getTujuanPenggunaan());
        addInfoRow(grid, 3, "Jenis Akad Syariah", label(record.pengajuan.getJenisAkad()));

        int bankShare = record.pengajuan.getNisbahBank();
        int custShare = record.pengajuan.getNisbahNasabah();
        addInfoRow(grid, 4, "Nisbah Bagi Hasil", bankShare + "% (Bank) / " + custShare + "% (Nasabah)");

        if (record.pengajuan.getJenisAkad() == JenisAkad.MUSYARAKAH) {
            addInfoRow(grid, 5, "Penyertaan Modal", rupiah(record.pengajuan.getModalNasabah()));
        }

        // Nisbah bar
        VBox barContainer = new VBox(4);
        barContainer.setPadding(new Insets(4, 0, 4, 0));
        HBox bar = new HBox();
        bar.getStyleClass().add("nisbah-bar");
        Region bPortion = new Region();
        bPortion.getStyleClass().add("nisbah-bank-portion");
        bPortion.prefWidthProperty().bind(bar.widthProperty().multiply(bankShare / 100.0));
        Region cPortion = new Region();
        cPortion.getStyleClass().add("nisbah-customer-portion");
        cPortion.prefWidthProperty().bind(bar.widthProperty().multiply(custShare / 100.0));
        bar.getChildren().addAll(bPortion, cPortion);
        barContainer.getChildren().add(bar);

        grid.add(barContainer, 0, 6, 2, 1);

        box.getChildren().addAll(title, grid);
        return box;
    }

    private Node createFinancialSummarySection(PengajuanPembiayaan pengajuan) {
        VBox box = new VBox(8);
        box.getStyleClass().add("panel-divider");

        Label title = new Label("Laporan Keuangan Usaha (Bulanan)");
        title.getStyleClass().add("panel-section-title");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("info-grid");

        addInfoRow(grid, 0, "Omzet Dilaporkan", rupiah(pengajuan.getOmzetBulananDilaporkan()));
        addInfoRow(grid, 1, "Biaya Langsung", rupiah(pengajuan.getBiayaLangsungBulananDilaporkan()));
        addInfoRow(grid, 2, "Biaya Operasional", rupiah(pengajuan.getBiayaOperasionalBulananDilaporkan()));
        addInfoRow(grid, 3, "Kewajiban Usaha", rupiah(pengajuan.getKewajibanUsahaBulananDilaporkan()));

        box.getChildren().addAll(title, grid);
        return box;
    }

    private Node createAnalystFinancialSection(PengajuanPembiayaan pengajuan) {
        VBox box = new VBox(8);
        box.getStyleClass().add("panel-divider");

        Label title = new Label("Hasil Analisis Keuangan & Rekomendasi");
        title.getStyleClass().add("panel-section-title");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("info-grid");

        AnalisisKelayakan analysis = pengajuan.getAnalisisKelayakan();
        if (analysis != null) {
            long laba = analysis.hitungLabaOperasionalBulanan();
            long arusKas = analysis.hitungArusKasTersediaBulanan();

            addInfoRow(grid, 0, "Laba Operasional Usaha", rupiah(laba));
            addInfoRow(grid, 1, "Arus Kas Tersedia Bersih", rupiah(arusKas));
            addInfoRow(grid, 2, "Rekomendasi Internal", label(analysis.getRekomendasiInternal()));
            addInfoRow(grid, 3, "Catatan Risiko Analis", analysis.getCatatanRisiko());
        } else {
            Label pending = new Label("Pemeriksaan keuangan usaha belum dilakukan.");
            pending.setStyle("-fx-font-size: 11px; -fx-text-fill: #a16207; -fx-font-style: italic;");
            grid.add(pending, 0, 0, 2, 1);
        }

        box.getChildren().addAll(title, grid);
        return box;
    }

    private Node createHistorySection(PengajuanPembiayaan pengajuan) {
        VBox box = new VBox(8);
        box.getStyleClass().add("panel-divider");

        Label title = new Label("Jejak Status (Singly Linked List)");
        title.getStyleClass().add("panel-section-title");

        VBox timeline = new VBox(10);
        RiwayatPengajuan node = pengajuan.getRiwayatPertama();
        while (node != null) {
            HBox item = new HBox(8);
            item.getStyleClass().add("timeline-item");

            Region dot = new Region();
            dot.getStyleClass().add("timeline-dot");

            VBox info = new VBox(2);
            HBox topRow = new HBox(8);
            Label time = new Label(node.getWaktu());
            time.getStyleClass().add("timeline-time");
            Label st = new Label("➔ " + label(node.getStatusSesudahnya()));
            st.getStyleClass().add("timeline-status");
            topRow.getChildren().addAll(time, st);

            Label desc = new Label(node.getKeterangan() + " (oleh " + node.getPelaku() + ")");
            desc.getStyleClass().add("timeline-desc");

            info.getChildren().addAll(topRow, desc);
            item.getChildren().addAll(dot, info);
            timeline.getChildren().add(item);

            node = node.getBerikutnya();
        }

        box.getChildren().addAll(title, timeline);
        return box;
    }

    private void addInfoRow(GridPane grid, int row, String key, String val) {
        Label k = new Label(key);
        k.getStyleClass().add("info-key");
        Label v = new Label(val == null ? "-" : val);
        v.getStyleClass().add("info-value");
        v.setWrapText(true);
        grid.add(k, 0, row);
        grid.add(v, 1, row);
    }

    /*
     * =========================================================================
     * AKAD EDUCATION MODAL (PANDUAN PRINSIP SYARIAH)
     * =========================================================================
     */

    @FXML
    private void openAkadEducationModal() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Panduan Prinsip Akad Pembiayaan Syariah");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        dialog.getDialogPane().setPrefWidth(580);

        VBox content = new VBox(14);
        content.setPadding(new Insets(10));

        Label header = new Label("Perbedaan Akad Mudharabah dan Musyarakah");
        header.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0b4635;");

        // Card Mudharabah
        VBox mudhCard = new VBox(6);
        mudhCard.setStyle(
                "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8px; -fx-padding: 12px;");
        Label mudhTitle = new Label("1. Akad Mudharabah (Kemitraan Penanaman Modal 100% Bank)");
        mudhTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0b4635;");
        Label mudhDesc = new Label(
                "• Prinsip: Bank (Shahibul Mal) menyediakan 100% modal usaha, sedangkan Nasabah (Mudharib) bertindak sebagai pengelola usaha.\n"
                        + "• Pembagian Hasil: Keuntungan dibagi sesuai nisbah yang disepakati bersama di awal (contoh: 35% Bank / 65% Nasabah).\n"
                        + "• Risiko Kerugian: Kerugian finansial ditanggung oleh Bank, kecuali jika kerugian terjadi akibat kelalaian atau pelanggaran nasabah.\n"
                        + "• Landasan Syariah: Fatwa DSN-MUI No. 07/DSN-MUI/IV/2000.");
        mudhDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: #334155;");
        mudhDesc.setWrapText(true);
        mudhCard.getChildren().addAll(mudhTitle, mudhDesc);

        // Card Musyarakah
        VBox musyCard = new VBox(6);
        musyCard.setStyle(
                "-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8px; -fx-padding: 12px;");
        Label musyTitle = new Label("2. Akad Musyarakah (Penyertaan Modal Kemitraan Bersama)");
        musyTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #c89228;");
        Label musyDesc = new Label(
                "• Prinsip: Bank dan Nasabah sama-sama menyertakan modal (porsi modal bersama) untuk membiayai usaha tertentu.\n"
                        + "• Pembagian Hasil: Laba dibagi berdasarkan nisbah yang disepakati para pihak.\n"
                        + "• Risiko Kerugian: Kerugian ditanggung secara proporsional sesuai porsi modal yang disetorkan masing-masing pihak.\n"
                        + "• Landasan Syariah: Fatwa DSN-MUI No. 08/DSN-MUI/IV/2000.");
        musyDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: #334155;");
        musyDesc.setWrapText(true);
        musyCard.getChildren().addAll(musyTitle, musyDesc);

        content.getChildren().addAll(header, mudhCard, musyCard);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    /*
     * =========================================================================
     * CUSTOM TABLE CELL FOR STATUS PILL BADGE
     * =========================================================================
     */

    private class StatusPillTableCell extends TableCell<CaseRecord, CaseRecord> {
        private final Label pill = new Label();

        @Override
        protected void updateItem(CaseRecord item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                pill.getStyleClass().setAll("status-pill");
                String text;
                AkadPembiayaan akad = item.pengajuan.getAkad();
                StatusPengajuan status = item.pengajuan.getStatus();

                if (akad != null && akad.getStatus() == StatusAkad.DICAIRKAN) {
                    pill.getStyleClass().add("status-dicairkan");
                    text = "Dicairkan";
                } else if (akad != null && akad.getStatus() == StatusAkad.MENUNGGU_PENCAIRAN) {
                    pill.getStyleClass().add("status-menunggu-pencairan");
                    text = "Menunggu Cair";
                } else if (akad != null && akad.getStatus() == StatusAkad.DRAFT) {
                    pill.getStyleClass().add("status-draft");
                    text = "Draft Akad";
                } else if (status == StatusPengajuan.DISETUJUI) {
                    pill.getStyleClass().add("status-disetujui");
                    text = "Disetujui";
                } else if (status == StatusPengajuan.DIPROSES) {
                    pill.getStyleClass().add("status-diproses");
                    text = "Diproses";
                } else if (status == StatusPengajuan.DITOLAK) {
                    pill.getStyleClass().add("status-ditolak");
                    text = "Ditolak";
                } else {
                    pill.getStyleClass().add("status-diajukan");
                    text = "Diajukan";
                }

                pill.setText(text);
                setGraphic(pill);
            }
        }
    }

    /*
     * =========================================================================
     * ACTIONS & DIALOG FORMS (PRESERVING ALL VALIDATIONS & LOGIC)
     * =========================================================================
     */

    @FXML
    private void createApplication() {
        Form form = new Form("Pengajuan Pembiayaan Baru", "/views/application-form.fxml");
        TextField customerId = form.text("customerId");
        TextField customerName = form.text("customerName");
        TextField address = form.text("address");
        DatePicker birthDate = form.date("birthDate", null);
        TextField occupation = form.text("occupation");
        TextField monthlySalary = form.money("monthlySalary", "");
        TextField businessName = form.text("businessName");
        TextField sector = form.text("sector");
        TextField businessAddress = form.text("businessAddress");
        TextField accountNumber = form.text("accountNumber");
        TextField startingBalance = form.money("startingBalance", "");
        TextField revenue = form.money("revenue", "");
        TextField directCosts = form.money("directCosts", "");
        TextField operatingCosts = form.money("operatingCosts", "");
        TextField obligations = form.money("obligations", "");
        TextField amount = form.money("amount", "");
        TextField tenor = form.number("tenor", "");
        TextField purpose = form.text("purpose");
        ChoiceBox<JenisAkad> akad = form.choice("akad", JenisAkad.values(), null, BankSyariahController::label);
        TextField bankShare = form.number("bankShare", "");
        TextField customerShare = form.number("customerShare", "");
        TextField customerCapital = form.money("customerCapital", "0");

        // Jika ada activeUser yang sedang login, kunci dan isi otomatis data nasabah!
        if (activeUser != null) {
            customerId.setText(activeUser.getNasabah().getIdPihak());
            customerId.setDisable(true);
            customerName.setText(activeUser.getNasabah().getNamaPihak());
            customerName.setDisable(true);
            address.setText(activeUser.getNasabah().getAlamatPihak());
            try {
                birthDate.setValue(LocalDate.parse(activeUser.getNasabah().getTanggalLahirPihak()));
            } catch (Exception ignore) {
                birthDate.setValue(LocalDate.now().minusYears(25));
            }
            occupation.setText(activeUser.getNasabah().getPekerjaanPihak());
            monthlySalary.setText(formatNominalInput(Long.toString(activeUser.getNasabah().getGajiBulanan())));
            accountNumber.setText(activeUser.getRekening().getNomorRekening());
            accountNumber.setDisable(true);
            startingBalance.setText(formatNominalInput(Long.toString(activeUser.getRekening().getSaldo())));
            startingBalance.setDisable(true);
        }

        akad.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, value) -> {
            boolean musyarakah = value == JenisAkad.MUSYARAKAH;
            customerCapital.setDisable(!musyarakah);
            if (musyarakah && oldValue != JenisAkad.MUSYARAKAH) {
                customerCapital.clear();
            } else if (!musyarakah) {
                customerCapital.setText("0");
            }
        });
        customerCapital.setDisable(true);

        if (!form.show(() -> {
            required(customerId);
            required(customerName);
            required(address);
            if (!dateValue(birthDate).isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Tanggal lahir harus sebelum hari ini.");
            }
            required(occupation);
            parseAmount(monthlySalary);
            required(businessName);
            required(sector);
            required(businessAddress);
            required(accountNumber);
            parseAmount(startingBalance);
            parseAmount(revenue);
            parseAmount(directCosts);
            parseAmount(operatingCosts);
            parseAmount(obligations);
            parsePositiveAmount(amount);
            parsePositiveInt(tenor);
            required(purpose);
            if (akad.getValue() == null) {
                throw new IllegalArgumentException("Pilih jenis akad pembiayaan.");
            }
            int bankNisbah = parseNonNegativeInt(bankShare);
            int customerNisbah = parseNonNegativeInt(customerShare);
            if ((long) bankNisbah + customerNisbah != 100) {
                throw new IllegalArgumentException("Total nisbah bank dan nasabah harus 100%.");
            }
            long capital = parseAmount(customerCapital);
            if (akad.getValue() == JenisAkad.MUSYARAKAH && capital == 0) {
                throw new IllegalArgumentException("Modal nasabah harus lebih besar dari 0 untuk Musyarakah.");
            }
        })) {
            return;
        }

        try {
            String id = nextId("P-", records.size() + 1);
            Nasabah nasabah = activeUser != null ? activeUser.getNasabah()
                    : new Nasabah(
                            required(customerId), required(customerName), required(address),
                            dateValue(birthDate).toString(), required(occupation),
                            parseAmount(monthlySalary));
            RekeningNasabah rekening = activeUser != null ? activeUser.getRekening()
                    : new RekeningNasabah(
                            required(accountNumber), nasabah, parseAmount(startingBalance));

            Usaha usaha = new Usaha(nextId("U-", records.size() + 1),
                    required(businessName), required(sector), required(businessAddress), nasabah);

            nasabah.tambahUsaha(usaha);
            if (activeUser == null) {
                nasabah.tambahRekening(rekening);
            }

            PengajuanPembiayaan pengajuan = new PengajuanPembiayaan(
                    id, nasabah, usaha, parsePositiveAmount(amount), parsePositiveInt(tenor),
                    required(purpose), LocalDate.now().toString(), akad.getValue(),
                    parseNonNegativeInt(bankShare), parseNonNegativeInt(customerShare),
                    parseAmount(customerCapital), parseAmount(revenue), parseAmount(directCosts),
                    parseAmount(operatingCosts), parseAmount(obligations));
            nasabah.ajukanPembiayaan(pengajuan);
            records.add(new CaseRecord(nasabah, usaha, rekening, pengajuan));
            refreshTables();
            customerTable.getSelectionModel().select(records.get(records.size() - 1));
            showSuccess("Pengajuan pembiayaan berhasil dibuat dan diteruskan ke analis bank.");
        } catch (IllegalArgumentException | ArithmeticException error) {
            showError(error.getMessage());
        }
    }

    private void recordAnalysis(CaseRecord record) {
        Form form = new Form("Pemeriksaan Data Keuangan Usaha", "/views/financial-review-form.fxml");
        TextField revenue = form.money("revenue", Long.toString(record.pengajuan.getOmzetBulananDilaporkan()));
        TextField directCost = form.money("directCost",
                Long.toString(record.pengajuan.getBiayaLangsungBulananDilaporkan()));
        TextField operatingCost = form.money("operatingCost",
                Long.toString(record.pengajuan.getBiayaOperasionalBulananDilaporkan()));
        TextField obligations = form.money("obligations",
                Long.toString(record.pengajuan.getKewajibanUsahaBulananDilaporkan()));
        Label cashFlowPreview = form.control("cashFlowPreview", Label.class);

        updateCashFlowPreview(cashFlowPreview, revenue, directCost, operatingCost, obligations);
        revenue.textProperty().addListener((obs, oldVal, newVal) -> updateCashFlowPreview(cashFlowPreview, revenue,
                directCost, operatingCost, obligations));
        directCost.textProperty().addListener((obs, oldVal, newVal) -> updateCashFlowPreview(cashFlowPreview, revenue,
                directCost, operatingCost, obligations));
        operatingCost.textProperty().addListener((obs, oldVal, newVal) -> updateCashFlowPreview(cashFlowPreview,
                revenue, directCost, operatingCost, obligations));
        obligations.textProperty().addListener((obs, oldVal, newVal) -> updateCashFlowPreview(cashFlowPreview, revenue,
                directCost, operatingCost, obligations));

        TextArea risk = form.control("risk", TextArea.class);
        CheckBox verified = form.control("verified", CheckBox.class);

        if (!form.show(() -> {
            if (!verified.isSelected()) {
                throw new IllegalArgumentException("Konfirmasi pemeriksaan data nasabah terlebih dahulu.");
            }
            String riskNote = required(risk);
            long checkedRevenue = parseAmount(revenue);
            long checkedDirectCost = parseAmount(directCost);
            long checkedOperatingCost = parseAmount(operatingCost);
            long checkedObligations = parseAmount(obligations);
            AnalisisKelayakan checkedAnalysis = new AnalisisKelayakan(
                    "A-VALIDASI", LocalDate.now().toString(), record.pengajuan, pegawai,
                    checkedRevenue, checkedDirectCost, checkedOperatingCost,
                    checkedObligations, riskNote);
            checkedAnalysis.hitungArusKasTersediaBulanan();
        })) {
            return;
        }

        try {
            String now = LocalDateTime.now().format(DATE_TIME);
            AnalisisKelayakan analysis = new AnalisisKelayakan(
                    nextId("A-", records.size() + 1), LocalDate.now().toString(),
                    record.pengajuan, pegawai, parseAmount(revenue), parseAmount(directCost),
                    parseAmount(operatingCost), parseAmount(obligations), required(risk));
            analysis.hitungArusKasTersediaBulanan();
            record.pengajuan.mulaiAnalisis(pegawai, now);
            record.pengajuan.catatAnalisis(analysis);
            refreshTables();
            renderEmployeeDetails(record);
            showSuccess("Analisis kelayakan berhasil dicatat. Status pengajuan kini menunggu keputusan komite.");
        } catch (IllegalArgumentException | IllegalStateException | ArithmeticException error) {
            showError(error.getMessage());
        }
    }

    private void decide(CaseRecord record) {
        Form form = new Form("Keputusan Komite Pembiayaan", "/views/decision-form.fxml");
        Label profile = form.control("profile", Label.class);
        profile.setText(customerAssessment(record));
        Label assessmentLimits = form.control("assessmentLimits", Label.class);
        assessmentLimits.setText(
                "Gaji dan data usaha berasal dari laporan nasabah yang telah diverifikasi analis. "
                        + "Pastikan analisis arus kas memadai sebelum menyetujui.");
        String analysisText = "Arus kas tersedia hasil pemeriksaan: "
                + rupiah(record.pengajuan.getAnalisisKelayakan().hitungArusKasTersediaBulanan())
                + ". Rekomendasi: "
                + label(record.pengajuan.getAnalisisKelayakan().getRekomendasiInternal());
        form.control("analysisSummary", Label.class).setText(analysisText);

        ChoiceBox<StatusKeputusan> decision = form.choice("decision", StatusKeputusan.values(), null,
                BankSyariahController::label);
        Label approvedAmount = form.control("approvedAmount", Label.class);
        approvedAmount.setText("Pilih keputusan pegawai.");

        decision.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, value) -> approvedAmount.setText(
                value == null ? "Pilih keputusan pegawai."
                        : value == StatusKeputusan.DISETUJUI
                                ? rupiah(record.pengajuan.getNominal()) + " (mengikuti nominal pengajuan)"
                                : rupiah(0) + " (tidak ada pencairan)"));

        TextField reason = form.text("reason");

        if (!form.show(() -> {
            if (decision.getValue() == null) {
                throw new IllegalArgumentException("Pilih keputusan pegawai.");
            }
            new KeputusanPembiayaan(
                    "K-VALIDASI", LocalDateTime.now().format(DATE_TIME), record.pengajuan,
                    pegawai, decision.getValue(),
                    jumlahDisetujui(decision.getValue(), record.pengajuan), required(reason));
        })) {
            return;
        }

        try {
            KeputusanPembiayaan result = new KeputusanPembiayaan(
                    nextId("K-", records.size() + 1), LocalDateTime.now().format(DATE_TIME),
                    record.pengajuan, pegawai, decision.getValue(),
                    jumlahDisetujui(decision.getValue(), record.pengajuan), required(reason));
            record.pengajuan.catatKeputusan(result);
            record.keputusan = result;
            refreshTables();
            renderEmployeeDetails(record);
            showSuccess("Keputusan komite berhasil dicatat.");
        } catch (IllegalArgumentException | IllegalStateException error) {
            showError(error.getMessage());
        }
    }

    private void createContract(CaseRecord record) {
        Form form = new Form("Penerbitan Draft Akad Pembiayaan", "/views/contract-form.fxml");
        LocalDate dueDate = LocalDate.parse(record.pengajuan.getTanggalPengajuan())
                .plusMonths(record.pengajuan.getTenorBulan());
        form.control("dueDate", Label.class).setText(dueDate.toString());

        if (!form.show(() -> {
            if (!dueDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "Tanggal jatuh tempo sesuai tenor sudah lewat; periksa tanggal pengajuan.");
            }
        })) {
            return;
        }

        try {
            record.pengajuan.buatAkad(nextId("AK-", records.size() + 1), record.keputusan, dueDate.toString());
            refreshTables();
            renderEmployeeDetails(record);
            showSuccess("Draft akad pembiayaan berhasil diterbitkan. Silakan lanjutkan ke tanda tangan akad.");
        } catch (IllegalArgumentException | IllegalStateException error) {
            showError(error.getMessage());
        }
    }

    private void signContract(CaseRecord record) {
        Form form = new Form("Tanda Tangani Akad Digital", "/views/signature-form.fxml");
        DatePicker signatureDate = form.date("signatureDate", LocalDate.now());

        if (!form.show(() -> {
            LocalDate signed = dateValue(signatureDate);
            LocalDate approved = LocalDateTime.parse(record.keputusan.getTanggalKeputusan(), DATE_TIME).toLocalDate();
            LocalDate due = LocalDate.parse(record.pengajuan.getAkad().getTanggalJatuhTempo());
            if (signed.isBefore(approved)) {
                throw new IllegalArgumentException("Tanggal tanda tangan tidak boleh sebelum keputusan pegawai.");
            }
            if (!due.isAfter(signed)) {
                throw new IllegalArgumentException("Tanggal tanda tangan harus sebelum tanggal jatuh tempo.");
            }
        })) {
            return;
        }

        try {
            record.pengajuan.getAkad().tandatangani(dateValue(signatureDate).toString());
            refreshTables();
            renderEmployeeDetails(record);
            showSuccess("Akad berhasil ditandatangani oleh kedua pihak. Pengajuan siap dicairkan.");
        } catch (IllegalArgumentException | IllegalStateException error) {
            showError(error.getMessage());
        }
    }

    private void disburse(CaseRecord record) {
        Form form = new Form("Pencairan Dana Pembiayaan ke Rekening", "/views/disbursement-form.fxml");
        form.control("amount", Label.class).setText(rupiah(record.pengajuan.getAkad().getJumlahDisetujui()));
        form.control("account", Label.class)
                .setText(record.rekening.getNomorRekening() + " — " + record.nasabah.getNamaPihak());
        DatePicker date = form.date("date", LocalDate.now());
        TextField reference = form.text("reference");

        if (!form.show(() -> {
            LocalDate disbursementDate = dateValue(date);
            LocalDate signatureDate = LocalDate.parse(record.pengajuan.getAkad().getTanggalTandaTangan());
            if (disbursementDate.isBefore(signatureDate)) {
                throw new IllegalArgumentException("Tanggal pencairan tidak boleh sebelum tanda tangan akad.");
            }
            required(reference);
        })) {
            return;
        }

        try {
            long saldoSebelum = record.rekening.getSaldo();
            Pencairan pencairan = record.pengajuan.getAkad().cairkan(
                    nextId("PC-", records.size() + 1), dateValue(date).toString(),
                    required(reference), record.rekening);
            long saldoTerbaru = record.rekening.getSaldo();
            refreshTables();
            renderEmployeeDetails(record);
            showSuccess("Alhamdulillah! Dana pembiayaan sebesar " + rupiah(pencairan.getJumlah())
                    + " berhasil dicairkan ke rekening " + record.rekening.getNomorRekening()
                    + ".\nSaldo bertambah dari " + rupiah(saldoSebelum) + " menjadi " + rupiah(saldoTerbaru) + ".");
        } catch (IllegalArgumentException | IllegalStateException | ArithmeticException error) {
            showError(error.getMessage());
        }
    }

    private String customerAssessment(CaseRecord record) {
        AnalisisKelayakan analysis = record.pengajuan.getAnalisisKelayakan();
        StringBuilder assessment = new StringBuilder()
                .append("Gaji nasabah dilaporkan: ").append(rupiah(record.nasabah.getGajiBulanan()))
                .append("/bulan.\n");
        if (analysis == null) {
            assessment.append("Arus kas usaha: belum diperiksa pegawai.");
        } else {
            assessment.append("Arus kas usaha setelah pemeriksaan: ")
                    .append(rupiah(analysis.hitungArusKasTersediaBulanan()))
                    .append("/bulan (").append(label(analysis.getRekomendasiInternal())).append(").");
        }
        return assessment.toString();
    }

    private void refreshTables() {
        CaseRecord employeeSelection = employeeTable.getSelectionModel().getSelectedItem();
        CaseRecord customerSelection = customerTable.getSelectionModel().getSelectedItem();
        tableRows.setAll(records);
        employeeTable.refresh();
        customerTable.refresh();
        updateCustomerMetrics();
        updateEmployeeMetrics();
        if (employeeSelection != null) {
            employeeTable.getSelectionModel().select(employeeSelection);
            renderEmployeeDetails(employeeSelection);
        }
        if (customerSelection != null) {
            customerTable.getSelectionModel().select(customerSelection);
            renderCustomerDetails(customerSelection);
        }
    }

    private static String required(TextInputControl input) {
        String value = input.getText() == null ? "" : input.getText().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Semua kolom wajib diisi.");
        }
        return value;
    }

    private static LocalDate dateValue(DatePicker input) {
        if (input.getValue() == null) {
            throw new IllegalArgumentException("Tanggal wajib dipilih.");
        }
        return input.getValue();
    }

    private void updateCashFlowPreview(
            Label preview,
            TextField revenue,
            TextField directCosts,
            TextField operatingCosts,
            TextField obligations) {
        try {
            long available = AnalisisKelayakan.hitungArusKasTersediaBulanan(
                    parseAmount(revenue), parseAmount(directCosts),
                    parseAmount(operatingCosts), parseAmount(obligations));
            AnalisisKelayakan.Rekomendasi recommendation = AnalisisKelayakan.rekomendasiUntukArusKas(available);
            preview.setText(rupiah(available) + " · " + label(recommendation));
        } catch (IllegalArgumentException | ArithmeticException error) {
            preview.setText("Lengkapi semua angka untuk melihat perhitungan.");
        }
    }

    private static long parseAmount(TextInputControl input) {
        try {
            long value = Long.parseLong(required(input).replace(".", ""));
            if (value < 0) {
                throw new IllegalArgumentException("Nominal tidak boleh negatif.");
            }
            return value;
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Masukkan nominal berupa angka bulat yang valid.");
        }
    }

    private static long jumlahDisetujui(StatusKeputusan statusKeputusan, PengajuanPembiayaan pengajuan) {
        return statusKeputusan == StatusKeputusan.DISETUJUI ? pengajuan.getNominal() : 0L;
    }

    private static String formatNominalInput(String digits) {
        StringBuilder formatted = new StringBuilder(digits.length() + digits.length() / 3);
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && (digits.length() - i) % 3 == 0) {
                formatted.append('.');
            }
            formatted.append(digits.charAt(i));
        }
        return formatted.toString();
    }

    private static int caretAfterDigits(String text, int digitCount) {
        if (digitCount == 0) {
            return 0;
        }
        int digitsSeen = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i)) && ++digitsSeen == digitCount) {
                return i + 1;
            }
        }
        return text.length();
    }

    private static long parsePositiveAmount(TextInputControl input) {
        long value = parseAmount(input);
        if (value == 0) {
            throw new IllegalArgumentException("Nominal harus lebih besar dari 0.");
        }
        return value;
    }

    private static int parsePositiveInt(TextInputControl input) {
        int value = parseNonNegativeInt(input);
        if (value == 0) {
            throw new IllegalArgumentException("Nilai harus lebih besar dari 0.");
        }
        return value;
    }

    private static int parseNonNegativeInt(TextInputControl input) {
        try {
            int value = Integer.parseInt(required(input));
            if (value < 0) {
                throw new IllegalArgumentException("Nilai tidak boleh negatif.");
            }
            return value;
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Masukkan angka bulat yang valid.");
        }
    }

    private static String nextId(String prefix, int sequence) {
        return prefix + String.format(Locale.ROOT, "%03d", sequence);
    }

    private static String label(Enum<?> value) {
        String lower = value.name().toLowerCase(Locale.ROOT).replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private static String rupiah(long amount) {
        return RUPIAH.format(amount);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Peringatan Sistem");
        alert.setHeaderText("Periksa Kembali Data Anda");
        alert.setContentText(message == null ? "Terjadi kesalahan yang tidak diketahui." : message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operasi Berhasil");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /*
     * =========================================================================
     * USER PROFILE, CASE RECORD & FORM DIALOG WRAPPERS
     * =========================================================================
     */

    public static final class UserProfile {
        private final Nasabah nasabah;
        private final RekeningNasabah rekening;

        public UserProfile(Nasabah nasabah, RekeningNasabah rekening) {
            this.nasabah = nasabah;
            this.rekening = rekening;
        }

        public Nasabah getNasabah() {
            return nasabah;
        }

        public RekeningNasabah getRekening() {
            return rekening;
        }

        @Override
        public String toString() {
            return nasabah.getNamaPihak() + " (" + nasabah.getIdPihak() + " - " + nasabah.getPekerjaanPihak() + ")";
        }
    }

    private static final class CaseRecord {
        private final Nasabah nasabah;
        private final Usaha usaha;
        private final RekeningNasabah rekening;
        private final PengajuanPembiayaan pengajuan;
        private KeputusanPembiayaan keputusan;

        private CaseRecord(Nasabah nasabah, Usaha usaha, RekeningNasabah rekening, PengajuanPembiayaan pengajuan) {
            this.nasabah = nasabah;
            this.usaha = usaha;
            this.rekening = rekening;
            this.pengajuan = pengajuan;
        }
    }

    private final class Form {
        private final Dialog<ButtonType> dialog = new Dialog<>();
        private final FXMLLoader loader;
        private final ButtonType submit = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);

        private Form(String title, String resource) {
            dialog.setTitle(title);
            dialog.getDialogPane().getButtonTypes().addAll(submit, ButtonType.CANCEL);
            dialog.setResizable(true);
            loader = new FXMLLoader(getClass().getResource(resource));
            ScrollPane scroll;
            try {
                scroll = loader.load();
            } catch (IOException error) {
                throw new IllegalStateException("Tidak dapat memuat formulir " + resource, error);
            }
            dialog.getDialogPane().setContent(scroll);
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            dialog.getDialogPane().setPrefWidth(560);
        }

        private <T> T control(String id, Class<T> type) {
            Object control = loader.getNamespace().get(id);
            if (!type.isInstance(control)) {
                throw new IllegalStateException(
                        "Kontrol '" + id + "' tidak tersedia atau tipenya salah di " + loader.getLocation());
            }
            return type.cast(control);
        }

        private TextField text(String id) {
            return control(id, TextField.class);
        }

        private TextField number(String id, String initialValue) {
            TextField field = text(id);
            field.setTextFormatter(
                    new TextFormatter<String>(change -> change.getControlNewText().matches("\\d*") ? change : null));
            field.setText(initialValue);
            return field;
        }

        private TextField money(String id, String initialValue) {
            TextField field = text(id);
            field.setTextFormatter(new TextFormatter<String>(change -> {
                String candidate = change.getControlNewText();
                if (!candidate.matches("[\\d.]*")) {
                    return null;
                }
                int caret = Math.min(change.getCaretPosition(), candidate.length());
                int digitsBeforeCaret = (int) candidate.substring(0, caret)
                        .chars()
                        .filter(Character::isDigit)
                        .count();
                String formatted = formatNominalInput(candidate.replace(".", ""));
                int newCaret = caretAfterDigits(formatted, digitsBeforeCaret);
                change.setRange(0, change.getControlText().length());
                change.setText(formatted);
                change.selectRange(newCaret, newCaret);
                return change;
            }));
            field.setText(formatNominalInput(initialValue.replace(".", "")));
            return field;
        }

        private DatePicker date(String id, LocalDate value) {
            DatePicker picker = control(id, DatePicker.class);
            picker.setValue(value);
            picker.setConverter(new StringConverter<>() {
                @Override
                public String toString(LocalDate date) {
                    return date == null ? "" : date.toString();
                }

                @Override
                public LocalDate fromString(String text) {
                    return text == null || text.isBlank() ? null : LocalDate.parse(text);
                }
            });
            picker.setMaxWidth(Double.MAX_VALUE);
            return picker;
        }

        private <T> ChoiceBox<T> choice(String id, T[] options, T initial, Function<T, String> converter) {
            Object configuredControl = loader.getNamespace().get(id);
            if (!(configuredControl instanceof ChoiceBox<?> untypedChoice)) {
                throw new IllegalStateException("Kontrol '" + id + "' bukan ChoiceBox di " + loader.getLocation());
            }
            @SuppressWarnings("unchecked")
            ChoiceBox<T> choice = (ChoiceBox<T>) untypedChoice;
            choice.setItems(FXCollections.observableArrayList(options));
            choice.setConverter(new StringConverter<>() {
                @Override
                public String toString(T value) {
                    return value == null ? "" : converter.apply(value);
                }

                @Override
                public T fromString(String text) {
                    return initial;
                }
            });
            choice.setValue(initial);
            choice.setMaxWidth(Double.MAX_VALUE);
            return choice;
        }

        private boolean show(Runnable validate) {
            Node submitButton = dialog.getDialogPane().lookupButton(submit);
            submitButton.addEventFilter(ActionEvent.ACTION, event -> {
                try {
                    validate.run();
                } catch (IllegalArgumentException | ArithmeticException error) {
                    event.consume();
                    showError(error.getMessage());
                }
            });
            return dialog.showAndWait().filter(result -> result.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                    .isPresent();
        }
    }
}
