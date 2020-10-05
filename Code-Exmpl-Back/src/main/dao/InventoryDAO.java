package main.dao;

import com.lis.qr_back.mapper.InventoryMapper;
import com.lis.qr_back.model.Inventory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryDAO implements InventoryMapper {
    private SqlSession sqlSession;
    private static final String INVENTORY_MAPPER = "com.lis.qr_back.main.mapper.InventoryMapper";

    public InventoryDAO(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public List<Inventory> getAll() {
        return sqlSession.selectList(INVENTORY_MAPPER + ".getAll");
    }

    @Override
    public List<Inventory> getByRoom(int room) {
        return sqlSession.selectList(INVENTORY_MAPPER + ".getByRoom", room);
    }

    @Override
    public List<Inventory> getByAddressId(int address_id) {
        return sqlSession.selectList(INVENTORY_MAPPER + ".getByAddressId", address_id);

    }

    @Override
    public int checkIfInventoryNumHasEquipment(String inventory_num) {
        return sqlSession.selectOne(INVENTORY_MAPPER + ".checkIfInventoryNumHasEquipment", inventory_num);
    }

    @Override
    public List<Inventory> getShortInventoryByRoom(int room) {
        return sqlSession.selectList(INVENTORY_MAPPER + ".getShortInventoryByRoom", room);
    }

    @Override
    public int insertInventory(Inventory inventory) {
        return sqlSession.insert(INVENTORY_MAPPER + ".insertInventory", inventory);
    }

    /**
     * Inserts List of Inventory, returns List of inventories that wasn't upload
     */
    public String[] insertListInventory(List<Inventory> inventories) {
        String[] missed_inventories = new String[inventories.size()];
        short counter = 0;
        int insert_result;

        for (Inventory inventory : inventories) {
            insert_result = insertInventory(inventory);

            if (insert_result == 0) {
                missed_inventories[counter] = inventory.getInventory_num();
                counter++;
            }
        }

        return missed_inventories;
    }

    @Override
    public int loadCsvToFinishInventory(String filename) {
        return sqlSession.insert(INVENTORY_MAPPER + ".loadCsvToFinishInventory", filename);
    }


}
