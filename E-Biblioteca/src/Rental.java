import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public class Rental {
    private final String name;
    private final String cnp;
    private final String[] books;
    private final LocalDate rentalDate;
    private final LocalDate returnDate;

    // constructor pentru creerea unui imprumut
    public Rental(String name, String cnp, String[] books, String date) {
        this.name = name;
        this.cnp = cnp;
        this.books = books;

        String[] dateInfo = date.split("/");
        this.returnDate = LocalDate.of(Integer.parseInt(dateInfo[2]), Integer.parseInt(dateInfo[1]), Integer.parseInt(dateInfo[0]));
        this.rentalDate = LocalDate.now();
    }

    // constructor pentru creerea unui imprumut cu datele din baza de date
    public Rental(String data) {
        String[] dataInfo = data.split("/");
        this.name = dataInfo[0];
        this.cnp = dataInfo[1];

        String[] rentalDateInfo = dataInfo[2].split("-");
        this.rentalDate = LocalDate.of(Integer.parseInt(rentalDateInfo[0]), Integer.parseInt(rentalDateInfo[1]), Integer.parseInt(rentalDateInfo[2]));

        String[] returnDateInfo = dataInfo[3].split("-");
        this.returnDate = LocalDate.of(Integer.parseInt(returnDateInfo[0]), Integer.parseInt(returnDateInfo[1]), Integer.parseInt(returnDateInfo[2]));

        this.books = dataInfo[4].substring(1, dataInfo[4].length()-1).split(",\s");


    }

    public String getName() {
        return name;
    }

    public String getCnp() {
        return cnp;
    }

    public String getBooks() {
        return Arrays.toString(books).replaceAll(",\s", "\n").replaceAll("\\[", "").replaceAll("]", "");
    }

    public String[] getBooksRaw() {
        return books;
    }

    public String getPayment() {
        return (ChronoUnit.DAYS.between(rentalDate, returnDate) / 2.0) + " RON";
    }

    @Override
    public String toString() {
        return name + "/" + cnp + "/" + rentalDate + "/" + returnDate + "/" + Arrays.toString(books);
    }
}
