package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.SpreadsheetSyncManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = AppRepository(database.appDao())
    private val syncManager = SpreadsheetSyncManager()

    // Preferences / Settings state
    private val sharedPrefs = application.getSharedPreferences("smart_hr_prefs", Application.MODE_PRIVATE)
    
    private val _webAppUrl = MutableStateFlow(sharedPrefs.getString("web_app_url", "") ?: "")
    val webAppUrl: StateFlow<String> = _webAppUrl.asStateFlow()

    // Session State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Search and Filter States
    val searchQuery = MutableStateFlow("")
    val estateFilter = MutableStateFlow("Semua")
    val statusFilter = MutableStateFlow("Semua")
    val dateFilter = MutableStateFlow("")

    // Sync progress state
    private val _syncStatus = MutableStateFlow<String?>(null)
    val syncStatus: StateFlow<String?> = _syncStatus.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // Load reactive data lists from repository
    val users: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val estates: StateFlow<List<EstateEntity>> = repository.allEstatesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rumah: StateFlow<List<RumahEntity>> = repository.allRumahFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val laporanPerumahan: StateFlow<List<LaporanPerumahanEntity>> = repository.allLaporanFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val guestHouses: StateFlow<List<GuestHouseEntity>> = repository.allGuestHousesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookings: StateFlow<List<BookingGHEntity>> = repository.allBookingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assets: StateFlow<List<AssetEntity>> = repository.allAssetsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transfers: StateFlow<List<TransferAssetEntity>> = repository.allTransfersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val carRequests: StateFlow<List<RequestMobilEntity>> = repository.allCarRequestsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotifikasiEntity>> = repository.allNotificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activityLogs: StateFlow<List<LogAktivitasEntity>> = repository.allLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            // Seed initial data if database is empty
            repository.prepopulateIfEmpty()
            
            // Check if user was previously logged in
            val savedEmail = sharedPrefs.getString("logged_in_email", "") ?: ""
            if (savedEmail.isNotEmpty()) {
                val user = repository.getUserByEmail(savedEmail)
                if (user != null) {
                    _currentUser.value = user
                    repository.addLog(user.nama, "Auto login berhasil", "Autentikasi")
                }
            }
        }
    }

    // --- AUTHENTICATION ---
    fun login(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val trimmedEmail = email.trim().lowercase()
            
            // Validate Company Domain Check
            // Valid domains: @company.com, @perusahaan.com
            // Also accept developer email: hannifasyafira99@gmail.com
            val isCompanyDomain = trimmedEmail.endsWith("@company.com") || 
                                  trimmedEmail.endsWith("@perusahaan.com") || 
                                  trimmedEmail == "hannifasyafira99@gmail.com"

            if (!isCompanyDomain) {
                onResult(false, "Akses ditolak. Silakan gunakan akun email perusahaan.")
                return@launch
            }

            // Get user from local database or auto-create a user for testing
            var user = repository.getUserByEmail(trimmedEmail)
            if (user == null) {
                // If it's a new email but has valid domain, automatically register them as a default employee for convenience
                val defaultName = trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
                user = UserEntity(
                    email = trimmedEmail,
                    nama = defaultName,
                    nik = "NIK-" + (10000 + Random().nextInt(9000)),
                    jabatan = "Staff Lapangan",
                    departemen = "Operations",
                    estate = "Pandan Estate",
                    status = "Aktif"
                )
                repository.insertUser(user)
            }

            _currentUser.value = user
            sharedPrefs.edit().putString("logged_in_email", trimmedEmail).apply()
            
            repository.addLog(user.nama, "Login berhasil", "Autentikasi")
            repository.addNotification(user.nama, "Login Berhasil", "Selamat datang di ADINDO HR OPERATIONS!")
            onResult(true, "Login Berhasil")
        }
    }

    fun logout() {
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch {
                repository.addLog(user.nama, "Logout dari aplikasi", "Autentikasi")
            }
        }
        _currentUser.value = null
        sharedPrefs.edit().remove("logged_in_email").apply()
    }

    // --- SETTINGS / SPREADSHEET URL ---
    fun saveWebAppUrl(url: String) {
        _webAppUrl.value = url.trim()
        sharedPrefs.edit().putString("web_app_url", url.trim()).apply()
        viewModelScope.launch {
            val user = _currentUser.value?.nama ?: "Guest"
            repository.addLog(user, "Menyimpan Web App URL Baru", "Sistem")
        }
    }

    // --- CLOUD SYNCING ---
    fun testCloudConnection() {
        val url = _webAppUrl.value
        if (url.isEmpty()) {
            _syncStatus.value = "URL Google Apps Script kosong!"
            return
        }
        _isSyncing.value = true
        _syncStatus.value = "Menghubungkan ke Google Apps Script..."
        viewModelScope.launch {
            val (success, message) = syncManager.testConnection(url)
            _syncStatus.value = message
            _isSyncing.value = false
            repository.addLog(_currentUser.value?.nama ?: "System", "Tes koneksi: $message", "Sistem")
        }
    }

    fun exportToSpreadsheet() {
        val url = _webAppUrl.value
        if (url.isEmpty()) {
            _syncStatus.value = "Silakan atur URL Google Apps Script di Pengaturan!"
            return
        }
        _isSyncing.value = true
        _syncStatus.value = "Mengekspor data ke Google Spreadsheet..."
        viewModelScope.launch {
            val (success, message) = syncManager.exportData(url, repository)
            _syncStatus.value = message
            _isSyncing.value = false
            repository.addLog(_currentUser.value?.nama ?: "System", "Export data ke Spreadsheet: $message", "Sistem")
        }
    }

    fun importFromSpreadsheet() {
        val url = _webAppUrl.value
        if (url.isEmpty()) {
            _syncStatus.value = "Silakan atur URL Google Apps Script di Pengaturan!"
            return
        }
        _isSyncing.value = true
        _syncStatus.value = "Mengimpor data dari Google Spreadsheet..."
        viewModelScope.launch {
            val (success, message) = syncManager.importData(url, repository)
            _syncStatus.value = message
            _isSyncing.value = false
            repository.addLog(_currentUser.value?.nama ?: "System", "Import data dari Spreadsheet: $message", "Sistem")
        }
    }

    fun clearSyncStatus() {
        _syncStatus.value = null
    }

    fun getAppsScriptCode(): String {
        return syncManager.getGoogleAppsScriptTemplate()
    }

    // --- HOUSING MODULE ---
    fun submitHousingReport(
        estate: String,
        nomorRumah: String,
        penghuni: String,
        jenisTemuan: String,
        deskripsi: String,
        status: String,
        pic: String,
        fotoBefore: String,
        fotoAfter: String,
        latitude: Double,
        longitude: Double,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val id = "REP-" + (1000 + Random().nextInt(9000))
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val pelaporEmail = _currentUser.value?.email ?: "anonymous@company.com"

            val report = LaporanPerumahanEntity(
                id = id,
                tanggal = dateStr,
                jam = timeStr,
                estate = estate,
                nomorRumah = nomorRumah,
                penghuni = penghuni,
                jenisTemuan = jenisTemuan,
                deskripsi = deskripsi,
                status = status,
                pic = pic,
                fotoBefore = fotoBefore,
                fotoAfter = fotoAfter,
                latitude = latitude,
                longitude = longitude,
                pelapor = pelaporEmail
            )

            repository.insertLaporan(report)
            
            // Log & Notify
            val userNama = _currentUser.value?.nama ?: "User"
            repository.addLog(userNama, "Membuat laporan perumahan $id", "Perumahan")
            repository.addNotification(userNama, "Laporan Baru: $jenisTemuan", "Laporan $id dibuat untuk rumah $nomorRumah di $estate.")
            
            onComplete()
        }
    }

    fun updateHousingReportStatus(reportId: String, newStatus: String, pic: String, fotoAfter: String) {
        viewModelScope.launch {
            val report = repository.getLaporanById(reportId)
            if (report != null) {
                val updatedReport = report.copy(
                    status = newStatus,
                    pic = pic,
                    fotoAfter = fotoAfter
                )
                repository.insertLaporan(updatedReport)
                
                val userNama = _currentUser.value?.nama ?: "User"
                repository.addLog(userNama, "Mengubah status laporan $reportId menjadi $newStatus", "Perumahan")
                repository.addNotification(userNama, "Laporan $reportId Diperbarui", "Status laporan perumahan telah diubah menjadi $newStatus.")
            }
        }
    }

    // --- GUEST HOUSE MODULE ---
    fun bookGuestHouseRoom(
        guestHouse: GuestHouseEntity,
        nama: String,
        nik: String,
        estateAsal: String,
        estateTujuan: String,
        checkIn: String,
        checkOut: String,
        lamaMenginap: Int,
        fotoCheckIn: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val idBooking = "BKG-" + (1000 + Random().nextInt(9000))
            val booking = BookingGHEntity(
                idBooking = idBooking,
                nama = nama,
                nik = nik,
                estateAsal = estateAsal,
                estateTujuan = estateTujuan,
                guestHouse = guestHouse.namaGH,
                nomorKamar = guestHouse.nomorKamar,
                checkIn = checkIn,
                checkOut = checkOut,
                lamaMenginap = lamaMenginap,
                status = "Booked",
                fotoCheckIn = fotoCheckIn,
                fotoCheckOut = ""
            )

            repository.insertBooking(booking)

            // Update GH Room status to "Booking"
            val updatedGH = guestHouse.copy(status = "Booking")
            repository.insertGuestHouse(updatedGH)

            val userNama = _currentUser.value?.nama ?: "User"
            repository.addLog(userNama, "Membuat booking GH $idBooking", "Guest House")
            repository.addNotification(userNama, "Booking GH Baru", "Booking kamar ${guestHouse.nomorKamar} di ${guestHouse.namaGH} berhasil.")

            onComplete()
        }
    }

    fun checkInBooking(booking: BookingGHEntity, fotoCheckIn: String) {
        viewModelScope.launch {
            val updatedBooking = booking.copy(
                status = "Checked In",
                fotoCheckIn = fotoCheckIn
            )
            repository.insertBooking(updatedBooking)

            // Find guest house and update status to "Terisi"
            val ghList = guestHouses.value
            val matchGH = ghList.find { it.namaGH == booking.guestHouse && it.nomorKamar == booking.nomorKamar }
            if (matchGH != null) {
                repository.insertGuestHouse(matchGH.copy(status = "Terisi"))
            }

            val userNama = _currentUser.value?.nama ?: "User"
            repository.addLog(userNama, "Check In Guest House ${booking.idBooking}", "Guest House")
        }
    }

    fun checkOutBooking(booking: BookingGHEntity, fotoCheckOut: String) {
        viewModelScope.launch {
            val updatedBooking = booking.copy(
                status = "Checked Out",
                fotoCheckOut = fotoCheckOut
            )
            repository.insertBooking(updatedBooking)

            // Find guest house and update status to "Tersedia"
            val ghList = guestHouses.value
            val matchGH = ghList.find { it.namaGH == booking.guestHouse && it.nomorKamar == booking.nomorKamar }
            if (matchGH != null) {
                repository.insertGuestHouse(matchGH.copy(status = "Tersedia"))
            }

            val userNama = _currentUser.value?.nama ?: "User"
            repository.addLog(userNama, "Check Out Guest House ${booking.idBooking}", "Guest House")
        }
    }

    // --- ASSETS MODULE ---
    fun addAsset(
        nomorAsset: String,
        namaAsset: String,
        kategori: String,
        merk: String,
        serialNumber: String,
        estate: String,
        lokasi: String,
        kondisi: String,
        fotoAsset: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            // Check uniqueness of asset code
            val existing = repository.getAssetByNomor(nomorAsset)
            if (existing != null) {
                onComplete(false, "Nomor Aset sudah terdaftar!")
                return@launch
            }

            val asset = AssetEntity(
                nomorAsset = nomorAsset,
                namaAsset = namaAsset,
                kategori = kategori,
                merk = merk,
                serialNumber = serialNumber,
                estate = estate,
                lokasi = lokasi,
                kondisi = kondisi,
                fotoAsset = fotoAsset,
                qrCode = "SMARTHR-AST-$nomorAsset"
            )

            repository.insertAsset(asset)

            val userNama = _currentUser.value?.nama ?: "User"
            repository.addLog(userNama, "Menambahkan aset baru $nomorAsset", "Asset")
            repository.addNotification(userNama, "Asset Baru Terdaftar", "Asset $namaAsset ($nomorAsset) telah didaftarkan di $estate.")
            
            onComplete(true, "Aset berhasil ditambahkan")
        }
    }

    fun editAsset(
        asset: AssetEntity,
        namaAsset: String,
        kategori: String,
        merk: String,
        serialNumber: String,
        estate: String,
        lokasi: String,
        kondisi: String,
        fotoAsset: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val updatedAsset = asset.copy(
                namaAsset = namaAsset,
                kategori = kategori,
                merk = merk,
                serialNumber = serialNumber,
                estate = estate,
                lokasi = lokasi,
                kondisi = kondisi,
                fotoAsset = fotoAsset.ifEmpty { asset.fotoAsset }
            )

            repository.insertAsset(updatedAsset)

            val userNama = _currentUser.value?.nama ?: "User"
            repository.addLog(userNama, "Mengubah data aset ${asset.nomorAsset}", "Asset")
            onComplete()
        }
    }

    // --- TRANSFER ASSET MODULE ---
    fun initiateTransferAsset(
        asset: AssetEntity,
        estateTujuan: String,
        penyerah: String,
        penerima: String,
        ttdPenyerah: String,
        ttdPenerima: String,
        fotoAsset: String,
        fotoSerahTerima: String,
        latitude: Double,
        longitude: Double,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val idTransfer = "TRF-" + (1000 + Random().nextInt(9000))
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            // A transfer is Completed only if both signatures are present
            val status = if (ttdPenyerah.isNotEmpty() && ttdPenerima.isNotEmpty()) "Completed" else "Pending"

            val transfer = TransferAssetEntity(
                idTransfer = idTransfer,
                tanggal = dateStr,
                jam = timeStr,
                nomorAsset = asset.nomorAsset,
                namaAsset = asset.namaAsset,
                estateAsal = asset.estate,
                estateTujuan = estateTujuan,
                penyerah = penyerah,
                ttdPenyerah = ttdPenyerah,
                penerima = penerima,
                ttdPenerima = ttdPenerima,
                fotoAsset = fotoAsset,
                fotoSerahTerima = fotoSerahTerima,
                latitude = latitude,
                longitude = longitude,
                status = status
            )

            repository.insertTransfer(transfer)

            // If Completed, also update the location/estate of the asset automatically!
            if (status == "Completed") {
                val updatedAsset = asset.copy(estate = estateTujuan)
                repository.insertAsset(updatedAsset)
            }

            val userNama = _currentUser.value?.nama ?: "User"
            repository.addLog(userNama, "Mentransfer aset ${asset.nomorAsset} ke $estateTujuan. Status: $status", "Transfer Asset")
            repository.addNotification(userNama, "Transfer Aset Diajukan", "Transfer aset ${asset.namaAsset} ke $estateTujuan berstatus $status.")
            
            onComplete()
        }
    }

    fun completeTransferSignatures(transfer: TransferAssetEntity, ttdPenyerah: String, ttdPenerima: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val updatedTransfer = transfer.copy(
                ttdPenyerah = ttdPenyerah,
                ttdPenerima = ttdPenerima,
                status = "Completed"
            )
            repository.insertTransfer(updatedTransfer)

            // Update asset estate
            val asset = repository.getAssetByNomor(transfer.nomorAsset)
            if (asset != null) {
                repository.insertAsset(asset.copy(estate = transfer.estateTujuan))
            }

            val userNama = _currentUser.value?.nama ?: "User"
            repository.addLog(userNama, "Melengkapi TTD Transfer ${transfer.idTransfer}", "Transfer Asset")
            onComplete()
        }
    }

    // --- CAR REQUEST MODULE ---
    fun submitCarRequest(
        estateAsal: String,
        tujuan: String,
        keperluan: String,
        tanggal: String,
        jamBerangkat: String,
        jamKembali: String,
        driver: String,
        kendaraan: String,
        fotoKilometerAwal: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val id = "MOB-" + (1000 + Random().nextInt(9000))
            val nama = _currentUser.value?.nama ?: "User"

            val request = RequestMobilEntity(
                id = id,
                nama = nama,
                estateAsal = estateAsal,
                tujuan = tujuan,
                keperluan = keperluan,
                tanggal = tanggal,
                jamBerangkat = jamBerangkat,
                jamKembali = jamKembali,
                driver = driver,
                kendaraan = kendaraan,
                fotoKilometerAwal = fotoKilometerAwal,
                fotoKilometerAkhir = "",
                status = "Pending"
            )

            repository.insertCarRequest(request)

            repository.addLog(nama, "Membuat request mobil $id", "Request Mobil")
            repository.addNotification(nama, "Request Mobil Berhasil", "Request mobil untuk tujuan $tujuan berhasil diajukan.")
            onComplete()
        }
    }

    fun completeCarRequest(request: RequestMobilEntity, fotoKilometerAkhir: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val updatedRequest = request.copy(
                status = "Completed",
                fotoKilometerAkhir = fotoKilometerAkhir
            )
            repository.insertCarRequest(updatedRequest)

            val nama = _currentUser.value?.nama ?: "User"
            repository.addLog(nama, "Menyelesaikan request mobil ${request.id}", "Request Mobil")
            onComplete()
        }
    }

    // --- EXPORT TO CSV (FOR EXCEL) & PRINTABLE HTML REPORT ---
    @Suppress("UNCHECKED_CAST")
    fun exportTableToCSV(tableName: String, dataList: List<Any>): String {
        val csv = java.lang.StringBuilder()
        if (dataList.isEmpty()) return "No data"

        // Generate dynamic headers based on type
        when (tableName) {
            "USER" -> {
                csv.append("Nama,Email,NIK,Jabatan,Departemen,Estate,Status\n")
                (dataList as List<UserEntity>).forEach {
                    csv.append("\"${it.nama}\",\"${it.email}\",\"${it.nik}\",\"${it.jabatan}\",\"${it.departemen}\",\"${it.estate}\",\"${it.status}\"\n")
                }
            }
            "ESTATE" -> {
                csv.append("Nama Estate,Lokasi\n")
                (dataList as List<EstateEntity>).forEach {
                    csv.append("\"${it.namaEstate}\",\"${it.lokasi}\"\n")
                }
            }
            "RUMAH" -> {
                csv.append("ID Rumah,Estate,Nomor Rumah,Jenis Rumah,Nama Mess,Penghuni,Status Rumah,Foto Rumah\n")
                (dataList as List<RumahEntity>).forEach {
                    csv.append("\"${it.idRumah}\",\"${it.estate}\",\"${it.nomorRumah}\",\"${it.jenisRumah}\",\"${it.namaMess}\",\"${it.penghuni}\",\"${it.statusRumah}\",\"${it.fotoRumah}\"\n")
                }
            }
            "LAPORAN_PERUMAHAN" -> {
                csv.append("ID,Tanggal,Jam,Estate,Nomor Rumah,Penghuni,Jenis Temuan,Deskripsi,Status,PIC,Foto Before,Foto After,Latitude,Longitude,Pelapor\n")
                (dataList as List<LaporanPerumahanEntity>).forEach {
                    csv.append("\"${it.id}\",\"${it.tanggal}\",\"${it.jam}\",\"${it.estate}\",\"${it.nomorRumah}\",\"${it.penghuni}\",\"${it.jenisTemuan}\",\"${it.deskripsi}\",\"${it.status}\",\"${it.pic}\",\"${it.fotoBefore}\",\"${it.fotoAfter}\",${it.latitude},${it.longitude},\"${it.pelapor}\"\n")
                }
            }
            "GUEST_HOUSE" -> {
                csv.append("ID,Estate,Nama GH,Nomor Kamar,Status\n")
                (dataList as List<GuestHouseEntity>).forEach {
                    csv.append("\"${it.id}\",\"${it.estate}\",\"${it.namaGH}\",\"${it.nomorKamar}\",\"${it.status}\"\n")
                }
            }
            "BOOKING_GH" -> {
                csv.append("ID Booking,Nama,NIK,Estate Asal,Estate Tujuan,Guest House,Nomor Kamar,Check In,Check Out,Lama Menginap,Status,Foto Check In,Foto Check Out\n")
                (dataList as List<BookingGHEntity>).forEach {
                    csv.append("\"${it.idBooking}\",\"${it.nama}\",\"${it.nik}\",\"${it.estateAsal}\",\"${it.estateTujuan}\",\"${it.guestHouse}\",\"${it.nomorKamar}\",\"${it.checkIn}\",\"${it.checkOut}\",${it.lamaMenginap},\"${it.status}\",\"${it.fotoCheckIn}\",\"${it.fotoCheckOut}\"\n")
                }
            }
            "ASSET" -> {
                csv.append("Nomor Asset,Nama Asset,Kategori,Merk,Serial Number,Estate,Lokasi,Kondisi,Foto Asset,QR Code\n")
                (dataList as List<AssetEntity>).forEach {
                    csv.append("\"${it.nomorAsset}\",\"${it.namaAsset}\",\"${it.kategori}\",\"${it.merk}\",\"${it.serialNumber}\",\"${it.estate}\",\"${it.lokasi}\",\"${it.kondisi}\",\"${it.fotoAsset}\",\"${it.qrCode}\"\n")
                }
            }
            "TRANSFER_ASSET" -> {
                csv.append("ID Transfer,Tanggal,Jam,Nomor Asset,Nama Asset,Estate Asal,Estate Tujuan,Penyerah,TTD Penyerah,Penerima,TTD Penerima,Foto Asset,Foto Serah Terima,Latitude,Longitude,Status\n")
                (dataList as List<TransferAssetEntity>).forEach {
                    csv.append("\"${it.idTransfer}\",\"${it.tanggal}\",\"${it.jam}\",\"${it.nomorAsset}\",\"${it.namaAsset}\",\"${it.estateAsal}\",\"${it.estateTujuan}\",\"${it.penyerah}\",\"[Signature]\",\"${it.penerima}\",\"[Signature]\",\"${it.fotoAsset}\",\"${it.fotoSerahTerima}\",${it.latitude},${it.longitude},\"${it.status}\"\n")
                }
            }
            "REQUEST_MOBIL" -> {
                csv.append("ID,Nama,Estate Asal,Tujuan,Keperluan,Tanggal,Jam Berangkat,Jam Kembali,Driver,Kendaraan,Foto Kilometer Awal,Foto Kilometer Akhir,Status\n")
                (dataList as List<RequestMobilEntity>).forEach {
                    csv.append("\"${it.id}\",\"${it.nama}\",\"${it.estateAsal}\",\"${it.tujuan}\",\"${it.keperluan}\",\"${it.tanggal}\",\"${it.jamBerangkat}\",\"${it.jamKembali}\",\"${it.driver}\",\"${it.kendaraan}\",\"${it.fotoKilometerAwal}\",\"${it.fotoKilometerAkhir}\",\"${it.status}\"\n")
                }
            }
            else -> {
                csv.append("Data\n")
                dataList.forEach { csv.append("\"${it.toString()}\"\n") }
            }
        }
        return csv.toString()
    }
}
