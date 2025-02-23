import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PuzzleSolver {
    private char[][] board; // Solve puzzle pake matriks
    private List<Block> blocks;
    private int N, M;
    private int iterationCount;

    // Baca input dari file
    private void readInput(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        // Baca ukuran papan N x M dan jumlah blok P
        String[] dims = br.readLine().split(" ");
        N = Integer.parseInt(dims[0]);
        M = Integer.parseInt(dims[1]);
        int P = Integer.parseInt(dims[2]);

        // Membaca mode Puzzle: DEFAULT/CUSTOM/PYRAMID
        String tyoe = br.readLine();

        // Inisialisasi papan
        board = new char[N][M];
        for (char [] row : board) {
            Arrays.fill(row, '.');
        }

        // Menyimpan blok yang ada
        blocks = new ArrayList<>();
        Set<Character> usedSymbols = new HashSet<>(); // Cek duplikasi huruf

        // Memulai untuk membaca blok puzzle
        String currentSymbol = null;
        List<String> currentShape = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) continue; // Lewati baris kosong
    
            char firstChar = line.charAt(0);
            // Validasi: Pastikan hanya huruf kapital A - Z
            if (!Character.isUpperCase(firstChar) || firstChar < 'A' || firstChar > 'Z') {
                throw new IllegalArgumentException("Error: Karakter blok harus berupa huruf kapital A-Z! Ditemukan: " + firstChar);
            }
    
            // Jika menemukan blok baru (karakter berubah)
            if (currentSymbol == null || firstChar != currentSymbol.charAt(0)) {
                if (!currentShape.isEmpty()) {
                    // Validasi: Pastikan blok tidak kosong
                    if (!hasValidCharacter(currentShape)) {
                        throw new IllegalArgumentException("Error: Blok " + currentSymbol + " tidak memiliki bentuk valid!");
                    }
    
                    blocks.add(new Block(currentSymbol.charAt(0), new ArrayList<>(currentShape)));
                    currentShape.clear();
                }
                currentSymbol = String.valueOf(firstChar);
    
                // **Validasi: Pastikan huruf tidak duplikat**
                if (usedSymbols.contains(currentSymbol.charAt(0))) {
                    throw new IllegalArgumentException("Error: Blok " + currentSymbol + " sudah ada! Tidak boleh ada duplikasi.");
                }
                usedSymbols.add(currentSymbol.charAt(0));
            }
            currentShape.add(line); // Tambahkan baris ke bentuk blok
        }
    
        // Tambahkan blok terakhir yang belum tersimpan
        if (!currentShape.isEmpty()) {
            if (!hasValidCharacter(currentShape)) {
                throw new IllegalArgumentException("Error: Blok " + currentSymbol + " tidak memiliki bentuk valid!");
            }
            blocks.add(new Block(currentSymbol.charAt(0), new ArrayList<>(currentShape)));
        }
    
        br.close();
            // Validasi: Pastikan jumlah total sel blok sama dengan ukuran papan
        int totalBlockCells = 0;
        for (Block b : blocks) {
            totalBlockCells += b.coordinates.size();
        }

        if (totalBlockCells != (N * M)) {
            throw new IllegalArgumentException("Error: Total sel blok (" + totalBlockCells + ") tidak sesuai dengan ukuran papan (" + (N * M) + ")");
        }
        // **Validasi: Pastikan jumlah blok sesuai dengan P**
        if (blocks.size() != P) {
            throw new IllegalArgumentException("Error: Jumlah blok tidak sesuai! Diharapkan: " + P + ", tetapi ditemukan: " + blocks.size());
        }
    }
    // Fungsi untuk memastikan blok memiliki minimal satu huruf yang valid
    private boolean hasValidCharacter(List<String> shape) {
        for (String row : shape) {
            for (char c : row.toCharArray()) {
                if (Character.isUpperCase(c) && c >= 'A' && c <= 'Z') {
                    return true; // Ditemukan karakter huruf kapital yang valid
                }
            }
        }
        return false; // Tidak ada huruf kapital dalam blok ini
    }

    // Konstruktor
    public PuzzleSolver(String filename) throws IOException {
        readInput(filename);
    }

    // Memulai pencarian solusi
    public boolean solve() {
        System.out.println("Memulai pencarian solusi...");
        return solveRecursively(0);
    }

    // Algoritma Bruteforce/Backtracking
    private boolean solveRecursively(int index) {
        if (index == blocks.size()) return isValidBoard(); // cek valid
    
        Block block = blocks.get(index);
        List<Block> orientations = block.generateOrientations();
    
        for (Block orient : orientations) {  
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < M; c++) {
                    if (canPlace(orient, r, c)) {
                        placeBlock(orient, r, c, block.symbol);
                        clearScreen();
                        printBoard();

                        try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                        
                        if (solveRecursively(index + 1)) return true; // Jika berhasil, langsung return
                        removeBlock(orient, r, c);
                        clearScreen();
                        printBoard();
                        try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                        iterationCount++;
                    }
                }
            }
        }
        return false; // backtrack
    }
    // Fungsi-fungsi pendukung untuk algoritma
    // Mengecek apakah blok bisa ditempatkan dengan mengecek batas papan dan memastikan tidak menimpa blok lain.
    private boolean canPlace(Block block, int startX, int startY) {
        for (int[] cell : block.coordinates) {
            int x = startX + cell[0];
            int y = startY + cell[1];
            if (x < 0 || x >= N || y < 0 || y >= M || board[x][y] != '.') {
                return false;
            }
        }
        return true;
    }
    // Menempatkan blok di simbol titik di papan
    private void placeBlock(Block block, int startX, int startY, char symbol) {
        for (int[] cell : block.coordinates) {
            int x = startX + cell[0];
            int y = startY + cell[1];
            board[x][y] = symbol;
        }
    }
    // Menghapus blok dari papan untuk backtracking
    private void removeBlock(Block block, int startX, int startY) {
        for (int[] cell : block.coordinates) {
            int x = startX + cell[0];
            int y = startY + cell[1];
            board[x][y] = '.';
        }
    }
    // Validasi akhir papan: memastikan semua sel terisi sebelum dijadikan solusi
    private boolean isValidBoard() {
        int filledCells = 0;
        for (char[] row : board) {
            for (char cell : row) {
                if (cell != '.') filledCells++;
            }
        }
        return filledCells == (N * M);
    }
        public void printBoard() {
        for (char[] row : board) {
            for (char cell : row) {
                System.out.print(Utils.getColoredChar(cell) + " ");
            }
            System.out.println();
        }
    }

    public void saveSolution(String fileName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (char[] row : board) {
            for (char cell : row) {
                bw.write(cell + " ");
            }
            bw.newLine();
        }
        bw.close();
    }

    public int getIterationCount() {
        return iterationCount;
    }
    public char[][] getBoard() {
        return board;
    }
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
