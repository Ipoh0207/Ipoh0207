package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppRepository(private val appDao: AppDao) {

    // Users
    val allUsers: Flow<List<UserEntity>> = appDao.getAllUsers()
    suspend fun getUserByEmail(email: String): UserEntity? = appDao.getUserByEmail(email)
    suspend fun insertUser(user: UserEntity) = appDao.insertUser(user)

    // Estates
    val allEstatesFlow: Flow<List<EstateEntity>> = appDao.getAllEstatesFlow()
    suspend fun getAllEstates(): List<EstateEntity> = appDao.getAllEstates()
    suspend fun insertEstate(estate: EstateEntity) = appDao.insertEstate(estate)

    // Rumah
    val allRumahFlow: Flow<List<RumahEntity>> = appDao.getAllRumahFlow()
    fun getRumahByEstateFlow(estate: String): Flow<List<RumahEntity>> = appDao.getRumahByEstateFlow(estate)
    suspend fun getRumahById(id: String): RumahEntity? = appDao.getRumahById(id)
    suspend fun insertRumah(rumah: RumahEntity) = appDao.insertRumah(rumah)

    // Laporan Perumahan
    val allLaporanFlow: Flow<List<LaporanPerumahanEntity>> = appDao.getAllLaporanFlow()
    suspend fun getLaporanById(id: String): LaporanPerumahanEntity? = appDao.getLaporanById(id)
    suspend fun insertLaporan(laporan: LaporanPerumahanEntity) = appDao.insertLaporan(laporan)

    // Guest House
    val allGuestHousesFlow: Flow<List<GuestHouseEntity>> = appDao.getAllGuestHousesFlow()
    suspend fun getGuestHouseById(id: String): GuestHouseEntity? = appDao.getGuestHouseById(id)
    suspend fun insertGuestHouse(guestHouse: GuestHouseEntity) = appDao.insertGuestHouse(guestHouse)

    // Booking GH
    val allBookingsFlow: Flow<List<BookingGHEntity>> = appDao.getAllBookingsFlow()
    suspend fun getBookingById(id: String): BookingGHEntity? = appDao.getBookingById(id)
    suspend fun insertBooking(booking: BookingGHEntity) = appDao.insertBooking(booking)

    // Assets
    val allAssetsFlow: Flow<List<AssetEntity>> = appDao.getAllAssetsFlow()
    suspend fun getAssetByNomor(nomor: String): AssetEntity? = appDao.getAssetByNomor(nomor)
    suspend fun insertAsset(asset: AssetEntity) = appDao.insertAsset(asset)

    // Transfer Asset
    val allTransfersFlow: Flow<List<TransferAssetEntity>> = appDao.getAllTransfersFlow()
    suspend fun getTransferById(id: String): TransferAssetEntity? = appDao.getTransferById(id)
    suspend fun insertTransfer(transfer: TransferAssetEntity) = appDao.insertTransfer(transfer)

    // Request Mobil
    val allCarRequestsFlow: Flow<List<RequestMobilEntity>> = appDao.getAllCarRequestsFlow()
    suspend fun getCarRequestById(id: String): RequestMobilEntity? = appDao.getCarRequestById(id)
    suspend fun insertCarRequest(request: RequestMobilEntity) = appDao.insertCarRequest(request)

    // Notifikasi
    val allNotificationsFlow: Flow<List<NotifikasiEntity>> = appDao.getAllNotificationsFlow()
    suspend fun insertNotification(notification: NotifikasiEntity) = appDao.insertNotification(notification)
    suspend fun markNotificationAsRead(id: Int) = appDao.markNotificationAsRead(id)

    // Log Aktivitas
    val allLogsFlow: Flow<List<LogAktivitasEntity>> = appDao.getAllLogsFlow()
    suspend fun insertLog(log: LogAktivitasEntity) = appDao.insertLog(log)

    suspend fun addLog(nama: String, aktivitas: String, modul: String) {
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        insertLog(
            LogAktivitasEntity(
                tanggal = dateStr,
                jam = timeStr,
                nama = nama,
                aktivitas = aktivitas,
                modul = modul
            )
        )
    }

    suspend fun addNotification(nama: String, judul: String, isi: String) {
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        insertNotification(
            NotifikasiEntity(
                tanggal = dateStr,
                nama = nama,
                judul = judul,
                isi = isi,
                status = "Unread"
            )
        )
    }

    // Prepopulate DB if empty
    suspend fun prepopulateIfEmpty() {
        val estates = appDao.getAllEstates()
        if (estates.isEmpty()) {
            Log.d("AppRepository", "Database empty. Seeding starter data...")

            // 1. Seed Estates
            val listEstates = listOf(
                EstateEntity("Pandan Estate", "Riau, Kampar"),
                EstateEntity("Tamiang Estate", "Aceh, Tamiang"),
                EstateEntity("Sinar Riau Estate", "Riau, Pelalawan"),
                EstateEntity("Bukit Sentosa Estate", "Kalimantan Tengah")
            )
            for (est in listEstates) {
                appDao.insertEstate(est)
            }

            // 2. Seed Users
            val listUsers = listOf(
                UserEntity("admin@company.com", "Adi Wijaya", "NIK-10024", "Senior Manager", "Operations", "Pandan Estate", "Aktif"),
                UserEntity("user@company.com", "Siti Aminah", "NIK-10025", "Supervisor HR", "HR", "Tamiang Estate", "Aktif"),
                UserEntity("hannifasyafira99@gmail.com", "Hannifa Syafira", "NIK-10026", "Operations Lead", "Operations", "Sinar Riau Estate", "Aktif"),
                UserEntity("spv@company.com", "Budi Santoso", "NIK-10027", "Field Supervisor", "Estate Operations", "Bukit Sentosa Estate", "Aktif")
            )
            for (usr in listUsers) {
                appDao.insertUser(usr)
            }

            // 3. Seed Rumah (Housing)
            val listRumah = listOf(
                RumahEntity("RUM-001", "Pandan Estate", "No. 12", "Tipe 36", "Mess Cendrawasih", "Yusuf K.", "Baik", ""),
                RumahEntity("RUM-002", "Pandan Estate", "No. 14", "Tipe 45", "Mess Rajawali", "Sutrisno", "Rusak Ringan", ""),
                RumahEntity("RUM-003", "Tamiang Estate", "No. A5", "Tipe 36", "Mess Melati", "Rahmat", "Baik", ""),
                RumahEntity("RUM-004", "Sinar Riau Estate", "No. B10", "Tipe 54", "Mess Utama", "Lina S.", "Baik", ""),
                RumahEntity("RUM-005", "Bukit Sentosa Estate", "No. C2", "Tipe 36", "Mess Engineering", "Heru P.", "Rusak Berat", "")
            )
            for (rum in listRumah) {
                appDao.insertRumah(rum)
            }

            // 4. Seed Guest Houses
            val listGH = listOf(
                GuestHouseEntity("GH-M-101", "Pandan Estate", "Meranti Guest House", "Kamar 101", "Tersedia"),
                GuestHouseEntity("GH-M-102", "Pandan Estate", "Meranti Guest House", "Kamar 102", "Tersedia"),
                GuestHouseEntity("GH-C-201", "Tamiang Estate", "Cendana Guest House", "Kamar 201", "Tersedia"),
                GuestHouseEntity("GH-C-202", "Tamiang Estate", "Cendana Guest House", "Kamar 202", "Tersedia"),
                GuestHouseEntity("GH-S-301", "Sinar Riau Estate", "Sinar Riau Guest House", "Kamar 301", "Tersedia")
            )
            for (gh in listGH) {
                appDao.insertGuestHouse(gh)
            }

            // 5. Seed Assets
            val listAssets = listOf(
                AssetEntity("AST-001", "Laptop ThinkPad L14", "IT Equipment", "Lenovo", "S/N: PF-34X2A", "Pandan Estate", "Kantor Estate", "Baik", "", "AST-001"),
                AssetEntity("AST-002", "Printer LaserJet Pro", "IT Equipment", "HP", "S/N: HP-99A11", "Tamiang Estate", "Logistics Office", "Baik", "", "AST-002"),
                AssetEntity("AST-003", "Genset Honda 5KVA", "Machinery", "Honda", "S/N: HN-88229", "Sinar Riau Estate", "Workshop", "Baik", "", "AST-003"),
                AssetEntity("AST-004", "Split AC 1.5 PK", "Facility", "Daikin", "S/N: DK-72183", "Bukit Sentosa Estate", "Guest House Room 1", "Baik", "", "AST-004")
            )
            for (ast in listAssets) {
                appDao.insertAsset(ast)
            }

            // 6. Seed Laporan
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            appDao.insertLaporan(
                LaporanPerumahanEntity(
                    id = "REP-1001",
                    tanggal = dateStr,
                    jam = "08:30",
                    estate = "Pandan Estate",
                    nomorRumah = "No. 14",
                    penghuni = "Sutrisno",
                    jenisTemuan = "Kebocoran Atap",
                    deskripsi = "Atap dapur bocor cukup deras saat hujan lebat.",
                    status = "Progress",
                    pic = "Handoko (Sipil)",
                    fotoBefore = "local_before_leak",
                    fotoAfter = "",
                    latitude = 0.4907,
                    longitude = 101.4478,
                    pelapor = "admin@company.com"
                )
            )

            // Seed Notifications
            addNotification("System", "Welcome to Adindo HR Operations", "Semua modul siap digunakan secara offline. Konfigurasikan URL Spreadsheet Anda di Pengaturan untuk sinkronisasi.")
            addLog("System", "Database initialized and seeded", "Sistem")
        }
    }
}
