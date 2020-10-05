import { Injectable } from '@angular/core';
import {StepperService} from "./stepper.service";

/**
 * Inherit from Stepper service. Initing tabs with class "step-tabs" for the element with id - element_id
 * and gives methods to switch between them.
 *
 * Connected with stepper component. Send data about current tabs, their amount. Allow to communicate parent component
 * with child stepper component, notifying when steps are finished.
 * */
@Injectable()
export class StepTabService extends StepperService {

  /** Inherit [parent]{@link StepperService} constructor*/
  constructor() {
    super();
  }

  /** Init tabs with id 'step-tab', send notification to subscriber*/
  initTabs(element_id): void {
    console.log("Init step-tabs for ", element_id);
    let element: HTMLElement = this.getElementById(element_id);
    this.tabItems = element.getElementsByClassName('step-tab');
    this.tabsAmount = this.tabItems.length;

    this.sendInitData({"currentTab": this.currentTab, "tabsAmount": this.tabsAmount});
  }
}
