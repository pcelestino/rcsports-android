package br.com.rcsports.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Pedro on 14/12/2014.
 */
public class DatabaseFactory extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DB_NAME = "RCSports.db";
    private final String DB_FILEPATH;
    private Context context;

    public DatabaseFactory(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
        DB_FILEPATH = context.getDatabasePath(DB_NAME).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Clients (" +
                "_id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "phone TEXT," +
                "address TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Products (" +
                "_id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "price DOUBLE NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS Sales (" +
                "_id INTEGER PRIMARY KEY," +
                "total DOUBLE," +
                "date TEXT NOT NULL," +
                "time TEXT NOT NULL," +
                "client_id INTEGER NOT NULL," +
                "FOREIGN KEY(client_id) REFERENCES Clients(_id));");

        db.execSQL("CREATE TABLE IF NOT EXISTS Transactions (" +
                "_id INTEGER PRIMARY KEY," +
                "amount TEXT NOT NULL," +
                "product_id INTEGER NOT NULL," +
                "sale_id INTEGER NOT NULL," +
                "FOREIGN KEY(product_id) REFERENCES Products(_id)," +
                "FOREIGN KEY(sale_id) REFERENCES Sales(_id));");

        db.execSQL("CREATE TABLE IF NOT EXISTS Payments (" +
                "_id INTEGER PRIMARY KEY," +
                "paid DOUBLE NOT NULL," +
                "date TEXT NOT NULL," +
                "time TEXT NOT NULL," +
                "sale_id INTEGER NOT NULL," +
                "FOREIGN KEY(sale_id) REFERENCES Sales(_id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Sales");
        db.execSQL("DROP TABLE IF EXISTS Clients");
        db.execSQL("DROP TABLE IF EXISTS Products");
        db.execSQL("DROP TABLE IF EXISTS Payments");
        db.execSQL("DROP TABLE IF EXISTS Transactions");
        onCreate(db);
    }

    public void importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String backupDBPath = "/" + DB_NAME;

                File backupDB = new File(DB_FILEPATH);
                File folder = new File(sd + "/RCSports");
                File currentDB = new File(folder, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, "Dados importados com sucesso!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Toast.makeText(context, "Falha ao importar!", Toast.LENGTH_SHORT).show();
        }
    }

    public void exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String backupDBPath = "/" + DB_NAME;

                File currentDB = new File(DB_FILEPATH);
                File folder = new File(sd + "/RCSports");

                // Caso a pasta RCSports não exista ela será criada
                boolean result = true;
                if (!folder.exists()) {
                    result = folder.mkdir();
                }

                File backupDB = new File(folder, backupDBPath);

                if (result) {

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(context, "Dados exportados com sucesso!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "Falha ao criar o diretório!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {

            Toast.makeText(context, "Falha ao exportar!", Toast.LENGTH_SHORT).show();
        }
    }

    public static class Clients {
        public static final String TABLE = "Clients";
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String PHONE = "phone";
        public static final String ADDRESS = "address";
        public static final String[] COLUMNS = new String[]{ID, NAME, PHONE, ADDRESS};
    }

    public static class Products {
        public static final String TABLE = "Products";
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String[] COLUMNS = new String[]{ID, NAME, PRICE};
    }

    public static class Sales {
        public static final String TABLE = "Sales";
        public static final String ID = "_id";
        public static final String TOTAL = "total";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String CLIENT_ID = "client_id";
        public static final String[] COLUMNS = new String[]{ID, TOTAL, DATE, TIME, CLIENT_ID};
    }

    public static class Transactions {
        public static final String TABLE = "Transactions";
        public static final String ID = "_id";
        public static final String AMOUNT = "amount";
        public static final String PRODUCT_ID = "product_id";
        public static final String SALE_ID = "sale_id";
        public static final String[] COLUMNS = new String[]{ID, AMOUNT, PRODUCT_ID, SALE_ID};
    }

    public static class Payments {
        public static final String TABLE = "Payments";
        public static final String ID = "_id";
        public static final String PAID = "paid";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String SALE_ID = "sale_id";
        public static final String[] COLUMNS = new String[]{ID, PAID, DATE, TIME, SALE_ID};
    }
}
