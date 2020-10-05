package main.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class QrImage {
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private byte [] data;

    private int equipment_id;

    public QrImage(String name, byte[] data, int equipment_id) {
        this.name = name;
        this.data = data;
        this.equipment_id = equipment_id;
    }
}
