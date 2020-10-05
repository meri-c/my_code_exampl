import {Injectable} from '@angular/core';
import {Observable, Subject, Subscription} from "rxjs";
import {FinishedInventory} from "../../main.model/finished-inventory";
import {HttpService} from "./http.service";
import {EquipmentFull} from "../../main.model/equipment-full";
import {Workplace} from "../../main.model/workplace";
import {User} from "../../main.model/user";
import {Address} from "../../main.model/address";
import {AddressMultiTp} from "../../main.model/address-multi-tp";
import {filter} from "rxjs/operators";


/** Service for {@link FilterComponent}.
 * Init filter filler map.
 * Send filter result to server.*/
@Injectable()
export class FilterService {

  /** Temp filter filler map storage*/
  _filterFiller: Map<string, any>;

  /** Subject to pass init info between service and {@link FilterComponent}*/
  filterFiller: Subject<Map<string, any>> = new Subject<Map<string, any>>();

  filterSearch: Subject<any> = new Subject<any>();
  private http$: Subscription = new Subscription();

  /** @ignore*/
  constructor(private http: HttpService) {
  }

  /** Add filter search result to the sender subject
   * @param filterSearchResult Search result
   * */
  sendFilterSearchResult(filterSearchResult: object[]) {
    this.filterSearch.next(filterSearchResult);
  }

  /** Get filter search result
   * @returns Observable<object[]>
   * */
  getFilterSearchResult(): Observable<object[]> {
    return this.filterSearch.asObservable();
  }




  //-------------------Init subjects---------------------

  /** Add inited filter filler map to the sender subject*/
  sendFilterFiller(filterFillerMap: Map<string, any>) {
    this.filterFiller.next(filterFillerMap);
  }

  /** Get inited filter filler map*/
  getFilterFiller(): Observable<Map<string, any>> {
    return this.filterFiller.asObservable();
  }

//----------------------Init methods----------------------

  /**
   *  Choose filter filler method based on passed type
   * @param {@link FilterName} type Filter type name
   * */
  initFilterFiller(type: FilterName){
    switch (type) {
      case FilterName.Equipment: {
        this.initFilterFillerEquipmentHandbook();
        break;
      }
      case FilterName.FinishedInventory: {
        this.initFilterFillerFinishedInventory();
        break;
      }
      case FilterName.Workplace: {
        this.initFilterFillerWorkplaceHandbook();
        break;
      }
      case FilterName.User: {
        this.initFilterFillerUserHandbook();
        break;
      }
      case FilterName.Address: {
        this.initFilterFillerAddressHandbook();
        break;
      }
  }
  }

  /**
   * Create filter_filler with: address, room, date, status, inventory_num params.
   * For {@link InventoryComponent}  filter type*/
  initFilterFillerFinishedInventory() {
    this._filterFiller = new Map();

    this._filterFiller.set('address', {name: 'Адреса', data: []});
    this._filterFiller.set('room', {name: 'Кімната', data: []});
    this._filterFiller.set('date', {name: 'Дата', data: []});
    this._filterFiller.set('status', {name: 'Статус', data: []});
    this._filterFiller.set('inventory_num', {name: 'Інвентарний номер', data: []});

    this.sendFilterFiller(this._filterFiller);
  }

  /**
   * Create filter_filler with: address, room, type, vendor,serial_num, inventory_num params.
   * For {@link HEquipmentComponent}  filter type*/
  initFilterFillerEquipmentHandbook() {
    this._filterFiller = new Map();

    this._filterFiller.set('address', {name: 'Адреса', data: []});
    this._filterFiller.set('room', {name: 'Кімната', data: []});
    this._filterFiller.set('type', {name: 'Тип обладнання', data: []});
    this._filterFiller.set('vendor', {name: 'Виробник', data: []});
    this._filterFiller.set('serial_num', {name: 'Серійний номер', data: []});
    this._filterFiller.set('inventory_num', {name: 'Інвентарний номер', data: []});

    this.sendFilterFiller(this._filterFiller);
  }


  /**
   * Create filter_filler with: position, direction, department params.
   * For {@link HWorkplaceComponent}  filter type*/
  initFilterFillerWorkplaceHandbook() {
    this._filterFiller = new Map();

    this._filterFiller.set('position', {name: 'Посада', data: []});
    this._filterFiller.set('direction', {name: 'Управління', data: []});
    this._filterFiller.set('department', {name: 'Відділ', data: []});

    this.sendFilterFiller(this._filterFiller);
  }

  /**
   * Create filter_filler with: address, room, position, direction, department, inn params.
   * For {@link HUserComponent}  filter type*/
  initFilterFillerUserHandbook() {
    this._filterFiller = new Map();

    this._filterFiller.set('address', {name: 'Адреса', data: []});
    this._filterFiller.set('room', {name: 'Кімната', data: []});
    this._filterFiller.set('position', {name: 'Посада', data: []});
    this._filterFiller.set('direction', {name: 'Управління', data: []});
    this._filterFiller.set('department', {name: 'Відділ', data: []});
    this._filterFiller.set('inn', {name: 'ІНН', data: []});

    this.sendFilterFiller(this._filterFiller);
  }


  /**
   * Create filter_filler with: city, street params.
   * For {@link HAddressComponent} filter type*/
  initFilterFillerAddressHandbook() {
    this._filterFiller = new Map();

    this._filterFiller.set('city', {name: 'Місто', data: []});
    this._filterFiller.set('street', {name: 'Вулиця', data: []});

    this.sendFilterFiller(this._filterFiller);

  }


  //--------Find by filter data----------


  /** Sends array from filter to server, on success notifies {@link FilterComponent} and return a result
   *  with {@link FinishedInventory}[] to it
   *
   * @param {object} filterSearchArray Array of maps from {@link FilterComponent}
   * with params what kind of filter inventory object does user want to get.
   * */
  findFilterInventory(filterSearchArray: object){
    this.http$ = this.http.findInventoryByFilterParams(filterSearchArray)
      .subscribe((data: FinishedInventory[]) => {
          this.sendFilterSearchResult(data);
        },
        error => console.log('Error occurred ' + JSON.stringify(error.json())));
  }


  /** Sends array from filter to server, on success notifies {@link FilterComponent} and return a result
   * with {@link EquipmentFull}[] to it
   *
   * @param {object} filterSearchArray Array of maps from {@link FilterComponent}
   * with params what kind of filter inventory object does user want to get.
   * */
  findEquipment(filterSearchArray: object){
    this.http$ = this.http.findEquipmentByFilterParams(filterSearchArray)
      .subscribe((data: EquipmentFull[]) => {
          this.sendFilterSearchResult(data);
        },
        error => console.log('Error occurred ' + JSON.stringify(error.json())));
  }



  /** Sends array from filter to server, on success notifies {@link FilterComponent} and return a result
   *  with {@link Workplace}[] to it
   *
   * @param {object} filterSearchArray Array of maps from {@link FilterComponent}
   * with params what kind of filter inventory object does user want to get.
   * */
  findWorkplace(filterSearchArray: object){
    this.http$ = this.http.findWorkplaceByFilterParams(filterSearchArray)
      .subscribe((data: Workplace[]) => {
          this.sendFilterSearchResult(data);
        },
        error => console.log('Error occurred ' + JSON.stringify(error.json())));
  }



  /** Sends array from filter to server, on success notifies {@link FilterComponent} and return a result
   *  with {@link User}[] to it
   *
   * @param {object} filterSearchArray Array of maps from {@link FilterComponent}
   * with params what kind of filter inventory object does user want to get.
   * */
  findUser(filterSearchArray: object){
    this.http$ = this.http.findUserByFilterParams(filterSearchArray)
      .subscribe((data: User[]) => {
          this.sendFilterSearchResult(data);
        },
        error => console.log('Error occurred ' + JSON.stringify(error.json())));
  }



  /** Sends array from filter to server, on success notifies {@link FilterComponent} and return a result
   *  with {@link AddressMultiTp}[] to it
   *
   * @param {object} filterSearchArray Array of maps from {@link FilterComponent}
   * with params what kind of filter inventory object does user want to get.
   * */
  findAddress(filterSearchArray: object){
    this.http$ = this.http.findAddressByFilterParams(filterSearchArray)
      .subscribe((data: AddressMultiTp[]) => {
          this.sendFilterSearchResult(data);
        },
        error => console.log('Error occurred ' + JSON.stringify(error.json())));
  }


/** Searches objects at server's depends on filter params and filter type
 *
 * @param {@link FilterName} filter_type Set the type of the filter
 * @param {@link object} filterSearchArray Array of maps with search params
 * */
  findByFilter(filter_type: FilterName, filterSearchArray: object){
    switch (filter_type) {

      case FilterName.FinishedInventory: {
        this.findFilterInventory(filterSearchArray);
        break;
      }

      case FilterName.Equipment: {
        console.log("Sending " + filterSearchArray);
        this.findEquipment(filterSearchArray);
        break;
      }

      case FilterName.Workplace: {
        console.log("Sending " + filterSearchArray);
        this.findWorkplace(filterSearchArray);
        break;
      }
      case FilterName.User: {
        this.findUser(filterSearchArray);
        break;
      }
      case FilterName.Address: {
        this.findAddress(filterSearchArray);
        break;
      }


    }
  }


  //--------------Additional-------------


  /** Check if the selected filter param already in use*/
  static isItemUnique(selectedFilterId: string, filterSearchArray: object, selectedId: any) {
    console.log('is item unique ');

    const filterArray = filterSearchArray[selectedFilterId];
    console.log(typeof (filterArray));

    if (filterArray !== undefined) {
      console.log(filterArray.includes(selectedId));
      return !filterArray.includes(selectedId);
    } else {
      console.log('nope');
      return true;
    }
  }

}
