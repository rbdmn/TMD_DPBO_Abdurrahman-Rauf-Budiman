## Fishbound
Game ini merupakan game 2D Pixelated dengan menggunakan tile-based map system dimana peta/map disusun dari potongan gambar kecil (16x16) pixel tile. 

Game ini menggunakan arsitektur MVVM (Model-View-ViewModel) dan menggunakan bahasa Java dengan OOP murni. Database yang dipakai yaitu MySQL dan GUI nya pake Java Swing.

Game ini ceritanya player bisa memancing target/ikan yang ada pada danau dan memasukkannya ke dalam basket untuk menyimpan ke database. Tiap target ada nilai value masing masing, seperti:
- Ikan Biru = 20 -> 50 poin
- Ikan Merah = 60 -> 100 poin
- Ikan Hijau = 100 -> 200 poin

Setiap poin makin tinggi maka kecepatan target jalannya pun semakin tinggi. Diberi timer hanya 1 menit untuk mencapai skor tertinggi. 

#### Cara mainnya:
- Gerakan player dengan arrow keys atau WASD
- Tembak hook ke arah ikan untuk menangkapnya
- Ketika hook kena akan otomatis ke tarik ikannya ke player
- Ketika sudah ada di tangan player, player harus menembakannya ke basket yang tersedia
- Saat ikan masuk ke basket artinya telah masuk datanya tersimpan ke database
- Jika ingin memberhentikan gamenya secara paksa (tidak menunggu sampai timer beres) bisa klik space

## Struktur Direktori

    TMD_DPBO/
    ├── lib/
    │   └── mysql-connector-j-9.3.0.jar
    ├── res/
    │   ├── audio
    │   ├── fonts
    │   ├── maps
    │   ├── player
    │   ├── target
    │   └── tiles
    └── src/
        ├── database/
        │   ├── Database.java
        │   └── T_Hasil.java
        ├── main/
        │   └── Main.java
        ├── model/
        │   ├── Basket.java
        │   ├── Tile.java
        │   ├── Target.java
        │   ├── TileManager.java
        │   ├── GameState.java
        │   ├── ThrowingTarget.java
        │   ├── Bullet.java
        │   ├── Player.java
        │   ├── Basket.java
        │   └── Entity.java
        ├── view/
        │   ├── GameFrame.java
        │   ├── GamePanel.java
        │   └── GUI.java
        └── viewmodel/
            ├── GameViewModel.java
            ├── PlayerViewModel.java
            └── TargetViewModel.java

Berikut penjelasan dari tiap sub-direktori:
- lib = folder lib hanya menyimpan .jar untuk koneksi ke sql menggunakan JDBC
- res = folder res ini isinya semua asset yang akan digunakan mulai dari audio hingga tiles yang dipakai
- src = folder src berisi semua logika dan tempat pemograman terjadi
- src/database = Folder database ini isinya untuk mendapat koneksi ke MySQL database dan perkuerian
- src/main = Folder main yang berisi main saja ini gunanya untuk inisialisasi/eksekusi game dijalankan
- src/model = Folder model ini berisi objek yang ada pada gamenya
- src/viewmodel = Folder viewmodel ini berisi logika game dan interaksi objek, sebagai perantara model dan view
- src/view = Folder view ini buat tampilan yang akan keluar di window (gamenya)

## Kompilasi Manual

    javac -cp "lib/*" -d out src/model/*.java src/view/*.java src/viewmodel/*.java src/database/*.java src/main/*.java && java -cp "out;res;lib/mysql-connector-j-9.3.0.jar" main.Main

Jalankan di CMD windows biasa dan pastikan posisi direktori saat itu di TMD_DPBO atau diluar src
## Kredit/Lisensi:

Sound effect: https://pixabay.com

------------------------------
Asset Sprites:

https://kenney.nl/assets/tiny-battle

https://kenney.nl/assets/pixel-platformer-industrial-expansion

https://kenney.nl/assets/platformer-art-pixel-redux

Tiny Battle (1.0)
Created/distributed by Kenney (www.kenney.nl)
Creation date: 08-08-2023

License: (Creative Commons Zero, CC0)
http://creativecommons.org/publicdomain/zero/1.0/
------------------------------
Background music:

"8 Bit Seas!" By HeatleyBros

https://www.youtube.com/watch?v=-vP3XSoAr4Q&list=PLobY7vO0pgVKn4FRDgwXk5FUSiGS8_jA8&index=16
