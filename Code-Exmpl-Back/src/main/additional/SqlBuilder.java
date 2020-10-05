package main.additional;

import mapper.AddressMapper;
import mapper.EquipmentMapper;
import mapper.PersonalDataMapper;
import qr_back.mapper.WorkplaceMapper;
import qr_back.model.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class SqlBuilder {

    boolean is_where_started = false;

    /**
     * get filter arrays from the passed map, parse them and build an sql request
     */



    public String equipmentFilter(FilterEquipment params) {
        StringBuilder query = new StringBuilder();

        String full_select = EquipmentMapper.FULL_SELECT;

        query.append(full_select);
        query.append("where ");


        /*tech platform*/
        //put it first
        query.append(whereQueryForTechPlatform(params.getAddress(), params.getRoom(), "E"));

        /*vendor*/
        query.append(whereQueryFromArray(params.getVendor(), "V.id", false));

        /*type*/
        query.append(whereQueryFromArray(params.getType(), "id_type", false));

        /*serial_num*/
        query.append(whereQueryFromArray(params.getSerial_num(), "serial_num", false));

        /*inventory_num*/
        query.append(whereQueryFromArray(params.getInventory_num(), "inventory_num", false));


        System.out.println(query.toString());

        return query.toString();
    }

    public String finishedInventoryFilter(Map params) {
        StringBuilder query = new StringBuilder();


        String[] addresses = (String[]) params.get("address");
        String[] rooms = (String[]) params.get("room");
        String[] dates = (String[]) params.get("date");
        String[] statuses = (String[]) params.get("status");
        String[] inventory_nums = (String[]) params.get("inventory_num");

        query.append("select finished_inventory.id, name, inventory_num, status.status, date, room, address_id " +
                "from finished_inventory join status on status.id=finished_inventory.status_id ");

        query.append("where ");

        /*address*/
        query.append(whereQueryFromArray(addresses, "address_id",false));

        /*room*/
        query.append(whereQueryFromArray(rooms, "room", false));

        /*date*/
        query.append(whereQueryFromArray(dates, "date", true));

        /*status*/
        query.append(whereQueryFromArray(statuses, "status_id",false));

        /*inventory_num*/
        query.append(whereQueryFromArray(inventory_nums, "inventory_num", false));

        query.append(" order by status");


        System.out.println(query.toString());

        return query.toString();

    }

    /**
     * append string like "table_name in ( a, b ,c d)"
     */

    public String whereQueryFromArray(Object[] array, @NotNull String table_name, boolean isStringParam) {
        StringBuilder query = new StringBuilder();

        if (array != null && array.length > 0) {

            /*check if there any where defined already*/
            if (is_where_started) {
                query.append(" AND ");
            }

            query.append(table_name + " in (");


            /*date should be quoted separately*/
            for (int i = 0; i < array.length; i++) {

                if (isStringParam) {
                    query.append("'");
                }

                query.append(array[i]);

                if (isStringParam) {
                    query.append("'");
                }

                if (i < array.length - 1) {
                    query.append(",");
                }
            }

            query.append(")");

            /*since now we have one condition, other will have to add "AND" to their conditions*/
            is_where_started = true;
        } else {
            query.append("");
        }

        return query.toString();
    }


    public String whereQueryForTechPlatform(Object[] address, Object[] rooms, String tableName) {

        StringBuilder query = new StringBuilder();
        boolean internal_is_where_started = false;

        if (address != null || rooms != null) {

            /*check if there any where defined already*/
            if (is_where_started) {
                query.append(" AND ");
            }

            query.append(tableName).append(".id_tp in (select id from technical_platform where ");

            /*add address params*/
            if (address != null && address.length != 0) {

                query.append(objectArrayToQueryFormat(address, "id_address"));

                //notify we must put the "and" word with the next param
                internal_is_where_started = true;
            }

            /*add room params*/
            if (rooms != null && rooms.length != 0) {

                if (internal_is_where_started) {
                    query.append(" AND ");
                }

                query.append(objectArrayToQueryFormat(rooms, "room"));

            }

            /*since now we have one condition, other will have to add "AND" to their conditions*/
            is_where_started = true;

            query.append(")");

        } else {
            query.append("");
        }

        return query.toString();
    }


    public String whereQueryForWorkplace(Object[] position, Object[] direction, Object[] department) {
        StringBuilder query = new StringBuilder();
        boolean internal_is_where_started = false;

        if (position != null || direction != null || department != null) {

            /*check if there any where defined already*/
            if (is_where_started) {
                query.append(" AND ");
            }

            query.append("personal_data.id_wp in (select workplace.id from workplace where ");

            /*add position params*/
            if (position != null && position.length != 0) {

                query.append(objectArrayToQueryFormat(position, "id_position"));

                //notify we must put the "and" word with the next param
                internal_is_where_started = true;
            }

            /*add room params*/

            if (department != null || direction != null) {
                boolean department_is_where_started = false;

                /*check if there any where defined already*/
                if (internal_is_where_started) {
                    query.append(" AND ");
                }

                query.append("id_department in (select department.id from department, direction where ");

                if (department != null && department.length != 0) {

                    query.append(objectArrayToQueryFormat(department, "department.id"));

                    department_is_where_started = true;
                }

                if (direction != null && direction.length != 0) {

                    if (department_is_where_started) {
                        query.append(" AND ");
                    }

                    query.append(objectArrayToQueryFormat(direction, "direction.id"));

                }

                query.append(")");

            }


            /*since now we have one condition, other will have to add "AND" to their conditions*/
            is_where_started = true;

            query.append(")");

        } else {
            query.append("");
        }

        return query.toString();
    }


    /*creates string from object array in format: ...tableName in (arr[0], arr[1])...*/

    private String objectArrayToQueryFormat(Object[] array, String tableName) {
        StringBuilder query = new StringBuilder();
        query.append(tableName).append(" in (");

        for (int i = 0; i < array.length; i++) {

            query.append(array[i]);

            if (i < array.length - 1) {
                query.append(",");
            }
        }
        query.append(")");

        return query.toString();
    }

}
