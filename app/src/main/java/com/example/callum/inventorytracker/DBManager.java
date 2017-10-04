package com.example.callum.inventorytracker;
import android.content.CursorLoader;
import android.database.ContentObservable;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
/**
 * Created by rorygoodman on 26/11/2016.
 */

public class DBManager extends SQLiteOpenHelper{
    private static int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Projects.db";
    private static final String TABLE_PROJECTS = "projects";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PROJECTNAME = "project_name";
    private static final String COLUMN_PROJECT_FINISH_DATE = "project_finish_date";
    private static final String TABLE_EQUIPMENT = "equipment";
    private static final String COLUMN_PROJECT_ID = "project_id";
    private static final String COLUMN_EQUIPMENT_NAME = "equipment_name";
    private static final String COLUMN_BARCODE = "barcode";
    private static final String COLUMN_INDIVIDUAL = "individual";
    private static final String COLUMN_EXPIRY = "expiry_date";
    private static final String COLUMN_DAMAGED = "damaged";




    public DBManager(Context context,String name,SQLiteDatabase.CursorFactory factory, int version){
        super(context,DATABASE_NAME,factory,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String query1 = "CREATE TABLE "+TABLE_PROJECTS+ "("+
                COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PROJECTNAME+" TEXT, "+COLUMN_PROJECT_FINISH_DATE+" TEXT);";
        String query2 = "CREATE TABLE "+TABLE_EQUIPMENT+ "("+
                COLUMN_PROJECT_ID+" INT, " +
                COLUMN_EQUIPMENT_NAME+" TEXT, "+COLUMN_INDIVIDUAL+" TEXT, "
                +COLUMN_BARCODE+" TEXT, "+COLUMN_EXPIRY+" TEXT,"+COLUMN_DAMAGED+" INT);" ;
        db.execSQL(query1);
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_EQUIPMENT);
        onCreate(db);
    }
    public void addProject(Project project){ //adds a new project to the project table
        ContentValues c = new ContentValues();
        c.put(COLUMN_ID,0);
        c.put(COLUMN_PROJECTNAME,project.getProjectName());
        c.put(COLUMN_PROJECT_FINISH_DATE,project.getExpiryDate());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PROJECTS,null,c);
        db.close();
    }

    public boolean deleteProject(Project project){
        int proj_id;
        String query = "SELECT * FROM "+TABLE_PROJECTS+" WHERE "+COLUMN_PROJECTNAME+"="+"'"+project.getProjectName()+"'";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();
        proj_id=c.getInt(c.getColumnIndex(COLUMN_PROJECT_ID));
        if(proj_id==-1){
            return false;
        }
        c.close();
        db = getWritableDatabase();
        db.execSQL("DELETE * FROM "+TABLE_EQUIPMENT+" WHERE "+COLUMN_PROJECT_ID+"="+"'"+proj_id+"'");
        db.execSQL("DELETE * FROM "+TABLE_PROJECTS+" WHERE "+COLUMN_ID+"="+"'"+proj_id+"'");
        db.execSQL("DELETE * FROM "+TABLE_EQUIPMENT+" WHERE "+COLUMN_PROJECT_ID+"="+"'"+proj_id+"'");
        db.execSQL("DELETE * FROM "+TABLE_PROJECTS+" WHERE "+COLUMN_ID+"="+"'"+proj_id+"'");
        return true;
    }
    public void deleteEquipmentItem(EquipmentItem item){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE * FROM "+TABLE_EQUIPMENT+" WHERE "+COLUMN_BARCODE+"="+"'"+item.getBarcodeID()+"'");
    }

    //Function to check if a project is already in the DB
    public boolean checkProject(String projectName){
        String query = "SELECT * FROM " + TABLE_PROJECTS + " WHERE " + COLUMN_PROJECTNAME + "="
                +"'"+projectName+"'";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query,null);
        return c.moveToFirst();
    }

    //removes data with the same barcode ID, replaces with new data
    public void editEquipment(EquipmentItem item){
        deleteEquipmentItem(item);
        addEquipment(item);
    }
    
    //removes data with the same project name, replaces with new data
    public void editProject(Project proj){
        deleteProject(proj);
        addProject(proj);
    }

    //adds new equipment item
    public void addEquipment(EquipmentItem equipment){//adds new equipments to equipment table
        ContentValues c = new ContentValues();
        c.put(COLUMN_PROJECT_ID,equipment.getProjectID());
        c.put(COLUMN_EQUIPMENT_NAME,equipment.getName());
        c.put(COLUMN_INDIVIDUAL,equipment.getIndividual());
        c.put(COLUMN_BARCODE,equipment.getBarcodeID());
        c.put(COLUMN_EXPIRY,equipment.getExpiryDate());
        c.put(COLUMN_DAMAGED,equipment.isDamaged());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_EQUIPMENT,null,c);
        db.close();
    }
    //returns object from name
    public EquipmentItem getEquipmentItem(String item) {
        EquipmentItem equipment;
        String query = "SELECT * FROM " + TABLE_EQUIPMENT + " WHERE " + COLUMN_EQUIPMENT_NAME + "=" + "'"+item+"'";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if(!c.moveToFirst()){
            c.close();
            return null;
        }

        equipment = new EquipmentItem(c.getString(c.getColumnIndex(COLUMN_EQUIPMENT_NAME)),
                c.getString(c.getColumnIndex(COLUMN_INDIVIDUAL)),
                c.getInt(c.getColumnIndex(COLUMN_PROJECT_ID)),
                c.getString(c.getColumnIndex(COLUMN_BARCODE)),
                c.getString(c.getColumnIndex(COLUMN_EXPIRY)),
                c.getInt(c.getColumnIndex(COLUMN_DAMAGED))>0
                );
        c.close();
        return equipment;
    }

    //Function to check if an equipment item is already in the DB
    public boolean checkEquipment(String equipmentName){
        String query = "SELECT * FROM " + TABLE_EQUIPMENT + " WHERE " + COLUMN_EQUIPMENT_NAME + " = "
                +"'"+equipmentName+"'";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query,null);
        boolean ret = c.moveToFirst();
        c.close();
        return ret;
    }
    public EquipmentItem checkBarcode(String barcode){
        String query = "SELECT * FROM " + TABLE_EQUIPMENT + " WHERE " + COLUMN_BARCODE + " = '"+barcode+"'";
        SQLiteDatabase db = getReadableDatabase();
        EquipmentItem equipment;
        Cursor c = db.rawQuery(query,null);
        if(!c.moveToFirst()){
            c.close();
            return null;
        }
        else{
            equipment = new EquipmentItem(c.getString(c.getColumnIndex(COLUMN_EQUIPMENT_NAME)),
                    c.getString(c.getColumnIndex(COLUMN_INDIVIDUAL)),
                    c.getInt(c.getColumnIndex(COLUMN_PROJECT_ID)),
                    c.getString(c.getColumnIndex(COLUMN_BARCODE)),
                    c.getString(c.getColumnIndex(COLUMN_EXPIRY)),
                    c.getInt(c.getColumnIndex(COLUMN_DAMAGED))>0

            );
            c.close();
            return equipment;
        }


    }

    public EquipmentItem[] searchItems(String search){
        EquipmentItem[] items;
        String query = "SELECT * FROM " + TABLE_EQUIPMENT + " WHERE " + COLUMN_EQUIPMENT_NAME + " LIKE '"+search+"%"+"'";
        SQLiteDatabase db = getReadableDatabase();
        EquipmentItem equipment;
        Cursor c = db.rawQuery(query,null);
        items = new EquipmentItem[c.getCount()];
        if(!c.moveToFirst()){
            return null;
        }
        else{
            for(int i=0;i<items.length;i++){
                items[i] = new EquipmentItem(c.getString(c.getColumnIndex(COLUMN_EQUIPMENT_NAME)),
                        c.getString(c.getColumnIndex(COLUMN_INDIVIDUAL)),
                        c.getInt(c.getColumnIndex(COLUMN_PROJECT_ID)),
                        c.getString(c.getColumnIndex(COLUMN_BARCODE)),
                        c.getString(c.getColumnIndex(COLUMN_EXPIRY)),
                        c.getInt(c.getColumnIndex(COLUMN_DAMAGED))>0

                );
                c.moveToNext();
            }
        }
        return items;
    }
}
