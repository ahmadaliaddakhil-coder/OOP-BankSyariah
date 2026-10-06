package ui;

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
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

public class BankSyariahApp extends Application {
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
    private final TableView<CaseRecord> employeeTable = new TableView<>();
    private final TableView<CaseRecord> customerTable = new TableView<>();
    private final TextField search = new TextField();
    private final Label selectedTitle = new Label("Pilih pengajuan untuk melihat detail.");
    private final TextArea selectedContent = new TextArea();
    private final VBox selectedActions = new VBox(8);
    private final TextArea customerDetails = new TextArea("Pilih pengajuan untuk melihat status.");
    private BorderPane root;
    private MenuItem customerMenu;
    private MenuItem employeeMenu;

    @Override
    public void start(Stage stage) {
        bank.tambahPegawai(pegawai);
        configureTable();

        root = new BorderPane();
        root.setTop(buildMenu());
        root.setCenter(buildCustomerView());
        root.setPadding(new Insets(16));
        Scene scene = new Scene(root, 1040, 700);
        stage.setTitle("Berkah | Pembiayaan Modal Syariah");
        stage.setMinWidth(820);
        stage.setMinHeight(580);
        stage.setScene(scene);
        stage.show();
        refreshTables();
    }

    private Node buildMenu() {
        Menu viewMenu = new Menu("Tampilan");
        customerMenu = new MenuItem("Nasabah");
        employeeMenu = new MenuItem("Pegawai");
        customerMenu.setOnAction(event -> root.setCenter(buildCustomerView()));
        employeeMenu.setOnAction(event -> root.setCenter(buildEmployeeView()));
        viewMenu.getItems().addAll(customerMenu, employeeMenu);
        MenuBar menuBar = new MenuBar(viewMenu);
        Label title = new Label("Sistem Pembiayaan Bank Syariah");
        Label demoNote = new Label(
                "Demo tanpa login: data pengajuan yang sama terlihat dari sisi nasabah dan pegawai.");
        return new VBox(8, title, menuBar, demoNote, new Separator());
    }

    private Node buildCustomerView() {
        Label title = new Label("Halaman Nasabah");
        Label instructions = new Label(
                "Ajukan pembiayaan dan lihat perkembangan pengajuan di bawah ini.");
        Button create = new Button("Ajukan Pembiayaan");
        create.setOnAction(event -> createApplication());
        customerDetails.setWrapText(true);
        customerDetails.setEditable(false);
        customerDetails.setPrefRowCount(8);
        customerTable.setPlaceholder(new Label("Belum ada pengajuan."));
        VBox content = new VBox(12, title, instructions, create,
                new Label("Pengajuan dan detail lengkap"), customerTable,
                new Label("Detail pengajuan yang dipilih"), customerDetails);
        VBox.setVgrow(customerTable, Priority.ALWAYS);
        content.setPadding(new Insets(16, 0, 0, 0));
        return content;
    }

    private Node buildEmployeeView() {
        Label title = new Label("Halaman Pegawai");
        Label employeeIdentity = new Label(
                "Pegawai demo: " + pegawai.getNama() + " — " + pegawai.getJabatan());
        Label instructions = new Label(
                "Pilih pengajuan untuk melihat profil nasabah dan melanjutkan satu tahap proses.");
        search.setPromptText("Cari nama, usaha, atau nomor pengajuan");
        selectedContent.setEditable(false);
        selectedContent.setWrapText(true);
        selectedContent.setPrefRowCount(9);
        VBox content = new VBox(12, title, employeeIdentity, instructions, search, employeeTable,
                selectedTitle, selectedContent, selectedActions);
        VBox.setVgrow(employeeTable, Priority.ALWAYS);
        content.setPadding(new Insets(16, 0, 0, 0));
        return content;
    }

    private void configureTable() {
        TableColumn<CaseRecord, String> id = textColumn("No. Pengajuan",
                item -> item.pengajuan.getIdPengajuan());
        TableColumn<CaseRecord, String> customer = textColumn("Nasabah",
                item -> item.nasabah.getNamaPihak());
        TableColumn<CaseRecord, String> business = textColumn("Usaha",
                item -> item.usaha.getNamaUsaha());
        TableColumn<CaseRecord, String> amount = textColumn("Nominal",
                item -> rupiah(item.pengajuan.getNominal()));
        TableColumn<CaseRecord, String> contract = textColumn("Akad",
                item -> label(item.pengajuan.getJenisAkad()));
        TableColumn<CaseRecord, String> status = textColumn("Status",
                item -> label(item.pengajuan.getStatus()));
        employeeTable.getColumns().setAll(id, customer, business, amount, contract, status);
        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        employeeTable.setPlaceholder(new Label("Belum ada pengajuan."));
        sortedEmployeeRows.comparatorProperty().bind(employeeTable.comparatorProperty());
        employeeTable.setItems(sortedEmployeeRows);
        employeeTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selected) -> showDetails(selected));
        search.textProperty().addListener((observable, oldValue, newValue) ->
                employeeRows.setPredicate(this::matchesSearch));

        TableColumn<CaseRecord, String> customerId = textColumn("No. Pengajuan",
                item -> item.pengajuan.getIdPengajuan());
        TableColumn<CaseRecord, String> customerAkad = textColumn("Akad",
                item -> label(item.pengajuan.getJenisAkad()));
        TableColumn<CaseRecord, String> customerAmount = textColumn("Nominal",
                item -> rupiah(item.pengajuan.getNominal()));
        TableColumn<CaseRecord, String> customerStatus = textColumn("Status",
                item -> label(item.pengajuan.getStatus()));
        customerTable.getColumns().setAll(customerId, customerAkad, customerAmount, customerStatus);
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        sortedCustomerRows.comparatorProperty().bind(customerTable.comparatorProperty());
        customerTable.setItems(sortedCustomerRows);
        customerTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selected) -> showCustomerDetails(selected));
    }

    private TableColumn<CaseRecord, String> textColumn(
            String title, Function<CaseRecord, String> value) {
        TableColumn<CaseRecord, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                value.apply(cell.getValue())));
        return column;
    }

    private boolean matchesSearch(CaseRecord item) {
        String query = search.getText() == null ? "" : search.getText().trim().toLowerCase(Locale.ROOT);
        return query.isEmpty()
                || item.pengajuan.getIdPengajuan().toLowerCase(Locale.ROOT).contains(query)
                || item.nasabah.getNamaPihak().toLowerCase(Locale.ROOT).contains(query)
                || item.usaha.getNamaUsaha().toLowerCase(Locale.ROOT).contains(query);
    }

    private void createApplication() {
        Form form = new Form("Pengajuan Pembiayaan Baru");
        form.section("Data Nasabah");
        TextField customerId = form.text("Nomor identitas nasabah", "ID nasabah");
        TextField customerName = form.text("Nama nasabah", "Nama lengkap");
        TextField address = form.text("Alamat", "Alamat nasabah");
        DatePicker birthDate = form.date("Tanggal lahir", null);
        TextField occupation = form.text("Pekerjaan", "Pekerjaan");
        TextField monthlySalary = form.number("Gaji yang diterima per bulan (Rp)", "");
        form.node("Catatan", new Label("Masukkan gaji yang benar-benar diterima tiap bulan."));
        form.section("Data Usaha dan Rekening");
        TextField businessName = form.text("Nama usaha", "Nama usaha");
        TextField sector = form.text("Sektor usaha", "Contoh: Kuliner");
        TextField businessAddress = form.text("Lokasi usaha", "Alamat usaha");
        TextField accountNumber = form.text("Nomor rekening", "Nomor rekening tujuan");
        TextField startingBalance = form.number("Saldo awal rekening (Rp)", "");
        form.section("Keuangan Usaha per Bulan (Rp)");
        TextField revenue = form.number("Omzet bulanan", "");
        TextField directCosts = form.number("Biaya langsung", "");
        TextField operatingCosts = form.number("Biaya operasional", "");
        TextField obligations = form.number("Kewajiban usaha", "");
        form.node("Catatan", new Label(
                "Data ini dilaporkan nasabah dan akan diperiksa oleh pegawai."));
        form.section("Rencana Pembiayaan");
        TextField amount = form.number("Nominal pembiayaan (Rp)", "");
        TextField tenor = form.number("Tenor (bulan)", "");
        TextField purpose = form.text("Tujuan penggunaan", "Tujuan pembiayaan");
        ChoiceBox<JenisAkad> akad = form.choice("Jenis akad",
                JenisAkad.values(), null, BankSyariahApp::label);
        TextField bankShare = form.number("Usulan nisbah bank (%)", "");
        TextField customerShare = form.number("Usulan nisbah nasabah (%)", "");
        form.node("Catatan", new Label("Total usulan nisbah harus 100%."));
        TextField customerCapital = form.number("Modal nasabah (Rp)", "0");
        form.node("Catatan", new Label("Modal nasabah diisi hanya untuk akad Musyarakah."));
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
        Form form = new Form("Pemeriksaan Data Keuangan");
        form.section("Data yang dilaporkan nasabah (Rp/bulan)");
        form.node("Sumber", new Label("Nilai awal berasal dari pengajuan nasabah."));
        TextField revenue = form.number("Omzet bulanan",
                Long.toString(record.pengajuan.getOmzetBulananDilaporkan()));
        TextField directCost = form.number("Biaya langsung",
                Long.toString(record.pengajuan.getBiayaLangsungBulananDilaporkan()));
        TextField operatingCost = form.number(
                "Biaya operasional",
                Long.toString(record.pengajuan.getBiayaOperasionalBulananDilaporkan()));
        TextField obligations = form.number("Kewajiban usaha",
                Long.toString(record.pengajuan.getKewajibanUsahaBulananDilaporkan()));
        form.section("Hasil pemeriksaan pegawai");
        form.node("Perhitungan", new Label(
                "Arus kas = omzet - biaya langsung - biaya operasional - kewajiban."));
        Label cashFlowPreview = new Label();
        form.node("Arus kas tersedia", cashFlowPreview);
        updateCashFlowPreview(cashFlowPreview, revenue, directCost, operatingCost, obligations);
        revenue.textProperty().addListener((observable, oldValue, newValue) ->
                updateCashFlowPreview(cashFlowPreview, revenue, directCost, operatingCost, obligations));
        directCost.textProperty().addListener((observable, oldValue, newValue) ->
                updateCashFlowPreview(cashFlowPreview, revenue, directCost, operatingCost, obligations));
        operatingCost.textProperty().addListener((observable, oldValue, newValue) ->
                updateCashFlowPreview(cashFlowPreview, revenue, directCost, operatingCost, obligations));
        obligations.textProperty().addListener((observable, oldValue, newValue) ->
                updateCashFlowPreview(cashFlowPreview, revenue, directCost, operatingCost, obligations));
        TextArea risk = form.area("Catatan pemeriksaan", "Catat hasil verifikasi / risiko");
        CheckBox verified = new CheckBox("Saya sudah memeriksa data keuangan di atas.");
        form.node("Konfirmasi", verified);
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
        Form form = new Form("Keputusan Pembiayaan");
        Label profile = new Label(customerAssessment(record));
        profile.setWrapText(true);
        form.node("Profil nasabah dan indikator awal", profile);
        form.node("Batas penilaian", new Label(
                "Gaji dan data usaha berasal dari laporan nasabah. Belum ada bukti dokumen, "
                        + "riwayat kredit, pengeluaran rumah tangga, atau jadwal pembayaran; "
                        + "kredibilitas dan kemampuan bayar belum dapat dipastikan."));
        Label analysisSummary = new Label(
                "Arus kas tersedia hasil pemeriksaan: "
                        + rupiah(record.pengajuan.getAnalisisKelayakan()
                                .hitungArusKasTersediaBulanan())
                        + ". Rekomendasi: "
                        + label(record.pengajuan.getAnalisisKelayakan().getRekomendasiInternal())
                        + " (bukan keputusan otomatis).");
        analysisSummary.setWrapText(true);
        form.node("Hasil analisis", analysisSummary);
        ChoiceBox<StatusKeputusan> decision = form.choice(
                "Keputusan pegawai", StatusKeputusan.values(), null, BankSyariahApp::label);
        TextField approvedAmount = form.number("Jumlah disetujui (Rp)", "");
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
        TextField reason = form.text("Alasan keputusan", "Alasan keputusan");
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
        Form form = new Form("Buat Akad Pembiayaan");
        LocalDate dueDate = LocalDate.parse(record.pengajuan.getTanggalPengajuan())
                .plusMonths(record.pengajuan.getTenorBulan());
        form.node("Jatuh tempo sesuai tenor", new Label(dueDate.toString()));
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
        Form form = new Form("Tanda Tangani Akad");
        DatePicker signatureDate = form.date("Tanggal tanda tangan", LocalDate.now());
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
        Form form = new Form("Pencairan Pembiayaan");
        Label amount = new Label(rupiah(record.pengajuan.getAkad().getJumlahDisetujui()));
        form.node("Jumlah pencairan", amount);
        Label account = new Label(record.rekening.getNomorRekening()
                + " — " + record.nasabah.getNamaPihak());
        form.node("Rekening tujuan", account);
        DatePicker date = form.date("Tanggal pencairan", LocalDate.now());
        TextField reference = form.text("Referensi transaksi", "Nomor referensi");
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
        private final GridPane grid = new GridPane();
        private final ButtonType submit = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        private int row;

        private Form(String title) {
            dialog.setTitle(title);
            dialog.getDialogPane().getButtonTypes().addAll(submit, ButtonType.CANCEL);
            dialog.setResizable(true);
            grid.setHgap(14);
            grid.setVgap(10);
            grid.setPadding(new Insets(14, 4, 8, 4));
            ColumnConstraints labelColumn = new ColumnConstraints();
            labelColumn.setMinWidth(155);
            ColumnConstraints inputColumn = new ColumnConstraints();
            inputColumn.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(labelColumn, inputColumn);
            ScrollPane scroll = new ScrollPane(grid);
            scroll.setFitToWidth(true);
            scroll.setPrefViewportHeight(580);
            dialog.getDialogPane().setContent(scroll);
            dialog.getDialogPane().setPrefWidth(540);
        }

        private TextField text(String label, String placeholder) {
            TextField field = new TextField();
            field.setPromptText(placeholder);
            node(label, field);
            return field;
        }

        private TextField number(String label, String initialValue) {
            TextField field = text(label, "Angka tanpa titik/koma");
            field.setTextFormatter(new TextFormatter<String>(change ->
                    change.getControlNewText().matches("\\d*") ? change : null));
            field.setText(initialValue);
            return field;
        }

        private TextArea area(String label, String placeholder) {
            TextArea area = new TextArea();
            area.setPromptText(placeholder);
            area.setWrapText(true);
            area.setPrefRowCount(3);
            node(label, area);
            return area;
        }

        private void section(String title) {
            Label heading = new Label(title);
            grid.add(heading, 0, row++, 2, 1);
        }

        private DatePicker date(String label, LocalDate value) {
            DatePicker picker = new DatePicker(value);
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
            node(label, picker);
            return picker;
        }

        private <T> ChoiceBox<T> choice(
                String label, T[] options, T initial, Function<T, String> converter) {
            ChoiceBox<T> choice = new ChoiceBox<>(FXCollections.observableArrayList(options));
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
            node(label, choice);
            return choice;
        }

        private void node(String label, Node input) {
            Label caption = new Label(label);
            grid.add(caption, 0, row);
            grid.add(input, 1, row++);
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

    public static void main(String[] args) {
        launch(args);
    }
}
