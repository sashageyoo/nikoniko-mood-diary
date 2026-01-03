# nikoniko - mood diary
Kelompok 8
- Arnando Michael Gtg (1313623014)
- Sabiyan Avril Chandrakanta (1313623020)
- Dinda Febriyanti Beta Kore (1313623061)

# 🍡 NikoNiko - Mood Diary

**NikoNiko** adalah aplikasi l yang menggabungkan fitur *Mood Tracker* dan *Habit Tracker* dengan pendekatan gamifikasi. Aplikasi ini dirancang untuk membantu pengguna membangun kebiasaan baik sambil memantau kondisi emosional mereka setiap hari dengan bantuan maskot interaktif bernama **Niko**.

---

## ✨ Fitur Utama

### 1. Mood Diary Interaktif
Bukan sekadar catatan, NikoNiko memungkinkan pengguna mendokumentasikan perasaan dengan detail:
- **Faktor Eksternal:** Menghubungkan mood dengan cuaca (Weather) dan interaksi sosial/aktivitas (Family, Friends, Work, dll).
- **Visual Evidence:** Dukungan untuk menambahkan catatan teks dan foto pada setiap entri mood.
- **Mood History:** Melihat kembali perjalanan emosional melalui histori yang bisa dikelola.

### 2. Weighted Habit Tracker
Sistem kebiasaan yang terukur. Setiap habit memiliki bobot poin tertentu yang berkontribusi pada kebahagiaan Niko:
| Habit | Bobot Poin |
| :--- | :---: |
| Sleep Early | 30 |
| Wake Up Early | 20 |
| Daily Pray | 20 |
| Exercise | 10 |
| Self Care | 10 |
| Hobbies | 10 |
| **Total Max** | **100** |

### 3. Maskot Niko (Emotional Feedback)
Niko adalah mochi putih yang ekspresinya bergantung pada seberapa rajin kamu menyelesaikan habit harian:
- **80% - 100%**: 🌟 *Very Happy*
- **60% - 79%**: 🙂 *Happy*
- **40% - 59%**: 😐 *Okay*
- **20% - 39%**: ☹️ *Sad*
- **1% - 19%**: 😵‍💫 *Exhausted*
- **0%**: 💀 *Dead* (Reset setiap pagi)

### 4. Insight & Analytics
Dapatkan analisis mendalam mengenai diri kamu melalui halaman profil:
- **Habit Progress:** Statistik keberhasilan habit (7 hari terakhir, 1 bulan, *all time*).
- **Mood Score:** Rata-rata skor kebahagiaan (Skala 0-10).
- **Mood Count:** Grafik batang yang menunjukkan frekuensi emosi yang paling sering dirasakan.

---

## 🚀 Alur Pengguna (User Journey)

1. **Onboarding:** Pemberian izin notifikasi agar Niko bisa memberikan reminder.
2. **Intro Slides:** Perkenalan filosofi NikoNiko melalui 4 slide edukatif.
3. **Biodata:** Pengisian profil dasar (Nama, Tanggal Lahir, Jenis Kelamin).
4. **Dashboard:** Pusat aktivitas untuk menceklis habit dan menambah mood.
5. **Add Mood:** Proses input mood yang detail (Skala emosi -> Faktor -> Catatan -> Simpan).

---

## 🛠️ Logic & Perhitungan

### Skor Mood
Rata-rata skor dihitung berdasarkan bobot berikut:
- **Happy!**: 10 | **Good**: 8 | **Okay**: 6 | **Sad**: 3 | **Terrible**: 0

**Rumus:** `Total Skor / Jumlah Hari = Average Score`

---

## 📱 Tampilan Antarmuka
- **Home:** Progress bar, Maskot Niko, Mood Section, & Habit Cards.
- **Add Mood:** Interface pemilihan emoji, cuaca, sosial, dan upload media.
- **Profile:** Statistik perkembangan diri dan grafik batang mood.
