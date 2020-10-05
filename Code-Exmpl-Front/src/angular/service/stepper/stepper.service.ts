 import {Injectable} from '@angular/core';
import {Observable, Subject} from "rxjs";


/**
 * Common abstract class for switch tabs which are inited in the initTabs() method;
 *
 *
 * Children:
 * StepTab, BigTab.
 *
 * StepTab - usual stepper. Include all the elements with class 'tab'
 * BigTab - stepper for sub-pages. All elements with class 'big-tab'
 *
 *
 *  E.G. big-tab(1) -> Some info, big-tab(2) -> Stepper with three pages ( tab(1) -> A, tab(2) -> B, tab(3) -> C), big-tab(3) -> Result from stepper.
 *
 * */
@Injectable()
export abstract class StepperService {
  /**
   * Subjects - for service and component communications.
   * U can put info, you can take it back in some other place
   * */
  initSubject = new Subject<any>();
  currentTabSubject = new Subject<any>();
  booleanSubject = new Subject<boolean>();

  /**
   *  Tab - connected tabs with common goal, including steps and finish button
   */
  tabItems: HTMLCollection;

  /** Amount of */
  tabsAmount;

  /** Current visible tab*/
  currentTab = 0;

  /** @ignore*/
  constructor() {
  }

//-------------control notifier---------

  /**
   * Communication between stepper service and stepper component.
   *  Send current tab.
   * */
  sendCurrentTabData(data) {
    this.currentTabSubject.next(data);
  }

  /** Get current visible tab*/
  getCurrentTabData(): Observable<any> {
    return this.currentTabSubject.asObservable();
  }

  /**
   * Communication between stepper service and stepper component.
   *  Send init tabs data.
   * */
  sendInitData(data) {
    this.initSubject.next(data);
  }

  /** Get init data for stepper*/
  getInitData(): Observable<any> {
    return this.initSubject.asObservable();
  }


  /** Send finish notification*/
  finish() {
    this.booleanSubject.next(true);
  }

  /** Get finish notification*/
  notifyFinished(): Observable<boolean> {
    return this.booleanSubject.asObservable();
  }

//-------------init---------
  /**
   * Search all the tabs: step-tab or big-tab.
   */
  abstract initTabs(element_id): void;


  /** Returns html element by its id*/
  getElementById(element_id: string): HTMLElement{
    return document.getElementById(element_id) as HTMLElement;
  }


  //-------------steps part---------
/**
 * Change tab to the next or previous, depends on passed value: 1 or -1.
 * On the first/last tabs prev/finish buttons are disabled.
 */
  nextPrev(n: number) {

    console.log('NextPrev');
    console.log('all tabs ' + this.tabsAmount);
    console.log('current: ' + this.currentTab);

    //hide current tab and show a new one
    this.hideShowTab(this.currentTab, true, this.tabItems);
    this.currentTab += n;
    console.log('next current: ' + this.currentTab);
    this.hideShowTab(this.currentTab, false, this.tabItems);

    this.sendCurrentTabData(this.currentTab);

  }


  /**
   * Hide or show tab with the following number from the tabItems array
   * */
  hideShowTab(tabNumber: number, isToHide: boolean, tabItems: HTMLCollection) {
    let tab = tabItems.item(tabNumber) as HTMLElement;

    if (isToHide) {
      tab.style.display = 'none';

    } else {
      tab.style.display = 'block';
    }
  }

  /**
   * Change stepper tabs like when 'next\previous' is pressed.
   *
   * Use to control stepper outside the component.
   * */
  switchToTheSpecifiedTab(tab_number:number){

    if(tab_number <= this.tabsAmount){

      if(tab_number != this.currentTab){
        //hide current tab and show a new one
        this.hideShowTab(this.currentTab, true, this.tabItems);
        this.currentTab = tab_number;
        console.log('next current: ' + this.currentTab);
        this.hideShowTab(this.currentTab, false, this.tabItems);

        /*for the navigation mark - 1/n pages*/
        this.sendCurrentTabData(this.currentTab);

      }

    }else{
      console.log("Cant switch to the tab with the number "+tab_number+". Tabs amount is "+ this.tabsAmount);
    }

  }


}
