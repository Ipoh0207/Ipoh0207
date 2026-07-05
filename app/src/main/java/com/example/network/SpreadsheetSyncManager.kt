package com.example.network

import android.util.Log
import com.example.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class SpreadsheetSyncManager {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    // Data structures for JSON serialization
    data class SyncPayload(
        val action: String,
        val users: List<UserEntity>? = null,
        val estates: List<EstateEntity>? = null,
        val rumah: List<RumahEntity>? = null,
        val laporan_perumahan: List<LaporanPerumahanEntity>? = null,
        val guest_house: List<GuestHouseEntity>? = null,
        val booking_gh: List<BookingGHEntity>? = null,
        val assets: List<AssetEntity>? = null,
        val transfer_asset: List<TransferAssetEntity>? = null,
        val request_mobil: List<RequestMobilEntity>? = null,
        val notifikasi: List<NotifikasiEntity>? = null,
        val log_aktivitas: List<LogAktivitasEntity>? = null
    )

    suspend fun testConnection(url: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$url?action=ping")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    if (body.contains("pong") || body.contains("success") || response.code == 200) {
                        Pair(true, "Koneksi Berhasil! Google Apps Script siap menerima data.")
                    } else {
                        Pair(true, "Terhubung, respon server: $body")
                    }
                } else {
                    Pair(false, "Error: Respon server kode ${response.code}")
                }
            }
        } catch (e: Exception) {
            Log.e("SyncManager", "Connection test failed", e)
            Pair(false, "Gagal terhubung: ${e.localizedMessage ?: "Timeout / URL salah"}")
        }
    }

    suspend fun exportData(url: String, repository: AppRepository): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            // Load all local data synchronously from Flow
            val users = repository.allUsers.first()
            val estates = repository.allEstatesFlow.first()
            val rumah = repository.allRumahFlow.first()
            val laporan = repository.allLaporanFlow.first()
            val guesthouses = repository.allGuestHousesFlow.first()
            val bookings = repository.allBookingsFlow.first()
            val assets = repository.allAssetsFlow.first()
            val transfers = repository.allTransfersFlow.first()
            val requests = repository.allCarRequestsFlow.first()
            val notifications = repository.allNotificationsFlow.first()
            val logs = repository.allLogsFlow.first()

            val payload = SyncPayload(
                action = "export",
                users = users,
                estates = estates,
                rumah = rumah,
                laporan_perumahan = laporan,
                guest_house = guesthouses,
                booking_gh = bookings,
                assets = assets,
                transfer_asset = transfers,
                request_mobil = requests,
                notifikasi = notifications,
                log_aktivitas = logs
            )

            val adapter = moshi.adapter(SyncPayload::class.java)
            val jsonString = adapter.toJson(payload)

            val body = jsonString.toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respBody = response.body?.string() ?: ""
                    Pair(true, "Export Sukses! Semua data lokal telah diunggah ke Google Spreadsheet.")
                } else {
                    Pair(false, "Export Gagal: Kode ${response.code} dari server.")
                }
            }
        } catch (e: Exception) {
            Log.e("SyncManager", "Export failed", e)
            Pair(false, "Gagal mengunggah data: ${e.localizedMessage ?: "Network error"}")
        }
    }

    suspend fun importData(url: String, repository: AppRepository): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$url?action=import")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Pair(false, "Import Gagal: Kode ${response.code} dari server.")
                }

                val jsonString = response.body?.string() ?: ""
                val adapter = moshi.adapter(SyncPayload::class.java)
                val data = adapter.fromJson(jsonString)

                if (data == null) {
                    return@withContext Pair(false, "Gagal membaca format data dari server.")
                }

                // Write into Local Database
                data.users?.forEach { repository.insertUser(it) }
                data.estates?.forEach { repository.insertEstate(it) }
                data.rumah?.forEach { repository.insertRumah(it) }
                data.laporan_perumahan?.forEach { repository.insertLaporan(it) }
                data.guest_house?.forEach { repository.insertGuestHouse(it) }
                data.booking_gh?.forEach { repository.insertBooking(it) }
                data.assets?.forEach { repository.insertAsset(it) }
                data.transfer_asset?.forEach { repository.insertTransfer(it) }
                data.request_mobil?.forEach { repository.insertCarRequest(it) }
                data.notifikasi?.forEach { repository.insertNotification(it) }
                data.log_aktivitas?.forEach { repository.insertLog(it) }

                repository.addLog("System Sync", "Import data dari Google Spreadsheet sukses", "Sistem")
                Pair(true, "Import Sukses! Semua data dari Google Spreadsheet telah disinkronkan ke lokal.")
            }
        } catch (e: Exception) {
            Log.e("SyncManager", "Import failed", e)
            Pair(false, "Gagal mengunduh data: ${e.localizedMessage ?: "Network error"}")
        }
    }

    // Google Apps Script template for user copy-paste
    fun getGoogleAppsScriptTemplate(): String {
        return """
// PT ADINDO HUTANI LESTARI HR OPERATIONS - GOOGLE APPS SCRIPT WEB API
// Cara Setup:
// 1. Buka Google Spreadsheet Anda.
// 2. Klik Ekstensi -> Apps Script.
// 3. Hapus kode bawaan dan ganti dengan seluruh kode di bawah ini.
// 4. Klik "Terapkan" -> "Penerapan Baru" -> Pilih jenis "Aplikasi Web".
// 5. Atur "Siapa yang memiliki akses" menjadi "Siapa saja" (Anyone).
// 6. Klik Terapkan, beri izin akses, lalu salin URL Aplikasi Web yang diberikan.
// 7. Tempel URL tersebut ke menu Pengaturan di aplikasi ADINDO HR Anda.

function doGet(e) {
  var action = e.parameter.action;
  
  if (action === "ping") {
    return ContentService.createTextOutput("pong")
      .setMimeType(ContentService.MimeType.TEXT);
  }
  
  if (action === "import") {
    return ContentService.createTextOutput(JSON.stringify(importAllData()))
      .setMimeType(ContentService.MimeType.JSON);
  }
  
  return ContentService.createTextOutput("PT Adindo Hutani Lestari GAS API is Running.")
    .setMimeType(ContentService.MimeType.TEXT);
}

function doPost(e) {
  try {
    var postData = JSON.parse(e.postData.contents);
    var action = postData.action;
    
    if (action === "export") {
      exportAllData(postData);
      return ContentService.createTextOutput(JSON.stringify({status: "success", message: "Data exported successfully"}))
        .setMimeType(ContentService.MimeType.JSON);
    }
    
    return ContentService.createTextOutput(JSON.stringify({status: "error", message: "Invalid action"}))
      .setMimeType(ContentService.MimeType.JSON);
  } catch(err) {
    return ContentService.createTextOutput(JSON.stringify({status: "error", message: err.toString()}))
      .setMimeType(ContentService.MimeType.JSON);
  }
}

// MEMBACA DATA DARI SPREADSHEET
function importAllData() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  return {
    users: readSheetData(ss, "USER"),
    estates: readSheetData(ss, "ESTATE"),
    rumah: readSheetData(ss, "RUMAH"),
    laporan_perumahan: readSheetData(ss, "LAPORAN_PERUMAHAN"),
    guest_house: readSheetData(ss, "GUEST_HOUSE"),
    booking_gh: readSheetData(ss, "BOOKING_GH"),
    assets: readSheetData(ss, "ASSET"),
    transfer_asset: readSheetData(ss, "TRANSFER_ASSET"),
    request_mobil: readSheetData(ss, "REQUEST_MOBIL"),
    notifikasi: readSheetData(ss, "NOTIFIKASI"),
    log_aktivitas: readSheetData(ss, "LOG_AKTIVITAS")
  };
}

function readSheetData(ss, sheetName) {
  var sheet = ss.getSheetByName(sheetName);
  if (!sheet) return [];
  
  var rows = sheet.getDataRange().getValues();
  if (rows.length <= 1) return [];
  
  var headers = rows[0];
  var data = [];
  
  for (var i = 1; i < rows.length; i++) {
    var row = rows[i];
    var item = {};
    for (var j = 0; j < headers.length; j++) {
      var key = headers[j].toString().trim().replace(/\s+/g, '_').toLowerCase();
      // Handle custom mapping if key name in Sheet differs from Entity class
      if (key === "id_rumah") key = "idRumah";
      if (key === "nama_estate") key = "namaEstate";
      if (key === "jenis_rumah") key = "jenisRumah";
      if (key === "nama_mess") key = "namaMess";
      if (key === "status_rumah") key = "statusRumah";
      if (key === "foto_rumah") key = "fotoRumah";
      if (key === "jenis_temuan") key = "jenisTemuan";
      if (key === "foto_before") key = "fotoBefore";
      if (key === "foto_after") key = "fotoAfter";
      if (key === "nama_gh") key = "namaGH";
      if (key === "nomor_kamar") key = "nomorKamar";
      if (key === "id_booking") key = "idBooking";
      if (key === "estate_asal") key = "estateAsal";
      if (key === "estate_tujuan") key = "estateTujuan";
      if (key === "guest_house") key = "guestHouse";
      if (key === "check_in") key = "checkIn";
      if (key === "check_out") key = "checkOut";
      if (key === "lama_menginap") key = "lamaMenginap";
      if (key === "foto_check_in") key = "fotoCheckIn";
      if (key === "foto_check_out") key = "fotoCheckOut";
      if (key === "nomor_asset") key = "nomorAsset";
      if (key === "nama_asset") key = "namaAsset";
      if (key === "serial_number") key = "serialNumber";
      if (key === "foto_asset") key = "fotoAsset";
      if (key === "qr_code") key = "qrCode";
      if (key === "id_transfer") key = "idTransfer";
      if (key === "ttd_penyerah") key = "ttdPenyerah";
      if (key === "ttd_penerima") key = "ttdPenerima";
      if (key === "foto_serah_terima") key = "fotoSerahTerima";
      if (key === "jam_berangkat") key = "jamBerangkat";
      if (key === "jam_kembali") key = "jamKembali";
      if (key === "foto_kilometer_awal") key = "fotoKilometerAwal";
      if (key === "foto_kilometer_akhir") key = "fotoKilometerAkhir";
      
      item[key] = row[j];
    }
    data.push(item);
  }
  return data;
}

// MENULIS DATA KE SPREADSHEET (OVERWRITE)
function exportAllData(payload) {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  
  writeToSheet(ss, "USER", payload.users, ["Nama", "Email", "NIK", "Jabatan", "Departemen", "Estate", "Status"]);
  writeToSheet(ss, "ESTATE", payload.estates, ["Nama Estate", "Lokasi"]);
  writeToSheet(ss, "RUMAH", payload.rumah, ["ID Rumah", "Estate", "Nomor Rumah", "Jenis Rumah", "Nama Mess", "Penghuni", "Status Rumah", "Foto Rumah"]);
  writeToSheet(ss, "LAPORAN_PERUMAHAN", payload.laporan_perumahan, ["ID", "Tanggal", "Jam", "Estate", "Nomor Rumah", "Penghuni", "Jenis Temuan", "Deskripsi", "Status", "PIC", "Foto Before", "Foto After", "Latitude", "Longitude", "Pelapor"]);
  writeToSheet(ss, "GUEST_HOUSE", payload.guest_house, ["ID", "Estate", "Nama GH", "Nomor Kamar", "Status"]);
  writeToSheet(ss, "BOOKING_GH", payload.booking_gh, ["ID Booking", "Nama", "NIK", "Estate Asal", "Estate Tujuan", "Guest House", "Nomor Kamar", "Check In", "Check Out", "Lama Menginap", "Status", "Foto Check In", "Foto Check Out"]);
  writeToSheet(ss, "ASSET", payload.assets, ["Nomor Asset", "Nama Asset", "Kategori", "Merk", "Serial Number", "Estate", "Lokasi", "Kondisi", "Foto Asset", "QR Code"]);
  writeToSheet(ss, "TRANSFER_ASSET", payload.transfer_asset, ["ID Transfer", "Tanggal", "Jam", "Nomor Asset", "Nama Asset", "Estate Asal", "Estate Tujuan", "Penyerah", "TTD Penyerah", "Penerima", "TTD Penerima", "Foto Asset", "Foto Serah Terima", "Latitude", "Longitude", "Status"]);
  writeToSheet(ss, "REQUEST_MOBIL", payload.request_mobil, ["ID", "Nama", "Estate Asal", "Tujuan", "Keperluan", "Tanggal", "Jam Berangkat", "Jam Kembali", "Driver", "Kendaraan", "Foto Kilometer Awal", "Foto Kilometer Akhir", "Status"]);
  writeToSheet(ss, "NOTIFIKASI", payload.notifikasi, ["Tanggal", "Nama", "Judul", "Isi", "Status"]);
  writeToSheet(ss, "LOG_AKTIVITAS", payload.log_aktivitas, ["Tanggal", "Jam", "Nama", "Aktivitas", "Modul"]);
}

function writeToSheet(ss, sheetName, items, headers) {
  if (!items) return;
  var sheet = ss.getSheetByName(sheetName);
  if (!sheet) {
    sheet = ss.insertSheet(sheetName);
  } else {
    sheet.clear();
  }
  
  sheet.appendRow(headers);
  if (items.length === 0) return;
  
  var values = [];
  for (var i = 0; i < items.length; i++) {
    var item = items[i];
    var row = [];
    for (var j = 0; j < headers.length; j++) {
      var key = headers[j].toString().trim().replace(/\s+/g, '_').toLowerCase();
      
      // Match key back to class property names
      if (key === "id_rumah") key = "idRumah";
      if (key === "nama_estate") key = "namaEstate";
      if (key === "jenis_rumah") key = "jenisRumah";
      if (key === "nama_mess") key = "namaMess";
      if (key === "status_rumah") key = "statusRumah";
      if (key === "foto_rumah") key = "fotoRumah";
      if (key === "jenis_temuan") key = "jenisTemuan";
      if (key === "foto_before") key = "fotoBefore";
      if (key === "foto_after") key = "fotoAfter";
      if (key === "nama_gh") key = "namaGH";
      if (key === "nomor_kamar") key = "nomorKamar";
      if (key === "id_booking") key = "idBooking";
      if (key === "estate_asal") key = "estateAsal";
      if (key === "estate_tujuan") key = "estateTujuan";
      if (key === "guest_house") key = "guestHouse";
      if (key === "check_in") key = "checkIn";
      if (key === "check_out") key = "checkOut";
      if (key === "lama_menginap") key = "lamaMenginap";
      if (key === "foto_check_in") key = "fotoCheckIn";
      if (key === "foto_check_out") key = "fotoCheckOut";
      if (key === "nomor_asset") key = "nomorAsset";
      if (key === "nama_asset") key = "namaAsset";
      if (key === "serial_number") key = "serialNumber";
      if (key === "foto_asset") key = "fotoAsset";
      if (key === "qr_code") key = "qrCode";
      if (key === "id_transfer") key = "idTransfer";
      if (key === "ttd_penyerah") key = "ttdPenyerah";
      if (key === "ttd_penerima") key = "ttdPenerima";
      if (key === "foto_serah_terima") key = "fotoSerahTerima";
      if (key === "jam_berangkat") key = "jamBerangkat";
      if (key === "jam_kembali") key = "jamKembali";
      if (key === "foto_kilometer_awal") key = "fotoKilometerAwal";
      if (key === "foto_kilometer_akhir") key = "fotoKilometerAkhir";
      
      var val = item[key];
      row.push(val !== undefined && val !== null ? val : "");
    }
    values.push(row);
  }
  
  sheet.getRange(2, 1, values.length, headers.length).setValues(values);
}
        """.trimIndent()
    }
}
