package br.com.rcsports.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.rcsports.database.DatabaseFactory;
import br.com.rcsports.model.Client;
import br.com.rcsports.model.Payment;
import br.com.rcsports.model.Product;
import br.com.rcsports.model.Sale;
import br.com.rcsports.model.Transaction;

/**
 * Created by mrped_000 on 15/12/2014.
 */
public class DAO {

    private static DAO instance;
    private DatabaseFactory dbFactory;
    private SQLiteDatabase db;

    public DAO(Context context) {
        super();
        this.dbFactory = new DatabaseFactory(context);
        this.db = this.dbFactory.getWritableDatabase();
    }

    public synchronized static DAO open(Context context) {
        if (instance == null) {
            instance = new DAO(context);
        }
        return instance;
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
            db = null;
        }
        if (dbFactory != null) {
            dbFactory.close();
            dbFactory = null;
        }
        instance = null;
    }

    public long insert(Client client) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseFactory.Clients.ID, client.getId());
        contentValues.put(DatabaseFactory.Clients.NAME, client.getName());
        contentValues.put(DatabaseFactory.Clients.PHONE, client.getPhone());
        contentValues.put(DatabaseFactory.Clients.ADDRESS, client.getAddress());
        long rowId = db.insert(DatabaseFactory.Clients.TABLE, null, contentValues);
        close();
        return rowId;
    }

    public long insert(Product product) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseFactory.Products.ID, product.getId());
        contentValues.put(DatabaseFactory.Products.NAME, product.getName());
        contentValues.put(DatabaseFactory.Products.PRICE, product.getPrice());
        long rowId = db.insert(DatabaseFactory.Products.TABLE, null, contentValues);
        close();
        return rowId;
    }

    public long insert(Sale sale) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseFactory.Sales.ID, sale.getId());
        contentValues.put(DatabaseFactory.Sales.TOTAL, sale.getTotal());
        contentValues.put(DatabaseFactory.Sales.DATE, sale.getDate());
        contentValues.put(DatabaseFactory.Sales.TIME, sale.getTime());
        contentValues.put(DatabaseFactory.Sales.CLIENT_ID, sale.getClient_id());
        long rowId = db.insert(DatabaseFactory.Sales.TABLE, null, contentValues);
        close();
        return rowId;
    }

    public long insert(Transaction transaction) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseFactory.Transactions.ID, transaction.getId());
        contentValues.put(DatabaseFactory.Transactions.AMOUNT, transaction.getAmount());
        contentValues.put(DatabaseFactory.Transactions.PRODUCT_ID, transaction.getProduct_id());
        contentValues.put(DatabaseFactory.Transactions.SALE_ID, transaction.getSale_id());
        long rowId = db.insert(DatabaseFactory.Transactions.TABLE, null, contentValues);
        close();
        return rowId;
    }

    public long insert(Payment payment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseFactory.Payments.ID, payment.getId());
        contentValues.put(DatabaseFactory.Payments.PAID, payment.getPaid());
        contentValues.put(DatabaseFactory.Payments.DATE, payment.getDate());
        contentValues.put(DatabaseFactory.Payments.TIME, payment.getTime());
        contentValues.put(DatabaseFactory.Payments.SALE_ID, payment.getSale_id());
        long rowId = db.insert(DatabaseFactory.Payments.TABLE, null, contentValues);
        close();
        return rowId;
    }

    public long update(Client client) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseFactory.Clients.ID, client.getId());
        contentValues.put(DatabaseFactory.Clients.NAME, client.getName());
        contentValues.put(DatabaseFactory.Clients.PHONE, client.getPhone());
        contentValues.put(DatabaseFactory.Clients.ADDRESS, client.getAddress());
        long rowId = db.update(DatabaseFactory.Clients.TABLE, contentValues,
                DatabaseFactory.Clients.ID + " = ?", new String[]{client.getId()});
        close();
        return rowId;
    }

    public long update(Product product) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseFactory.Products.ID, product.getId());
        contentValues.put(DatabaseFactory.Products.NAME, product.getName());
        contentValues.put(DatabaseFactory.Products.PRICE, product.getPrice());
        long rowId = db.update(DatabaseFactory.Products.TABLE, contentValues,
                DatabaseFactory.Products.ID + " = ?", new String[]{product.getId()});
        close();
        return rowId;
    }

    public long update(Sale sale) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseFactory.Sales.ID, sale.getId());
        contentValues.put(DatabaseFactory.Sales.TOTAL, sale.getTotal());
        contentValues.put(DatabaseFactory.Sales.DATE, sale.getDate());
        contentValues.put(DatabaseFactory.Sales.TIME, sale.getTime());
        contentValues.put(DatabaseFactory.Sales.CLIENT_ID, sale.getClient_id());
        long rowId = db.update(DatabaseFactory.Sales.TABLE, contentValues,
                DatabaseFactory.Sales.ID + " = ?", new String[]{sale.getId()});
        close();
        return rowId;
    }

    public long update(Transaction transaction) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseFactory.Transactions.ID, transaction.getId());
        contentValues.put(DatabaseFactory.Transactions.AMOUNT, transaction.getAmount());
        contentValues.put(DatabaseFactory.Transactions.PRODUCT_ID, transaction.getProduct_id());
        contentValues.put(DatabaseFactory.Transactions.SALE_ID, transaction.getSale_id());
        long rowId = db.update(DatabaseFactory.Transactions.TABLE, contentValues,
                DatabaseFactory.Transactions.ID + " = ?", new String[]{transaction.getId()});
        close();
        return rowId;
    }

    public long update(Payment payment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseFactory.Payments.ID, payment.getId());
        contentValues.put(DatabaseFactory.Payments.PAID, payment.getPaid());
        contentValues.put(DatabaseFactory.Payments.DATE, payment.getDate());
        contentValues.put(DatabaseFactory.Payments.TIME, payment.getTime());
        contentValues.put(DatabaseFactory.Payments.SALE_ID, payment.getSale_id());
        long rowId = db.update(DatabaseFactory.Payments.TABLE, contentValues,
                DatabaseFactory.Payments.ID + " = ?", new String[]{payment.getId()});
        close();
        return rowId;
    }

    public boolean delete(Client client) {
        int removed = db.delete(DatabaseFactory.Clients.TABLE,
                DatabaseFactory.Clients.ID + " = ?", new String[]{client.getId()});
        close();
        return removed > 0;
    }

    public boolean delete(Product product) {
        int removed = db.delete(DatabaseFactory.Products.TABLE,
                DatabaseFactory.Products.ID + " = ?", new String[]{product.getId()});
        close();
        return removed > 0;
    }

    public boolean delete(Sale sale) {
        int removed = db.delete(DatabaseFactory.Sales.TABLE,
                DatabaseFactory.Sales.ID + " = ?", new String[]{sale.getId()});
        close();
        return removed > 0;
    }

    public boolean delete(Transaction transaction) {
        int removed = db.delete(DatabaseFactory.Transactions.TABLE,
                DatabaseFactory.Transactions.ID + " = ?", new String[]{transaction.getId()});
        close();
        return removed > 0;
    }

    public boolean delete(Payment payment) {
        int removed = db.delete(DatabaseFactory.Payments.TABLE,
                DatabaseFactory.Payments.ID + " = ?", new String[]{payment.getId()});
        close();
        return removed > 0;
    }

    public Client findClientById(String id) {
        Cursor cursor = db.query(DatabaseFactory.Clients.TABLE,
                DatabaseFactory.Clients.COLUMNS,
                DatabaseFactory.Clients.ID + " = ?",
                new String[]{id}, null, null, null);

        if (cursor.moveToNext()) {
            Client client = createClient(cursor);
            cursor.close();
            close();
            return client;
        }
        return null;
    }

    public Product findProductById(String id) {
        Cursor cursor = db.query(DatabaseFactory.Products.TABLE,
                DatabaseFactory.Products.COLUMNS,
                DatabaseFactory.Products.ID + " = ?",
                new String[]{id}, null, null, null);

        if (cursor.moveToNext()) {
            Product product = createProduct(cursor);
            cursor.close();
            close();
            return product;
        }
        return null;
    }

    public Sale findSaleById(String id) {
        Cursor cursor = db.query(DatabaseFactory.Sales.TABLE,
                DatabaseFactory.Sales.COLUMNS,
                DatabaseFactory.Sales.ID + " = ?",
                new String[]{id}, null, null, null);

        if (cursor.moveToNext()) {
            Sale sale = createSale(cursor);
            cursor.close();
            close();
            return sale;
        }
        return null;
    }

    public Payment findPaymentById(String id) {
        Cursor cursor = db.query(DatabaseFactory.Payments.TABLE,
                DatabaseFactory.Payments.COLUMNS,
                DatabaseFactory.Payments.ID + " = ?",
                new String[]{id}, null, null, null);

        if (cursor.moveToNext()) {
            Payment payment = createPayment(cursor);
            cursor.close();
            close();
            return payment;
        }
        return null;
    }

    public List<Client> getListClients() {
        Cursor cursor = db.query(DatabaseFactory.Clients.TABLE,
                DatabaseFactory.Clients.COLUMNS,
                null, null, null, null, null);

        List<Client> clients = new ArrayList<>();

        while (cursor.moveToNext()) {
            Client client = createClient(cursor);
            clients.add(client);
        }
        cursor.close();
        close();
        return clients;
    }

    public List<Product> getListProducts() {
        Cursor cursor = db.query(DatabaseFactory.Products.TABLE,
                DatabaseFactory.Products.COLUMNS,
                null, null, null, null, null);

        List<Product> products = new ArrayList<>();

        while (cursor.moveToNext()) {
            Product product = createProduct(cursor);
            products.add(product);
        }
        cursor.close();
        close();
        return products;
    }

    public List<Sale> getListSales() {
        Cursor cursor = db.query(DatabaseFactory.Sales.TABLE,
                DatabaseFactory.Sales.COLUMNS,
                null, null, null, null, null);

        List<Sale> sales = new ArrayList<>();

        while (cursor.moveToNext()) {
            Sale sale = createSale(cursor);
            sales.add(sale);
        }
        cursor.close();
        close();
        return sales;
    }

    public List<Payment> getListPayments() {
        Cursor cursor = db.query(DatabaseFactory.Payments.TABLE,
                DatabaseFactory.Payments.COLUMNS,
                null, null, null, null, null);

        List<Payment> payments = new ArrayList<>();

        while (cursor.moveToNext()) {
            Payment payment = createPayment(cursor);
            payments.add(payment);
        }
        cursor.close();
        close();
        return payments;
    }

    public Client createClient(Cursor cursor) {
        Client client = new Client();
        client.setId(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Clients.ID)));
        client.setName(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Clients.NAME)));
        client.setPhone(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Clients.PHONE)));
        client.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Clients.ADDRESS)));
        return client;
    }

    public Product createProduct(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Products.ID)));
        product.setName(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Products.NAME)));
        product.setPrice(cursor.getDouble(cursor.getColumnIndex(DatabaseFactory.Products.PRICE)));
        return product;
    }

    public Sale createSale(Cursor cursor) {
        Sale sale = new Sale();
        sale.setId(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Sales.ID)));
        sale.setTotal(cursor.getDouble(cursor.getColumnIndex(DatabaseFactory.Sales.TOTAL)));
        sale.setDate(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Sales.DATE)));
        sale.setTime(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Sales.TIME)));
        sale.setClient_id(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Sales.CLIENT_ID)));
        return sale;
    }

    public Transaction createTransaction(Cursor cursor) {
        Transaction transaction = new Transaction();
        transaction.setId(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Transactions.ID)));
        transaction.setAmount(cursor.getInt(cursor.getColumnIndex(DatabaseFactory.Transactions.AMOUNT)));
        transaction.setProduct_id(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Transactions.PRODUCT_ID)));
        transaction.setSale_id(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Transactions.SALE_ID)));
        return transaction;
    }

    public Payment createPayment(Cursor cursor) {
        Payment payment = new Payment();
        payment.setId(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Payments.ID)));
        payment.setPaid(cursor.getDouble(cursor.getColumnIndex(DatabaseFactory.Payments.PAID)));
        payment.setDate(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Payments.DATE)));
        payment.setTime(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Payments.TIME)));
        payment.setSale_id(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Payments.SALE_ID)));
        return payment;
    }

    public List<Transaction> findTransactionsWithSaleId(String saleId) {
        Cursor cursor = db.query(DatabaseFactory.Transactions.TABLE,
                DatabaseFactory.Transactions.COLUMNS,
                DatabaseFactory.Transactions.SALE_ID + " = ?", new String[]{saleId}, null, null, null);

        List<Transaction> transactions = new ArrayList<>();

        while (cursor.moveToNext()) {
            Transaction transaction = createTransaction(cursor);
            transactions.add(transaction);
        }
        cursor.close();
        close();
        return transactions;
    }

    public List<Sale> findSalesWithClientId(String clientId) {
        Cursor cursor = db.query(DatabaseFactory.Sales.TABLE,
                DatabaseFactory.Sales.COLUMNS,
                DatabaseFactory.Sales.CLIENT_ID + " = ?", new String[]{clientId}, null, null, null);

        List<Sale> sales = new ArrayList<>();

        while (cursor.moveToNext()) {
            Sale sale = createSale(cursor);
            sales.add(sale);
        }
        cursor.close();
        close();
        return sales;
    }

    public List<Sale> findSalesWithProductId(String productId) {
        Cursor cursor = db.query(DatabaseFactory.Transactions.TABLE,
                DatabaseFactory.Transactions.COLUMNS,
                DatabaseFactory.Transactions.PRODUCT_ID + " = ?", new String[]{productId}, null, null, null);

        List<Sale> sales = new ArrayList<>();

        while (cursor.moveToNext()) {
            Transaction transaction = createTransaction(cursor);
            Sale sale = findSaleById(transaction.getSale_id());
            sales.add(sale);
        }
        cursor.close();
        close();
        return sales;
    }

    public List<Payment> findPaymentsWithSaleId(String saleId) {
        Cursor cursor = db.query(DatabaseFactory.Payments.TABLE,
                DatabaseFactory.Payments.COLUMNS,
                DatabaseFactory.Payments.SALE_ID + " = ?", new String[]{saleId}, null, null, null);

        List<Payment> payments = new ArrayList<>();

        while (cursor.moveToNext()) {
            Payment payment = createPayment(cursor);
            payments.add(payment);
        }
        cursor.close();
        close();
        return payments;
    }

    public void importDatabase() {
        dbFactory.importDB();
    }

    public void exportDatabase() {
        dbFactory.exportDB();
    }

}
