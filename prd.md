# Product Requirements Document (PRD)

# Rumah-Lelang— Platform Rumah Lelang Online (REST API)

**Versi:** 1.0
**Tanggal:** 4 September 2026
**Jenis Proyek:** Mini Project Backend (Portfolio/Learning)
**Target Skill:** Spring IoC/DI, Java Stream API, Native SQL Query (Intermediate), Containerization & Microservices

---

## 1. Latar Belakang & Tujuan

Rumah lelang konvensional sering terkendala jangkauan peserta dan transparansi proses tawar-menawar (bidding). **Rumah-Lelang** adalah backend REST API untuk platform lelang online di mana penjual (seller) dapat mendaftarkan barang, dan pembeli (bidder) dapat mengikuti lelang secara real-time hingga waktu lelang berakhir, dengan penawar tertinggi otomatis menjadi pemenang.

Proyek ini dibuat sebagai _mini project_ untuk mendemonstrasikan penguasaan:

- **Spring IoC & Dependency Injection** — arsitektur service/repository yang loosely-coupled.
- **Java Stream API** — pengolahan data koleksi (filter bid tertinggi, rekap laporan, agregasi).
- **Native SQL Query (Intermediate)** — query kompleks (JOIN, subquery, window function) untuk kebutuhan reporting/leaderboard yang sulit diekspresikan lewat JPQL biasa.
- **Containerization & Microservices** — pemisahan layanan (Auction Service, Payment Service, Notification Service) yang dikemas dalam Docker & diorkestrasi dengan Docker Compose.

---

## 2. Target Pengguna

| Role       | Deskripsi                                                           |
| ---------- | ------------------------------------------------------------------- |
| **Admin**  | Mengelola kategori barang, memverifikasi seller, memantau transaksi |
| **Seller** | Mendaftarkan item lelang, menentukan harga awal & waktu lelang      |
| **Bidder** | Melakukan penawaran (bid) terhadap item yang sedang berlangsung     |
| **Guest**  | Melihat daftar lelang tanpa login                                   |

---

## 3. Lingkup Fitur (Scope)

### 3.1 Modul Utama

1. **Auth & User Management** — registrasi, login (JWT), role-based access (ADMIN/SELLER/BIDDER)
2. **Item Lelang (Auction Item)** — CRUD barang, upload gambar (path/URL), kategori, status (`DRAFT`, `SCHEDULED`, `ONGOING`, `ENDED`, `SOLD`, `CANCELLED`)
3. **Proses Bidding** — submit bid, validasi bid harus lebih tinggi dari bid tertinggi saat ini, riwayat bid per item
4. **Penutupan Lelang & Penentuan Pemenang** — scheduled job otomatis menutup lelang saat waktu habis, menentukan pemenang (bid tertinggi)
5. **Pembayaran (Payment Service - terpisah)** — simulasi invoice untuk pemenang lelang
6. **Notifikasi (Notification Service - terpisah)** — event saat outbid (kalah tawaran), lelang dimenangkan, lelang berakhir
7. **Laporan & Leaderboard (Reporting)** — top bidder, item terlaris, riwayat transaksi seller (menggunakan Native SQL)

### 3.2 Di Luar Scope (Out of Scope)

- Payment gateway asli (cukup simulasi status pembayaran)
- Real-time WebSocket (opsional/nice-to-have, bukan wajib)
- Mobile app / frontend UI

---

## 4. Arsitektur & Microservices

Sistem dipecah menjadi 3 service independen, masing-masing punya database sendiri (database-per-service), berkomunikasi via REST (dan opsional message broker untuk notifikasi):

```
                        ┌─────────────────┐
                        │   API Gateway    │  (opsional: Spring Cloud Gateway)
                        └────────┬─────────┘
           ┌───────────────────┼───────────────────┐
           ▼                   ▼                   ▼
 ┌──────────────────┐ ┌──────────────────┐ ┌────────────────────┐
 │ Auction Service   │ │ Payment Service   │ │ Notification Service│
 │ (Users, Items,    │ │ (Invoice, Status  │ │ (Email/Log event   │
 │  Bidding)         │ │  Pembayaran)      │ │  outbid & winner)   │
 │ PostgreSQL: auction_db │ PostgreSQL: payment_db │ (in-memory/queue) │
 └──────────────────┘ └──────────────────┘ └────────────────────┘
```

- **Auction Service** memanggil **Payment Service** via REST saat lelang berakhir untuk membuat invoice pemenang.
- **Auction Service** mempublish event ke **Notification Service** (bisa via REST callback sederhana, atau RabbitMQ jika ingin level lanjut).
- Semua service dikemas sebagai **Docker image** terpisah, dijalankan bersama **PostgreSQL** melalui **Docker Compose**.

---

## 5. Pemetaan ke Kompetensi Teknis

| Kompetensi                           | Implementasi Konkret di Proyek                                                                                                                                                                                                                                                                                |
| ------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Spring IoC/DI**                    | `@Service`, `@Repository`, `@Component` untuk BidValidator, AuctionScheduler, NotificationClient. Constructor injection di seluruh service layer. Interface `PaymentClient` di-inject implementasinya (RestTemplate/WebClient based) agar loosely coupled dan mudah di-mock saat testing.                     |
| **Java Stream API**                  | - Filter bid valid & urutkan bid tertinggi (`stream().filter().sorted().findFirst()`) <br> - Hitung rata-rata kenaikan bid per item (`mapToDouble().average()`) <br> - Group item per kategori & status (`Collectors.groupingBy`) <br> - Rekap top 5 bidder teraktif (`Collectors.toMap` + `Comparator`)      |
| **Native SQL Query (Intermediate)**  | - Query leaderboard bidder dengan `RANK() OVER (PARTITION BY ...)` <br> - Subquery cari bid tertinggi per item (`SELECT MAX(amount) ... GROUP BY item_id`) <br> - JOIN 3 tabel (users, items, bids) untuk laporan riwayat transaksi seller <br> - Query pakai `@Query(nativeQuery = true)` di Spring Data JPA |
| **Containerization & Microservices** | Dockerfile multi-stage per service, `docker-compose.yml` untuk auction-service + payment-service + notification-service + postgres (2 instance/skema terpisah), environment variable via `.env`, health check container                                                                                       |

---

## 6. Data Model (Ringkas)

**Auction Service DB (`auction_db`)**

- `users` (id, name, email, password, role)
- `categories` (id, name)
- `items` (id, title, description, category_id, seller_id, starting_price, current_highest_bid, start_time, end_time, status, image_url)
- `bids` (id, item_id, bidder_id, amount, created_at)

**Payment Service DB (`payment_db`)**

- `invoices` (id, item_id, winner_id, amount, status, created_at)

---

## 7. Contoh Endpoint API

### Auction Service

| Method | Endpoint                                        | Deskripsi                                 | Role         |
| ------ | ----------------------------------------------- | ----------------------------------------- | ------------ |
| POST   | `/api/auth/register`                            | Registrasi user                           | Public       |
| POST   | `/api/auth/login`                               | Login, dapat JWT                          | Public       |
| POST   | `/api/items`                                    | Buat item lelang baru                     | SELLER       |
| GET    | `/api/items?status=ONGOING&category=elektronik` | List lelang berjalan                      | Public       |
| GET    | `/api/items/{id}`                               | Detail item + riwayat bid                 | Public       |
| POST   | `/api/items/{id}/bids`                          | Ajukan penawaran                          | BIDDER       |
| GET    | `/api/items/{id}/bids`                          | Riwayat bid pada item                     | Public       |
| POST   | `/api/items/{id}/close`                         | Tutup lelang manual (fallback)            | ADMIN        |
| GET    | `/api/reports/leaderboard`                      | Top bidder (Native SQL + window function) | ADMIN        |
| GET    | `/api/reports/sellers/{id}/summary`             | Rekap penjualan seller (Native SQL JOIN)  | SELLER/ADMIN |

### Payment Service

| Method | Endpoint                 | Deskripsi                                           |
| ------ | ------------------------ | --------------------------------------------------- |
| POST   | `/api/invoices`          | Dibuat otomatis saat lelang selesai (internal call) |
| GET    | `/api/invoices/{id}`     | Detail invoice                                      |
| PATCH  | `/api/invoices/{id}/pay` | Tandai invoice lunas                                |

### Notification Service

| Method | Endpoint                       | Deskripsi                        |
| ------ | ------------------------------ | -------------------------------- |
| POST   | `/api/notify/outbid`           | Trigger notifikasi kalah tawaran |
| POST   | `/api/notify/winner`           | Trigger notifikasi pemenang      |
| GET    | `/api/notify/history/{userId}` | Riwayat notifikasi user          |

---

## 8. Business Rules Penting

1. Bid baru **harus** lebih besar dari `current_highest_bid` (minimal +5% atau nominal minimum kenaikan).
2. Lelang otomatis ditutup oleh **scheduled task** (`@Scheduled`) saat `end_time` terlampaui.
3. Bila tidak ada bid sama sekali saat lelang berakhir, status menjadi `ENDED` tanpa `SOLD`.
4. Seller tidak boleh bid pada item miliknya sendiri.
5. Setelah lelang `SOLD`, sistem otomatis memanggil Payment Service untuk membuat invoice ke pemenang.

---

## 9. Non-Functional Requirements

- **Keamanan:** Autentikasi JWT, otorisasi berbasis role (Spring Security).
- **Skalabilitas:** Tiap service dapat di-scale independen (stateless, database terpisah).
- **Observability:** Logging terstruktur, endpoint `/actuator/health` tiap service.
- **Portability:** Seluruh service dan database dapat dijalankan dengan satu perintah `docker compose up`.
- **Testing:** Unit test untuk service layer (mock repository via DI), integration test dasar untuk endpoint bidding.

---

## 10. Tech Stack

- **Bahasa/Framework:** sesuaikan dengan eksisting pom.xml (Spring Web, Spring Data JPA, Spring Security, Spring Scheduling) tambahkan sesuai kebutuhan
- **Database:** PostgreSQL (native query support kuat)
- **Build Tool:** Maven/Gradle
- **Container:** Docker, Docker Compose
- **Dokumentasi API:** Swagger/OpenAPI (springdoc-openapi)
- **(Opsional lanjutan):** RabbitMQ untuk komunikasi antar service, Spring Cloud Gateway sebagai API Gateway

---

## 11. Milestone Pengerjaan (Estimasi 4–5 Minggu)

| Minggu | Fokus                                                                                       |
| ------ | ------------------------------------------------------------------------------------------- |
| 1      | Setup project multi-module, desain DB, Auction Service (Auth, CRUD item) — fokus Spring IoC |
| 2      | Modul bidding + scheduler penutupan lelang — fokus Java Stream untuk logic bid tertinggi    |
| 3      | Native SQL query untuk reporting/leaderboard, unit test                                     |
| 4      | Payment Service & Notification Service (microservices terpisah) + komunikasi antar service  |
| 5      | Dockerfile per service, docker-compose, dokumentasi API (Swagger), polishing & demo         |

---

## 12. Kriteria Keberhasilan (Definition of Done)

- [ ] Semua endpoint di atas berjalan dan terdokumentasi di Swagger
- [ ] Minimal 3 native SQL query kompleks (JOIN/subquery/window function) terintegrasi di modul reporting
- [ ] Minimal 3 penggunaan Java Stream API yang bermakna (bukan sekadar `forEach`)
- [ ] Dependency Injection konsisten (tidak ada `new Service()` manual di controller)
- [ ] 3 service berjalan sebagai container terpisah dan bisa di-_up_ dengan satu `docker-compose.yml`
- [ ] Skenario end-to-end berhasil: buat item → bid berkali-kali → lelang otomatis tutup → invoice terbuat → notifikasi terkirim

pakai clean architecture domain-dto-repository-service-impl-controller
tambah folder jika dibutuhkan ex:config, enum, constant dll
pakai lombok anotation @Data @allargs, @Noargs...etc
buat format text rapih tidak memenjang kesamping 
buat javadoc

buatkan dokumentasi teknis .md
