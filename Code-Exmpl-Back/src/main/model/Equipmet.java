package main.model;

import lombok.*;
import lombok.extern.java.Log;

import javax.validation.constraints.NotNull;
import java.util.HashMap;


@Data
@Log
@RequiredArgsConstructor
public class Equipment {
    Integer id;

    String title;

    @NotNull
    String type;

    @NotNull
    String vendor;

    @NotNull
    String model;

    @NotNull
    String series;

    @NotNull
    String inventory_num;

    HashMap<String, Object> attributes;

    String serial_num;

    Integer room;

    Integer id_asDetailIn;

    Integer id_tp;

    Integer id_pd;

    String user_info;

    Object address;



    public Equipment(Integer id, @NotNull String type, @NotNull String vendor, @NotNull String model,
                     @NotNull String series, @NotNull String inventory_num, HashMap<String, Object> attributes, String serial_num,
                     Integer room, Integer id_asDetailIn, Integer id_tp, Integer id_pd, String user_info, Object address) {
        this.id = id;
        this.type = type;
        this.vendor = vendor;
        this.model = model;
        this.series = series;
        this.inventory_num = inventory_num;
        this.attributes = attributes;
        this.serial_num = serial_num;
        this.room = room;
        this.id_asDetailIn = id_asDetailIn;
        this.id_tp = id_tp;
        this.id_pd = id_pd;
        this.user_info = user_info;
        this.address = address;
    }


    public Equipment(@NotNull String type, @NotNull String vendor, @NotNull String model, @NotNull String series,
                     String serial_num, @NotNull String inventory_num, HashMap<String, Object> attributes, Integer room, Integer id_asDetailIn,
                     Integer id_tp, Integer id_pd) {
        this.type = type;
        this.vendor = vendor;
        this.model = model;
        this.series = series;
        this.serial_num = serial_num;
        this.inventory_num = inventory_num;
        this.attributes = attributes;
        this.room = room;
        this.id_asDetailIn = id_asDetailIn;
        this.id_tp = id_tp;
        this.id_pd = id_pd;
    }

    public Equipment(Integer id, @NotNull String type, @NotNull String vendor, @NotNull String model,
                     @NotNull String series, @NotNull String inventory_num) {
        this.id = id;
        this.type = type;
        this.vendor = vendor;
        this.model = model;
        this.series = series;
        this.inventory_num = inventory_num;
    }

}

