/*
Saya Abdurrahman Rauf Budiman dengan NIM 2301102 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

// merupakan package folder dari main
package main;

// import library java dan import dari GUI
import view.GUI;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // saat di run itu akan merujuk ke GUI langsung
        SwingUtilities.invokeLater(GUI::new);
    }
}
