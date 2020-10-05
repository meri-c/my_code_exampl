import {Component, Input, OnInit} from '@angular/core';
import {EquipmentFull} from "../../../main.model/equipment-full";


/** A card with equipment data representation*/
@Component({
  selector: 'app-equipment-card',
  templateUrl: './equipment-card.component.html',
  styleUrls: ['./equipment-card.component.css']
})
export class EquipmentCardComponent implements OnInit {
  /** Input checkbox near to card*/
  _checkEquipment: boolean = false;

  /** Passed equipment data to put into the card*/
  @Input()
  equipment: EquipmentFull;

  /**@ignore*/
  constructor() {
  }

  /**@ignore*/
  ngOnInit() {
  }

  /** Setter for [check_equipment]{@link EquipmentCardComponent._checkEquipment}*/
  @Input('checkEquipment')
  set checkEquipment(isCheck: any){
    this._checkEquipment = isCheck;
  }

  /** Get [check_equipment]{@link EquipmentCardComponent._checkEquipment}*/
  get checkEquipment(){
    return this._checkEquipment;
  }


}
