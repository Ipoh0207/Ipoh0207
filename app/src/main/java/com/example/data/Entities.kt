package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val nama: String,
    val nik: String,
    val jabatan: String,
    val departemen: String,
    val estate: String,
    val status: String
)

@Entity(tableName = "estates")
data class EstateEntity(
    @PrimaryKey val namaEstate: String,
    val lokasi: String
)

@Entity(tableName = "rumah")
data class RumahEntity(
    @PrimaryKey val idRumah: String,
    val estate: String,
    val nomorRumah: String,
    val jenisRumah: String,
    val namaMess: String,
    val penghuni: String,
    val statusRumah: String, // e.g., "Baik", "Rusak Ringan", "Rusak Berat"
    val fotoRumah: String // URL or local path
)

@Entity(tableName = "laporan_perumahan")
data class LaporanPerumahanEntity(
    @PrimaryKey val id: String,
    val tanggal: String,
    val jam: String,
    val estate: String,
    val nomorRumah: String,
    val penghuni: String,
    val jenisTemuan: String,
    val deskripsi: String,
    val status: String, // "Open", "Progress", "Closed"
    val pic: String,
    val fotoBefore: String,
    val fotoAfter: String,
    val latitude: Double,
    val longitude: Double,
    val pelapor: String
)

@Entity(tableName = "guest_house")
data class GuestHouseEntity(
    @PrimaryKey val id: String,
    val estate: String,
    val namaGH: String,
    val nomorKamar: String,
    val status: String // "Tersedia", "Terisi", "Maintenance"
)

@Entity(tableName = "booking_gh")
data class BookingGHEntity(
    @PrimaryKey val idBooking: String,
    val nama: String,
    val nik: String,
    val estateAsal: String,
    val estateTujuan: String,
    val guestHouse: String,
    val nomorKamar: String,
    val checkIn: String,
    val checkOut: String,
    val lamaMenginap: Int,
    val status: String, // "Booked", "Checked In", "Checked Out"
    val fotoCheckIn: String,
    val fotoCheckOut: String
)

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey val nomorAsset: String,
    val namaAsset: String,
    val kategori: String,
    val merk: String,
    val serialNumber: String,
    val estate: String,
    val lokasi: String,
    val kondisi: String, // "Baik", "Rusak", "Hilang"
    val fotoAsset: String,
    val qrCode: String
)

@Entity(tableName = "transfer_asset")
data class TransferAssetEntity(
    @PrimaryKey val idTransfer: String,
    val tanggal: String,
    val jam: String,
    val nomorAsset: String,
    val namaAsset: String,
    val estateAsal: String,
    val estateTujuan: String,
    val penyerah: String,
    val ttdPenyerah: String, // Base64 representation of digital signature
    val penerima: String,
    val ttdPenerima: String, // Base64 representation of digital signature
    val fotoAsset: String,
    val fotoSerahTerima: String,
    val latitude: Double,
    val longitude: Double,
    val status: String // "Pending", "Completed"
)

@Entity(tableName = "request_mobil")
data class RequestMobilEntity(
    @PrimaryKey val id: String,
    val nama: String,
    val estateAsal: String,
    val tujuan: String,
    val keperluan: String,
    val tanggal: String,
    val jamBerangkat: String,
    val jamKembali: String,
    val driver: String,
    val kendaraan: String,
    val fotoKilometerAwal: String,
    val fotoKilometerAkhir: String,
    val status: String // "Pending", "Approved", "Completed", "Rejected"
)

@Entity(tableName = "notifikasi")
data class NotifikasiEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tanggal: String,
    val nama: String,
    val judul: String,
    val isi: String,
    val status: String // "Unread", "Read"
)

@Entity(tableName = "log_aktivitas")
data class LogAktivitasEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tanggal: String,
    val jam: String,
    val nama: String,
    val aktivitas: String,
    val modul: String
)
