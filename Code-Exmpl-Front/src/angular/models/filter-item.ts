export class FilterItem {
  id: any;
  filterId: string;
  name: string;


  constructor(id: any, filterId: string, name: string) {
    this.id = id;
    this.filterId = filterId;
    this.name = name;
  }
}
