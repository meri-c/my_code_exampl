import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable, of} from 'rxjs/index';
import {FinishedInventory} from '../../main.model/finished-inventory';
import {Address} from '../../main.model/address';
import {ConverterService} from './converter.service';
import {catchError, map} from 'rxjs/internal/operators';
import {AddressSingleTp} from "../../main.model/address-single-tp";
import {AddressMultiTp} from "../../main.model/address-multi-tp";

/** Custom http service to get and transform data from server.
 * Works along with {@link ConverterService}
 * */
@Injectable({
  providedIn: 'root'
})
export class HttpService {
  //--------Static urls for different requests--------
  /**@ignore*/
  static readonly urlPatternFinishedInventory = 'http://localhost:8090/api/finished_inventory/';
  /**@ignore*/
  static readonly urlPatternUsersIdName = 'http://localhost:8090/api/personal_data/id_name';
  /**@ignore*/
  static readonly urlPatternUsersPersonalData = 'http://localhost:8090/api/personal_data/';
  /**@ignore*/
  static readonly urlPatternUser = 'http://localhost:8090/api/users/';
  /**@ignore*/
  static readonly urlPatternEquipment = 'http://localhost:8090/api/equipment/';
  /**@ignore*/
  static readonly urlPatternWorkplace = 'http://localhost:8090/api/workplace/';
  /**@ignore*/
  static readonly urlPatternAddress = 'http://localhost:8090/api/addresses/';
  /**@ignore*/
  static readonly urlPatternRoomsByAddressId = 'http://localhost:8090/api/rooms/by_address/';
  /**@ignore*/
  static readonly urlPatternRooms = 'http://localhost:8090/api/rooms/';
  /**@ignore*/
  static readonly urlPatternQr = 'http://localhost:8090/api/qr/';
  /**@ignore*/
  static readonly urlInventories = 'http://localhost:8090/api/equipment/inventory_num';
  /**@ignore*/
  static readonly urlQrImgByInventoryNum = 'http://localhost:8090/api/qr/inventory/';
  /**@ignore*/
  static readonly urlPatternFileUpload = 'http://localhost:8090/api/uploadFile/';
  /**@ignore*/
  static readonly defaultRoom = '/8888';


  /**
   * @param {@link HttpClient} http Basic class to send requests
   * @param {@link ConverterService} converter Converter for incoming data
   * */
  constructor(private http: HttpClient, private converter: ConverterService) {
  }

  /** Send get request to server*/
  getData(url: string) {
    return this.http.get(url);
  }


  /** Get finished inventory by given url
   *
   * @return
   * An {@link Observable} with modified data obj [] to FinishedInventory obj
   * */
  getFinishedInvetory(url: string) {

    // kostyl
    const id = 'id';
    const name = 'name';
    const inventory_num = 'inventory_num';
    const status = 'status';
    const date = 'date';
    const room = 'room';
    const address_id = 'address_id';

    return this.http.get(url).pipe(map((data: object []) => {
      return data.map(inventory => {
        return {
          id: inventory[id], name: inventory[name], inventoryNum: inventory[inventory_num],
          status: inventory[status], date: inventory[date], room: inventory[room],
          addressId: inventory[address_id]
        };
      });
    }));
  }


  //------filter--------
  /** Get finished inventory by given url and filter params
   *
   * @param {object} filterSearchArray Object of params for inventory filtering
   *
   * @return
   * An {@link Observable} with modified data obj [] to FinishedInventory obj
   * */
  findInventoryByFilterParams(filterSearchArray: object): Observable<FinishedInventory[]> {

    return this.postData(HttpService.urlPatternFinishedInventory, filterSearchArray).pipe(
      map((data: any []) => {
          return data.map(inventory => {
            /*convert to Finished invetory*/
            return {
              id: inventory.id, name: inventory.name, inventoryNum: inventory.inventory_num,
              status: inventory.status, date: inventory.date, room: inventory.room, addressId: inventory.address_id
            };
          });
        }
      ));
  }

  /** Post data to server with filter
   *
   * @param {object} filterSearchArray Object of params for equipment filtering
   *
   * @return
   * Filtered {@link Equipment} list
   * */
  findEquipmentByFilterParams(filterSearchArray: object) {

    return this.postData(HttpService.urlPatternEquipment + "filter", filterSearchArray);
  }


  /** Post data to server with filter
   *
   * @param {object} filterSearchArray Object of params for workplace filtering
   *
   * @return
   * Filtered {@link Workplace} list
   * */
  findWorkplaceByFilterParams(filterSearchArray: object) {
    return this.postData(HttpService.urlPatternWorkplace + "filter", filterSearchArray);
  }


  /** Post data to server with filter
   *
   * @param {object} filterSearchArray Object of params for user filtering
   *
   * @return
   * Filtered {@link User} list
   * */
  findUserByFilterParams(filterSearchArray: object) {
    return this.postData(HttpService.urlPatternUsersPersonalData + "filter", filterSearchArray);
  }


  /** Post data to server with filter
   *
   * @param {object} filterSearchArray Object of params for address filtering
   *
   * @return
   * Filtered {@link AddressMultiTp} list
   * */
  findAddressByFilterParams(filterSearchArray: object) {
    return this.postData(HttpService.urlPatternAddress + "filter", filterSearchArray);
  }


  // ----data for select----

  /** Get list of addresses, convert to {@link AddressSingleTp}
   *
   * @return
   * {@link Observable} with the list of {@link AddressSingleTp}
   * */
  getAddresses(url: string): Observable<AddressSingleTp[]> {
    // @ts-ignore
    return this.getData(url).pipe(map(listOfAddresses => listOfAddresses.map(address => new AddressSingleTp(address))));
  }


  //----with converter------

  /**
   * Get data from server and transform to idValueMap
   *
   * Id value means that a response will have max two field in obj, like "id" and "type", etc
   *
   * @param {string} url Url for request
   * @param {string} itemId Name of the item the response is dedicated to. Is used in switch, and as a map value creation.
   * */
  getIdValueMap(url: string, itemId: string): Observable<Map<any, string>> {
    return this.getData(url).pipe(map((data: object[]) => {

        switch (itemId) {

          case 'user': {
            console.log('Users');
            return this.converter.convertIdValuesObject(data, 'id', 'name');
          }
          default: {
            console.log('Default', itemId);
            return this.converter.convertIdValuesObject(data, 'id', itemId);
          }
        }

      })
    );
  }

  /**
   * Get data from server and transform to idValueMap
   *
   * Two value means that a response will have one value as a key and a value in the map.
   *
   * @param {string} url Url for request
   * */
  //two value means that a response will have max two field in obj, like "id" and "type. so on
  getValueValueMap(url: string): Observable<Map<any, string>> {
    return this.getData(url).pipe(map((data: object[]) => {
        return this.converter.convertValueValueObject(data);
      })
    );
  }


  /** Get addresses from server and make it suitable for select map*/
  getMapAddresses(url: string): Observable<Map<any, string>> {
    return this.getData(url).pipe(map((data: AddressSingleTp[]) => {

        console.log('Address');
        return this.converter.convertAddress(data);

      })
    );
  }


  /** Get rooms from server and make it suitable for select map*/
  getMapRooms(url: string): Observable<Map<any, string>> {
    return this.getData(url).pipe(map((data: object[]) => {
        console.log('Room');
        return this.converter.convertRoom(data);
      }
    ));
  }

  /** Get list of dates from server and make it suitable for select map*/
  getDates(url: string, itemId: string): Observable<Map<any, string>> {
    return this.getData(url + itemId).pipe(map((data: object[]) => {
        console.log('Dates');
        return this.converter.convertDate(data);
      }
    ));
  }



  /** Plain get method. Just a cover for {@link HttpService.getData}*/
  getInventoryArrSimple(url: string): Observable<any> {
    return this.getData(url);
  }

  /** Get tech_platform from server and make it suitable for select map*/
  getTechPlatforms(url: string): Observable<Map<any, string>> {
    return this.getData(url).pipe(map((data: object[]) => {
        console.log('Tech platforms');
        return this.converter.convertTech_platform(data);
      }
    ));
  }


  //--------get with params
/** Get request with param in line
 * @param url Url for request
 * @param paramName Set param name to {@link HttpParams}. By this name you can get this param at server's side
 * @param paramValue Set param value to {@link HttpParams}
 * */
  getWithParams(url: string, paramName: string, paramValue) {
    let http_params = new HttpParams().set(paramName, paramValue);
    return this.http.get(url, {params: http_params});
  }

  /**
   * Return true of false depends on whether subj exist or not. Make sure you have a necessary api for your needs.
   * */
  checkWithParams(url: string, paramName: string, paramValue): Observable<boolean> {
    return this.getWithParams(url, paramName, paramValue).pipe(
      map(data => {
        return true;
      }),
      catchError(err => {
        return of(false);
      }));

  }

  // --------QR-------

  /** Create equipment method, just a cover for {@link HttpService.postData}*/
  createEquipmentReturnId(url: string, body: object): Observable<any> {
    return this.postData(url, body);
  }

  /** Combine url and equipment_id, return the following qr_code for equipment*/
  getQrCodeByEquipmentId(equipment_id: number): Observable<any> {
    return this.http.get(HttpService.urlPatternQr + "equipment/" + equipment_id);
  }

  /** Send inventory_nums list, get the location of formed word_doc with qr-codes*/
  sendInventoriesGetQrDoc(inventory_nums: string[]) {
    console.log("Sending inventories..");
    return this.http.post(HttpService.urlPatternQr + "form_doc", inventory_nums, {observe: 'response'});
  }


  //------------file

  /** Send file with inventory data to server*/
  sendInventoryFile(url: string, file: File) {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(url, formData);
  }

  /** Check if inventory_nums has equipment and qr_code at server (send file with nums)*/
  sendFileCheckInventoryHasQr(url: string, file: File) {
    return this.sendInventoryFile(url, file);
  }

  /** Get formed word doc with qr codes by the file location param*/
  getQrFile(file_location: string) {
    const headers: HttpHeaders = new HttpHeaders({'Location': file_location});

    console.log("H", headers.get("Location"));

    return this.http.get(HttpService.urlPatternQr + "qr_file", {headers: headers, responseType: 'blob'});
  }

  //------
  /** Plain post request with body param object*/
  postData(url: string, body: object) {
    return this.http.post(url, body);
  }


}
