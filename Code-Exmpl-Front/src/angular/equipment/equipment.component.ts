import {Component, EventEmitter, Input, OnDestroy, OnInit, Optional, Output, SkipSelf} from '@angular/core';
import {EquipmentShort} from "../../../main.model/equipment-short";
import {HttpService} from "../../../service/utility/http.service";
import {FileQrCreationService} from "../../../service/component/file-qr-creation.service";
import {Subscription} from "rxjs";

/**
 * Component for equipment creation\selection.
 *
 *
 * Load data for selects types and vendors.
 *
 * Set preset inventory_num data if exist.
 *
 * On vendor selects, load models, and then series.
 *
 * Check if serial_num, inventory_num are unique.
 *
 * Combine and add main.additional param to equipment.
 *
 * When "submit" has gotten from parent, and equipment is valid - send equipment to parent.
 * */
@Component({
  selector: 'app-equipment',
  templateUrl: './equipment.component.html',
  styleUrls: ['./equipment.component.css']
})
export class EquipmentComponent implements OnInit, OnDestroy {
  //---------subscriptions-------------
  /**@ignore*/
  types$: Subscription = new Subscription();
  /**@ignore*/
  vendors$: Subscription = new Subscription();
  /**@ignore*/
  models$: Subscription = new Subscription();
  /**@ignore*/
  series$: Subscription = new Subscription();
  /**@ignore*/
  checkSerial$: Subscription = new Subscription();
  /**@ignore*/
  checkInventory$: Subscription = new Subscription();
  /**@ignore*/
  presetInv$: Subscription = new Subscription();
  /**@ignore*/
  equipmentSubmit$: Subscription = new Subscription();

  //------------------------------
  /** The main equipment object itself*/
  equipment: EquipmentShort;

  /** Permit to send equipment when it filled and full*/
  isEquipmentValid = false;

  //-----equipment params--------
  /** Map with main.additional params inside*/
  additionalParams: Map<string, string>;

  /** Additional param name */
  paramName: string;

  /** Additional param value*/
  paramValue: string;

  //-----pre set info--------

  /** Pre set inventory_num field information*/
  preset_inventory_num: string;

  //----selects-----
  /** Select map of types*/
  selectTypes: Map<any, string>;
  /** Select map of vendors*/
  selectVendors: Map<any, string>;
  /** Select map of models*/
  selectModels: Map<any, string>;
  /** Select map of series*/
  selectSeries: Map<any, string>;

  //----fiels check-----

  /** Check if serial_num is unique*/
  serial_num_isValid: boolean = true;

  /** Check if inventory_num is unique*/
  inventory_num_isValid: boolean = true;

  /** Serial_num duplicate value invalid message*/
  serial_num_invalid_msg = "Такий серійний номер вже існує";
  /** Inventory_num duplicate value invalid message*/
  inventory_num_invalid_msg = "Такий інвентарний номер вже існує";

  //---------------------
  /** Get submit conformation from parent*/
  @Input()
  equipmentSubmit: EventEmitter<boolean>;

  /** Emitter to notify parent, when [equipment]{@link EquipmentComponent.equipment} is valid*/
  @Output()
  onEquipmentValid = new EventEmitter<EquipmentShort>();

  /**Init [equipment]{@link EquipmentComponent.equipment} with an empty instance
   * @param {@link HttpService} http Service to communicate with server
   * @param {@link FileQrCreationService} sender Get instance of parent sender service. To get preset data if exist.
   * */
  constructor(private http: HttpService, @SkipSelf() @Optional() private sender: FileQrCreationService) {
    this.equipment = new EquipmentShort();
  }

  /** Get types and vendors maps for selects.
   *
   * If [sender]{@link FileQrCreationService} is not null,
   * get the preset inventory_num and add it to the [equipment]{@link EquipmentComponent.equipment} and html page.
   *
   * If parent send submit, check [equipment]{@link EquipmentComponent.equipment}  and send ir to parent
   * */
  ngOnInit() {
    /*get init info*/
    console.log("Load types");
    this.types$ = this.http.getIdValueMap(HttpService.urlPatternEquipment + 'type', 'type').subscribe(data => this.selectTypes = data);
    this.vendors$ = this.http.getIdValueMap(HttpService.urlPatternEquipment + 'vendor', 'vendor').subscribe(data => this.selectVendors = data);

    /*get preset info*/
    if (this.sender !== null) {
      this.presetInv$ = this.sender.getPreSetInventoryNum().subscribe(inv_num => {
        console.log("got preset inv_num", this.preset_inventory_num);
        this.preset_inventory_num = inv_num;
        this.addInventoryNum(this.preset_inventory_num);
      });
    }

    /*check and return equipment to the parent component*/
    if (this.equipmentSubmit) {
      this.equipmentSubmit$ = this.equipmentSubmit.subscribe(data => {
        console.log("Equipment: ", data);
        if (this.checkEquipmentValid()) {
          this.returnToParentValidEquipment();
        } else {
          console.log("Equipment: not enough data");
        }
      });

    }
  }

  /** Add selected type to [equipment]{@link EquipmentComponent.equipment} */
  addType(event: number) {
    this.equipment.id_type = +event;
  }

  //--------Model-seria--------
  /** When a vendor selected, load all models of this vendor*/
  loadModel(vendor: any) {
    //clean series map and equipment series
    this.selectSeries = new Map<any, string>();
    this.equipment.id_model_series = undefined;

    //load models
    this.models$ = this.http.getIdValueMap(HttpService.urlPatternEquipment + 'main.model/' + vendor, 'model')
      .subscribe(data => this.selectModels = data);
  }

  /** When a main.model selected, load all series of this main.model*/
  loadSeries(event: any) {
    this.series$ = this.http.getIdValueMap(HttpService.urlPatternEquipment + 'series/' + event, 'series')
      .subscribe(data => this.selectSeries = data);

  }

  /** Add selected series to [equipment]{@link EquipmentComponent.equipment} */
  addSeries(event: number) {
    this.equipment.id_model_series = +event;
  }


  //-----------------------------------------//

  /** When inputted, check if serial_num already exists, if no, set valid and add serial_num */
  checkSerialNum(event: any) {
    console.log("serial num " + event);
    //check if exists at server
    this.checkSerial$ = this.http.checkWithParams(HttpService.urlPatternEquipment + 'check_serial', 'serial_num', event)
      .subscribe(check_result => {
        this.serial_num_isValid = check_result;

        if (check_result == true) {
          this.addSerialNum(event);
        }
      });
  }

  /** Add serial_num to [equipment]{@link EquipmentComponent.equipment} */
  addSerialNum(serial_num: any) {
    console.log("Serial num " + serial_num + " was added.");
    this.equipment.serial_num = serial_num;
  }

  /** When inputted, check if inventory_num already exists, if no, set valid and add inventory_num */
  checkInventoryNum(event: any) {
    console.log("inventory num " + event);
    //check if exists at server
    this.checkInventory$ = this.http.checkWithParams(HttpService.urlPatternEquipment + 'check_inventory', 'inventory_num', event)
      .subscribe(check_result => {
        this.inventory_num_isValid = check_result;
        if (check_result == true) {
          this.addInventoryNum(event);
        }
      });
  }

  /** Add inventory_num to [equipment]{@link EquipmentComponent.equipment} */
  addInventoryNum(inventory_num: any) {
    console.log("Inventory num " + inventory_num + " was added.");
    this.equipment.inventory_num = inventory_num;
  }


  //------for one aditional param todo make multiple main.additional params

  /** Add param name */
  addName(event: any) {
    this.paramName = event;
    //clean up old attr
  }

  /** Add param value, When both param name, and param value are valid, add them to main.additional equipment params*/
  addValue(event: any) {
    this.paramValue = event;

    if (this.paramName !== undefined && this.paramValue !== undefined) {
      this.addAdditionalParams();
    }
  }

  /** Create attribute from param name and value, add to [equipment]{@link EquipmentComponent.equipment} */
  addAdditionalParams() {
    console.log("Attribute " + this.paramName, " : ", this.paramValue, " was added");
    this.additionalParams = new Map<string, string>().set(this.paramName, this.paramValue);
    this.equipment.attributes = this.additionalParams;
  }

  //-------------------------

  /** Return to parent valid and filled [equipment]{@link EquipmentComponent.equipment} */
  returnToParentValidEquipment() {
    this.onEquipmentValid.emit(this.equipment);
  }

  /** Check all important fields in [equipment]{@link EquipmentComponent.equipment}
   * Set valid true
   * */
  checkEquipmentValid(): boolean {
    if (this.equipment.id_type !== undefined && this.equipment.id_model_series !== undefined
      && this.equipment.serial_num !== undefined && this.equipment.inventory_num !== undefined) {
      this.isEquipmentValid = true;
      return true;
    } else {
      return false;
    }
  }

  /** Close async connections*/
  closeAllAsyncSubscriptions() {
    this.types$.unsubscribe();
    this.vendors$.unsubscribe();
    this.models$.unsubscribe();
    this.series$.unsubscribe();
    this.checkSerial$.unsubscribe();
    this.checkInventory$.unsubscribe();
    this.presetInv$.unsubscribe();
    this.equipmentSubmit$.unsubscribe();
  }

  /**@ignore*/
  ngOnDestroy(): void {
    this.closeAllAsyncSubscriptions();
  }


}
