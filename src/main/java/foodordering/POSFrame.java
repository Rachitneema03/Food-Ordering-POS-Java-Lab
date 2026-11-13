package foodordering;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public final class POSFrame extends JFrame {
    private static final Color NAVY = new Color(25, 38, 64), ORANGE = new Color(235, 102, 27),
            BG = new Color(246, 247, 249), INK = Color.BLACK, MUTED = new Color(75, 85, 99),
            LINE = new Color(226, 232, 240);
    private final CardLayout pages = new CardLayout();
    private final JPanel pageHost = new JPanel(pages);
    private final Cart cart = new Cart();
    private final MenuRepository menuRepo = new MenuRepository();
    private final OrderRepository orderRepo = new OrderRepository();
    private List<FoodItem> menu = List.of();
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private String activeCategory = "All", activeType = "All", searchText = "";
    private final JPanel productGrid = new JPanel(new GridLayout(0, 3, 14, 14));
    private final JPanel cartRows = new JPanel();
    private final JLabel cartCount = new JLabel("0 items");
    private final JLabel subtotalLabel = label("₹0.00", 16, INK, true), taxLabel = label("₹0.00", 13, MUTED, false),
            totalLabel = label("₹0.00", 22, ORANGE, true);
    private final JTextField search = new JTextField(22), customer = new JTextField(), phone = new JTextField();
    private final JComboBox<String> orderType = new JComboBox<>(new String[] { "Dine-in", "Takeaway", "Delivery" }),
            payment = new JComboBox<>(new String[] { "Cash", "UPI", "Card", "Net Banking" });
    private final DefaultTableModel ordersModel = new DefaultTableModel(
            new String[] { "Order ID", "Customer", "Type", "Total (₹)", "Payment", "Date" }, 0) {
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final DefaultTableModel reportModel = new DefaultTableModel(
            new String[] { "Item", "Qty Sold", "Revenue (₹)" }, 0) {
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JLabel reportOrders = label("0", 24, INK, true), reportSales = label("₹0.00", 24, INK, true),
            reportAvg = label("₹0.00", 24, INK, true);
    private final JLabel status = label("Ready", 11, MUTED, false);

    public POSFrame() {
        setTitle("Foodie's Restaurant | Point of Sale");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 720));
        setSize(1380, 850);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildMain(), BorderLayout.CENTER);
        loadMenu();
        showPage("Order");
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(NAVY);
        side.setPreferredSize(new Dimension(218, 0));
        side.setBorder(new EmptyBorder(22, 14, 18, 14));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        JLabel brand = label("●  FOODIE'S", 20, Color.WHITE, true),
                sub = label("    RESTAURANT POS", 11, new Color(185, 198, 219), true);
        side.add(brand);
        side.add(sub);
        side.add(Box.createVerticalStrut(36));
        addNav(side, "Dashboard", "Dashboard");
        addNav(side, "New Order", "Order");
        addNav(side, "Cart  •", "Cart");
        addNav(side, "Order History", "Orders");
        addNav(side, "Reports", "Reports");
        addNav(side, "Menu Items", "Menu");
        side.add(Box.createVerticalGlue());
        side.add(label("JAVA SWING  •  JDBC", 10, new Color(150, 165, 190), true));
        side.add(Box.createVerticalStrut(5));
        side.add(label("Local restaurant terminal", 10, new Color(150, 165, 190), false));
        return side;
    }

    private void addNav(JPanel side, String text, String key) {
        JButton b = new JButton(text);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(190, 43));
        b.setPreferredSize(new Dimension(190, 43));
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setForeground(new Color(220, 228, 240));
        b.setBackground(NAVY);
        b.setBorder(new EmptyBorder(10, 14, 10, 8));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> showPage(key));
        navButtons.put(key, b);
        side.add(b);
        side.add(Box.createVerticalStrut(5));
    }

    private JPanel buildMain() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, LINE), new EmptyBorder(18, 26, 18, 26)));
        top.add(label("FOOD ORDERING SYSTEM", 16, INK, true), BorderLayout.WEST);
        JLabel date = label("Restaurant counter  |  Local POS", 12, MUTED, false);
        top.add(date, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);
        pageHost.setBackground(BG);
        pageHost.add(buildOrderPage(), "Order");
        pageHost.add(buildCartPage(), "Cart");
        pageHost.add(buildOrdersPage(), "Orders");
        pageHost.add(buildReportsPage(), "Reports");
        pageHost.add(buildDashboardPage(), "Dashboard");
        pageHost.add(buildMenuPage(), "Menu");
        p.add(pageHost, BorderLayout.CENTER);
        JPanel foot = new JPanel(new BorderLayout());
        foot.setBackground(Color.WHITE);
        foot.setBorder(new EmptyBorder(7, 22, 7, 22));
        foot.add(status, BorderLayout.WEST);
        foot.add(label("Foodie's Restaurant  •  Academic Project", 10, MUTED, false), BorderLayout.EAST);
        p.add(foot, BorderLayout.SOUTH);
        return p;
    }

    private JPanel page(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(22, 24, 20, 24));
        JPanel h = new JPanel(new GridLayout(2, 1, 0, 4));
        h.setOpaque(false);
        h.add(label(title, 24, INK, true));
        h.add(label(subtitle, 12, MUTED, false));
        p.add(h, BorderLayout.NORTH);
        return p;
    }

    private JPanel buildOrderPage() {
        JPanel p = page("Order Dashboard", "Browse the menu, add products and build an order.");
        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setOpaque(false);
        JPanel left = new JPanel(new BorderLayout(0, 12));
        left.setOpaque(false);
        JPanel controls = new JPanel(new BorderLayout(10, 0));
        controls.setOpaque(false);
        styleTextField(search);
        search.setBorder(fieldBorder());
        search.setPreferredSize(new Dimension(280, 40));
        search.putClientProperty("JTextField.placeholderText", "Search food items");
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                searchText = search.getText().toLowerCase();
                renderProducts();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                searchText = search.getText().toLowerCase();
                renderProducts();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                searchText = search.getText().toLowerCase();
                renderProducts();
            }
        });
        JPanel cats = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        cats.setOpaque(false);
        for (String c : List.of("All", "Pizza", "Burger", "Starters", "Rice", "Beverages", "Dessert", "Veg Only",
                "Non-Veg Only")) {
            JButton b = smallButton(c);
            b.addActionListener(e -> {
                if (c.equals("Veg Only") || c.equals("Non-Veg Only"))
                    activeType = c;
                else {
                    activeCategory = c;
                    activeType = "All";
                }
                renderProducts();
            });
            cats.add(b);
        }
        controls.add(search, BorderLayout.NORTH);
        controls.add(cats, BorderLayout.CENTER);
        left.add(controls, BorderLayout.NORTH);
        productGrid.setOpaque(false);
        left.add(new JScrollPane(productGrid) {
            {
                setBorder(null);
                getViewport().setBackground(BG);
                setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            }
        }, BorderLayout.CENTER);
        JPanel right = buildMiniCart();
        right.setPreferredSize(new Dimension(320, 0));
        body.add(left, BorderLayout.CENTER);
        body.add(right, BorderLayout.EAST);
        p.add(body, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildMiniCart() {
        JPanel card = card(new BorderLayout(0, 12));
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(label("Current Order", 17, INK, true), BorderLayout.WEST);
        head.add(cartCount, BorderLayout.EAST);
        cartCount.setFont(new Font("SansSerif", Font.BOLD, 11));
        cartCount.setForeground(ORANGE);
        card.add(head, BorderLayout.NORTH);
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setName("miniCartRows");
        JScrollPane sc = new JScrollPane(list);
        sc.setBorder(null);
        sc.getViewport().setBackground(Color.WHITE);
        card.add(sc, BorderLayout.CENTER);
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.add(summaryLine("Subtotal", subtotalLabel));
        south.add(Box.createVerticalStrut(8));
        south.add(summaryLine("GST (5%)", taxLabel));
        south.add(new JSeparator());
        south.add(summaryLine("Total", totalLabel));
        JButton go = primary("Review Cart & Checkout");
        go.addActionListener(e -> showPage("Cart"));
        south.add(Box.createVerticalStrut(12));
        south.add(go);
        card.add(south, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildCartPage() {
        JPanel p = page("Cart & Checkout", "Review quantities, enter customer details and place the order.");
        JPanel content = new JPanel(new BorderLayout(16, 0));
        content.setOpaque(false);
        JPanel left = card(new BorderLayout());
        left.add(label("Items in your order", 16, INK, true), BorderLayout.NORTH);
        cartRows.setOpaque(false);
        cartRows.setLayout(new BoxLayout(cartRows, BoxLayout.Y_AXIS));
        left.add(new JScrollPane(cartRows), BorderLayout.CENTER);
        JPanel right = card(new GridBagLayout());
        styleCombo(orderType);
        styleCombo(payment);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 4, 7, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.gridx = 0;
        g.gridy = 0;
        right.add(label("Customer Details", 17, INK, true), g);
        addField(right, g, 1, "Customer name", customer);
        addField(right, g, 2, "Phone number", phone);
        g.gridy = 3;
        right.add(label("Order type", 12, MUTED, true), g);
        g.gridy = 4;
        right.add(orderType, g);
        g.gridy = 5;
        right.add(label("Payment method", 12, MUTED, true), g);
        g.gridy = 6;
        right.add(payment, g);
        g.gridy = 7;
        right.add(new JSeparator(), g);
        g.gridy = 8;
        right.add(summaryLine("Subtotal", subtotalLabel), g);
        g.gridy = 9;
        right.add(summaryLine("GST (5%)", taxLabel), g);
        g.gridy = 10;
        right.add(summaryLine("TOTAL", totalLabel), g);
        g.gridy = 11;
        JButton clear = secondary("Clear Cart");
        clear.addActionListener(e -> {
            if (!cart.isEmpty() && JOptionPane.showConfirmDialog(this, "Remove all items from the cart?", "Clear Cart",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                cart.clear();
                refreshCartUI();
            }
        });
        right.add(clear, g);
        g.gridy = 12;
        JButton place = primary("Place Order & Generate Bill");
        place.addActionListener(e -> checkout());
        right.add(place, g);
        content.add(left, BorderLayout.CENTER);
        right.setPreferredSize(new Dimension(350, 0));
        content.add(right, BorderLayout.EAST);
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildOrdersPage() {
        JPanel p = page("Order History", "Previously completed orders saved in the local SQLite database.");
        JPanel c = card(new BorderLayout(0, 10));
        JButton refresh = secondary("Refresh");
        refresh.addActionListener(e -> refreshOrders());
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.add(label("Recent transactions", 15, INK, true), BorderLayout.WEST);
        h.add(refresh, BorderLayout.EAST);
        c.add(h, BorderLayout.NORTH);
        JTable t = new JTable(ordersModel);
        styleTable(t);
        c.add(new JScrollPane(t), BorderLayout.CENTER);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildReportsPage() {
        JPanel p = page("Reports & Analytics", "Live totals and best-selling items calculated from saved orders.");
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false);
        stats.add(statCard("Total Orders", reportOrders));
        stats.add(statCard("Total Sales", reportSales));
        stats.add(statCard("Average Order", reportAvg));
        content.add(stats, BorderLayout.NORTH);
        JPanel c = card(new BorderLayout());
        c.add(label("Top-selling products", 16, INK, true), BorderLayout.NORTH);
        JTable t = new JTable(reportModel);
        styleTable(t);
        c.add(new JScrollPane(t), BorderLayout.CENTER);
        content.add(c, BorderLayout.CENTER);
        JButton refresh = primary("Refresh Report");
        refresh.addActionListener(e -> refreshReports());
        content.add(refresh, BorderLayout.SOUTH);
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildDashboardPage() {
        JPanel p = page("Restaurant Overview", "Quick access to ordering and a snapshot of your business.");
        JPanel c = new JPanel(new GridLayout(2, 2, 16, 16));
        c.setOpaque(false);
        c.add(actionCard("Start a new order", "Browse products and add them to a cart", "Order"));
        c.add(actionCard("Review current cart", "Update quantities and complete checkout", "Cart"));
        c.add(actionCard("Order history", "View previous transactions", "Orders"));
        c.add(actionCard("Sales reports", "View revenue and popular products", "Reports"));
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildMenuPage() {
        JPanel p = page("Menu Catalogue", "Menu items are stored in SQLite and loaded when the application starts.");
        JPanel c = card(new BorderLayout());
        JTable t = new JTable(new DefaultTableModel(
                new String[] { "ID", "Product", "Category", "Type", "Price (₹)", "Description" }, 0));
        DefaultTableModel m = (DefaultTableModel) t.getModel();
        for (FoodItem f : menu)
            m.addRow(new Object[] { f.getId(), f.getName(), f.getCategory(), f.getFoodType(),
                    String.format("%.2f", f.getPrice()), f.getDescription() });
        styleTable(t);
        c.add(new JScrollPane(t), BorderLayout.CENTER);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private void loadMenu() {
        try {
            menu = menuRepo.getAll();
            renderProducts();
            refreshCartUI();
        } catch (SQLException e) {
            error(e);
        }
    }

    private void renderProducts() {
        productGrid.removeAll();
        for (FoodItem f : menu) {
            if (!activeCategory.equals("All") && !f.getCategory().equals(activeCategory))
                continue;
            if (activeType.equals("Veg Only") && !f.getFoodType().equals("Veg"))
                continue;
            if (activeType.equals("Non-Veg Only") && !f.getFoodType().equals("Non-Veg"))
                continue;
            if (!f.getName().toLowerCase().contains(searchText))
                continue;
            productGrid.add(productCard(f));
        }
        productGrid.revalidate();
        productGrid.repaint();
    }

    private JPanel productCard(FoodItem f) {
        JPanel c = card(new BorderLayout(0, 8));
        JLabel image = new JLabel(foodEmoji(f.getCategory()), SwingConstants.CENTER);
        image.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 46));
        image.setOpaque(true);
        image.setBackground(new Color(255, 244, 235));
        image.setPreferredSize(new Dimension(180, 108));
        java.net.URL imageUrl = getClass().getResource("/images/" + f.getImageFile());
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            Image scaled = icon.getImage().getScaledInstance(240, 132, Image.SCALE_SMOOTH);
            image.setText("");
            image.setIcon(new ImageIcon(scaled));
        }
        c.add(image, BorderLayout.NORTH);
        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        JLabel name = label(f.getName(), 14, INK, true);
        name.setAlignmentX(LEFT_ALIGNMENT);
        JLabel desc = label(f.getDescription(), 10, MUTED, false);
        desc.setAlignmentX(LEFT_ALIGNMENT);
        desc.setToolTipText(f.getDescription());
        JLabel type = label("● " + f.getFoodType(), 10,
                f.getFoodType().equals("Veg") ? new Color(22, 140, 76) : new Color(190, 55, 55), true);
        type.setAlignmentX(LEFT_ALIGNMENT);
        details.add(name);
        details.add(Box.createVerticalStrut(4));
        details.add(desc);
        details.add(Box.createVerticalStrut(5));
        details.add(type);
        c.add(details, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(label("₹" + String.format(Locale.US, "%.2f", f.getPrice()), 15, INK, true), BorderLayout.WEST);
        JButton add = primary("＋ Add");
        add.addActionListener(e -> {
            cart.add(f, 1);
            refreshCartUI();
            status.setText("Added " + f.getName() + " to cart.");
        });
        bottom.add(add, BorderLayout.EAST);
        c.add(bottom, BorderLayout.SOUTH);
        return c;
    }

    private String foodEmoji(String cat) {
        return switch (cat) {
            case "Pizza" -> "🍕";
            case "Burger" -> "🍔";
            case "Starters" -> "🍗";
            case "Rice" -> "🍛";
            case "Beverages" -> "🥤";
            case "Dessert" -> "🍨";
            default -> "🍽";
        };
    }

    private void refreshCartUI() {
        cartCount.setText(cart.count() + " items");
        updateTotals();
        for (Component comp : pageHost.getComponents())
            if (comp instanceof JPanel) {
            }
        ;
        JPanel mini = findMiniRows();
        if (mini != null) {
            mini.removeAll();
            for (var e : cart.getItems().entrySet())
                mini.add(cartLine(e.getKey(), e.getValue(), false));
            mini.revalidate();
            mini.repaint();
        }
        cartRows.removeAll();
        if (cart.isEmpty()) {
            cartRows.add(label("Your cart is empty. Add products from the Order Dashboard.", 13, MUTED, false));
        } else
            for (var e : cart.getItems().entrySet())
                cartRows.add(cartLine(e.getKey(), e.getValue(), true));
        cartRows.revalidate();
        cartRows.repaint();
    }

    private JPanel findMiniRows() {
        return findNamed(pageHost, "miniCartRows");
    }

    private JPanel findNamed(Container c, String name) {
        for (Component x : c.getComponents()) {
            if (x instanceof JPanel p && name.equals(p.getName()))
                return p;
            if (x instanceof Container nested) {
                JPanel found = findNamed(nested, name);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    private JPanel cartLine(FoodItem f, int qty, boolean controls) {
        JPanel row = new JPanel(new BorderLayout(8, 4));
        row.setOpaque(false);
        row.setBorder(new MatteBorder(0, 0, 1, 0, LINE));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, controls ? 76 : 62));
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(label(f.getName(), 12, INK, true));
        info.add(label("₹" + String.format("%.2f", f.getPrice()) + "  ×  " + qty, 11, MUTED, false));
        row.add(info, BorderLayout.CENTER);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 5));
        right.setOpaque(false);
        if (controls) {
            JButton minus = miniButton("−"), plus = miniButton("+"), remove = miniButton("×");
            minus.addActionListener(e -> {
                try {
                    if (qty == 1)
                        cart.remove(f);
                    else
                        cart.setQuantity(f, qty - 1);
                    refreshCartUI();
                } catch (Exception ex) {
                    error(ex);
                }
            });
            plus.addActionListener(e -> {
                try {
                    cart.setQuantity(f, qty + 1);
                    refreshCartUI();
                } catch (Exception ex) {
                    error(ex);
                }
            });
            remove.addActionListener(e -> {
                cart.remove(f);
                refreshCartUI();
            });
            right.add(minus);
            right.add(label(String.valueOf(qty), 12, INK, true));
            right.add(plus);
            right.add(remove);
        } else
            right.add(label("₹" + String.format("%.2f", f.getPrice() * qty), 12, INK, true));
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private void updateTotals() {
        double sub = cart.calculateSubtotal(), tax = cart.calculateTax(Order.TAX_RATE);
        subtotalLabel.setText("₹" + String.format(Locale.US, "%.2f", sub));
        taxLabel.setText("₹" + String.format(Locale.US, "%.2f", tax));
        totalLabel.setText("₹" + String.format(Locale.US, "%.2f", sub + tax));
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add at least one product before checkout.");
            return;
        }
        try {
            Order o = new Order(customer.getText(), phone.getText(), (String) orderType.getSelectedItem(),
                    (String) payment.getSelectedItem(), cart);
            int ok = JOptionPane.showConfirmDialog(this,
                    "Place order " + o.getCode() + " for ₹" + String.format("%.2f", o.getTotal()) + "?",
                    "Confirm Order", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION)
                return;
            orderRepo.save(o);
            Path receipt = new ReceiptService().save(o);
            JTextArea area = new JTextArea(receiptText(o), 22, 46);
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            area.setForeground(INK);
            area.setBackground(new Color(250, 251, 252));
            area.setCaretColor(INK);
            area.setSelectionColor(new Color(255, 225, 204));
            area.setSelectedTextColor(INK);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Order placed • " + o.getCode(),
                    JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(this, "Bill saved locally:\n" + receipt.toAbsolutePath(), "Receipt saved",
                    JOptionPane.INFORMATION_MESSAGE);
            cart.clear();
            customer.setText("");
            phone.setText("");
            refreshCartUI();
            refreshOrders();
            refreshReports();
            showPage("Orders");
            status.setText("Order " + o.getCode() + " saved successfully.");
        } catch (Exception ex) {
            error(ex);
        }
    }

    private String receiptText(Order o) {
        StringBuilder b = new StringBuilder("             FOODIE'S RESTAURANT\n               TAX INVOICE\n\n");
        b.append("Order: ").append(o.getCode()).append("\nCustomer: ").append(o.getCustomer()).append("\nOrder type: ")
                .append(o.getOrderType()).append("\nPayment: ").append(o.getPayment()).append("\n\n");
        for (var e : o.getItems().entrySet())
            b.append(String.format("%-24s %2d  ₹%.2f%n", e.getKey().getName(), e.getValue(),
                    e.getKey().getPrice() * e.getValue()));
        b.append(String.format("%nSubtotal: ₹%.2f%nGST (5%%): ₹%.2f%nTOTAL: ₹%.2f", o.getSubtotal(), o.getTax(),
                o.getTotal()));
        return b.toString();
    }

    private void refreshOrders() {
        try {
            ordersModel.setRowCount(0);
            for (String[] r : orderRepo.recent())
                ordersModel.addRow(r);
        } catch (SQLException e) {
            error(e);
        }
    }

    private void refreshReports() {
        try {
            double[] s = orderRepo.summary();
            reportOrders.setText(String.valueOf((int) s[0]));
            reportSales.setText("₹" + String.format("%.2f", s[1]));
            reportAvg.setText("₹" + String.format("%.2f", s[2]));
            reportModel.setRowCount(0);
            for (String[] r : orderRepo.bestSellers())
                reportModel.addRow(r);
        } catch (SQLException e) {
            error(e);
        }
    }

    private void showPage(String key) {
        pages.show(pageHost, key);
        navButtons.forEach((k, b) -> {
            boolean on = k.equals(key);
            b.setBackground(on ? ORANGE : NAVY);
            b.setForeground(Color.WHITE);
            b.repaint();
        });
        if (key.equals("Cart"))
            refreshCartUI();
        if (key.equals("Orders"))
            refreshOrders();
        if (key.equals("Reports"))
            refreshReports();
        if (key.equals("Menu")) {
            pageHost.remove(pageHost.getComponentCount() - 1);
            pageHost.add(buildMenuPage(), "Menu");
            pages.show(pageHost, "Menu");
        }
    }

    private JPanel card(LayoutManager l) {
        JPanel p = new JPanel(l);
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(LINE, 1, true), new EmptyBorder(14, 14, 14, 14)));
        return p;
    }

    private JLabel label(String s, int size, Color color, boolean bold) {
        JLabel l = new JLabel(s);
        l.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }

    private JPanel summaryLine(String name, JLabel value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(label(name, 12, MUTED, false), BorderLayout.WEST);
        p.add(value, BorderLayout.EAST);
        return p;
    }

    private JPanel statCard(String title, JLabel value) {
        JPanel p = card(new GridLayout(2, 1, 0, 8));
        p.add(label(title, 12, MUTED, true));
        p.add(value);
        return p;
    }

    private JPanel actionCard(String title, String sub, String target) {
        JPanel p = card(new BorderLayout(0, 10));
        p.add(label(title, 17, INK, true), BorderLayout.NORTH);
        p.add(label(sub, 12, MUTED, false), BorderLayout.CENTER);
        JButton b = primary("Open");
        b.addActionListener(e -> showPage(target));
        p.add(b, BorderLayout.SOUTH);
        return p;
    }

    private void addField(JPanel p, GridBagConstraints g, int y, String title, JTextField f) {
        g.gridy = y;
        g.gridx = 0;
        p.add(label(title, 12, MUTED, true), g);
        g.gridy = y + 1;
        styleTextField(f);
        f.setBorder(fieldBorder());
        f.setPreferredSize(new Dimension(260, 36));
        p.add(f, g);
    }

    private Border fieldBorder() {
        return new CompoundBorder(new LineBorder(LINE, 1, true), new EmptyBorder(8, 10, 8, 10));
    }

    private JButton primary(String t) {
        JButton b = new JButton(t);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(ORANGE);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 14, 10, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton secondary(String t) {
        JButton b = new JButton(t);
        b.setFont(new Font("SansSerif", Font.BOLD, 11));
        b.setForeground(INK);
        b.setBackground(Color.WHITE);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(LINE, 1, true));
        return b;
    }

    private JButton smallButton(String t) {
        JButton b = secondary(t);
        b.setBorder(new EmptyBorder(7, 11, 7, 11));
        return b;
    }

    private JButton miniButton(String t) {
        JButton b = new JButton(t);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(INK);
        b.setBackground(new Color(248, 249, 251));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(LINE, 1, true));
        b.setPreferredSize(new Dimension(28, 25));
        return b;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(INK);
        field.setBackground(Color.WHITE);
        field.setCaretColor(INK);
        field.setSelectionColor(new Color(235, 102, 27));
        field.setSelectedTextColor(Color.WHITE);
        field.setDisabledTextColor(MUTED);
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setForeground(INK);
        combo.setBackground(Color.WHITE);
        combo.setOpaque(true);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected,
                    boolean focus) {
                JLabel cell = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                cell.setFont(new Font("SansSerif", Font.PLAIN, 13));
                cell.setOpaque(true);
                cell.setBackground(selected ? new Color(255, 239, 228) : Color.WHITE);
                cell.setForeground(selected ? INK : INK);
                cell.setBorder(new EmptyBorder(5, 8, 5, 8));
                return cell;
            }
        });
    }

    private void styleTable(JTable t) {
        t.setFont(new Font("SansSerif", Font.PLAIN, 12));
        t.setForeground(INK);
        t.setBackground(Color.WHITE);
        t.setSelectionForeground(INK);
        t.setSelectionBackground(new Color(255, 239, 228));
        t.setGridColor(LINE);
        t.setRowHeight(34);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setOpaque(true);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(244, 246, 249));
        t.getTableHeader().setForeground(INK);
        t.getTableHeader().setOpaque(true);
        t.getTableHeader().setReorderingAllowed(false);
    }

    private void error(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Application error", JOptionPane.ERROR_MESSAGE);
    }
}