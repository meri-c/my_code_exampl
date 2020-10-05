import {Component, OnInit, ViewChild} from '@angular/core';
import {HttpService} from "../../../service/utility/http.service";
import {QrImage} from "../../../main.model/qr-image";
import {AlertComponent} from "../../utility/alert/alert.component";
import {BigTabService} from "../../../service/stepper/big-tab.service";
import {Subscription} from "rxjs";


/** Load qr code from selected inventory number
 *
 * Work logic:
 *
 *  1. Load inventory addresses.
 *
 *  2. On select and btn find pressed send inventory num to server.
 *
 *  3. Look at the equipment qr-code if there any with such inventory_num, puts it to response.
 *
 *  4. On successful search, change tab and show qr picture with download btn.
 *
 * */
@Component({
  selector: 'app-create-qr-from-exist',
  templateUrl: './create-qr-from-exist.component.html',
  styleUrls: ['./create-qr-from-exist.component.css'],
  providers: [BigTabService]
})
export class CreateQrFromExistComponent implements OnInit {
  private inventory_nums$: Subscription = new Subscription();
  private qr_img$: Subscription = new Subscription();

  inventoryNumSelect = [];
  image = '';

  qrImage: QrImage;
  @ViewChild(AlertComponent)
  alert: AlertComponent;


  constructor(private http: HttpService, private bigTabService: BigTabService) { }

  ngOnInit() {
    // load data to inventory number select
    this.inventory_nums$ = this.http.getInventoryArrSimple(HttpService.urlInventories).subscribe((data: string[]) => this.inventoryNumSelect = data);
    this.bigTabService.initTabs("create-qr-from-exist");
  }


  //--- get code from db---

  loadQrCode(inventoryNum: string) {
    // hide alert if exist
    this.alert.hide();
    console.log('Load qr-code with following inventory number ' + inventoryNum);

    this.clearOldImgData();

    // call api
    this.qr_img$ = this.http.getData(HttpService.urlQrImgByInventoryNum + inventoryNum).subscribe((qrImage: QrImage) => {
        this.image = 'data:image/jpeg;base64,' + qrImage.data;
        this.qrImage = qrImage;
        this.bigTabService.forward();
      },
      error => {
        console.log('error ' + error);
        this.alert.errorAlert('За цим обладнанням не закріплено QR-коду');
        this.bigTabService.forward();
      });
  }


  clearOldImgData(){
    this.image = '';
    this.qrImage = undefined;
  }

  // download the img to your device
  doDownload(value) {
    let link = document.createElement('a');
    link.href = value;
    link.download = this.qrImage.name ;
    document.body.appendChild(link);
    link.click();
  }

  back(){
    this.bigTabService.back();
  }

/** Close all opened async subscriptions*/
  closeAllAsyncSubscriptions() {
    this.inventory_nums$.unsubscribe();
    this.qr_img$.unsubscribe();

  }
/**@ignore*/
  ngOnDestroy(): void {
    this.closeAllAsyncSubscriptions();
  }


  //--- qr code. manual creation---

}
