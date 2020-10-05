import {Component, EventEmitter, Input, OnInit, Output, SkipSelf} from '@angular/core';
import {StepperService} from "../../../service/stepper/stepper.service";
import {Subscription} from "rxjs";
import {StepTabService} from "../../../service/stepper/step-tab.service";


/**
 * Custom stepper at the bottom of your pages.
 *
 * Works along with StepTabService class.
 *
 * */
@Component({
  selector: 'app-stepper',
  templateUrl: './stepper.component.html',
  styleUrls: ['./stepper.component.css'],
})
export class StepperComponent implements OnInit {


  /** Amount of tabs with name "step-tab"*/
  tabsAmount: number;

  /** Current visible tab*/
  currentTab: number;

  //------subscription------------

  /**
   * Subscription - to get init information for stepper (tabs amount, current tab)
   * */
  private init_data$: Subscription =new Subscription();

    /** Get current tab if changed*/
  private currentTab$: Subscription = new Subscription();

  //-----------------------------


  /**
   * Subscribe to init info (how many tabs are available, current tab) from stepper service
   *
   * @param {@link StepTabService} stepTabService Service for stepper, takes parent instance of service
   * */
  constructor(@SkipSelf() private stepTabService: StepTabService) {
   this.init_data$  = this.stepTabService.getInitData().subscribe(initData => {
      console.log("Init data came");
      this.tabsAmount = initData["tabsAmount"];
      this.currentTab = initData["currentTab"];
    });

    this.currentTab$ = this.stepTabService.getCurrentTabData().subscribe(currentTab => {
      console.log("Current tab subscription: " + currentTab);
      this.currentTab = currentTab;
    })
  }

  /** @ignore*/
  ngOnInit() {
  }

  /**
   * Send notification to change tab
   * */
  change(value: number) {
    this.stepTabService.nextPrev(value);
  }

  /**
   * Send notification to finish steps
   * */
  finish() {
    this.stepTabService.finish();
  }

  /** Close async subscriptions*/
  closeAllAsyncSubscriptions() {
    this.init_data$.unsubscribe();
    this.currentTab$.unsubscribe();
  }

  /**@ignore*/
  ngOnDestroy(): void {
    this.closeAllAsyncSubscriptions();
  }


}
