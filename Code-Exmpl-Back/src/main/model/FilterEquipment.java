package main.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterEquipment {
    @JsonProperty("address")
    String[] address;
    @JsonProperty("room")
    String[] room;
    @JsonProperty("type")
    String[] type;
    @JsonProperty("vendor")
    String[] vendor;
    @JsonProperty("serial_num")
    String[] serial_num;
    @JsonProperty("inventory_num")
    String[] inventory_num;


}
