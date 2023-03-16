import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class E_Biblioteca {
    private final JFrame borrowFrame;
    private final JFrame returnFrame;
    private final JFrame infoFrame;
    private final JTextField borrowNameText, borrowCNPText, borrowDateText, returnText, infoNameText, infoCNPText, infoPaymentText;
    private final JTextArea borrowBookText, infoBookText;
    private final JComboBox<String> borrowBookPopup;

    private static final HashMap<String, Book> books = new HashMap<>();
    private static final HashMap<String, Rental> rentals = new HashMap<>();
    private static final ArrayList<String> archive = new ArrayList<>();
    private final ArrayList<String> selectedBooks = new ArrayList<>();

    public E_Biblioteca() {
        getData();
        // MAIN FRAME //
        // Creare fereastra principala
        JFrame mainFrame = new JFrame("Biblioteca");
        JButton mainBorrowButton = new JButton("Imprumut");
        JButton mainReturnButton = new JButton("Inapoiere");
        mainBorrowButton.setMargin(new Insets(0, 0, 0, 0));
        mainBorrowButton.setBounds(10, 5, 100, 20);
        mainReturnButton.setMargin(new Insets(0, 0, 0, 0));
        mainReturnButton.setBounds(115, 5, 100, 20);
        // event-uri pentru atunci cand se apasa unul din butoanele de pe fereastra principala
        mainBorrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                borrowFrame.setVisible(true);
            }
        });
        mainReturnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnFrame.setVisible(true);
            }
        });
        mainFrame.add(mainBorrowButton);
        mainFrame.add(mainReturnButton);
        mainFrame.setSize(240, 70);
        // event pentru atunci cand se inchide fereastra principala
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                E_Biblioteca.saveBooks();
                E_Biblioteca.saveRentals();
                System.exit(-1);
            }
        });
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);

        // BORROW FRAME //
        // creare fereastra de imprumut
        borrowFrame = new JFrame("Imprumut");
        JLabel borrowNameLabel = new JLabel("Nume si prenume");
        JLabel borrowCNPLabel = new JLabel("Carte identitate: CNP");
        JLabel borrowBookLabel = new JLabel("Alege carti");
        JLabel borrowDateLabel = new JLabel("Data returnare");
        borrowNameLabel.setBounds(5, 5, 100, 20);
        borrowCNPLabel.setBounds(5, 45, 120, 20);
        borrowBookLabel.setBounds(5, 85, 75, 20);
        borrowDateLabel.setBounds(5, 350, 85, 20);
        borrowBookPopup = new JComboBox<>(E_Biblioteca.getAvailableBooks());
        borrowBookPopup.setBounds(135, 87, 100, 20);
        // event pentru atunci cand selectam o carte
        borrowBookPopup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookName = borrowBookPopup.getSelectedItem().toString();
                selectedBooks.add(bookName);
                borrowBookText.append(books.get(bookName).toStringSimple() + "\n");
            }
        });
        borrowNameText = new JTextField();
        borrowCNPText = new JTextField();
        borrowDateText = new JTextField();
        borrowNameText.setBounds(135, 7, 325, 20);
        borrowCNPText.setBounds(135, 47, 100, 20);
        borrowDateText.setBounds(95, 352, 100, 20);
        borrowBookText = new JTextArea(15, 17);
        borrowBookText.setEditable(false);
        JScrollPane borrowBookScroll = new JScrollPane(borrowBookText);
        borrowBookScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        borrowBookScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        JPanel borrowBookPanel = new JPanel();
        borrowBookPanel.add(borrowBookScroll);
        borrowBookPanel.setBounds(235, 82, 250, 265);
        JButton borrowDoneButton = new JButton("Imprumuta");
        borrowDoneButton.setMargin(new Insets(0, 0, 0, 0));
        borrowDoneButton.setBounds(200, 390, 75, 20);
        // event pentru atunci cand se face un imprumut
        borrowDoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // verificam daca toate campurile au fost completate
                if (!borrowNameText.getText().equals("") && !borrowCNPText.getText().equals("") && !borrowBookText.getText().equals("") && !borrowDateText.getText().equals("")) {
                    // ne asiguram ca nu exista deja un imprumut pe cnp-ul introdus
                    if (!rentals.containsKey(borrowCNPText.getText())) {
                        // adaugam imprumutul in lista
                        rentals.put(borrowCNPText.getText(), new Rental(borrowNameText.getText(), borrowCNPText.getText(), borrowBookText.getText().split("\n"), borrowDateText.getText()));
                        // marcam toate cartile imprumutate corespunzator
                        for (String book : selectedBooks) {
                            books.get(book).setAvailable(false);
                        }
                        // scoatem cartile imprumutate din lista meniul din care alegem cartile.
                        for (int i = 0; i < borrowBookPopup.getItemCount(); i++) {
                            if (!books.get(borrowBookPopup.getItemAt(i)).isAvailable()) {
                                borrowBookPopup.removeItemAt(i);
                            }
                        }
                        // stergem textul din toate campurile.
                        borrowNameText.setText("");
                        borrowCNPText.setText("");
                        borrowBookText.setText("");
                        borrowDateText.setText("");
                        selectedBooks.clear();
                        borrowFrame.setVisible(false);
                    }
                }
            }
        });
        borrowFrame.add(borrowNameLabel);
        borrowFrame.add(borrowCNPLabel);
        borrowFrame.add(borrowBookLabel);
        borrowFrame.add(borrowDateLabel);
        borrowFrame.add(borrowNameText);
        borrowFrame.add(borrowCNPText);
        borrowFrame.add(borrowDateText);
        borrowFrame.add(borrowBookPanel);
        borrowFrame.add(borrowBookPopup);
        borrowFrame.add(borrowDoneButton);
        borrowFrame.setSize(500, 465);
        // event pentru atunci cand este inchisa fereastra de imprumut
        borrowFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                borrowNameText.setText("");
                borrowCNPText.setText("");
                borrowBookText.setText("");
                borrowDateText.setText("");
                selectedBooks.clear();
            }
        });
        borrowFrame.setLayout(null);
        borrowFrame.setVisible(false);

        // RETURN BOOK FRAME //
        // creare ferastra de cautat imprumuturi
        returnFrame = new JFrame("Returnare");
        JLabel returnLabel = new JLabel("CNP");
        returnLabel.setBounds(5, 5, 30, 20);
        returnText = new JTextField();
        returnText.setBounds(35, 7, 180, 20);
        JButton returnDoneButton = new JButton("Cauta");
        returnDoneButton.setMargin(new Insets(0, 0, 0, 0));
        returnDoneButton.setBounds(85, 35, 50, 20);
        // event pentru atunci cand cautam un imprumut
        returnDoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ne asiguram ca toate campurile au fost completate si ca exista un imprumut pe cnp-ul introdus
                if (!returnText.getText().equals("") && rentals.containsKey(returnText.getText())) {
                    Rental rental = rentals.get(returnText.getText());
                    returnText.setText("");
                    returnFrame.setVisible(false);

                    // trecem prin toate campurile din imprumut si le reintroducem in meniul de carti dispnibile
                    String[] rentedBooks = rental.getBooksRaw();
                    for (String book: rentedBooks) {
                        String bookName = book.substring(0, book.indexOf("_"));
                        boolean inserted = false;
                        books.get(bookName).setAvailable(true);
                        for (int i = 0; i < borrowBookPopup.getItemCount(); i++) {
                            if (bookName.compareTo(borrowBookPopup.getItemAt(i)) < 0) {
                                borrowBookPopup.insertItemAt(bookName, i);
                                inserted = true;
                                break;
                            }
                        }
                        if (!inserted) {
                            borrowBookPopup.addItem(bookName);
                        }
                    }

                    // introducem informatiile in campurile din fereastra cu infomatiile imprumutului
                    infoNameText.setText(rental.getName());
                    infoCNPText.setText(rental.getCnp());
                    infoBookText.setText(rental.getBooks());
                    infoPaymentText.setText(rental.getPayment());
                    infoFrame.setVisible(true);
                }
            }
        });
        returnFrame.add(returnLabel);
        returnFrame.add(returnText);
        returnFrame.add(returnDoneButton);
        returnFrame.setSize(240, 100);
        returnFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnText.setText("");
            }
        });
        returnFrame.setLayout(null);
        returnFrame.setVisible(false);

        // BORROW INFO FRAME //
        // creare fereastra cu informatiile imprumutului
        infoFrame = new JFrame("Imprumut");
        JLabel infoNameLabel = new JLabel("Nume si prenume");
        JLabel infoCNPLabel = new JLabel("CNP");
        JLabel infoBookLabel = new JLabel("Carti imprumutate");
        JLabel infoPaymentLabel = new JLabel("Pret");
        infoNameLabel.setBounds(5, 5, 100, 20);
        infoCNPLabel.setBounds(5, 45, 30, 20);
        infoBookLabel.setBounds(5, 85, 110, 20);
        infoPaymentLabel.setBounds(5, 250, 70, 20);

        infoNameText = new JTextField();
        infoCNPText = new JTextField();
        infoPaymentText = new JTextField();
        infoNameText.setBounds(140, 7, 225, 20);
        infoCNPText.setBounds(140, 47, 100, 20);
        infoPaymentText.setBounds(40, 252, 97, 20);

        JPanel infoPanel = new JPanel();
        infoBookText = new JTextArea(8, 19);
        JScrollPane infoScroll = new JScrollPane(infoBookText);
        infoScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        infoScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        infoPanel.add(infoScroll);
        infoPanel.setBounds(137, 83, 230, 175);

        JButton infoDoneButton = new JButton("OK");
        infoDoneButton.setMargin(new Insets(0, 0, 0, 0));
        infoDoneButton.setBounds(175, 280, 30, 20);
        // event pentru atunci cand se apasa butonul "OK"
        infoDoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // scoatem imprumutul din lista de imprumuturi
                archive.add(rentals.remove(infoCNPText.getText()).toString());
                // stergem textul din toate campurile
                infoNameText.setText("");
                infoCNPText.setText("");
                infoBookText.setText("");
                infoPaymentText.setText("");
                E_Biblioteca.saveArchive();
                infoFrame.setVisible(false);
            }
        });

        infoNameText.setEditable(false);
        infoCNPText.setEditable(false);
        infoBookText.setEditable(false);
        infoPaymentText.setEditable(false);

        infoFrame.add(infoNameLabel);
        infoFrame.add(infoCNPLabel);
        infoFrame.add(infoBookLabel);
        infoFrame.add(infoPaymentLabel);
        infoFrame.add(infoNameText);
        infoFrame.add(infoCNPText);
        infoFrame.add(infoPaymentText);
        infoFrame.add(infoPanel);
        infoFrame.add(infoDoneButton);
        infoFrame.setSize(400, 350);
        infoFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // scoatem imprumutul din lista de imprumuturi
                archive.add(rentals.remove(infoCNPText.getText()).toString());
                // stergem textul din toate campurile
                infoNameText.setText("");
                infoCNPText.setText("");
                infoBookText.setText("");
                infoPaymentText.setText("");
                E_Biblioteca.saveArchive();
                E_Biblioteca.saveRentals();
            }
        });
        infoFrame.setLayout(null);
        infoFrame.setVisible(false);
    }

    // functie care returneaza o lista cu toate cartile dispnibile
    private static String[] getAvailableBooks() {
        ArrayList<String> availableBooks = new ArrayList<>();
        for (Book book: books.values()) {
            if (book.isAvailable()) {
                availableBooks.add(book.getName());
            }
        }
        Collections.sort(availableBooks);
        return availableBooks.toArray(new String[0]);
    }

    // functie care citeste din baza de date
    private static void getData() {
        File file = new File("data/carti.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                Book book = new Book(scanner.nextLine());
                books.put(book.getName(), book);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        file = new File("data/arhiva.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                archive.add(scanner.nextLine());
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        file = new File("data/imprumuturi.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                Rental rental = new Rental(scanner.nextLine());
                rentals.put(rental.getName(), rental);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // functie care salveaza cartile in baza de date
    private static void saveBooks() {
        StringBuilder stringBooks = new StringBuilder();
        for (Book book : books.values()) {
            stringBooks.append(book.toString());
            stringBooks.append("\n");
        }
        store("carti.txt", stringBooks.toString());
    }

    // functie care salveaza arhiva in baza de date
    private static void saveArchive() {
        StringBuilder stringBooks = new StringBuilder();
        for (String item : archive) {
            stringBooks.append(item);
            stringBooks.append("\n");
        }
        store("arhiva.txt", stringBooks.toString());
    }

    // functie care salveaza imprumuturile in baza de date
    private static void saveRentals() {
        StringBuilder stringBooks = new StringBuilder();
        for (Rental rental: rentals.values()) {
            stringBooks.append(rental.toString());
            stringBooks.append("\n");
        }
        store("imprumuturi.txt", stringBooks.toString());
    }

    // functie ajutatoare care scrie textul din argumentul "text" in fisierul cu numele din argumentul "filename"
    private static void store(String filename, String text) {
        try {
            FileOutputStream outputFile = new FileOutputStream("data/"+filename);
            try {
                outputFile.write(text.getBytes(StandardCharsets.UTF_8));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
