export class Equipment {
  id: number;
  title: string;
  inventory_num: string;
  serial_num: string;
  id_asDetailIn: number;
  room: number;

  attributes: Map<string, string>;

  //personal data
  id_pd: number;
  //tech platform
  id_tp: number;
}
