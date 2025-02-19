import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== IQ Puzzler Pro Solver ===");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Masukkan nama file test case (.txt): ");
        String fileName = scanner.nextLine();

        try {
            PuzzleSolver solver = new PuzzleSolver(fileName);
            long start = System.currentTimeMillis();
            boolean solved = solver.solve();
            long end = System.currentTimeMillis();

            if (solved) {
                solver.printBoard();
                System.out.println("Waktu pencarian: " + (end - start) + " ms");
                System.out.println("Banyak iterasi: " + solver.getIterationCount());

                System.out.print("Ingin menyimpan solusi? (ya/tidak): ");
                String save = scanner.nextLine();
                if (save.equalsIgnoreCase("ya")) {
                    solver.saveSolution("test/solution.txt");
                    System.out.println("Solusi disimpan di 'test/solution.txt'");
                }
            } else {
                System.out.println("Tidak ada solusi ditemukan.");
            }
        } catch (IOException e) {
            System.err.println("Gagal membaca file: " + e.getMessage());
        }
    }
}
