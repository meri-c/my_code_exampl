import { Injectable } from '@angular/core';
import {StepperService} from "./stepper.service";
/**
* Inherit from Stepper service. Initing tabs with class "big-tabs" for the element with id - element_id
 * and gives methods to switch between them.
 *
 * !!! all element except for the first one which are marked as 'big-tab', define style="display: none"
 * */
@Injectable()
export class BigTabService extends StepperService{

  /** Inherit [parent]{@link StepperService} constructor*/
  constructor() {
    super();
  }

  /** Init tabs with id 'big-tab'*/
  initTabs(element_id) {
    let element: HTMLElement = this.getElementById(element_id);
    this.tabItems = element.getElementsByClassName('big-tab');
    this.tabsAmount = this.tabItems.length;
  }

  /** Switch to forward big tab*/
  forward(){
    this.nextPrev(1);
  }

  /** Switch to prev big tab*/
  back(){
    this.nextPrev(-1);
  }


}
