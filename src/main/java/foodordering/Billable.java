package foodordering;

/**
 * Contract for objects that produce a monetary subtotal.
 * Demonstrates: Interface.
 */
public interface Billable {
    double calculateSubtotal();
    default double calculateTax(double taxRate) {
        return calculateSubtotal() * taxRate;
    }
}
