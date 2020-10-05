import {Equipment} from "./equipment";
import {AddressSingleTp} from "./address-single-tp";

export class EquipmentFull extends Equipment{

  type: string;
  vendor: string;
  model: string;
  series: string;

  address: AddressSingleTp;

  user_info: string;

}
