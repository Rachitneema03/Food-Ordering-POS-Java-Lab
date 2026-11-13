package foodordering;
import java.io.*; import java.nio.charset.StandardCharsets; import java.nio.file.*; import java.time.format.DateTimeFormatter;
public final class ReceiptService {
    public Path save(Order o) throws IOException {
        Path dir=Path.of("receipts");Files.createDirectories(dir);Path file=dir.resolve(o.getCode()+".txt");
        StringBuilder b=new StringBuilder();
        b.append("==============================================\n              FOODIE'S RESTAURANT\n             FOOD ORDER RECEIPT\n==============================================\n");
        b.append("Order ID : ").append(o.getCode()).append('\n').append("Customer : ").append(o.getCustomer()).append('\n');
        b.append("Phone    : ").append(o.getPhone().isBlank()?"N/A":o.getPhone()).append('\n').append("Type     : ").append(o.getOrderType()).append('\n');
        b.append("Payment  : ").append(o.getPayment()).append('\n').append("Date     : ").append(o.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append("\n----------------------------------------------\n");
        b.append(String.format("%-24s %4s %10s%n","Item","Qty","Amount"));
        for(var e:o.getItems().entrySet())b.append(String.format("%-24s %4d %10.2f%n",e.getKey().getName(),e.getValue(),e.getKey().getPrice()*e.getValue()));
        b.append("----------------------------------------------\n").append(String.format("%-30s Rs. %8.2f%n","Subtotal",o.getSubtotal()));
        b.append(String.format("%-30s Rs. %8.2f%n","GST (5%)",o.getTax())).append(String.format("%-30s Rs. %8.2f%n","TOTAL",o.getTotal()));
        b.append("==============================================\n              Thank you. Visit again!\n");
        Files.writeString(file,b.toString(),StandardCharsets.UTF_8);return file;
    }
}