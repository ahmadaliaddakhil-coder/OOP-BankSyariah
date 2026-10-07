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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
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

public class BankSyariahController {
    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final NumberFormat RUPIAH =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    private final BankSyariah bank =
            new BankSyariah("B-001", "Bank Syariah Berkah");
    private final PegawaiBank pegawai =
            new PegawaiBank("PG-001", "Ahmad Fauzi", "Analis Pembiayaan", bank);
    private final List<CaseRecord> records = new ArrayList<>();
    private final ObservableList<CaseRecord> tableRows = FXCollections.observableArrayList();
    private final FilteredList<CaseRecord> employeeRows =
            new FilteredList<>(tableRows, item -> true);
    private final FilteredList<CaseRecord> customerRows =
            new FilteredList<>(tableRows, item -> true);
    private final SortedList<CaseRecord> sortedEmployeeRows = new SortedList<>(employeeRows);
    private final SortedList<CaseRecord> sortedCustomerRows = new SortedList<>(customerRows);
    @FXML private VBox customerView;
    @FXML private VBox employeeView;
    @FXML private Button customerNavButton;
    @FXML private Button employeeNavButton;
    @FXML private TableView<CaseRecord> employeeTable;
    @FXML private TableView<CaseRecord> customerTable;
    @FXML private TableColumn<CaseRecord, String> employeeIdColumn;
    @FXML private TableColumn<CaseRecord, String> employeeCustomerColumn;
    @FXML private TableColumn<CaseRecord, String> employeeBusinessColumn;
    @FXML private TableColumn<CaseRecord, String> employeeAmountColumn;
    @FXML private TableColumn<CaseRecord, String> employeeStatusColumn;
    @FXML private TableColumn<CaseRecord, String> customerIdColumn;
    @FXML private TableColumn<CaseRecord, String> customerAkadColumn;
    @FXML private TableColumn<CaseRecord, String> customerAmountColumn;
    @FXML private TableColumn<CaseRecord, String> customerStatusColumn;
    @FXML private TextField search;
    @FXML private Label employeeIdentity;
    @FXML private Label selectedTitle;
    @FXML private TextArea selectedContent;
    @FXML private VBox selectedActions;
    @FXML private TextArea customerDetails;

    @FXML
    private void initialize() {
        bank.tambahPegawai(pegawai);
        configureTable();
        updateNavigation(customerNavButton, employeeNavButton);
    }

    @FXML
    private void showCustomerView() {
        customerView.setVisible(true);
        customerView.setManaged(true);
        employeeView.setVisible(false);
        employeeView.setManaged(false);
        updateNavigation(customerNavButton, employeeNavButton);
    }

    @FXML
    private void showEmployeeView() {
        employeeIdentity.setText(
                "Pegawai demo: " + pegawai.getNama() + " — " + pegawai.getJabatan());
        customerView.setVisible(false);
        customerView.setManaged(false);
        employeeView.setVisible(true);
        employeeView.setManaged(true);
        updateNavigation(employeeNavButton, customerNavButton);
    }

    private void updateNavigation(Button active, Button inactive) {
        if (!active.getStyleClass().contains("nav-active")) {
            active.getStyleClass().add("nav-active");
        }
        inactive.getStyleClass().remove("nav-active");
    }

    private void configureTable() {
        employeeIdColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().pengajuan.getIdPengajuan()));
        employeeCustomerColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().nasabah.getNamaPihak()));
        employeeBusinessColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().usaha.getNamaUsaha()));
        employeeAmountColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                rupiah(cell.getValue().pengajuan.getNominal())));
        employeeStatusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                label(cell.getValue().pengajuan.getStatus())));
        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        employeeTable.setPlaceholder(new Label("Belum ada pengajuan."));
        sortedEmployeeRows.comparatorProperty().bind(employeeTable.comparatorProperty());
        employeeTable.setItems(sortedEmployeeRows);
        employeeTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selected) -> showDetails(selected));
        search.textProperty().addListener((observable, oldValue, newValue) ->
                employeeRows.setPredicate(this::matchesSearch));

        customerIdColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().pengajuan.getIdPengajuan()));
        customerAkadColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                label(cell.getValue().pengajuan.getJenisAkad())));
        customerAmountColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                rupiah(cell.getValue().pengajuan.getNominal())));
        customerStatusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                label(cell.getValue().pengajuan.getStatus())));
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        sortedCustomerRows.comparatorProperty().bind(customerTable.comparatorProperty());
        customerTable.setItems(sortedCustomerRows);
        customerTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selected) -> showCustomerDetails(selected));
    }

    private boolean matchesSearch(CaseRecord item) {
        String query = search.getText() == null ? "" : search.getText().trim().toLowerCase(Locale.ROOT);
        return query.isEmpty()
                || item.pengajuan.getIdPengajuan().toLowerCase(Locale.ROOT).contains(query)
                || item.nasabah.getNamaPihak().toLowerCase(Locale.ROOT).contains(query)
                || item.usaha.getNamaUsaha().toLowerCase(Locale.ROOT).contains(query);
    }

    @FXML
    private void createApplication() {
        Form form = new Form("Pengajuan Pembiayaan Baru", "/views/application-form.fxml");
        TextField customerId = form.text("customerId");
        TextField customerName = form.text("customerName");
        TextField address = form.text("address");
        DatePicker birthDate = form.date("birthDate", null);
        TextField occupation = form.text("occupation");
        TextField monthlySalary = form.number("monthlySalary", "");
        TextField businessName = form.text("businessName");
        TextField sector = form.text("sector");
        TextField businessAddress = form.text("businessAddress");
        TextField accountNumber = form.text("accountNumber");
        TextField startingBalance = form.number("startingBalance", "");
        TextField revenue = form.number("revenue", "");
        TextField directCosts = form.number("directCosts", "");
        TextField operatingCosts = form.number("operatingCosts", "");
        TextField obligations = form.number("obligations", "");
        TextField amount = form.number("amount", "");
        TextField tenor = form.number("tenor", "");
        TextField purpose = form.text("purpose");
        ChoiceBox<JenisAkad> akad = form.choice(
                "akad", JenisAkad.values(), null, BankSyariahController::label);
        TextField bankShare = form.number("bankShare", "");
        TextField customerShare = form.number("customerShare", "");
        TextField customerCapital = form.number("customerCapital", "0");
        akad.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, value) -> {
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
                throw new IllegalArgumentException(
                        "Modal nasabah harus lebih besar dari 0 untuk Musyarakah.");
            }
        })) {
            return;
        }
        try {
            String id = nextId("P-", records.size() + 1);
            Nasabah nasabah = new Nasabah(
                    required(customerId), required(customerName), required(address),
                    dateValue(birthDate).toString(), required(occupation),
                    parseAmount(monthlySalary));
            Usaha usaha = new Usaha(nextId("U-", records.size() + 1),
                    required(businessName), required(sector), required(businessAddress), nasabah);
            RekeningNasabah rekening = new RekeningNasabah(
                    required(accountNumber), nasabah, parseAmount(startingBalance));
            nasabah.tambahUsaha(usaha);
            nasabah.tambahRekening(rekening);
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
            showSuccess("Pengajuan berhasil dibuat.");
        } catch (IllegalArgumentException | ArithmeticException error) {
            showError(error.getMessage());
        }
    }

    private void recordAnalysis(CaseRecord record) {
        Form form = new Form("Pemeriksaan Data Keuangan", "/views/financial-review-form.fxml");
        TextField revenue = form.number("revenue",
                Long.toString(record.pengajuan.getOmzetBulananDilaporkan()));
        TextField directCost = form.number("directCost",
                Long.toString(record.pengajuan.getBiayaLangsungBulananDilaporkan()));
        TextField operatingCost = form.number("operatingCost",
                Long.toString(record.pengajuan.getBiayaOperasionalBulananDilaporkan()));
        TextField obligations = form.number("obligations",
                Long.toString(record.pengajuan.getKewajibanUsahaBulananDilaporkan()));
        Label cashFlowPreview = form.control("cashFlowPreview", Label.class);
        updateCashFlowPreview(cashFlowPreview, revenue, directCost, operatingCost, obligations);
        revenue.textProperty().addListener((observable, oldValue, newValue) ->
                updateCashFlowPreview(cashFlowPreview, revenue, directCost, operatingCost, obligations));
        directCost.textProperty().addListener((observable, oldValue, newValue) ->
                updateCashFlowPreview(cashFlowPreview, revenue, directCost, operatingCost, obligations));
        operatingCost.textProperty().addListener((observable, oldValue, newValue) ->
                updateCashFlowPreview(cashFlowPreview, revenue, directCost, operatingCost, obligations));
        obligations.textProperty().addListener((observable, oldValue, newValue) ->
                updateCashFlowPreview(cashFlowPreview, revenue, directCost, operatingCost, obligations));
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
            showDetails(record);
            showSuccess("Pemeriksaan dicatat. Pengajuan sekarang menunggu keputusan pegawai.");
        } catch (IllegalArgumentException | IllegalStateException | ArithmeticException error) {
            showError(error.getMessage());
        }
    }

    private void decide(CaseRecord record) {
        Form form = new Form("Keputusan Pembiayaan", "/views/decision-form.fxml");
        Label profile = form.control("profile", Label.class);
        profile.setText(customerAssessment(record));
        Label assessmentLimits = form.control("assessmentLimits", Label.class);
        assessmentLimits.setText(
                "Gaji dan data usaha berasal dari laporan nasabah. Belum ada bukti dokumen, "
                        + "riwayat kredit, pengeluaran rumah tangga, atau jadwal pembayaran; "
                        + "kredibilitas dan kemampuan bayar belum dapat dipastikan.");
        String analysisText = "Arus kas tersedia hasil pemeriksaan: "
                        + rupiah(record.pengajuan.getAnalisisKelayakan()
                                .hitungArusKasTersediaBulanan())
                        + ". Rekomendasi: "
                        + label(record.pengajuan.getAnalisisKelayakan().getRekomendasiInternal())
                        + " (bukan keputusan otomatis).";
        form.control("analysisSummary", Label.class).setText(analysisText);
        ChoiceBox<StatusKeputusan> decision = form.choice(
                "decision", StatusKeputusan.values(), null, BankSyariahController::label);
        TextField approvedAmount = form.number("approvedAmount", "");
        approvedAmount.setDisable(true);
        decision.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, value) -> {
                    approvedAmount.setDisable(value != StatusKeputusan.DISETUJUI);
                    if (value == StatusKeputusan.DITOLAK) {
                        approvedAmount.setText("0");
                    } else if (oldValue == StatusKeputusan.DITOLAK) {
                        approvedAmount.clear();
                    }
                });
        TextField reason = form.text("reason");
        if (!form.show(() -> {
            if (decision.getValue() == null) {
                throw new IllegalArgumentException("Pilih keputusan pegawai.");
            }
            new KeputusanPembiayaan(
                    "K-VALIDASI", LocalDateTime.now().format(DATE_TIME), record.pengajuan,
                    pegawai, decision.getValue(), parseAmount(approvedAmount), required(reason));
        })) {
            return;
        }
        try {
            KeputusanPembiayaan result = new KeputusanPembiayaan(
                    nextId("K-", records.size() + 1), LocalDateTime.now().format(DATE_TIME),
                    record.pengajuan, pegawai, decision.getValue(),
                    parseAmount(approvedAmount), required(reason));
            record.pengajuan.catatKeputusan(result);
            record.keputusan = result;
            refreshTables();
            showDetails(record);
            showSuccess("Keputusan berhasil dicatat.");
        } catch (IllegalArgumentException | IllegalStateException error) {
            showError(error.getMessage());
        }
    }

    private void createContract(CaseRecord record) {
        Form form = new Form("Buat Akad Pembiayaan", "/views/contract-form.fxml");
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
            record.pengajuan.buatAkad(
                    nextId("AK-", records.size() + 1), record.keputusan,
                    dueDate.toString());
            refreshTables();
            showDetails(record);
            showSuccess("Draft akad berhasil dibuat. Tanda tangan menjadi langkah berikutnya.");
        } catch (IllegalArgumentException | IllegalStateException error) {
            showError(error.getMessage());
        }
    }

    private void signContract(CaseRecord record) {
        Form form = new Form("Tanda Tangani Akad", "/views/signature-form.fxml");
        DatePicker signatureDate = form.date("signatureDate", LocalDate.now());
        if (!form.show(() -> {
            LocalDate signed = dateValue(signatureDate);
            LocalDate approved = LocalDateTime.parse(
                    record.keputusan.getTanggalKeputusan(), DATE_TIME).toLocalDate();
            LocalDate due = LocalDate.parse(
                    record.pengajuan.getAkad().getTanggalJatuhTempo());
            if (signed.isBefore(approved)) {
                throw new IllegalArgumentException(
                        "Tanggal tanda tangan tidak boleh sebelum keputusan pegawai.");
            }
            if (!due.isAfter(signed)) {
                throw new IllegalArgumentException(
                        "Tanggal tanda tangan harus sebelum tanggal jatuh tempo.");
            }
        })) {
            return;
        }
        try {
            record.pengajuan.getAkad().tandatangani(dateValue(signatureDate).toString());
            refreshTables();
            showDetails(record);
            showSuccess("Akad ditandatangani. Pengajuan sekarang menunggu pencairan.");
        } catch (IllegalArgumentException | IllegalStateException error) {
            showError(error.getMessage());
        }
    }

    private void disburse(CaseRecord record) {
        Form form = new Form("Pencairan Pembiayaan", "/views/disbursement-form.fxml");
        form.control("amount", Label.class).setText(
                rupiah(record.pengajuan.getAkad().getJumlahDisetujui()));
        form.control("account", Label.class).setText(record.rekening.getNomorRekening()
                + " — " + record.nasabah.getNamaPihak());
        DatePicker date = form.date("date", LocalDate.now());
        TextField reference = form.text("reference");
        if (!form.show(() -> {
            LocalDate disbursementDate = dateValue(date);
            LocalDate signatureDate = LocalDate.parse(
                    record.pengajuan.getAkad().getTanggalTandaTangan());
            if (disbursementDate.isBefore(signatureDate)) {
                throw new IllegalArgumentException(
                        "Tanggal pencairan tidak boleh sebelum tanda tangan akad.");
            }
            required(reference);
        })) {
            return;
        }
        try {
            Pencairan pencairan = record.pengajuan.getAkad().cairkan(
                    nextId("PC-", records.size() + 1), dateValue(date).toString(),
                    required(reference), record.rekening);
            refreshTables();
            showDetails(record);
            showSuccess("Dana berhasil dicairkan. " + rupiah(pencairan.getAkad().getJumlahDisetujui()));
        } catch (IllegalArgumentException | IllegalStateException | ArithmeticException error) {
            showError(error.getMessage());
        }
    }

    private void showDetails(CaseRecord record) {
        if (record == null) {
            selectedTitle.setText("Pilih pengajuan untuk melihat detail.");
            selectedContent.setText("");
            selectedActions.getChildren().clear();
            return;
        }
        PengajuanPembiayaan application = record.pengajuan;
        selectedTitle.setText(application.getIdPengajuan() + " · " + record.nasabah.getNamaPihak());
        StringBuilder details = new StringBuilder()
                .append("Profil nasabah\n")
                .append("ID: ").append(record.nasabah.getIdPihak())
                .append(" · Tanggal lahir: ").append(record.nasabah.getTanggalLahirPihak())
                .append("\nAlamat: ").append(record.nasabah.getAlamatPihak())
                .append("\nPekerjaan: ").append(record.nasabah.getPekerjaanPihak())
                .append(" · Gaji dilaporkan: ").append(rupiah(record.nasabah.getGajiBulanan()))
                .append("/bulan\n\n")
                .append("Usaha\n").append(record.usaha.getNamaUsaha()).append(" · ")
                .append(record.usaha.getSektor()).append("\n\n")
                .append("Rekening\n").append(record.rekening.getNomorRekening())
                .append(" · Saldo saat ini: ").append(rupiah(record.rekening.getSaldo()))
                .append("\n\n")
                .append("Keuangan dilaporkan nasabah (per bulan)\n")
                .append("Omzet: ").append(rupiah(application.getOmzetBulananDilaporkan()))
                .append(" · Biaya langsung: ")
                .append(rupiah(application.getBiayaLangsungBulananDilaporkan()))
                .append("\nBiaya operasional: ")
                .append(rupiah(application.getBiayaOperasionalBulananDilaporkan()))
                .append(" · Kewajiban: ")
                .append(rupiah(application.getKewajibanUsahaBulananDilaporkan())).append("\n\n")
                .append(customerAssessment(record)).append("\n\n")
                .append("Pembiayaan\n").append(rupiah(application.getNominal()))
                .append(" · ").append(application.getTenorBulan()).append(" bulan\n")
                .append(label(application.getJenisAkad())).append(" · Nisbah ")
                .append(application.getNisbahBank()).append("% bank / ")
                .append(application.getNisbahNasabah()).append("% nasabah\n")
                .append("Modal nasabah: ").append(rupiah(application.getModalNasabah()))
                .append("\n\nStatus\n").append(label(application.getStatus()));
        AnalisisKelayakan analysis = application.getAnalisisKelayakan();
        if (analysis != null) {
            details.append("\n\nData keuangan setelah pemeriksaan pegawai (per bulan)\n")
                    .append("Omzet: ").append(rupiah(analysis.getOmzetBulanan()))
                    .append(" · Biaya langsung: ").append(rupiah(analysis.getBiayaLangsungBulanan()))
                    .append("\nBiaya operasional: ")
                    .append(rupiah(analysis.getBiayaOperasionalBulanan()))
                    .append(" · Kewajiban: ")
                    .append(rupiah(analysis.getKewajibanUsahaBulanan()))
                    .append("\nArus kas tersedia: ")
                    .append(rupiah(analysis.hitungArusKasTersediaBulanan()))
                    .append("\nRekomendasi internal (bukan keputusan otomatis): ")
                    .append(label(analysis.getRekomendasiInternal()));
        }
        if (record.keputusan != null) {
            details.append("\n\nKeputusan\n").append(label(record.keputusan.getStatusKeputusan()))
                    .append(" · ").append(record.keputusan.getAlasan());
        }
        if (application.getAkad() != null) {
            details.append("\n\nAkad\n").append(application.getAkad().getNomorAkad())
                    .append(" · ").append(label(application.getAkad().getStatus()))
                    .append("\nJatuh tempo: ").append(application.getAkad().getTanggalJatuhTempo());
        }
        if (application.getAkad() != null && application.getAkad().getPencairan() != null) {
            details.append("\n\nRekening setelah pencairan\n")
                    .append(rupiah(record.rekening.getSaldo()));
        }
        details.append("\n\nRiwayat");
        RiwayatPengajuan history = application.getRiwayatPertama();
        while (history != null) {
            details.append("\n").append(history.getWaktu()).append(" · ")
                    .append(label(history.getStatusSesudahnya()));
            history = history.getBerikutnya();
        }
        selectedContent.setText(details.toString());

        List<Node> actions = new ArrayList<>();
        StatusPengajuan status = application.getStatus();
        if (status == StatusPengajuan.DIAJUKAN) {
            actions.add(actionButton("Periksa data keuangan", () -> recordAnalysis(record)));
        } else if (status == StatusPengajuan.DIPROSES
                && application.getAnalisisKelayakan() != null) {
            actions.add(actionButton("Tetapkan keputusan", () -> decide(record)));
        } else if (status == StatusPengajuan.DISETUJUI && application.getAkad() == null) {
            actions.add(actionButton("Buat draft akad", () -> createContract(record)));
        } else if (application.getAkad() != null
                && application.getAkad().getStatus() == StatusAkad.DRAFT) {
            actions.add(actionButton("Tanda tangani akad", () -> signContract(record)));
        } else if (application.getAkad() != null
                && application.getAkad().getPencairan() == null) {
            actions.add(actionButton("Proses pencairan", () -> disburse(record)));
        }
        if (actions.isEmpty()) {
            String message = status == StatusPengajuan.DITOLAK
                    ? "Pengajuan telah ditolak."
                    : status == StatusPengajuan.DIPROSES
                            ? "Status sedang diproses, tetapi catatan analisis belum tersedia."
                            : "Seluruh proses pengajuan telah selesai.";
            Label done = new Label(message);
            actions.add(done);
        }
        selectedActions.getChildren().setAll(actions);
    }

    private void showCustomerDetails(CaseRecord record) {
        if (record == null) {
            customerDetails.setText("Pilih pengajuan untuk melihat detail lengkapnya.");
            return;
        }
        StringBuilder details = new StringBuilder()
                .append("DATA NASABAH\n")
                .append("Nama: ").append(record.nasabah.getNamaPihak())
                .append(" · ID: ").append(record.nasabah.getIdPihak())
                .append("\nAlamat: ").append(record.nasabah.getAlamatPihak())
                .append("\nTanggal lahir: ").append(record.nasabah.getTanggalLahirPihak())
                .append("\nPekerjaan: ").append(record.nasabah.getPekerjaanPihak())
                .append(" · Gaji bulanan: ").append(rupiah(record.nasabah.getGajiBulanan()))
                .append("\nRekening: ").append(record.rekening.getNomorRekening())
                .append(" · Saldo: ").append(rupiah(record.rekening.getSaldo()))
                .append("\n\nDATA USAHA\n")
                .append("Nama: ").append(record.usaha.getNamaUsaha())
                .append(" · Sektor: ").append(record.usaha.getSektor())
                .append("\nAlamat: ").append(record.usaha.getAlamat())
                .append("\n\nDETAIL PENGAJUAN\n")
                .append("Nomor: ").append(record.pengajuan.getIdPengajuan())
                .append(" · Tanggal: ").append(record.pengajuan.getTanggalPengajuan())
                .append("\nStatus: ").append(label(record.pengajuan.getStatus()))
                .append("\nNominal: ").append(rupiah(record.pengajuan.getNominal()))
                .append(" · Tenor: ").append(record.pengajuan.getTenorBulan()).append(" bulan")
                .append("\nTujuan: ").append(record.pengajuan.getTujuanPenggunaan())
                .append("\nAkad: ").append(label(record.pengajuan.getJenisAkad()))
                .append(" · Nisbah: ").append(record.pengajuan.getNisbahBank())
                .append("% bank / ").append(record.pengajuan.getNisbahNasabah())
                .append("% nasabah\nModal nasabah: ")
                .append(rupiah(record.pengajuan.getModalNasabah()))
                .append("\n\nKEUANGAN USAHA DILAPORKAN PER BULAN\nOmzet ")
                .append(rupiah(record.pengajuan.getOmzetBulananDilaporkan()))
                .append(" · Biaya langsung ")
                .append(rupiah(record.pengajuan.getBiayaLangsungBulananDilaporkan()))
                .append("\nBiaya operasional ")
                .append(rupiah(record.pengajuan.getBiayaOperasionalBulananDilaporkan()))
                .append(" · Kewajiban ")
                .append(rupiah(record.pengajuan.getKewajibanUsahaBulananDilaporkan()));
        if (record.pengajuan.getAnalisisKelayakan() != null) {
            AnalisisKelayakan analysis = record.pengajuan.getAnalisisKelayakan();
            details.append("\n\nHASIL PEMERIKSAAN PEGAWAI PER BULAN\nArus kas tersedia: ")
                    .append(rupiah(analysis.hitungArusKasTersediaBulanan()))
                    .append(" · Rekomendasi internal: ")
                    .append(label(analysis.getRekomendasiInternal()));
        }
        if (record.keputusan != null) {
            details.append("\nKeputusan pegawai: ")
                    .append(label(record.keputusan.getStatusKeputusan()));
            if (record.keputusan.getStatusKeputusan() == StatusKeputusan.DISETUJUI) {
                details.append(" · disetujui ")
                        .append(rupiah(record.keputusan.getJumlahDisetujui()));
            }
        }
        RiwayatPengajuan history = record.pengajuan.getRiwayatPertama();
        RiwayatPengajuan latest = history;
        while (history != null) {
            latest = history;
            history = history.getBerikutnya();
        }
        if (latest != null) {
            details.append("\nPembaruan terakhir: ").append(latest.getKeterangan());
        }
        if (record.pengajuan.getAkad() != null) {
            details.append("\n\nAKAD: ").append(record.pengajuan.getAkad().getNomorAkad())
                    .append(" (").append(label(record.pengajuan.getAkad().getStatus())).append(")");
        }
        details.append("\n\nSaldo rekening saat ini: ")
                .append(rupiah(record.rekening.getSaldo()));
        customerDetails.setText(details.toString());
    }

    private String customerAssessment(CaseRecord record) {
        AnalisisKelayakan analysis = record.pengajuan.getAnalisisKelayakan();
        StringBuilder assessment = new StringBuilder()
                .append("INDIKATOR KEUANGAN AWAL (bukan cek kredibilitas)\n")
                .append("Gaji dilaporkan: ").append(rupiah(record.nasabah.getGajiBulanan()))
                .append("/bulan");
        if (analysis == null) {
            assessment.append("\nArus kas usaha: belum diperiksa pegawai.")
                    .append("\nKemampuan bayar dan kredibilitas belum dapat disimpulkan.");
        } else {
            assessment.append("\nArus kas usaha setelah pemeriksaan: ")
                    .append(rupiah(analysis.hitungArusKasTersediaBulanan()))
                    .append("/bulan (").append(label(analysis.getRekomendasiInternal()))
                    .append(").");
            if (analysis.hitungArusKasTersediaBulanan() < 0) {
                assessment.append("\nPerlu tinjauan khusus: arus kas usaha negatif.");
            }
            assessment.append("\nGaji dan arus kas adalah laporan; bukan bukti atau penilaian kredit.");
        }
        assessment.append("\nBelum ada data pengeluaran pribadi, bukti pendapatan, "
                + "riwayat kredit, atau jadwal pembayaran untuk memastikan kemampuan bayar.");
        return assessment.toString();
    }

    private Button actionButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-button");
        button.setOnAction(event -> action.run());
        return button;
    }

    private void refreshTables() {
        CaseRecord employeeSelection = employeeTable.getSelectionModel().getSelectedItem();
        CaseRecord customerSelection = customerTable.getSelectionModel().getSelectedItem();
        tableRows.setAll(records);
        employeeTable.refresh();
        customerTable.refresh();
        if (employeeSelection != null) {
            employeeTable.getSelectionModel().select(employeeSelection);
            showDetails(employeeSelection);
        }
        if (customerSelection != null) {
            customerTable.getSelectionModel().select(customerSelection);
            showCustomerDetails(customerSelection);
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
            AnalisisKelayakan.Rekomendasi recommendation =
                    AnalisisKelayakan.rekomendasiUntukArusKas(available);
            preview.setText(rupiah(available) + " · " + label(recommendation));
        } catch (IllegalArgumentException | ArithmeticException error) {
            preview.setText("Lengkapi semua angka untuk melihat perhitungan.");
        }
    }

    private static long parseAmount(TextInputControl input) {
        try {
            long value = Long.parseLong(required(input));
            if (value < 0) {
                throw new IllegalArgumentException("Nominal tidak boleh negatif.");
            }
            return value;
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Masukkan nominal berupa angka bulat tanpa pemisah.");
        }
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
        alert.setTitle("Tidak dapat memproses");
        alert.setHeaderText("Periksa kembali data pengajuan");
        alert.setContentText(message == null ? "Terjadi kesalahan yang tidak diketahui." : message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Berhasil");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static final class CaseRecord {
        private final Nasabah nasabah;
        private final Usaha usaha;
        private final RekeningNasabah rekening;
        private final PengajuanPembiayaan pengajuan;
        private KeputusanPembiayaan keputusan;

        private CaseRecord(
                Nasabah nasabah,
                Usaha usaha,
                RekeningNasabah rekening,
                PengajuanPembiayaan pengajuan) {
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
            dialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/css/app.css").toExternalForm());
            dialog.getDialogPane().setPrefWidth(540);
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
            field.setTextFormatter(new TextFormatter<String>(change ->
                    change.getControlNewText().matches("\\d*") ? change : null));
            field.setText(initialValue);
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

        private <T> ChoiceBox<T> choice(
                String id, T[] options, T initial, Function<T, String> converter) {
            Object configuredControl = loader.getNamespace().get(id);
            if (!(configuredControl instanceof ChoiceBox<?> untypedChoice)) {
                throw new IllegalStateException(
                        "Kontrol '" + id + "' bukan ChoiceBox di " + loader.getLocation());
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
            return dialog.showAndWait().filter(result ->
                    result.getButtonData() == ButtonBar.ButtonData.OK_DONE).isPresent();
        }
    }

}
