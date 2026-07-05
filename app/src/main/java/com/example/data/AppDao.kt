package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- USER ---
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    // --- ESTATE ---
    @Query("SELECT * FROM estates")
    fun getAllEstatesFlow(): Flow<List<EstateEntity>>

    @Query("SELECT * FROM estates")
    suspend fun getAllEstates(): List<EstateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstate(estate: EstateEntity)

    // --- RUMAH ---
    @Query("SELECT * FROM rumah")
    fun getAllRumahFlow(): Flow<List<RumahEntity>>

    @Query("SELECT * FROM rumah WHERE estate = :estate")
    fun getRumahByEstateFlow(estate: String): Flow<List<RumahEntity>>

    @Query("SELECT * FROM rumah WHERE idRumah = :id LIMIT 1")
    suspend fun getRumahById(id: String): RumahEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRumah(rumah: RumahEntity)

    // --- LAPORAN PERUMAHAN ---
    @Query("SELECT * FROM laporan_perumahan ORDER BY tanggal DESC, jam DESC")
    fun getAllLaporanFlow(): Flow<List<LaporanPerumahanEntity>>

    @Query("SELECT * FROM laporan_perumahan WHERE id = :id LIMIT 1")
    suspend fun getLaporanById(id: String): LaporanPerumahanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaporan(laporan: LaporanPerumahanEntity)

    // --- GUEST HOUSE ---
    @Query("SELECT * FROM guest_house")
    fun getAllGuestHousesFlow(): Flow<List<GuestHouseEntity>>

    @Query("SELECT * FROM guest_house WHERE id = :id LIMIT 1")
    suspend fun getGuestHouseById(id: String): GuestHouseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuestHouse(guestHouse: GuestHouseEntity)

    // --- BOOKING GH ---
    @Query("SELECT * FROM booking_gh ORDER BY checkIn DESC")
    fun getAllBookingsFlow(): Flow<List<BookingGHEntity>>

    @Query("SELECT * FROM booking_gh WHERE idBooking = :id LIMIT 1")
    suspend fun getBookingById(id: String): BookingGHEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingGHEntity)

    // --- ASSET ---
    @Query("SELECT * FROM assets")
    fun getAllAssetsFlow(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE nomorAsset = :nomor LIMIT 1")
    suspend fun getAssetByNomor(nomor: String): AssetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity)

    // --- TRANSFER ASSET ---
    @Query("SELECT * FROM transfer_asset ORDER BY tanggal DESC, jam DESC")
    fun getAllTransfersFlow(): Flow<List<TransferAssetEntity>>

    @Query("SELECT * FROM transfer_asset WHERE idTransfer = :id LIMIT 1")
    suspend fun getTransferById(id: String): TransferAssetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: TransferAssetEntity)

    // --- REQUEST MOBIL ---
    @Query("SELECT * FROM request_mobil ORDER BY tanggal DESC")
    fun getAllCarRequestsFlow(): Flow<List<RequestMobilEntity>>

    @Query("SELECT * FROM request_mobil WHERE id = :id LIMIT 1")
    suspend fun getCarRequestById(id: String): RequestMobilEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarRequest(request: RequestMobilEntity)

    // --- NOTIFIKASI ---
    @Query("SELECT * FROM notifikasi ORDER BY tanggal DESC")
    fun getAllNotificationsFlow(): Flow<List<NotifikasiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotifikasiEntity)

    @Query("UPDATE notifikasi SET status = 'Read' WHERE id = :id")
    suspend fun markNotificationAsRead(id: Int)

    // --- LOG AKTIVITAS ---
    @Query("SELECT * FROM log_aktivitas ORDER BY tanggal DESC, jam DESC")
    fun getAllLogsFlow(): Flow<List<LogAktivitasEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogAktivitasEntity)
}
