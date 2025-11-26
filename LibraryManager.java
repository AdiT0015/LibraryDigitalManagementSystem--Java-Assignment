import java.io.*;
import java.util.*;
public class LibraryManager {
    private Map<Integer, Book> books = new HashMap<>();
    private Map<Integer, Member> members = new HashMap<>();
    private Scanner sc = new Scanner(System.in);

    private final String BOOK_FILE = "books.txt";
    private final String MEMBER_FILE = "members.txt";

    public LibraryManager() {
        loadFromFile();
    }

    public void loadFromFile() {
        try {
            File f1 = new File(BOOK_FILE);
            File f2 = new File(MEMBER_FILE);

            if (!f1.exists()) f1.createNewFile();
            if (!f2.exists()) f2.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader(BOOK_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",");
                if (arr.length == 5) {
                    Book b = new Book(
                            Integer.parseInt(arr[0]),
                            arr[1],
                            arr[2],
                            arr[3]
                    );
                    if (Boolean.parseBoolean(arr[4])) b.markAsIssued();
                    books.put(b.getBookId(), b);
                }
            }
            br.close();

            br = new BufferedReader(new FileReader(MEMBER_FILE));
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",");
                if (arr.length >= 3) {
                    Member m = new Member(
                            Integer.parseInt(arr[0]),
                            arr[1],
                            arr[2]
                    );

                    for (int i = 3; i < arr.length; i++) {
                        m.addIssuedBook(Integer.parseInt(arr[i]));
                    }
                    members.put(m.getMemberId(), m);
                }
            }
            br.close();

        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    public void saveToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(BOOK_FILE));
            for (Book b : books.values()) {
                bw.write(b.getBookId() + "," +
                        b.getTitle() + "," +
                        b.getAuthor() + "," +
                        b.getCategory() + "," +
                        b.isIssued());
                bw.newLine();
            }
            bw.close();

            bw = new BufferedWriter(new FileWriter(MEMBER_FILE));
            for (Member m : members.values()) {
                bw.write(m.getMemberId() + "," +
                        m.getIssuedBooks().toString()
                                .replace("[", "")
                                .replace("]", ""));
                bw.newLine();
            }
            bw.close();

        } catch (IOException e) {
            System.out.println("File saving error: " + e.getMessage());
        }
    }

    public void addBook() {
        System.out.print("Enter Book ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Book Title: ");
        String title = sc.nextLine();

        System.out.print("Enter Author: ");
        String author = sc.nextLine();

        System.out.print("Enter Category: ");
        String category = sc.nextLine();

        Book b = new Book(id, title, author, category);
        books.put(id, b);

        saveToFile();
        System.out.println("Book added successfully!");
    }

    public void addMember() {
        System.out.print("Enter Member ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Member Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        Member m = new Member(id, name, email);
        members.put(id, m);

        saveToFile();
        System.out.println("Member added successfully!");
    }

    public void issueBook() {
        System.out.print("Enter Book ID: ");
        int bookId = sc.nextInt();
        System.out.print("Enter Member ID: ");
        int memberId = sc.nextInt();

        if (!books.containsKey(bookId)) {
            System.out.println("Book not found!");
            return;
        }
        if (!members.containsKey(memberId)) {
            System.out.println("Member not found!");
            return;
        }

        Book b = books.get(bookId);
        Member m = members.get(memberId);

        if (b.isIssued()) {
            System.out.println("Book already issued!");
            return;
        }

        b.markAsIssued();
        m.addIssuedBook(bookId);

        saveToFile();
        System.out.println("Book issued successfully!");
    }

    public void returnBook() {
        System.out.print("Enter Book ID: ");
        int bookId = sc.nextInt();

        Book b = books.get(bookId);
        if (b == null) {
            System.out.println("Book not found!");
            return;
        }

        b.markAsReturned();
        for (Member m : members.values()) {
            m.returnIssuedBook(bookId);
        }

        saveToFile();
        System.out.println("Book returned successfully!");
    }

    public void searchBooks() {
        sc.nextLine();
        System.out.print("Enter keyword: ");
        String key = sc.nextLine().toLowerCase();

        books.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(key)
                        || b.getAuthor().toLowerCase().contains(key)
                        || b.getCategory().toLowerCase().contains(key))
                .forEach(Book::displayBookDetails);
    }

    // ---------------- SORT BOOKS --------------------
    public void sortBooks() {
        List<Book> list = new ArrayList<>(books.values());

        System.out.println("1. Sort by Title");
        System.out.println("2. Sort by Author");
        int ch = sc.nextInt();

        if (ch == 1)
            Collections.sort(list);
        else
            list.sort(Comparator.comparing(Book::getAuthor));

        list.forEach(Book::displayBookDetails);
    }

    // ---------------- MENU --------------------------
    public void menu() {
        int ch;
        do {
            System.out.println("\n====== City Library Digital Management System ======");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Books");
            System.out.println("6. Sort Books");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");
            ch = sc.nextInt();
            switch (ch) {
                case 1 -> addBook();
                case 2 -> addMember();
                case 3 -> issueBook();
                case 4 -> returnBook();
                case 5 -> searchBooks();
                case 6 -> sortBooks();
                case 7 -> {
                    saveToFile();
                    System.out.println("Exiting...");
                }
                default -> System.out.println("Invalid choice!");
            }

        } while (ch != 7);
    }
    public static void main(String[] args) {
        new LibraryManager().menu();
    }
}
