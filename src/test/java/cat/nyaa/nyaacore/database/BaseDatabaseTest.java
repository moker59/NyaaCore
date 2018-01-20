package cat.nyaa.nyaacore.database;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.UUID;
import static org.junit.Assert.*;

public class BaseDatabaseTest {
    TestSQLiteDatabase db;
    TestTable record;

    @Before
    public void prepareDatabase() {
        db = new TestSQLiteDatabase();
        record = new TestTable();
        record.id = null;
        record.string = "test";
        record.uuid = UUID.randomUUID();
        record.uuid_indirect = UUID.randomUUID();
        db.query(TestTable.class).insert(record);
        //db.query(TestTable.class).insert(record);
        record.id = 1L; // SQLite auto inc for PRIMARY INT
    }

    @After
    public void closeDatabase() {
        db.close();
        db.dbFile.delete();
    }

    @Test
    public void test1() throws Exception {
        TestTable ret = db.query(TestTable.class).selectUnique();
        assertEquals(record.id, ret.id);
        assertEquals(record.string, ret.string);
        assertEquals(record.uuid, ret.uuid);
        assertEquals(record.uuid_indirect, ret.uuid_indirect);
    }

    @Test
    public void test2() throws Exception {
        TestTable ret = db.query(TestTable.class).whereEq("id", record.id).selectUnique();
        assertEquals(record.id, ret.id);
        assertEquals(record.string, ret.string);
        assertEquals(record.uuid, ret.uuid);
        assertEquals(record.uuid_indirect, ret.uuid_indirect);
    }

    @Test
    public void test3() throws Exception {
        TestTable ret = db.query(TestTable.class).whereEq("string", record.string).selectUnique();
        assertEquals(record.id, ret.id);
        assertEquals(record.string, ret.string);
        assertEquals(record.uuid, ret.uuid);
        assertEquals(record.uuid_indirect, ret.uuid_indirect);
    }

    @Test
    public void test4() throws Exception {
        TestTable ret = db.query(TestTable.class).whereEq("uuid", record.uuid).selectUnique();
        assertEquals(record.id, ret.id);
        assertEquals(record.string, ret.string);
        assertEquals(record.uuid, ret.uuid);
        assertEquals(record.uuid_indirect, ret.uuid_indirect);
    }

    @Test
    public void test5() throws Exception {
        TestTable ret = db.query(TestTable.class).whereEq("uuid_indirect", record.uuid_indirect.toString()).selectUnique();
        assertEquals(record.id, ret.id);
        assertEquals(record.string, ret.string);
        assertEquals(record.uuid, ret.uuid);
        assertEquals(record.uuid_indirect, ret.uuid_indirect);
    }

    @Test
    public void test6() throws Exception {
        assertEquals(1, db.query(TestTable.class).count());
    }
}