# Import ke Postman

1. Import `Rumah-Lelang.postman_collection.json` sebagai Collection.
2. Import `Rumah-Lelang.local.postman_environment.json` sebagai Environment,
   lalu pilih environment tersebut.
3. Jalankan request berurutan: `1. Auth` → `2. Auction Catalog` →
   `3. Bidding and Closing`.

Collection menyimpan token dan ID ke collection variables otomatis. Request
`Close Auction` membuat invoice. Masukkan ID invoice dari database/DBeaver ke
variable `invoiceId` sebelum memakai request pembayaran.

Port default Docker: auction `8083`, payment `8081`, notification `8082`.
Sample payload yang dapat disalin manual ada di `sample-payloads.json`.
