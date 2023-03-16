public class Book {
    private final int isbn;
    private final String name;
    private final String publisher;

    private boolean available;

    public Book(String data) {
        String[] info = data.split("_");
        this.isbn = Integer.parseInt(info[0]);
        this.name = info[1];
        this.publisher = info[2];
        this.available = Boolean.parseBoolean(info[3]);
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return isbn + "_" + name + "_" + publisher + "_" + available;
    }

    public String toStringSimple() {
        return name + "_" + publisher;
    }
}
