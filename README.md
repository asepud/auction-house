# Rumah-Lelang Microservices

## Hasil implementasi

Proyek adalah Maven multi-module yang menghasilkan tiga Spring Boot JAR mandiri:

| Service      | JAR                              | Port | Database          | Tanggung jawab                               |
| ------------ | -------------------------------- | ---: | ----------------- | -------------------------------------------- |
| Auction      | `auction-service-1.0.0.jar`      | 8083 | `auction_db`      | user, kategori, item, bid, scheduler, report |
| Payment      | `payment-service-1.0.0.jar`      | 8081 | `payment_db`      | invoice dan status pembayaran                |
| Notification | `notification-service-1.0.0.jar` | 8082 | `notification_db` | event outbid, winner, dan auction ended      |

Masing-masing service memiliki aplikasi Spring Boot, JAR, Dockerfile, datasource,
dan schema JPA sendiri. Payment dan notification **tidak** memiliki foreign key ke
database auction; mereka menyimpan `itemId`/`winnerId`/`userId` sebagai remote ID.

Saat memakai Docker Compose, ketiga database tersebut berada di **satu** container
PostgreSQL bernama `postgres`; tetap berupa database PostgreSQL berbeda dalam satu
instance. Port database internal tetap `5432`, tetapi dipublikasikan sebagai
`localhost:5442` untuk menghindari konflik dengan PostgreSQL lokal.

Folder `legacy-single-app` adalah arsip source monolith sebelum pemisahan dan
tidak terdaftar sebagai Maven module atau ikut dibangun. Source aktif hanya ada
di tiga folder service di atas.

```text
client -> auction-service:8083
             | POST /api/invoices (X-Internal-Key)
             +------------------> payment-service:8081 -> payment_db
             |
             | POST /api/notify/* (X-Internal-Key)
             +------------------> notification-service:8082 -> notification_db
             |
             +------------------> auction_db
```

## Build dan menjalankan

Prasyarat: Java 17+ dan Maven 3.9+.

```powershell
mvn clean package
```

JAR berada pada:

```text
auction-service/target/auction-service-1.0.0.jar
payment-service/target/payment-service-1.0.0.jar
notification-service/target/notification-service-1.0.0.jar
```

Untuk run lokal dengan H2 (jalankan payment dan notification sebelum auction):

```powershell
java -jar payment-service/target/payment-service-1.0.0.jar
java -jar notification-service/target/notification-service-1.0.0.jar
java -jar auction-service/target/auction-service-1.0.0.jar
```

Ketiga proses harus memakai `JWT_SECRET` yang sama. Untuk production, tetapkan
juga `INTERNAL_API_KEY` yang sama pada ketiganya. Jangan gunakan nilai default.

Untuk menjalankan seluruh stack PostgreSQL:

```powershell
Copy-Item .env.example .env
docker compose up --build
```

### DBeaver

Gunakan koneksi PostgreSQL berikut di DBeaver setelah stack aktif:

| Field | Nilai |
|---|---|
| Host | `localhost` |
| Port | `5442` |
| User / password | `POSTGRES_USER` / `POSTGRES_PASSWORD` dari `.env` |
| Database | `auction_db`, `payment_db`, atau `notification_db` |

Script `docker/postgres/init/01-create-databases.sql` membuat tiga database saat
volume PostgreSQL pertama kali diinisialisasi. Jangan mengubah atau menghapusnya
jika volume sudah berisi data yang ingin dipertahankan.

## Batas komunikasi dan keamanan

- Auction menerbitkan invoice melalui `POST payment-service/api/invoices`.
- Auction menerbitkan notifikasi ke endpoint notification service.
- Request service-to-service wajib membawa `X-Internal-Key`; endpoint akan
  menolak key yang salah.
- JWT yang ditandatangani auction juga diverifikasi payment dan notification.
  Dengan demikian pemenang bisa melihat/membayar invoice dan melihat riwayat
  notifikasi tanpa database user disalin ke service lain.
- Semua database dipisah. Tidak ada repository atau entity lintas service.

## Endpoint

Auction service (`:8083`) menangani `/api/auth`, `/api/categories`,
`/api/items`, dan `/api/reports`. Swagger hanya berada di auction service:
`http://localhost:8083/swagger-ui/index.html`.

Payment service (`:8081`) menangani:

- `POST /api/invoices` — internal, header `X-Internal-Key`
- `GET /api/invoices/{id}` — token pemenang atau ADMIN
- `PATCH /api/invoices/{id}/pay` — token pemenang atau ADMIN

Notification service (`:8082`) menangani:

- `POST /api/notify/outbid`, `/winner`, `/ended` — internal
- `GET /api/notify/history/{userId}` — token pemilik atau ADMIN

Health check setiap service tersedia di `/actuator/health`.

## Postman

File siap impor berada di folder `postman/`:

- `Rumah-Lelang.postman_collection.json` — request end-to-end, token dan ID
  disimpan otomatis sebagai collection variable;
- `Rumah-Lelang.local.postman_environment.json` — base URL Docker lokal;
- `sample-payloads.json` — payload contoh untuk referensi cepat.

Ikuti urutan folder `1. Auth`, `2. Auction Catalog`, kemudian `3. Bidding and
Closing`. Petunjuk ringkas tersedia pada `postman/README.md`.

## Pengujian

`auction-service` memiliki unit test validasi kenaikan bid dan integration test
HTTP register → buat item → bid. Jalankan `mvn -pl auction-service test`.
Test tidak menghubungi service lain karena alur tersebut belum menutup lelang.

## Konsistensi dan batasan operasional

Lelang yang `SOLD` melakukan panggilan sinkron ke payment dan notification. Jika
payment service tidak tersedia, transaksi penutupan gagal dan scheduler akan
mencoba lagi pada siklus berikutnya; pembuatan invoice idempoten berdasarkan
`itemId`. Notification diperlakukan sama agar event tidak diam-diam hilang.

Ini belum memakai message broker/outbox. Untuk ketahanan produksi tingkat lanjut,
langkah berikutnya adalah transactional outbox di auction service dan consumer
idempoten pada payment/notification.
